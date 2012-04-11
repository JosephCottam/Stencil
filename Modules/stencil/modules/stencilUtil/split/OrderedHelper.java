package stencil.modules.stencilUtil.split;

import static stencil.module.operator.StencilOperator.STATE_ID_FACET;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.types.Converter;

public class OrderedHelper implements OperatorCache {
	private final SplitDescriptor split;

	private int stateID=Integer.MIN_VALUE;
	private Object oldKey;
	private StencilOperator cachedOp;
	
	public OrderedHelper(SplitDescriptor split) {
		this.split = split;
	}
		
	@Override
	public Object doSplit(StencilOperator operator, String facet, Object[] args) {
		Object key = Split.getKey(split.size(), args);
		Object[] newArgs = Split.getArgs(split.size(), args);

		if (!key.equals(oldKey) || operator.getName() != cachedOp.getName()) {
			try {cachedOp = operator.duplicate();}
			catch (Exception e) {throw new Error("Error creating new split operator instance.", e);}
			oldKey = key;
			stateID++;
		}
		
		Invokeable inv = cachedOp.getFacet(facet);
		Invokeable stateIDFacet = cachedOp.getFacet(STATE_ID_FACET);

		int oldID = Converter.toInteger(stateIDFacet.invoke(EMPTY_ARGS));
		Object rv = inv.invoke(newArgs);
		int newID = Converter.toInteger(stateIDFacet.invoke(EMPTY_ARGS));
		if (oldID != newID) {stateID++;}
		
		return rv;
	}
	
	@Override
	public Object querySplit(StencilOperator operator, String facet, Object[] args) {
		Object key = Split.getKey(split.size(), args);
		Object[] newArgs = Split.getArgs(split.size(), args);
		
		StencilOperator useOp = cachedOp;
		if (!key.equals(oldKey) || operator.getName() != cachedOp.getName()) {
			try {useOp = operator.duplicate();}
			catch (Exception e) {throw new Error("Error creating new split operator instance.", e);}
		}
		facet = useOp.getOperatorData().getFacet(facet).counterpart();		//Switch to counterpart facet
		
		Invokeable inv = useOp.getFacet(facet);
		return inv.invoke(newArgs);

	}

	
	@Override
	public OperatorCache viewpoint() {
		OrderedHelper newCache = new OrderedHelper(split);
		newCache.oldKey = this.oldKey;
		newCache.cachedOp = this.cachedOp.viewpoint();
		newCache.stateID = this.stateID;
		return newCache;
	}

	@Override
	public int stateID() {return stateID;}


}
