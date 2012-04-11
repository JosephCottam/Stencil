package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;

@Description("Summarization of groups of things (currently just max and min).")
@Module()
public class Summary extends BasicModule {
	
	

	private static abstract class Extreme<T extends StencilOperator> extends AbstractOperator.Statefull<T> {
 		protected final Class targetClass;
 		protected final boolean GT;
 		
 		protected Extreme(OperatorData opData, Specializer spec, boolean GT) {
 			super(opData);
 			this.GT = GT;
 			
 			try {targetClass = Converter.toClass(spec.get("c"));}
 			catch (Exception e) {throw new SpecializationException(opData.module(), opData.name(), spec, "Unknown class specified.");}
 			
 		}
 		
 		protected Extreme(OperatorData opData, Class target, boolean GT) {
 			super(opData);
 			targetClass = target;
 			this.GT = GT;
 		}
 		
 		public Comparable extreme(Object... values) {
 			if (!Comparable.class.isAssignableFrom(targetClass)) {throw new IllegalArgumentException("Can only use comparable clases; recieved " + targetClass.getName());}
 			
 			Comparable extreme = null;
 			for (Object v: values) {
 				Comparable c = (Comparable) Converter.convert(v, targetClass);
 				if (extreme  == null) {extreme = c;}
 				else if ((GT && extreme.compareTo(c) < 0)
 						|| (!GT && extreme.compareTo(c)>0)) {extreme = c;}
			}
			return extreme;
 		} 		
	}
	
	@Operator(name="Min", spec="[c: \"Comparable\"]")
	public static class Min extends Extreme {
		private Min(OperatorData opData, Class target) {super(opData, target, false);}
		public Min(OperatorData opData, Specializer spec) {
			super(opData, spec, false);
		}
		
 		@Facet(memUse="FUNCTION", prototype="(double v)", alias={"map", "query"})
		public Comparable extreme(Object... values) {return super.extreme(values);}
 		public Min duplicate() {return new Min(operatorData, targetClass);}
	}
	
	@Operator(name="Max", spec="[c: \"Comparable\"]")
	public static class Max extends Extreme {
		private Max(OperatorData opData, Class target) {super(opData, target, false);}
		public Max(OperatorData opData, Specializer spec) {
			super(opData, spec, true);
		}
 		@Facet(memUse="FUNCTION", prototype="(double v)", alias={"map", "query"})
		public Comparable extreme(Object... values) {return super.extreme(values);}
 		public Max duplicate() {return new Max(operatorData, targetClass);}
	}
	
	private static abstract class FullExtreme extends Extreme {
		private Comparable extreme;

		protected FullExtreme(OperatorData opData, Specializer spec, Boolean GT) {
			super(opData, spec, GT);
		}

 		protected FullExtreme(OperatorData opData, Class target, boolean GT) {
 			super(opData, target, GT);
 		}

 		public Comparable map(Object... values) {
 			Comparable newExtreme = super.extreme(values);
 			if (extreme != null) {newExtreme = super.extreme(newExtreme, extreme);}
 			if (newExtreme != extreme) {
 				stateID++;
 				extreme = newExtreme;
 			}
			return extreme;
		}
 		
 		public Comparable query(Object... values) {
 			return extreme;
 		}
	}
	
	@Operator(name="FullMin", spec="[c: \"Comparable\"]", defaultFacet="map")
	public static class FullMin extends FullExtreme {
		private FullMin(OperatorData opData, Class target) {super(opData, target, false);}
		public FullMin(OperatorData opData, Specializer spec) {
			super(opData, spec, false);
		}
		
		@Facet(memUse="WRITER", prototype="(Comparable v)", counterpart="query")
 		public Comparable map(Object... values) {return super.map(values);}
 		
 		@Facet(memUse="READER", prototype="(Comparable v)")
 		public Comparable query(Object... values) {return super.query(values);}
 		
 		public FullMin duplicate() {return new FullMin(operatorData, targetClass);}
	}

	@Operator(name="FullMax", spec="[c: \"Comparable\"]", defaultFacet="map")
	public static class FullMax extends FullExtreme {
		private FullMax(OperatorData opData, Class target) {super(opData, target, false);}
		public FullMax(OperatorData opData, Specializer spec) {
			super(opData, spec, true);
		}
		
		@Facet(memUse="WRITER", prototype="(Comparable v)", counterpart="query")
 		public Comparable map(Object... values) {return super.map(values);}
 		
 		@Facet(memUse="READER", prototype="(Comparable v)")
 		public Comparable query(Object... values) {return super.query(values);}
 		public FullMax duplicate() {return new FullMax(operatorData, targetClass);}
	}
}