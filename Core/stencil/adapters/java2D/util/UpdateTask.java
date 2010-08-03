package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;

import stencil.parser.tree.StencilTree;
import stencil.parser.tree.util.Path;

public abstract class UpdateTask<T> implements Callable<Finisher> {
	public static final Finisher NO_WORK = new Finisher() {public void finish() {}};
	
	
	/**The path to the fragment in the source tree**/
	protected final Path path;
	
	/**The fragment to be updated.  This may change over time
	 * (e.g. a fragment from a different tree, as occurs with analysis viewpoint changes),
	 * but it must  always correspond to the path.  
	 */
	protected T fragment;
	
	public UpdateTask(Path path) {this.path = path;}
	
	
	/**Does this updater need to run?*/
	public abstract boolean needsUpdate();
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public abstract Finisher update();

	/**Set the core stencil fragment that will be executed in this update.
	 * For example, in a dynamic update, this is a derivative of the dynamically bound rule.*/
	public void setStencilFragment(StencilTree root) {
		fragment = (T) path.apply(root);
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
