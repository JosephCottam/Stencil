package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;

public abstract class UpdateTask implements Callable<Finisher> {
	public static final Finisher NO_WORK = new Finisher() {public void finish() {}};
	
	
	/**Does this updater need to run?*/
	public abstract boolean needsUpdate();
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public abstract Finisher update();

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
