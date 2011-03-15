/**
 * 
 */
package stencil.modules.stencilUtil;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.SeedOperator;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.Facet;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public abstract class SeedBase<T extends SeedBase> extends AbstractOperator.Statefull<T> implements SeedOperator<T>, Cloneable {
	protected SeedBase(OperatorData opData) {super(opData);}
					
	/**Complete the operator data, given the specializer.*/
	protected static OperatorData complete(OperatorData base, Specializer spec) {
		OperatorData od = new OperatorData(base);
		FacetData fd = od.getFacet(StencilOperator.MAP_FACET);
		fd = new FacetData(fd.getName(), MemoryUse.WRITER, new String[0]);
		od.addFacet(fd);
		
		fd = od.getFacet(StencilOperator.QUERY_FACET);
		fd = new FacetData(fd.getName(), MemoryUse.WRITER, new String[0]);
		od.addFacet(fd);
		
		return od;
	}

	public abstract Tuple map(Object... args);
	
	@Facet(memUse="OPAQUE", prototype="(VALUE)")
	public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}

	@Override
	public SampleSeed getSeed() {throw new RuntimeException("No implemented.");}
}