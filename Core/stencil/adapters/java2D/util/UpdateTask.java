package stencil.adapters.java2D.util;

public interface UpdateTask {
	/**Does this updater need to run?*/
	public boolean needsUpdate();
	
	/**Run this updater if required.*/
	public void conservativeUpdate();
	
	/**Run this updater, regardless of it needs to be run or not.*/
	public void update();
}
