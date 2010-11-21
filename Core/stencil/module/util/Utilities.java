package stencil.module.util;

import stencil.module.operator.StencilOperator;
import stencil.module.util.FacetData.MemoryUse;

public final class Utilities {
	private Utilities() {}
	
	/**Indicate that all facets in the operator data are not functions.*/
 	public static OperatorData noFunctions(OperatorData od) {
 		OperatorData nod = new OperatorData(od);
 		for (FacetData fd: nod.getFacets()) {
 			switch (fd.getMemUse()) {
 				case FUNCTION: fd.setMemUse(MemoryUse.READER); break;
 				case READER: 
 					if (!fd.getName().equals(StencilOperator.STATE_ID_FACET)) {
 						fd.setMemUse(MemoryUse.READER);
 					}
 					break;
 				default: break; 	//no changes for WRITER and UNSPECIFIED
 			} 			
 		}
 		return nod;
 	}
}
