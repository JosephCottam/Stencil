package stencil.adapters.java2D.interaction;

import java.awt.Rectangle;
import java.awt.Color;

import stencil.adapters.java2D.Canvas;
import stencil.types.Converter;


public class CanvasTuple extends stencil.display.CanvasTuple {
	private Canvas canvas;
	
	public CanvasTuple(Canvas c) {canvas = c;}
	
	@Override
	public Canvas getComponent() {return canvas;}

	@Override
	protected Rectangle getBounds() {return canvas.contentBounds(true);}

	public void set(String field, Object value) {
		if (BACKGROUND_COLOR.equals(field)) {
			canvas.setBackground((Color) Converter.convert(value, Color.class));
		} else {
			throw new IllegalArgumentException("Cannot modify " + field + " on canvas.");
		}
	}
}
