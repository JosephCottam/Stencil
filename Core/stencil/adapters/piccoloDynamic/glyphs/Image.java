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
package stencil.adapters.piccoloDynamic.glyphs;

import stencil.WorkingDirectory;
import stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.piccoloDynamic.util.Attribute;
import stencil.adapters.piccoloDynamic.util.Attributes;
import edu.umd.cs.piccolo.nodes.PImage;


public class Image extends CommonNode{
	public static final String IMPLANTATION_NAME  = "IMAGE";

	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		for (Attribute a : CommonNode.PROVIDED_ATTRIBUTES.values()) {PROVIDED_ATTRIBUTES.put(a);}

		PROVIDED_ATTRIBUTES.put(new Attribute("FILE", "getFile", "setFile", Image.class, null, String.class));

		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.WIDTH, "getWidth", "setWidth", Image.class, new Double(10)));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.HEIGHT, "getHeight", "setHeight", Image.class, new Double(10)));
	}

	protected String filename;
	protected PImage image;
	
	//Variables to control when auto-scaling occurs
	protected boolean hasSetWidth; //Must be initialized after the apply defaults to be effective since apply uses the setter methods.
	protected boolean hasSetHeight;//Must be initialized after the apply defaults to be effective since apply uses the setter methods.

	protected Image(String id, Attributes attributes) {super(id, IMPLANTATION_NAME, attributes);}
	public Image(String id) {
		super(id, IMPLANTATION_NAME, PROVIDED_ATTRIBUTES);
		image = new PImage();
		super.setChild(image);
		applyDefaults();
		hasSetWidth = false;
		hasSetHeight = false;
	}

	public String getFile() {return filename;}
	public void setFile(String filename) {
		filename = WorkingDirectory.resolvePath(filename);
		if (filename.equals(this.filename)) {return;} //Only update if it is a new filename
		

		image.setImage(filename);
		this.filename = filename;
	}

	public void setWidth(Double width) {
		hasSetWidth = true;
		if (!hasSetHeight) {
			image.setHeight(scale(image.getWidth(), width, image.getHeight()));
		}
		image.setWidth(width);
	}
	
	public double getWidth() {return image.getWidth();}

	public void setHeight(Double height) {
		hasSetHeight = true;
		if (!hasSetWidth) {
			image.setWidth(scale(image.getHeight(), height, image.getWidth()));
		}
		image.setHeight(height);
	}
	
	public double getHeight() {return image.getHeight();}
	
	/**Given the shift in the primary value, what would the secondary value now be?*/
	private final double scale(double oldPrimary, double newPrimary, double oldSecondary) {
		if (newPrimary ==0) {return 0;}
		else {return (newPrimary/oldPrimary) * oldSecondary;}
	}
}
