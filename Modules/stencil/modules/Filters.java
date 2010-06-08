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
import stencil.types.Converter;

public class Filters extends BasicModule {

	public static final boolean gte(double n1, double n2) {return n1 >= n2;}
	public static final boolean gteI(int n1, int n2) {return n1 >= n2;}

	public static final boolean gt(double n1, double n2) {return n1 > n2;}
	public static final boolean gtI(int n1, int n2) {return n1 > n2;}

	public static final boolean lt(double n1, double n2) {return n1 < n2;}
	public static final boolean ltI(int n1, int n2) {return n1 < n2;}

	public static final boolean lteq(double n1, double n2) {return n1 <= n2;}
	public static final boolean lteqI(int n1, int n2) {return n1 <= n2;}
	
	public static final boolean eqD(double n1, double n2) {return n1 == n2;}
	public static final boolean eqI(int n1, int n2) {return n1 == n2;}
	public static final boolean neqD(double n1, double n2) {return n1 != n2;}
	public static final boolean neqI(int n1, int n2) {return n1 != n2;}

	
	public static final boolean eqs(Object lhs, Object rhs) {
		if (rhs == null || lhs == null) {return lhs == rhs;}
		if (rhs instanceof Number || lhs instanceof Number) {
			double l = Converter.toDouble(lhs).doubleValue();
			double r = Converter.toDouble(rhs).doubleValue();
			return l == r;
		} else {
			return lhs.equals(rhs);
		}
	}
	public static final boolean neqs(Object lhs, Object rhs) {return !eqs(lhs, rhs);}


	public static final boolean trivialTrue(Object... args) {return true;}
	public static final boolean trivialFalse(Object... args) {return false;}
		
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
		
		public boolean query(String value, String pattern) {			
			Pattern matcher = patternCache != null ? patternCache : Pattern.compile(pattern);
			return !negated == matcher.matcher(value).matches();
		}
		
		public boolean match(String value) {
			assert patternCache != null;
			return !negated == patternCache.matcher(value).matches();
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
