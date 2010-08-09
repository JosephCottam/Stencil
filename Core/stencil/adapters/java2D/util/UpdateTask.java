package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;

import stencil.parser.tree.StencilTree;
import stencil.parser.tree.util.Path;

public abstract class UpdateTask<T extends StencilTree> implements Callable<Finisher> {
	public static final Finisher NO_WORK = new Finisher() {public void finish() {}};
	
	
	/**The path to the fragment in the source tree**/
	protected final Path path;
	
	/**The base fragment to be updated.*/
	protected final T originalFragment;

	/**The transient viewpoint fragment. 
	 * This should correspond to the original fragment in some meaningful way
	 * (they usually have the same path in their respective trees).
	 */
	protected T viewPointFragment;
	
	public UpdateTask(T original) {
		this.originalFragment = original;
		this.path = new Path(original);
	}
	
	
	/**Does this updater need to run?*/
	public abstract boolean needsUpdate();
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public abstract Finisher update();

	/**Set the core stencil fragment that will be executed in this update.
	 * For example, in a dynamic update, this is a derivative of the dynamically bound rule.*/
	public void setStencilFragment(StencilTree root) {
		viewPointFragment = (T) path.apply(root);
	}
	
	public Path getPath() {return path;}
	
	/**Run this updater if required.
	 * Returns true if the update ran; false otherwise.
	 * */
	public Finisher call() {
		if (needsUpdate()) {
			return update(); 
		}
		return NO_WORK;
	}
}
