package stencil.adapters.java2D.util;

import java.util.concurrent.Callable;


public abstract class UpdateTask implements Callable<Boolean> {
	/**Does this updater need to run?*/
	public abstract boolean needsUpdate();
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public abstract void update();

	/**Run this updater if required.
	 * Returns true if the update ran; false otherwise.
	 * */
	public Boolean call() {
		if (needsUpdate()) {
			update(); 
			return true;
		}
		return false;
	}
}
