package stencil.module.util;

import java.util.*;

import stencil.interpreter.tree.Specializer;
import stencil.module.MetadataHoleException;
import stencil.module.operator.UnknownFacetException;

public final class OperatorData {
	private final Specializer spec;		 /**Default Specializer*/
	private final String name;			 /**Operator name*/
	
	//TODO: Make this a real class or method (not just a string pointing at one)
	private final String target;			 /**Method or class to use for the given operator (containing class is determined by the module).*/
	private final String module;		     /**Module this operator belongs to.*/
	private final Map<String, FacetData> facets;
	private final String defaultFacet;

	
	private final String description;   	 /**Text description of the operator.*/

	

	public OperatorData(String module, String name, Specializer specializer, String target, String defaultFacet, Collection<FacetData> facets) {
		this(module, name, specializer, target, defaultFacet, prepFacets(facets));
	}
	
	public OperatorData(String module, String name, Specializer specializer, String target, String defaultFacet, Map<String, FacetData> facets) {
		
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
		
		validate();
	}
	
	public OperatorData(OperatorData source) {
		spec = source.spec;
		name = source.name;
		target = source.target;
		module = source.module;
		description = source.description;
		facets = new HashMap(source.facets);
		defaultFacet = source.defaultFacet;
		validate();
	}

	private void validate() {
		if (defaultFacet == null || defaultFacet.trim().equals("")) { 
			throw new MetadataHoleException(module, name, "Default facet required but not provided.");
		}
		
		if (!facets.containsKey(defaultFacet)) {
			throw new MetadataHoleException(module, name, String.format("Default facet '%1$s' not found in facets lists.", defaultFacet));		
		}
	}
	
	/**Return facet data for the default facet.*/
	public FacetData defaultFacet() {return getFacet(defaultFacet);}	
	public Specializer defaultSpecializer() {return spec;}
	
	public FacetData getFacet(String name) {
		if (facets.containsKey(name)) {return facets.get(name);}
		throw new UnknownFacetException(this.name, name, facetNames());
	}
	
	public boolean hasFacet(String name) {return facetNames().contains(name);}
	public List<String> facetNames() {return new ArrayList(facets.keySet());}
	public List<FacetData> facets() {return new ArrayList(facets.values());}

	public String module() {return module;}
	public String name() {return name;}
	public String target() {return target;}
	public String description() {return description;}
	
	
	public OperatorData module(String module) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets);
	}
	
	public OperatorData name(String name) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets);
	}
		
	public OperatorData defaultSpecializer(Specializer spec) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets);
	}
	
	public OperatorData target(String target) {
		return new OperatorData(module, name, spec, target, defaultFacet, facets);
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
		return new OperatorData(module, name, spec, target, defaultFacet, facets);
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
