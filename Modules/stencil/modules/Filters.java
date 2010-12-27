package stencil.modules;

import java.util.regex.Pattern;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

@Module
@Description("Module for doing boolean comparisons.  These are the operators used in the Filter operations.")
public class Filters extends BasicModule {

	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean GTE(double n1, double n2) {return n1 >= n2;}

	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean GT(double n1, double n2) {return n1 > n2;}
	
	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean LT(double n1, double n2) {return n1 < n2;}

	@Operator @Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
	public static final boolean LTE(double n1, double n2) {return n1 <= n2;}

		
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
	public static class RegExp extends AbstractOperator {
		private static final String PATTERN_KEY = "pattern";
		private final Pattern patternCache; 
		private final boolean negated;

		public RegExp(OperatorData opData, Specializer specializer) {
			this(opData, specializer, false);
		}
		
		protected RegExp(OperatorData opData, Specializer specializer, boolean negated) {
			super(opData);
			Atom pattern = specializer.get(PATTERN_KEY);
			patternCache = (pattern == null || pattern.isNull()) ? null : Pattern.compile(pattern.getText());
			this.negated = negated;
		}
		
		@Facet(memUse="FUNCTION", prototype="(boolean V)", alias={"map", "query"})
		public boolean query(String value, String pattern) {			
			Pattern matcher = patternCache != null ? patternCache : Pattern.compile(pattern);
			return !negated == matcher.matcher(value).matches();
		}
		
		@Facet(memUse="FUNCTION", prototype="(boolean V)")
		public boolean match(String value) {
			assert patternCache != null;
			return !negated == patternCache.matcher(value).matches();
		}
	}

	
}
