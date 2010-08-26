package stencil.modules;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

public class Geometry extends BasicModule {
	public static class Scale extends AbstractOperator {
		private static final String SCALE_KEY = "by";
		
		final double scale;
		
		public Scale(OperatorData opData, Specializer spec) {
			super(opData);
			scale = Converter.toDouble(spec.get(SCALE_KEY));
		}
		
		public double[] query(double x, double y) {
			double[] results = new double[2];
			results[0] = scale * x; 
			results[1] = scale * y;
			return results;
		}
	}

	public static class ScaleRect extends AbstractOperator {
		private static final String SCALE_KEY = "by";
		
		final double shift;
		final double scale;
		
		public ScaleRect(OperatorData opData, Specializer spec) {
			super(opData);
			scale = Converter.toDouble(spec.get(SCALE_KEY));
			shift = scale/2;
		}
		
		public double[] query(double x, double y, double width, double height) {
			double[] results = new double[4];
			results[0] = shift * x; 
			results[1] = shift * y;
			results[2] = (scale * width);
			results[3] = (scale * height);			
			return results;
			
		}
	}

	public Geometry(ModuleData md) {super(md);}
}
