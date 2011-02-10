package stencil.modules;

import stencil.module.SpecializationException;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.FacetData;
import stencil.module.util.ann.*;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.interpreter.tree.Specializer;

import org.pcollections.*;

@Module
public class PersistentData extends BasicModule {
	
	/**Given a static list of values, returns the value based on the input.*/
	@Operator()
	@Description("A list that can be de-referenced by the index in the list.  The list is given in the specializer under the key `vals'.")
	public static class StaticList extends AbstractOperator {
		private static final String VALUES_KEY = "vals";
		private final Object[] out;
		
		public StaticList(OperatorData opData, Specializer spec) {
			super(opData);
									
			String literalMask = (String) spec.get(VALUES_KEY);
			String[] parts = literalMask.split("\\s*,\\s*");

			out = new Object[parts.length];
			for (int i =0; i< out.length; i++) {
				out[i] = parts[i];
			}
		}
		
		@Facet(memUse="FUNCTION", prototype="(VALUE)")
		public Object query(int inIndex) {return out[inIndex];}
	}
	
	@Operator()
	@Description("Dictionary for key/value pairs.  The specializer determines how many of the passed values are the key and how many are the values. Retrieval by key yields a tuple with just the values.")
	public static class Dict extends AbstractOperator.Statefull {		
		public static final String NAMES = "fields";
		public static final String NAME = "Dict";

		private PMap<Object, Object[]> dict;
		private final String[] names;
		private final boolean caseSensitive;


		public Dict(OperatorData opData, Specializer spec) {
			this(opData, spec.containsKey("CaseInsensitive"), getNames(spec));
		}

		protected Dict(OperatorData opData,  boolean caseSensitive, String...names) {
			this(HashTreePMap.empty(), opData, caseSensitive, names);
		}

		protected Dict(PMap dict, OperatorData opData,  boolean caseSensitive, String...names) {
			super(opData);
			this.dict = dict;
			this.caseSensitive = caseSensitive;
			this.names = names;
		}

		@Facet(memUse="WRITER", prototype="()")
		public Object[] map(Object key, Object... values) {
			Object[] objects = new Object[values.length];
			System.arraycopy(values, 0, objects, 0, values.length); //Copy is required for storage.
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			if (objects.length== names.length){ //TODO: Add compile-time call-site verification of argument lengths
				dict = dict.plus(key, objects);
				stateID++;
			} else {
				throw new IllegalArgumentException("Objects to store list must match the prototype names list length.");
			}
			return objects;
		}
		
		@Facet(memUse="READER", prototype="()")
		public Object[] query(Object key, Object... values) {
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			Object result = dict.get(key);
			if (result == null) {return null;}
			else {return (Object[]) result;}
		}

		public Dict duplicate() {return new Dict(operatorData, caseSensitive, names);}
		public Dict viewPoint() {return new Dict(dict, operatorData, caseSensitive, names);}
		
		private static OperatorData getOperatorData(OperatorData basic, Specializer specializer) throws SpecializationException{
			String module = basic.getModule();
			String name = basic.getName();
			String[] fields;
			
			try {fields = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(module, name, specializer, e);}

			OperatorData od = new OperatorData(basic);
			od.addFacet(new FacetData(MAP_FACET, MemoryUse.WRITER, fields));
			od.addFacet(new FacetData(QUERY_FACET, MemoryUse.READER, fields));
			return od;
		}

		private static String[] getNames(Specializer spec) {return ((String) spec.get(NAMES)).split("\\s*,\\s*");}
		
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {
		if(name.equals(Dict.NAME)) {
			return Dict.getOperatorData(getModuleData().getOperator(name), specializer);
		}
		return super.getOperatorData(name, specializer);
	}
}
