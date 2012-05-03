package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.modules.Average.FullMean;
import stencil.modules.stencilUtil.range.Range;
import stencil.modules.stencilUtil.range.RangeDescriptor;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Context;
import stencil.parser.tree.StencilTree;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;

//TODO: Auto optimize for range all

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
		
 		@Override
		@Facet(memUse="FUNCTION", prototype="(double v)", alias={"map", "query"})
		public Comparable extreme(Object... values) {return super.extreme(values);}
 		@Override
		public Min duplicate() {return new Min(operatorData, targetClass);}
	}
	
	@Operator(name="Max", spec="[c: \"Comparable\"]")
	public static class Max extends Extreme {
		private Max(OperatorData opData, Class target) {super(opData, target, false);}
		public Max(OperatorData opData, Specializer spec) {
			super(opData, spec, true);
		}
 		@Override
		@Facet(memUse="FUNCTION", prototype="(double v)", alias={"map", "query"})
		public Comparable extreme(Object... values) {return super.extreme(values);}
 		@Override
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
		
		@Override
		@Facet(memUse="WRITER", prototype="(Comparable v)", counterpart="query")
 		public Comparable map(Object... values) {return super.map(values);}
 		
 		@Override
		@Facet(memUse="READER", prototype="(Comparable v)")
 		public Comparable query(Object... values) {return super.query(values);}
 		
 		@Override
		public FullMin duplicate() {return new FullMin(operatorData, targetClass);}
	}

	@Operator(name="FullMax", spec="[c: \"Comparable\"]", defaultFacet="map")
	public static class FullMax extends FullExtreme {
		private FullMax(OperatorData opData, Class target) {super(opData, target, false);}
		public FullMax(OperatorData opData, Specializer spec) {
			super(opData, spec, true);
		}
		
		@Override
		@Facet(memUse="WRITER", prototype="(Comparable v)", counterpart="query")
 		public Comparable map(Object... values) {return super.map(values);}
 		
 		@Override
		@Facet(memUse="READER", prototype="(Comparable v)")
 		public Comparable query(Object... values) {return super.query(values);}
 		@Override
		public FullMax duplicate() {return new FullMax(operatorData, targetClass);}
	}
//	
//	@Override
//	public StencilOperator optimize(StencilOperator op, Context context) {
////		if (op.getName().equals("Min") || op.getName().equals("Max") 
////			&& context.callSites().size() == 1
////			&& context.callSites().get(0).is(StencilParser.OP_AS_ARG)) {
////			StencilTree site = context.callSites().get(0).getAncestor(StencilParser.FUNCTION);
////			MultiPartName name = Freezer.multiName(site.find(StencilParser.OP_NAME));
////
////			//--------------------------------------------------------------------------------------------------------
////			//HACK: This is a bad idea...but I don't have a better one right now!
////			boolean isRange = name.name().contains("Range"); 
////			//--------------------------------------------------------------------------------------------------------
////			
////			if (!isRange) {return op;}	//Can only optimize ranges...
////			
////			Specializer spec = Freezer.specializer(site.find(StencilParser.SPECIALIZER));
////			if (!spec.containsKey(Range.RANGE_KEY)) {return op;}
////			
////			RangeDescriptor r = new RangeDescriptor(spec.get(Range.RANGE_KEY));
////			if (r.isFullRange()) {
////				OperatorData od=ModuleDataParser.operatorData(FullMean.class, this.getName());
////				od = od.name(op.getName());
////				if (op.getName().equals("Min")) {return new FullMin(od, spec);}
////				else if (op.getName().equals("Max")) {return new FullMax(od,spec);}
////				else {throw new Error("Neither min nor max requested of summary...");}
////			}
////		}
//		return op;
//	}
}