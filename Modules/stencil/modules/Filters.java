package stencil.modules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stencil.module.operator.util.DirectOperator;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.types.Converter;
import stencil.types.NumericWrapper;

@Module
@Description("Module for doing boolean comparisons.  These are the operators used in the Filter operations.")
public class Filters extends BasicModule {
	/**Base class for non-reflectively invoked filters.**/
	private static abstract class FilterOp extends DirectOperator {
		protected FilterOp(OperatorData od) {super(od);}
		
		/**Actual work to be performed.**/
		protected abstract boolean op(double arg1, double arg2);
		@Override
		public Object invoke(Object[] arguments)
				throws MethodInvokeFailedException {
			double d0 = NumericWrapper.toDouble(arguments[0]);
			double d1 = NumericWrapper.toDouble(arguments[1]);			
			return op(d0, d1);
		}		
	}
	
	@Operator 
	public static final class GTE extends FilterOp {
		public GTE(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean op(double n1, double n2) {return n1 >= n2;}		
	}

	@Operator 
	public static final class GT extends FilterOp {
		public GT(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean op(double n1, double n2) {return n1 > n2;}		
	}

	@Operator 
	public static final class LT extends FilterOp {
		public LT(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean op(double n1, double n2) {return n1 < n2;}		
	}

	@Operator 
	public static final class LTE extends FilterOp {
		public LTE(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean op(double n1, double n2) {return n1 <= n2;}		
	}
	
	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean EQ(Object lhs, Object rhs) {
		if (rhs == null || lhs == null) {return lhs == rhs;}
		if (rhs instanceof Number || lhs instanceof Number) {
			double l = Converter.toDouble(lhs).doubleValue();
			double r = Converter.toDouble(rhs).doubleValue();
			return l == r;
		} else {
			return lhs.equals(rhs);
		}
	}

	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean NEQ(Object lhs, Object rhs) {return !EQ(lhs, rhs);}


	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean trivialTrue(Object... args) {return true;}

	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean trivialFalse(Object... args) {return false;}
		
	
	@Operator(name="NRE")
	public static class NRegExp extends RegExp {
		public NRegExp(OperatorData opData, Specializer specializer) {super(opData, specializer, true);}
	}
	
	
	@Operator(name="RE")
	public static class RegExp extends DirectOperator {
		private static final String PATTERN_KEY = "pattern";
		private final Matcher matcherCache; 
		private final boolean negated;

		public RegExp(OperatorData opData, Specializer specializer) {
			this(opData, specializer, false);
		}
		
		protected RegExp(OperatorData opData, Specializer specializer, boolean negated) {
			super(opData);
			String pattern = (String) specializer.get(PATTERN_KEY);
			matcherCache = pattern == null ? null : Pattern.compile(pattern).matcher("");
			this.negated = negated;
		}
		
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean query(String value, String pattern) {			
			Matcher matcher = matcherCache != null ? matcherCache : Pattern.compile(pattern).matcher(value);
			return !negated == matcher.reset(value).matches();
		}
		
		@Facet(memUse="FUNCTION", prototype="(boolean V)")
		public boolean match(String value) {
			assert matcherCache != null;
			return !negated == matcherCache.reset(value).matches();
		}

		@Override
		public Boolean invoke(Object[] arguments) {
			if (arguments.length ==1) {return match(arguments[0].toString());}
			else if (arguments.length ==2) {return query(arguments[0].toString(), arguments[1].toString());}
			else {throw new IllegalArgumentException(String.format("Received %1$s, but expect 1 or 2.", arguments.length));} 
		}

	}

	
}
