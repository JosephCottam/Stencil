package stencil.module.operator.wrappers;

import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import static stencil.module.operator.StencilOperator.STATE_ID_FACET;

public final class Utilities {
	private Utilities() {}
	
	/**Indicate that all facets in the operator data are not functions.*/
 	public static OperatorData noFunctions(OperatorData od, boolean addStateID) {
 		OperatorData nod = new OperatorData(od);
 		for (FacetData fd: nod.getFacets()) {
 			switch (fd.getMemUse()) {
 				case FUNCTION: fd.setMemUse(MemoryUse.READER); break;
 				case READER: 
 					if (!fd.getName().equals(STATE_ID_FACET)) {
 						fd.setMemUse(MemoryUse.READER);
 					}
 					break;
 				default: break; 	//no changes for WRITER and UNSPECIFIED
 			} 			
 		}
 		
 		if (addStateID) {return addStateID(nod);}
 		else {return nod;}
 	}
 	
 	public static OperatorData addStateID(OperatorData od) {
 		OperatorData nod = new OperatorData(od);
 		FacetData fd = new FacetData(STATE_ID_FACET, MemoryUse.READER, "VALUE");
 		nod.addFacet(fd);
 		return nod;
 	}
}
