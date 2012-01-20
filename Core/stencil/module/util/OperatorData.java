package stencil.module.util;

import java.util.*;

import stencil.interpreter.tree.Specializer;
import stencil.module.MetadataHoleException;
import stencil.module.operator.UnknownFacetException;

public final class OperatorData {
	public static final String HIGHER_ORDER_TAG = "HIGHER_ORDER";
	
	private final Specializer spec;		 /**Default Specializer*/
	private final String name;			 /**Operator name*/
	
	//TODO: Make this a real class or method (not just a string pointing at one)
	private final String target;			 /**Method or class to use for the given operator (containing class is determined by the module).*/
	private final String module;		     /**Module this operator belongs to.*/
	private final Set<String> tags; /**Extra flags used in further optimization**/
	private final Map<String, FacetData> facets;
	private final String defaultFacet;

	
	private final String description;   	 /**Text description of the operator.*/

	

	public OperatorData(String module, String name, Specializer specializer, String target, String defaultFacet, Collection<FacetData> facets, Collection<String> tags) {
		this(module, name, specializer, target, defaultFacet, prepFacets(facets), prepTags(tags));
	}
	
	public OperatorData(String module, String name, Specializer specializer, String target, String defaultFacet, Map<String, FacetData> facets, Set<String> tags) {
		
		//If there is no default facet...
		/// AND there is only one facet grab that facet
		//  OR if there is facet named "map", grab that one instead
		if (defaultFacet == null || defaultFacet.trim().equals("")) {
			if(facets.size() == 1) {defaultFacet = facets.values().iterator().next().name();}  //Get the first facet from the map...
			else if (facets.containsKey("map")) {defaultFacet = "map";}
			else {throw new MetadataHoleException(module, name, "Must provide defaultFacet");}
		}
		
		this.module = module;
		this.name = name;
		this.spec = specializer;
		this.target = target;
		this.defaultFacet = defaultFacet;
		this.description = null;
		this.facets = new HashMap(facets);
		this.tags = new HashSet(tags);
		
		validate();
	}
	
	public OperatorData(OperatorData source) {
		spec = source.spec;
		name = source.name;
		target = source.target;
		module = source.module;
		description = source.description;
		tags = new HashSet(source.tags);
		facets = new HashMap(source.facets);
		defaultFacet = source.defaultFacet;
		validate();
	}

	private void validate() {
		assert defaultFacet != null && defaultFacet.trim() != "" : String.format("Default facet not provided for %1$s.%2$s", module, name);
		assert facets.containsKey(defaultFacet) : String.format("Default facet not found in facets lists for %1$s.%2$s", module, name);		
	}
	
	/**Return facet data for the default facet.*/
	public FacetData defaultFacet() {return getFacet(defaultFacet);}	
	public Specializer defaultSpecializer() {return spec;}
	public boolean hasTag(String name) {return tags.contains(name);}
	public void addTag(String name) {tags.add(name);}
	public void removeTag(String name) {tags.remove(name);}
	
	public FacetData getFacet(String name) {
		if (facets.containsKey(name)) {return facets.get(name);}
		throw new UnknownFacetException(this.name, name, getFacetNames());
	}
	
	public boolean hasFacet(String name) {return getFacetNames().contains(name);}
	public List<String> getFacetNames() {return new ArrayList(facets.keySet());}
	public List<FacetData> getFacets() {return new ArrayList(facets.values());}

	public String module() {return module;}
	public String name() {return name;}
	public String target() {return target;}
	public String description() {return description;}
	
	
	public OperatorData module(String module) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets, tags);
	}
	
	public OperatorData name(String name) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets, tags);
	}
		
	public OperatorData defaultSpecializer(Specializer spec) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets, tags);
	}
	
	public OperatorData target(String target) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets, tags);
	}
	
	
	public OperatorData modFacets(Collection<FacetData> facets) {
		OperatorData od = this;
		for (FacetData fd: facets) {od = od.modFacet(fd);}
		return od;
	}
	
	public OperatorData modFacet(FacetData facet) {
		Map<String, FacetData> facets = new HashMap();
		facets.putAll(this.facets);
		facets.put(facet.name(), facet);
		return new OperatorData(module, name, spec, target, defaultFacet, facets, tags);
	}

	
	public static Set<String> prepTags(Collection<String> tags) {
		Set<String> tgs = new HashSet();
		tgs.addAll(tags);
		return tgs;
	}

	public static Map<String, FacetData> prepFacets(Collection<FacetData> facets) {
		Map<String, FacetData> fds = new HashMap();
		for (FacetData fd: facets) {fds.put(fd.name(), fd);}
		return fds;
	}

		
	public boolean isComplete() {
		return target != null
				&& defaultFacet != null
				&& facets.containsKey(defaultFacet)
				&& spec != null 
				&& name != null 
				&& module != null;
	}

}
