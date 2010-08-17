package stencil.module.util;

public final class Utilities {
	private Utilities() {}
	
	/**Indicate that all facets in the operator data are not functions.*/
 	public static OperatorData noFunctions(OperatorData od) {
 		OperatorData nod = new OperatorData(od);
 		for (FacetData fd: nod.getFacets()) {fd.setFunction(false);}
 		return nod;
 	}
}
