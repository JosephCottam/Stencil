package stencil.modules.stencilUtil.split;

import java.util.HashMap;
import java.util.Map;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.types.Converter;
import static stencil.module.operator.StencilOperator.STATE_ID_FACET;

public class UnorderedHelper implements OperatorCache {
	private final SplitDescriptor split;
	private int stateID=Integer.MIN_VALUE;
	private final Map<Object, StencilOperator> operators = new HashMap();
	
	public UnorderedHelper(SplitDescriptor split) {
		this.split = split;
	}
	
	
	@Override
	public Object doSplit(StencilOperator op, String facet, Object[] args) {
		Object key = Split.getKey(split.size(), args);

		op = getOp(op, key);
		
		Object[] newArgs = Split.getArgs(split.size(), args);
		Invokeable inv = op.getFacet(facet);		
		Invokeable stateIDFacet = op.getFacet(STATE_ID_FACET);
		
		int oldID = Converter.toInteger(stateIDFacet.invoke(EMPTY_ARGS));
		Object rv = inv.invoke(newArgs);
		int newID = Converter.toInteger(stateIDFacet.invoke(EMPTY_ARGS));
		if (oldID != newID) {stateID++;}
		
		return rv;
	}
	

	@Override
	public Object querySplit(StencilOperator op, String facet, Object[] args) {
		Object key = Split.getKey(split.size(), args);
		Object[] newArgs = Split.getArgs(split.size(), args);

		StencilOperator useOp;
		if (operators.containsKey(key)) {useOp = operators.get(key);}
		else {useOp = op.duplicate();}
		facet = useOp.getOperatorData().getFacet(facet).counterpart();		//Switch to counterpart facet
		Invokeable inv = op.getFacet(facet);
		return inv.invoke(newArgs);
	}
			
	/**Check if the key has been seen before.
	 * Return the operator used on that occasion if it 
	 * has been, otherwise create a new base-operator duplicate.
	 * 
	 * @param key How to determine which operator replicate to use
	 * @return Operator to invoke for the given arguments
	 */
	//TODO: Remove the assumption that the passed operator is essentially a constant not referred to anywhere else
	private StencilOperator getOp(StencilOperator operator, Object key) {
		if (!operators.containsKey(key)) {
			operators.put(key, operator.duplicate());
			stateID++;
		}
		return operators.get(key);
	}

	@Override
	public int stateID() {return stateID;}
	
	@Override
	public UnorderedHelper viewpoint() {
		UnorderedHelper newCache = new UnorderedHelper(split);
		for (Object key: operators.keySet()) {
			StencilOperator op = operators.get(key);
			StencilOperator view = op.viewpoint();
			newCache.operators.put(key, view);
		}
		return newCache;
	}

}
