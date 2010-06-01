package stencil.operator.module.provided;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.*;
import stencil.operator.util.BasicProject;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.types.Converter;


public class TupleUtil extends BasicModule {
	
	/**Rename the components of a tuple with new names, 
	 * like an echo but with variable name.
	 */
	public static final class Rename extends BasicProject {
		private static String NAMES = "names";

		String[] keys;

		public Rename(OperatorData opData, Specializer specializer) {
			super(opData);
			keys = specializer.get(NAMES).getText().split("\\s+,\\s+");
		}

		public Rename(OperatorData opData, String...keys) {
			super(opData);
			this.keys = keys;
		}

		public Tuple query(Object... values) {
			assert keys.length == values.length : "Keys and values lengths do not match.";
			
			return new ArrayTuple(values);
		}
		
		public Rename duplicate() {return new Rename(operatorData, keys);}
	}

	/**How many elements does the given tuple have?
	 * This does a single-level descent into all tuples passed
	 * as arguments. If no tuple is passed, the result is 
	 * equal to the number of arguments.
	 * */
	public static final class Size extends BasicProject {
		protected Size(OperatorData opData) {super(opData);}
		public int query(Tuple t) {return t.size();}
		public Size duplicate() {return this;}
	}
	
	
	/**Takes a tuple and returns a new tuple where every element
	 * of the original tuple is now a tuple in the new tuple.
	 */
	public static final class EnfoldValues extends BasicProject {
		protected EnfoldValues(OperatorData opData) {super(opData);}
		public Tuple[] query(Tuple t) {
			Tuple[] ts = new Tuple[t.size()];
			for (int i=0; i< t.size(); i++) {
				ts[i] = Converter.toTuple(t.get(i));
			}
			return ts;
		}
		public EnfoldValues duplicate() {return this;}
	}
	
	/**Take a tuple of tuples.  Retrieve the n-th field from
	 * each of those tuples to form a new tuple.
	 * 
	 * @author jcottam
	 *
	 */
	public static final class Select extends BasicProject {
		private final int field;
		
		public Select(OperatorData opData, Specializer spec) {
			super(opData);
			field = Converter.toInteger(spec.get("field"));
		}
		
		public Object[] query(Tuple t) {
			Object[] values = new Object[t.size()];
			for (int i=0; i< t.size() ;i++) {
				if (t.get(i) instanceof Tuple) {
					values[i] = Converter.toTuple(t.get(i)).get(field);
				}
			}
			return values;
		}
	}
	
	
	/**Takes a tuple and extends it by the passed values.
	 * Resulting tuple can only safely be numerically dereferenced.
	 * */
	public static final class ExtendTuple extends BasicProject {
		public ExtendTuple(OperatorData opData) {super(opData);}
		
		public Tuple query(Tuple t, Object... more) {
			if (t == null) {t = new ArrayTuple();}
			Object[] values = Tuples.toArray(t);
			Object[] target = new Object[values.length + more.length];
			
			System.arraycopy(values, 0, target, 0, values.length);
			System.arraycopy(more, 0, target, values.length, more.length);
			
			return new ArrayTuple(target);
		}
	}
	
	public static final class Get extends BasicProject {
		public Get(OperatorData opData) {super(opData);}
		public Tuple query(Tuple t, int i) {return Converter.toTuple(t.get(i));}
	}
	
	public static final class ToTuple extends BasicProject {
		public ToTuple(OperatorData opData) {super(opData);}
		public Tuple query(Object o) {return Converter.toTuple(o);}
	}
	
	public TupleUtil(ModuleData md) {super(md);}
		
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData ld = moduleData.getOperator(name);

		if (ld.isComplete()) {return ld;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		OperatorData opData = getOperatorData(name, specializer);

		if (name.equals("Rename")) {return new Rename(opData, specializer);}
		if (name.equals("Select")) {return new Select(opData, specializer);}
		else {return super.instance(name, specializer);}
	}
}
