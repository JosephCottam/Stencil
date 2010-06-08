package stencil.modules;

import java.util.regex.Pattern;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.BasicProject;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.tuple.BooleanSingleton;
import stencil.types.Converter;

public class Filters extends BasicModule {

	public static final BooleanSingleton gte(double n1, double n2) {return BooleanSingleton.instance(n1 >= n2);}
	public static final BooleanSingleton gteI(int n1, int n2) {return BooleanSingleton.instance(n1 >= n2);}

	public static final BooleanSingleton gt(double n1, double n2) {return BooleanSingleton.instance(n1 > n2);}
	public static final BooleanSingleton gtI(int n1, int n2) {return BooleanSingleton.instance(n1 > n2);}

	public static final BooleanSingleton lt(double n1, double n2) {return BooleanSingleton.instance(n1 < n2);}
	public static final BooleanSingleton ltI(int n1, int n2) {return BooleanSingleton.instance(n1 < n2);}

	public static final BooleanSingleton lteq(double n1, double n2) {return BooleanSingleton.instance(n1 <= n2);}
	public static final BooleanSingleton lteqI(int n1, int n2) {return BooleanSingleton.instance(n1 <= n2);}
	
	public static final BooleanSingleton eqD(double n1, double n2) {return BooleanSingleton.instance(n1 == n2);}
	public static final BooleanSingleton eqI(int n1, int n2) {return BooleanSingleton.instance(n1 == n2);}
	public static final BooleanSingleton neqD(double n1, double n2) {return BooleanSingleton.instance(n1 != n2);}
	public static final BooleanSingleton neqI(int n1, int n2) {return BooleanSingleton.instance(n1 != n2);}

	private static final boolean eqsBase(Object lhs, Object rhs) {
		if (rhs == null || lhs == null) {return lhs == rhs;}
		if (rhs instanceof Number || lhs instanceof Number) {
			double l = Converter.toDouble(lhs).doubleValue();
			double r = Converter.toDouble(rhs).doubleValue();
			return l == r;
		} else {
			return lhs.equals(rhs);
		}
	}
	
	public static final BooleanSingleton eqs(Object lhs, Object rhs) {
		return BooleanSingleton.instance(eqsBase(lhs, rhs));
	}
	public static final BooleanSingleton neqs(Object lhs, Object rhs) {return BooleanSingleton.instance(!eqsBase(lhs, rhs));}


	public static final BooleanSingleton trivialTrue(Object... args) {return BooleanSingleton.TRUE;}
	public static final BooleanSingleton trivialFalse(Object... args) {return BooleanSingleton.FALSE;}
		
	public static class RegExp extends BasicProject {
		private static final String PATTERN_KEY = "pattern";
		private final Pattern patternCache; 
		private final boolean negated;

		public RegExp(OperatorData opData, Specializer specializer, boolean negated) {
			super(opData);
			Atom pattern = specializer.get(PATTERN_KEY);
			patternCache = (pattern == null || pattern.isNull()) ? null : Pattern.compile(pattern.getText());
			this.negated = negated;
		}
		
		public BooleanSingleton query(String value, String pattern) {			
			Pattern matcher = patternCache != null ? patternCache : Pattern.compile(pattern);
			return BooleanSingleton.instance(!negated == matcher.matcher(value).matches());
		}
		
		public BooleanSingleton match(String value) {
			assert patternCache != null;
			return BooleanSingleton.instance(!negated == patternCache.matcher(value).matches());
		}

	
	}
	
	public Filters(ModuleData md) {super(md);}
	
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getModuleData().getOperator(name);
		
		if(name.equals("RE")) {
			return new RegExp(operatorData, specializer, false);
		} else if (name.equals("NRE")) {
			return new RegExp(operatorData, specializer, true);
		} else {
			return super.instance(name, specializer);
		}
	}

	
	
}
