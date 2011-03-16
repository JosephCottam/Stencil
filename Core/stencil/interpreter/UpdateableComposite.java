package stencil.interpreter;

import stencil.interpreter.tree.StateQuery;

/**An entity that (1) can change its state and (2) potentially contains multiple sub-entities.
 * Such entities need to be able to (1) present a consistent view of their composite state (per viewpoint)
 * and (2) report changes to that state over time (thus the state query requirement).
 * 
 */
public interface UpdateableComposite<C extends UpdateableComposite> extends Viewpoint<C>{
	/**Return a state query query for this composite.**/
	public StateQuery stateQuery();
}
