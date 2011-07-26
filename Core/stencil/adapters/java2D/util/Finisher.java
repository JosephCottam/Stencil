package stencil.adapters.java2D.util;

/**Update tasks typically have two parts: one can be 
 *   safely done in parallel with other updates and one that must 
 *   be serialized with those other updates.  For example, dynamic
 *   updates can be computed in parallel with other  dynamic updates
 *   BUT the updates must be applied to the layer only after other updates
 *   have been done. 
 *   
 *   Updates run the parallel part through an executor. 
 *   Then finishers execute after all updates have completed.  
 *   Finishers may be run in parallel with each other.
 */
interface Finisher {
	/**Perform the work that could not be done in parallel with other updates.*/
	public void finish();
}