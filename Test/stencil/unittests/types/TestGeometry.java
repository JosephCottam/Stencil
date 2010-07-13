package stencil.unittests.types;

import java.awt.geom.*;

import stencil.tuple.Tuple;
import stencil.types.geometry.*;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;


public class TestGeometry extends StencilTestCase {
	public void testPointTuple() {
		for (int i=0; i<100; i++) {
			Point2D v = new Point2D.Double(i*2,-i/2);
			PointTuple t = new PointTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}
	
	
	public void testRectangleTuple() {
		for (int i=0; i<100; i++) {
			Rectangle2D v = new Rectangle2D.Double(i*2, i/2, i*3.5, i/3);
			RectangleTuple t = new RectangleTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}
	
	public void testWrapPoint() {
		GeometryWrapper wrapper = new GeometryWrapper();
		for (int i=0; i<100; i++) {
			Point2D v = new Point2D.Double(i*3.2,-i/3);
			Tuple t = wrapper.toTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}


	public void testWrapRectangle() {
		GeometryWrapper wrapper = new GeometryWrapper();
		for (int i=0; i<100; i++) {
			Rectangle2D v = new Rectangle2D.Double(-i*2.6,i/7, i/2.2,-i*5.6);
			Tuple t = wrapper.toTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}

	
	
	public void testPointConverter() {
		for (int i=0; i<100; i++) {
			Point2D v = new Point2D.Double(i*3.2,-i/3);
			Tuple t = Converter.toTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}
	
	public void testRectangleConverter() {
		for (int i=0; i<100; i++) {
			Rectangle2D v = new Rectangle2D.Double(-i*2.6,i/7, i/2.2,-i*5.6);
			Tuple t = Converter.toTuple(v);

			assertEquals(v.getX(), t.get("X"));
			assertEquals(v.getY(), t.get("Y"));
		}
	}

}
