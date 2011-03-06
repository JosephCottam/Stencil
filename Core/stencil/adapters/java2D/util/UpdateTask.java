package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;

import stencil.interpreter.Viewpoint;
import stencil.interpreter.tree.StateQuery;

public abstract class UpdateTask<T extends Viewpoint<T>> implements Callable<Finisher> {
	public static final Finisher NO_WORK = new Finisher() {public void finish() {}};
	
	/**The base fragment to be updated.*/
	protected final StateQuery stateQuery;

	/**How should this task be identified in string form?*/
	protected final String identifier;

	/**The transient viewpoint fragment. 
	 * This should correspond to the original fragment in some meaningful way
	 * (they usually have the same path in their respective trees).
	 */
	protected T viewpointFragment;
	
	protected final T original;
	
	protected UpdateTask(T original, StateQuery stateQuery, String identifier) {
		this.original = original;
		this.stateQuery = stateQuery;
		this.identifier = this.getClass().getName() + ":" + identifier;
	}
	
	
	/**Does this updater need to run?*/
	public boolean needsUpdate() {return stateQuery.requiresUpdate();}
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public abstract Finisher update();

	/**Set the core stencil fragment that will be executed in this update.
	 * For example, in a dynamic update, this is a derivative of the dynamically bound rule.*/
	public void viewpoint() {
		viewpointFragment = original.viewpoint(); 
	}
	
	/**Run this updater if required.
	 * Return an appropriate finisher to complete work (if required).
	 * */
	public Finisher call() {
		if (needsUpdate()) {return update();} 
		return NO_WORK;
	}
	
	public String toString() {return identifier;}
}
