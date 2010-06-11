package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.BasicProject;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

public class Geometry extends BasicModule {
	public static class Scale extends BasicProject {
		private static final String SCALE_KEY = "by";
		
		final double scale;
		
		protected Scale(OperatorData opData, Specializer spec) {
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

	public static class ScaleRect extends BasicProject {
		private static final String SCALE_KEY = "by";
		
		final double shift;
		final double scale;
		
		protected ScaleRect(OperatorData opData, Specializer spec) {
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
	
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		OperatorData opData = super.getOperatorData(name, specializer);
		if (name.equals("ScaleRect")) {return new ScaleRect(opData, specializer);}
		else if (name.equals("Scale")) {return new Scale(opData, specializer);}
		
		throw new RuntimeException("Could not instantiate (but passed validation: " + name);
	}
}
