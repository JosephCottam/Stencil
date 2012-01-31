package stencil.modules;

import java.util.ArrayDeque;
import java.util.Queue;

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
	
	@Operator(name="Queue", defaultFacet="offer")
	@Description("A FIFO queue (with peek).  Not suitable for dynamic binding (no map/query pair).")
	public static class QueueIt extends AbstractOperator {
		private final Queue queue=new ArrayDeque();
		
		public QueueIt(OperatorData opData) {
			super(opData);
		}

		@Facet(memUse="READER", prototype="()")
		public Object peek(Object def) {
			if (queue.size() ==0) {return def;}
			else {return queue.peek();}
		}
		
		@Facet(memUse="WRITER", prototype="(size)", alias={"push","offer"}, counterpart="peek")
		public Object offer(Object value) {
			queue.add(value);
			return queue.size();
		}

		
		@Facet(memUse="READER", prototype="()")
		public Object poll(Object def) {
			if (queue.size() == 0) {return def;}
			else {return queue.poll();}
		}
		
		@Override
		public QueueIt viewpoint() {
			QueueIt other = new QueueIt(operatorData);
			other.queue.addAll(queue);
			return other;
		}
		
		@Override
		public QueueIt duplicate() {return new QueueIt(operatorData);}
	}
	
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
		
		@Facet(memUse="FUNCTION", prototype="(VALUE)", alias={"map","query"})
		public Object query(int inIndex) {return out[inIndex];}
	}
	
	@Operator
	@Description("A mutable list.  Can add, remove and query based on indices. (Can hold null, so remove must be done explicitly).")
	public static final class List extends AbstractOperator.Statefull {
		private TreePVector list = TreePVector.empty();
		public  List(OperatorData opData) {super(opData);}

		@Facet(memUse="WRITER", prototype="(value)", counterpart="query")
		@Description("Add an element to the list at the given index.  If index <0, it is added to the end.  Returns the added value.")
		public Object map(int index, Object value) {
			if (index < 0) {list = list.plus(value);}
			else {list = list.plus(index, value);}
			return value;
		}
		
		@Facet(memUse="WRITER", prototype="(value)", counterpart="query")
		@Description("Removes the element at the given index.  Returns the element removed.")
		public Object remove(int index) {
			Object rv = list.get(index);
			list = list.minus(index);
			return rv;
		}
		
		@Facet(memUse="READER", prototype="(value)")
		@Description("Returns the value at the given index.")
		public Object query(int index) {return list.get(index);}
		
		@Facet(memUse="READER", prototype="(size)")
		public int size() {return list.size();}
	}
	
	@Operator(defaultFacet="put")
	@Description("Dictionary for key/value pairs.  The specializer determines how many of the passed values are the key and how many are the values. Retrieval by key yields a tuple with just the values.")
	public static final class Dict extends AbstractOperator.Statefull {		
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

		@Facet(memUse="WRITER", prototype="()", counterpart="putQuery")
		public Object[] put(Object key, Object... values) {
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
		
		@Description("get, but ignores all but the first argument.  Provide as counterpart to `put`; useage of get directly is preferred.")
		@Facet(memUse="READER", prototype="()")
		public Object[] putQuery(Object key, Object... values) {return get(key);}
		
		@Facet(memUse="READER", prototype="()")
		public Object[] get(Object key) {
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			Object result = dict.get(key);
			if (result == null) {return null;}
			else {return (Object[]) result;}
		}
		
		@Facet(memUse="READER", prototype="(contains)")
		public boolean contains(Object key) {
			return dict.containsKey(key);
		}

		@Override
		public Dict duplicate() {return new Dict(operatorData, caseSensitive, names);}

		@Override
		public Dict viewpoint() {return new Dict(dict, operatorData, caseSensitive, names);}
		
		private static OperatorData getOperatorData(OperatorData basic, Specializer specializer) throws SpecializationException{
			String module = basic.module();
			String name = basic.name();
			String[] fields;
			
			try {fields = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(module, name, specializer, e);}

			OperatorData od = new OperatorData(basic)
				.modFacet(new FacetData("put", "putQuery", MemoryUse.WRITER, fields))
				.modFacet(new FacetData("putQuery", MemoryUse.READER, fields))
				.modFacet(new FacetData("get", MemoryUse.READER, fields));

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
