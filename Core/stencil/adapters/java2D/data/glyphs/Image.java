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
package stencil.adapters.java2D.data.glyphs;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import stencil.WorkingDirectory;
import stencil.adapters.GlyphAttributes.StandardAttribute;

import stencil.adapters.general.Registrations;
import stencil.adapters.java2D.data.DisplayLayer;
import stencil.adapters.java2D.util.Attribute;
import stencil.adapters.java2D.util.AttributeList;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public final class Image extends Basic {
	private static final double AUTO_SCALE = -1;

	protected static final AttributeList ATTRIBUTES = new AttributeList(Basic.ATTRIBUTES);
	protected static final AttributeList UNSETTABLES= new AttributeList();
	
	private static final Attribute<Double> X = new Attribute("X", 0d);
	private static final Attribute<Double> Y = new Attribute("Y", 0d);
	private static final Attribute<Double> HEIGHT = new Attribute(StandardAttribute.HEIGHT.name(), AUTO_SCALE);
	private static final Attribute<Double> WIDTH = new Attribute(StandardAttribute.WIDTH.name(), AUTO_SCALE);
	private static final Attribute<String> FILE= new Attribute("FILE", null, String.class);
	protected static final Attribute<Double> ROTATION = new Attribute(StandardAttribute.ROTATION);
	
	static {
		ATTRIBUTES.add(X);
		ATTRIBUTES.add(Y);
		ATTRIBUTES.add(FILE);
		ATTRIBUTES.add(HEIGHT);
		ATTRIBUTES.add(WIDTH);
		ATTRIBUTES.add(ROTATION);
	}

	private final String filename;
	private final double width;
	private final double height;

	private final double rotation;
	
	private double oldSX, oldSY;
	private BufferedImage base;
	private BufferedImage display;

	public Image(DisplayLayer layer, String id) {
		super(layer, id);
		
		filename = FILE.defaultValue;
		
		rotation = ROTATION.defaultValue;
		width = WIDTH.defaultValue;
		height = HEIGHT.defaultValue;

		Point2D topLeft = Registrations.registrationToTopLeft(registration, X.defaultValue, Y.defaultValue, height, width);
		
		super.updateBoundsRef(getBounds(topLeft));
	}
	
	
	
	protected Image(String id, Image source) {
		super(id, source);
		this.filename = source.filename;
		this.width = source.width;
		this.height = source.height;
		this.rotation = source.rotation;
		this.oldSX = source.oldSX;
		this.oldSY = source.oldSY;
		this.base = source.base;
		this.display = source.display;
	}



	protected Image(Image source, Tuple option) {
		super(source, option, UNSETTABLES);
		
		filename = switchCopy(source.filename, safeGet(option, FILE));
		width = switchCopy(source.width, safeGet(option, WIDTH));
		height = switchCopy(source.height, safeGet(option, HEIGHT));
		rotation = switchCopy(source.rotation, safeGet(option, ROTATION));
		Point2D topLeft = mergeRegistrations(source, option, width, height, X, Y);

		super.updateBoundsRef(getBounds(topLeft));
	}
	
	private final Rectangle2D getBounds(Point2D p) {return new Rectangle2D.Double(p.getX(), p.getY(), getWidth(), getHeight());}

	protected AttributeList getAttributes() {return ATTRIBUTES;}
	protected AttributeList getUnsettables() {return UNSETTABLES;}


	public Object get(String name) {
		if (HEIGHT.is(name)){return getHeight();}
		if (WIDTH.is(name))	{return getWidth();}
		if (FILE.is(name)) 	{return filename;}
		if (X.is(name))		{return Registrations.topLeftToRegistration(registration, bounds).getX();}
		if (Y.is(name))		{return Registrations.topLeftToRegistration(registration, bounds).getY();}
		if (ROTATION.is(name)) {return rotation;}
		return super.get(name);
	}
	
	public String getImplantation() {return "IMAGE";}

	private double getHeight() {
		if (height != AUTO_SCALE) {return height;}
		else if (base == null) {return 0;}
		return base.getHeight() * autoScale();
	}
	
	public double getWidth() {
		if (width != AUTO_SCALE) {return width;}
		else if (base ==null) {return 0;}
		return base.getWidth() * autoScale();
	}
	
	private double autoScale() {
		if (base == null) {return 1;} 								//Nothing to scale yet
		if (width == AUTO_SCALE && height == AUTO_SCALE) {return 1;}//No scale specified
		if (width == AUTO_SCALE) {return height/base.getHeight();}	//Scale width based on height specified
		if (height == AUTO_SCALE) {return width/base.getWidth();}	//Scale width based on height specified
		return 1;//Default scale factor
	}
	
	/**Given the current information, make sure the image is 
	 * ready to be rendered.
	 */
	private void verifyImage() {
		try {
			if (base == null && filename !=null) {
				String filename = WorkingDirectory.resolvePath(this.filename);
				base = javax.imageio.ImageIO.read(new File(filename));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error validating image.", e);
		}
	}

	@Override
	public void render(Graphics2D g, AffineTransform baseTransform) {
		verifyImage();
		if (base == null) {return;}
		

		g.translate(bounds.getX(), bounds.getY());
		 
		AffineTransform t = g.getTransform();
		g.setTransform(AffineTransform.getTranslateInstance(t.getTranslateX(), t.getTranslateY()));
		double sx = t.getScaleX() * (getWidth()/base.getWidth());
		double sy = t.getScaleY() * (getHeight()/base.getHeight());
		
		if (sx != oldSX || sy != oldSY) {	
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(sx, sy), AffineTransformOp.TYPE_BICUBIC);
			display = op.createCompatibleDestImage(base,base.getColorModel());
			op.filter(base, display);
			oldSX = sx;
			oldSY = sy;
		}		
		
		g.drawRenderedImage(display, null);
		super.postRender(g,baseTransform);
	}

	public Image update(Tuple t) throws IllegalArgumentException {
		if (Tuples.transferNeutral(t, this)) {return this;}

		return new Image(this, t);
	}
	public Image updateID(String id) {return new Image(id, this);}
}
