package stencil.modules;

import java.util.ArrayList;

import stencil.module.MetadataHoleException;
import stencil.module.SpecializationException;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.*;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MultiResultTuple;
import stencil.tuple.instances.Singleton;
import stencil.types.Converter;


@Description("Utilities to manipulate the tuple data representation; tuples so manipulated can generally only be de-referenced numerically.")
@Module
public class TupleUtil extends BasicModule {
	/**How many elements does the given tuple have?
	 * This does a single-level descent into all tuples passed
	 * as arguments. If no tuple is passed, the result is 
	 * equal to the number of arguments.
	 * */
    @Description("How many fields in this tuple?")
    @Operator
    @Facet(memUse="FUNCTION", prototype="(Size)", alias={"map", "query"})
	public static final int TupleSize(Tuple t) {return t.size();}

    @Description("Tuple -> Tuple; The original tuple values are now each tuples in the new tuple.")
    @Operator
    @Facet(memUse="FUNCTION", prototype="()", alias={"map", "query"})
	public static final Tuple[] EnfoldValues(Tuple t) {
		Tuple[] ts = new Tuple[t.size()];
		for (int i=0; i< t.size(); i++) {
			ts[i] = Converter.toTuple(t.get(i));
		}
		return ts;
	}

	/**Take a tuple of tuples.  Retrieve the n-th field from
	 * each of those tuples to form a new tuple.
	 * 
	 * TODO: Investigate using map and get instead of Select
	 * TODO: Convert to just a simple operator (no memory) when conversions are cheaper
	 */
    @Description("Given a tuple of tuples, takes the n-th field from each sub-tuple.")
    @Operator()
	public static final class Select extends AbstractOperator {
		private final int field;
		
		public Select(OperatorData opData, Specializer spec) {
			super(opData);
			field = Converter.toInteger(spec.get("field"));
		}
		
		@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
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
    
    @Description("Create a multi-result tuple from the passed values (each value becomes a tuple); roughly equivalent to mapping the echo operator")
    @Operator()
    public static final class MultiResult extends AbstractOperator {
		public MultiResult(OperatorData opData) {super(opData);}

	    @Facet(memUse="FUNCTION", prototype="()", alias={"array"})
		public MultiResultTuple array(Object[] values) {
	    	Tuple[] ts = new Tuple[values.length];
	    	for (int i=0; i<values.length;i++) {
	    		ts[i] = Singleton.from(values[i]);
	    	}
	    	return new MultiResultTuple(ts);
	    }
		
	    @Facet(memUse="FUNCTION", prototype="()", alias={"map", "query"})
		public MultiResultTuple query(Object... values) {
	    	Tuple[] ts = new Tuple[values.length];
	    	for (int i=0; i<values.length;i++) {
	    		ts[i] = Converter.toTuple(values[i]);
	    	}
	    	return new MultiResultTuple(ts);
		}
    }
    
    
    
    @Description("Object* -> Tuple: Given objects, will create a tuple out of them.  Also does type conversion (default is for all Objects).")
    @Operator(spec="[CONVERT: \"java.lang.Object\"]")
    public static final class ToTuple extends AbstractOperator {
    	private final Class[] converts;
    	
		public ToTuple(OperatorData opData, Specializer spec) {
			super(opData);
			String[] types = Converter.toString(spec.get("CONVERT")).split("\\s+,\\s+");
			converts = new Class[types.length];
			
			for (int i=0; i< types.length; i++) {
				try {converts[i] = Class.forName(types[i]);}
				catch (Exception e) {throw new RuntimeException("Error loading class: " + types[i],e);}
			}
		}
		
	    @Facet(memUse="FUNCTION", prototype="()", alias={"map", "query"})
		public Tuple query(Object... values) {
	    	Object[] vals = new Object[values.length];
	    	for (int i=0; i< values.length;i++) {
	    		int classIdx = (i >= converts.length ? converts.length-1 : i);
	    		vals[i] = Converter.convert(values[i], converts[classIdx]);
	    	}	    	
	    	return new ArrayTuple(vals);
		}
    	
    	
    }
	
	
	/**Rename the components of a tuple with new names; 
	 * like an echo but with variable names updated in the prototype.
	 */
    @Description("Construct a tuple with a prototype from the names (passed in the specializer under the `names' key) and the values (passed as arguments).")
    @Operator
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


