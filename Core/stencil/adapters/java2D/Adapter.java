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
package stencil.adapters.java2D;

 import java.awt.Color;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.display.StencilPanel;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Program;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.data.glyphs.Basic;
import stencil.adapters.java2D.data.guides.*;
import stencil.adapters.java2D.util.Painter;
import stencil.adapters.java2D.util.ZoomPanHandler;

public final class Adapter implements stencil.adapters.Adapter<Glyph2D> {
	public static final Adapter INSTANCE = new Adapter();
	
	private boolean defaultMouse;
	
	public Panel generate(Program program) {
		Panel panel = new Panel(program);
		constructGuides(panel, program);
		
		if (defaultMouse) {
			ZoomPanHandler zp = new ZoomPanHandler();
			panel.addMouseListener(zp);
			panel.addMouseMotionListener(zp);
		}
		return panel;
	}

	public Class getGuideClass(String name) {
		if (name.equals("axis")) {return Axis.class;}
		else if (name.equals("sidebar")) {return Sidebar.class;}
		
		throw new IllegalArgumentException(String.format("Guide type %1$s not known in adapter.", name));
	}

	public DisplayLayer makeLayer(Layer l) {return DisplayLayer.instance(l);}
	public void setDefaultMouse(boolean m) {this.defaultMouse = m;}
	public void setDebugColor(Color c) {Basic.DEBUG_COLOR = c;}
	

	public void setRenderQuality(String value) throws IllegalArgumentException {
		if (value.equals("LOW")) {
			Painter.renderQuality = Painter.LOW_QUALITY;
		} else if (value.equals("HIGH")) {
			Painter.renderQuality = Painter.HIGH_QUALITY;
		} else {
			throw new IllegalArgumentException("Could not set render quality to unknown value: " + value);
		}
	}

	public void finalize(StencilPanel panel) {/**No finalization required...yet**/}
	
	public Panel compile(String programSource) throws Exception {
		return generate(ParseStencil.parse(programSource, this));
	}
	
	
	//TODO: Lift out into a grammar pass...
	private void constructGuides(Panel panel, Program program) {
		int sidebarCount = 0;//How many side-bars have been created?
		
		for (Guide guideDef : program.getCanvasDef().getGuides()) {
			DisplayLayer l = panel.getLayer(guideDef.getLayer());
			String attribute = guideDef.getAttribute();
			String guideType = guideDef.getGuideType();
			
			if (guideType.equals("axis")) {
				Guide2D guide = new Axis(attribute, guideDef);
				l.addGuide(attribute, guide);
				
				if (l.hasGuide("X") && l.hasGuide("Y")) {
					((Axis) l.getGuide("X")).setConnect(true);
					((Axis) l.getGuide("Y")).setConnect(true);
				}
			} else if (guideType.equals("sidebar")) {
				Guide2D guide = new Sidebar(attribute, guideDef, sidebarCount++);
				l.addGuide(attribute, guide);	
			} else {
				throw new IllegalArgumentException("Unknown guide type requested: " +guideType);
			}
		}
	}
}
