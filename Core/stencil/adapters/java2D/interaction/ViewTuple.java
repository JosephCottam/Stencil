package stencil.adapters.java2D.interaction;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import stencil.adapters.java2D.Canvas;
import stencil.adapters.java2D.Panel;
import stencil.tuple.InvalidNameException;
import stencil.types.Converter;
import stencil.util.DoubleDimension;

public final class ViewTuple extends stencil.display.ViewTuple {
	private Panel view;
	private Canvas canvas;
	
	public ViewTuple(Panel view) {
		this.view = view;
		canvas = view.getCanvas().getComponent();
	}

	public void set(String name, Object value) {
		if (PROTOTYPE.contains(name)) {
			AffineTransform t = canvas.viewTransform();
			double scaleY = t.getScaleY();
			double scaleX = t.getScaleX();

			double space;
			double val = Converter.toDouble(value);
			double oldY = -getY();
			double oldX = -getX();
			Point2D p = new Point2D.Double(t.getTranslateX(), t.getTranslateY());

			if (name.equals("ZOOM")) {
				canvas.zoomTo(p, val);
				return;
			} else if (name.equals("X")) {
				t.setToScale(scaleX, scaleY);
				t.translate(-val, oldY);
				try {canvas.setViewTransform(t);}
				catch (NoninvertibleTransformException e) {/*Modifications to translate cannot give errors.*/}
				return;
			} else if (name.equals("Y")) {
				t.setToScale(scaleX, scaleY);
				t.translate(oldX, -val);
				try {canvas.setViewTransform(t);}
				catch (NoninvertibleTransformException e) {/*Modifications to translate cannot give errors.*/}
				return;
			} else if (name.equals("WIDTH")) {
				space = canvas.getWidth();
				if (space == 0) {space =1;} //Keep the world from exploding
				if (val == 0) {val =1;}
				
				t.setToScale(space/val, scaleY);
				t.translate(oldX, oldY);

				try {canvas.setViewTransform(t);}
				catch (NoninvertibleTransformException e) {/*Scale so calculated cannot yield error.*/}
				return;
			} else if (name.equals("HEIGHT")) {
				space = canvas.getHeight();
				if (space == 0) {space =1;} //Keep the world from exploding
				if (val == 0) {val =1;}

				t.setToScale(scaleX, space/val);
				t.translate(oldX, oldY);

				try {canvas.setViewTransform(t);}
				catch (NoninvertibleTransformException e) {/*Scale so calculated cannot yield error.*/}
				return;
			}
		}
		throw new IllegalArgumentException(String.format("Cannot set %1$s on view.", name));
	}

	public Object get(String name) throws InvalidNameException {
		if (PROTOTYPE.contains(name)) {
			AffineTransform t = canvas.inverseViewTransform();
		
			if (name.equals("ZOOM")) {return canvas.getScale();}
			if (name.equals("X")){ return getX();}
			if (name.equals("Y")){ return getY();}
			if (name.equals("PORTAL_WIDTH")){ return view.getInsetBounds().getWidth();}
			if (name.equals("PORTAL_HEIGHT")){return view.getInsetBounds().getHeight();}
			if (name.equals("WIDTH")){ 
				Point2D p = new Point2D.Double(view.getInsetBounds().getWidth(), 0);
				return t.deltaTransform(p,p).getX();
			}
			if (name.equals("HEIGHT")){ 
				Point2D p = new Point2D.Double(0,view.getBounds().getHeight());
				return t.deltaTransform(p, p).getY();
			}
			if (name.equals("RIGHT")) {return (Double) get("X") + (Double) get("WIDTH");}
			if (name.equals("BOTTOM")) {return (Double) get("Y") + (Double) get("HEIGHT");}
		}
		throw new IllegalArgumentException("Unknown field, cannot query " + name + " on view.");
	}
	
	private double getX() {
		AffineTransform t = canvas.viewTransform();
		return -t.getTranslateX()/t.getScaleX();
	}
	
	private double getY() {
		AffineTransform t = canvas.viewTransform();
		return -t.getTranslateY()/t.getScaleY();	
	}
	
	public Point2D canvasToView(Point2D p) {
		return canvas.viewTransform().transform(p, p);
	}
	
	public Dimension2D canvasToView(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		canvas.viewTransform().deltaTransform(p, p);
		d.setSize(p.getX(), p.getY());
		return d;
	}
	
	public Point2D viewToCanvas(Point2D p) {
		return canvas.inverseViewTransform().transform(p, p);
	}
	
	public Dimension2D viewToCanvas(Dimension2D d) {
		Point2D p = new Point2D.Double(d.getWidth(), d.getHeight());
		p = canvas.inverseViewTransform().deltaTransform(p, p);
		return new DoubleDimension(p.getX(), p.getY());
	}
}