		@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
		public Tuple query(Object... values) {
			assert keys.length == values.length : "Keys and values lengths do not match.";
			
			return new ArrayTuple(values);
		}
		
		private static String[] getNames(Specializer spec) {
			return ((String) spec.get(NAMES)).split("\\s+,\\s+");
		}
		
		public static OperatorData complete(OperatorData base, Specializer spec) {
			OperatorData od = new OperatorData(base);
			String[] keys  = getNames(spec);
			FacetData fd = od.getFacet("map");
			fd = new FacetData(fd.name(), MemoryUse.FUNCTION, keys);
			od = od.modFacet(fd);
			
			fd = od.getFacet("query");
			fd = new FacetData(fd.name(), MemoryUse.FUNCTION, keys);
			od = od.modFacet(fd);
			return od;
		}
		
	}
	
	/**Takes a tuple and extends it by the passed values.
	 * Resulting tuple can only safely be numerically dereferenced.
	 * */
    @Description("Tuple X [value] -> New tuple extended by value list")
    @Operator
	public static final class ExtendTuple extends AbstractOperator {
		public ExtendTuple(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
		public Tuple query(Tuple t, Object... more) {
			if (t == null) {t = Tuples.EMPTY_TUPLE;}
			Object[] values = Tuples.toArray(t);
			Object[] target = new Object[values.length + more.length];
			
			System.arraycopy(values, 0, target, 0, values.length);
			System.arraycopy(more, 0, target, values.length, more.length);
			
			return new ArrayTuple(target);
		}
	}
	
    @Description("Tuple X int -> Value at position indicated by the int")
    @Operator
    @Facet(memUse="FUNCTION", prototype="(value)", alias={"map","query"})
	public static final Tuple get(Tuple t, int i) {return Converter.toTuple(t.get(i));}
		
	/**Takes a tuple, returns a singleton tuple whose value is an array of the original tuples values.
	 * TODO: Move this to a general converter module.
	 * */
    @Description("Convert a tuple to a singleton tuple containing an array of the original values")
    @Operator
    @Facet(memUse="FUNCTION", prototype="(value)", alias={"map","query"})
	public static final Tuple toArray(Tuple t) {return new ArrayTuple(new Object[]{Tuples.toArray(t)});}

    @Description("Convert a tuple to a singleton tuple containing an array of the original values")
    @Operator
    @Facet(memUse="FUNCTION", prototype="(values)", alias={"map","query"})
	public static final Tuple values(Tuple t) {return new ArrayTuple(new Object[]{Tuples.toArray(t)});}

    
    
	/**Given a tuple, select part of that tuple.
	 * If end <0, it will select the remainder of the tuple.*/
    @Description("Get a contiguous subset of the passed tuple (includes first index, does not include second; -1 as second index indicates all remaining values).")
    @Operator
    @Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	public static final Tuple subset(Tuple t, int start, int end) {
		Object[] values = Tuples.toArray(t);
		if (end <0) {end = values.length;}
		Object[] newValues = new Object[end-start];
		System.arraycopy(values, start, newValues, 0, newValues.length);
		return new ArrayTuple(values);
	}
	
	/**Filters the passed tuple according to the regular expression passed as a pattern.
	 * Only elements whose toString matches the pattern will be part of the output.
	 * TODO: Generalize the pattern matcher to something...probably need to take functions as arguments to do that nicely though...
	 */
    @Description("Tuple X Regular Expression -> Tuple where only values that match the regular expression are retained.")
    @Operator
    @Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	public static final Tuple filter(Tuple input, String pattern) {
		ArrayList l = new ArrayList(input.size());
		
		for (int i=0; i<input.size(); i++) {
			Object o = input.get(i);
			if (o.toString().matches(pattern)) {l.add(o);}
		}
		
		return new ArrayTuple(l.toArray());
	}
			
	@Override
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		if (name.equals("Rename")) {return Rename.complete(od, specializer);}
		else if (od.isComplete()) {return od;}
		throw new MetadataHoleException(od, specializer);
	}
}
