package stencil.modules;

import java.util.ArrayList;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.FacetData.MemoryUse;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;


public class TupleUtil extends BasicModule {
	/**How many elements does the given tuple have?
	 * This does a single-level descent into all tuples passed
	 * as arguments. If no tuple is passed, the result is 
	 * equal to the number of arguments.
	 * */
	public static final class Size extends AbstractOperator {
		protected Size(OperatorData opData) {super(opData);}
		public int query(Tuple t) {return t.size();}
		public Size duplicate() {return this;}
	}
	
	
	/**Takes a tuple and returns a new tuple where every element
	 * of the original tuple is now a tuple in the new tuple.
	 */
	public static final class EnfoldValues extends AbstractOperator {
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
	 * TODO: Investigate using map and get instead of Select
	 * 
	 * @author jcottam
	 *
	 */
	public static final class Select extends AbstractOperator {
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
	
	
	/**Rename the components of a tuple with new names; 
	 * like an echo but with variable names updated in the prototype.
	 */
	public static final class Rename extends AbstractOperator {
		private static String NAMES = "names";

		private final String[] keys;

		public Rename(OperatorData opData, Specializer specializer) {
			super(opData);
			keys = getNames(specializer);
		}

		public Rename(OperatorData opData, String...keys) {
			super(opData);
			this.keys = keys;
		}

		
		public Tuple map(Object... values) {return query(values);}
		public Tuple query(Object... values) {
			assert keys.length == values.length : "Keys and values lengths do not match.";
			
			return new ArrayTuple(values);
		}
		
		public Rename duplicate() {return new Rename(operatorData, keys);}
		
		private static String[] getNames(Specializer spec) {
			return spec.get(NAMES).getText().split("\\s+,\\s+");
		}
		
		public static OperatorData complete(OperatorData base, Specializer spec) {
			OperatorData od = new OperatorData(base);
			String[] keys  = getNames(spec);
			FacetData fd = od.getFacet(StencilOperator.MAP_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.FUNCTION, keys);
			od.addFacet(fd);
			
			fd = od.getFacet(StencilOperator.QUERY_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.FUNCTION, keys);
			od.addFacet(fd);
			return od;
		}
		
	}
	
	/**Takes a tuple and extends it by the passed values.
	 * Resulting tuple can only safely be numerically dereferenced.
	 * */
	public static final class ExtendTuple extends AbstractOperator {
		public ExtendTuple(OperatorData opData) {super(opData);}
		
		public Tuple query(Tuple t, Object... more) {
			if (t == null) {t = Tuples.EMPTY_TUPLE;}
			Object[] values = Tuples.toArray(t);
			Object[] target = new Object[values.length + more.length];
			
			System.arraycopy(values, 0, target, 0, values.length);
			System.arraycopy(more, 0, target, values.length, more.length);
			
			return new ArrayTuple(target);
		}
	}
	
	public static final class Get extends AbstractOperator {
		public Get(OperatorData opData) {super(opData);}
		public Tuple query(Tuple t, int i) {return Converter.toTuple(t.get(i));}
	}
	
	/**TODO: Move this to a general converter module.*/
	public static final Tuple toTuple(Object o) {return Converter.toTuple(o);}
	
	/**Takes a tuple, returns a singleton tuple whose value is an array of the original tuples values.
	 * TODO: Move this to a general converter module.
	 * */
	public static final Tuple toArray(Tuple t) {return new ArrayTuple(new Object[]{Tuples.toArray(t)});}


	/**Given a tuple, select part of that tuple.
	 * If end <0, it will select the remainder of the tuple.*/
	public static final Tuple subset(Tuple t, int start, int end) {
		Object[] values = Tuples.toArray(t);
		if (end <0) {end = values.length;}
		Object[] newValues = new Object[end-start];
		System.arraycopy(values, start, newValues, 0, newValues.length);
		return new ArrayTuple(values);
	}
	
	/**Filters the passed tuple according to the regular expression passed as a pattern.
	 * Only elements whose toString matches the pattern will be part of the output.
	 * TODO: Generalize the pattern matcher to something...probably need some higher-order stuff to do that nicely though...
	 */
	public static final Tuple filter(Tuple input, String pattern) {
		ArrayList l = new ArrayList(input.size());
		
		for (int i=0; i<input.size(); i++) {
			Object o = input.get(i);
			if (o.toString().matches(pattern)) {l.add(o);}
		}
		
		return new ArrayTuple(l.toArray());
	}
	
	
	public TupleUtil(ModuleData md) {super(md);}
		
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		if (name.equals("Rename")) {return Rename.complete(od, specializer);}
		else if (od.isComplete()) {return od;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, od);
	}
}
