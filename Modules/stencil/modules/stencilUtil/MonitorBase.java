/**
 * 
 */
package stencil.modules.stencilUtil;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.MonitorOperator;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.Facet;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public abstract class MonitorBase<T extends MonitorBase> extends AbstractOperator.Statefull<T> implements MonitorOperator<T>, Cloneable {
	protected MonitorBase(OperatorData opData) {super(opData);}
					
	/**Complete the operator data, given the specializer.*/
	protected static OperatorData complete(OperatorData base, Specializer spec) {
		OperatorData od = new OperatorData(base);
		FacetData fd = od.getFacet("map");
		fd = new FacetData(fd.name(), MemoryUse.WRITER, new String[0]);
		od = od.modFacet(fd);
		
		fd = od.getFacet("query");
		fd = new FacetData(fd.name(), MemoryUse.WRITER, new String[0]);
		od = od.modFacet(fd);
		
		return od;
	}

	@Facet(memUse="OPAQUE", prototype="(VALUE)")
	public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}

	@Override
	public abstract SampleSeed getSeed();
}