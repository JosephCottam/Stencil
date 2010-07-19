package stencil.modules;

import static stencil.module.util.OperatorData.TYPE_PROJECT;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.FacetData;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import clojure.lang.*;

public class PersistentData extends BasicModule {
	public static class Dict extends AbstractOperator {		
		public static final String NAMES = "fields";
		public static final String NAME = "Dict";

		private IPersistentMap dict;
		private final String[] names;
		private final boolean caseSensitive;


		protected Dict(OperatorData opData, Specializer spec) {
			this(opData, spec.containsKey("CaseInsensitive"), getNames(spec));
		}

		protected Dict(OperatorData opData,  boolean caseSensitive, String...names) {
			this(PersistentHashMap.create(), opData, caseSensitive, names);
		}

		protected Dict(IPersistentMap dict, OperatorData opData,  boolean caseSensitive, String...names) {
			super(opData);
			this.dict = dict;
			this.caseSensitive = caseSensitive;
			this.names = names;
		}
		
		
		public Object[] put(Object key, Object... values) {
			Object[] objects = new Object[values.length];
			System.arraycopy(values, 0, objects, 0, values.length); //Copy is required for storage.
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			if (objects.length== names.length){ //TODO: Add compile-time call-site verification of argument lengths
				dict = dict.assoc(key, objects);
				stateID++;
			} else {
				throw new IllegalArgumentException("Objects to store list must match the prototype names list length.");
			}
			return objects;
		}
		
		
		public Object[] map(Object key, Object... args) {return query(key, args);}
		public Object[] query(Object key, Object... args) {
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			Object result = dict.valAt(key);
			if (result == null) {return null;}
			else {return (Object[]) result;}	//TODO: Store the tuple itself, will always end up wrapped anyway...
		}

		public Dict duplicate() {return new Dict(operatorData, caseSensitive, names);}
		public Dict snapshot() {return new Dict(dict, operatorData, caseSensitive, names);}
		
		private static OperatorData getOperatorData(OperatorData basic, Specializer specializer) throws SpecializationException{
			String module = basic.getModule();
			String name = basic.getName();
			String[] fields;
			
			try {fields = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(module, name, specializer, e);}

			OperatorData od = new OperatorData(basic);
			od.addFacet(new FacetData(MAP_FACET, TYPE_PROJECT, false, fields));
			od.addFacet(new FacetData(QUERY_FACET, TYPE_PROJECT, false, fields));
			return od;
		}

		private static String[] getNames(Specializer spec) {return spec.get(NAMES).getText().split("\\s*,\\s*");}
		
	}
	
	public PersistentData(ModuleData md) {super(md);}
	
	public OperatorData getOperatorData(String name, Specializer specializer)
	throws SpecializationException {

		if(name.equals(Dict.NAME)) {
			return Dict.getOperatorData(getModuleData().getOperator(name), specializer);
		}

		return super.getOperatorData(name, specializer);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		
		try {
			if (name.equals(Dict.NAME)) {
				return new Dict(getOperatorData(name, specializer), specializer);
			}
			return super.instance(name, specializer);
		} catch (Exception e) {throw new Error("Error retriving " + name + " method.",e);}
	}
}
