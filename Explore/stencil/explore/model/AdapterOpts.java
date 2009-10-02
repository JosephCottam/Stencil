/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.explore.model;

import java.awt.Color;
import java.util.Map;

import static stencil.explore.Application.reporter;

import stencil.adapters.Adapter;


/**Captures the options that may be set on the compiler/interpreter
 * and associated adapter.
 *
 * @author jcottam
 *
 */
public final class AdapterOpts {
	private String adapterName;
	private Color debug;
	private boolean defaultMouse;
	private String renderQuality;

	/**The mapping between names and adapters.
	 * This mapping will be used by getAdapter to provide an adapter, when requested.*/
	public static Map<String, String> adapterMap;
	public static String defaultRenderQuality = "HIGH";

	public AdapterOpts() {
		if (adapterMap.keySet().size() >0) {adapterName = adapterMap.keySet().iterator().next();}//First item in the set
		else {adapterName = "No Adapters Specified.";}

		debug = null;
		defaultMouse = true;
		renderQuality = defaultRenderQuality;
	}

	public AdapterOpts(String adapter, Color debug, boolean defaultMouse, String renderQuality) {
		this.adapterName = adapter;
		this.debug = debug;
		this.defaultMouse = defaultMouse;
		this.renderQuality = renderQuality;
	}


	/**Get an adapter instance that conforms to as many of the options specified as possible.
	 *
	 * @throws IllegalArgumentException Could not find an adapter of the class specified.
	 *
	 **/
	public Adapter getAdapter() {
		if (adapterName == null) {return null;}
		if (!adapterMap.containsKey(adapterName)) {throw new IllegalArgumentException(String.format("No adapter of name %1$s known.", adapterName));}
		String className = null;
		Adapter a;
		
		try {
			className = adapterMap.get(adapterName);
			Class clss = Class.forName(className);
			a = (Adapter) clss.getField("INSTANCE").get(null);
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Could not select proper adapter for %1$s given class name %1$s.", adapterName, className) ,e);
		}
		
		try {a.setDebugColor(debug);}
		catch (UnsupportedOperationException e) {reporter.addError("Could not set debug color.");}
		
		try{a.setDefaultMouse(defaultMouse);}
		catch (UnsupportedOperationException e) {reporter.addError("Could not set mouse handling.");}

		try {a.setRenderQuality(renderQuality);}
		catch (UnsupportedOperationException e) {reporter.addError("Could not set render quality.");}
		catch (IllegalArgumentException e) 		{reporter.addError("Render quality cannot be set to " + renderQuality + " for current adapter.");}
		
		return a;
	}

	public void setAdapterName(String name) {adapterName = name;}
	public String getAdapterName() {return adapterName;}

	public void setDefaultMouse(boolean mouse) {defaultMouse = mouse;}
	public boolean getDefaultMouse() {return defaultMouse;}

	public Color getDebug() {return debug;}
	public void setDebug(Color debug) {this.debug = debug;}
	
	public String getRenderQualiyty() {return renderQuality;}
	public void setRenderQualiyt(String renderQuality) {this.renderQuality = renderQuality;}
}
