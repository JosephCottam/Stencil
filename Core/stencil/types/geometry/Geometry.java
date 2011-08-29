package stencil.types.geometry;

import java.awt.geom.Point2D;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;

@Module
@Description("Geometric operations")
public class Geometry extends BasicModule {
	
	@Operator
	public static class Scale extends AbstractOperator {
		private static final String SCALE_KEY = "by";
		
		final double scale;
		
		public Scale(OperatorData opData, Specializer spec) {
			super(opData);
			scale = Converter.toDouble(spec.get(SCALE_KEY));
		}
		
		@Facet(memUse="FUNCTION", prototype="(double x, double y)", alias={"map","query"})
		public double[] query(double x, double y) {
			double[] results = new double[2];
			results[0] = scale * x; 
			results[1] = scale * y;
			return results;
		}
	}

	@Operator(spec="[by:1]")
	public static class ScaleRect extends AbstractOperator {
		private static final String SCALE_KEY = "by";
		
		final double shift;
		final double scale;
		
		public ScaleRect(OperatorData opData, Specializer spec) {
			super(opData);
			scale = Converter.toDouble(spec.get(SCALE_KEY));
			shift = scale/2;
		}
		
		@Facet(memUse="FUNCTION", prototype="(double x, double y, double width, double height)", alias={"map","query"})
		public double[] query(double x, double y, double width, double height) {
			double[] results = new double[4];
			results[0] = shift * x; 
			results[1] = shift * y;
			results[2] = (scale * width);
			results[3] = (scale * height);			
			return results;
			
		}
	}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(double x, double y)", alias={"map","query"})
	public static final Point2D point(double x, double y) {return new Point2D.Double(x,y);}
	
	@Description("Distance bewteen two points (from the first to the second)")
	@Operator
	@Facet(memUse="FUNCTION", prototype="(double distance)", alias={"map","query"})
	public static double distance(Point2D p1, Point2D p2) {
		return p1.distance(p2);
	}
	
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(double x, double y)", alias={"map","query"})
	public static PointTuple projectAlong(Point2D p1, Point2D p2, double distance) {
		double ratio = distance/p1.distance(p2);
		double rise = p2.getY()-p1.getY();
		double Y = p1.getY() + (ratio*rise);
		
		double run = p2.getX()-p1.getX();
		double X = p1.getX() + (ratio*run);
		return new PointTuple(new Point2D.Double(X,Y));

	}
	
}
