package stencil.interpreter.tree;

import java.util.Arrays;

import stencil.interpreter.Viewpoint;
import stencil.module.operator.util.Invokeable;
import stencil.types.Converter;

//TODO: Split into two types: one for copositeStateID and one for regular duty
public class StateQuery implements Viewpoint<StateQuery> {
	private static final Object[] EMPTY_ARGS = new Object[0];
	private int[] cachedIDs;
	private final Invokeable[] queries;
	
	public StateQuery(Invokeable[] queries) {this.queries = Arrays.copyOf(queries, queries.length);}

	/**Get a StateID that is the composite of all current stateIDs.
	 * (This will calculate new stateIDs, but not store them in the cache.)
	 */
	public int compositeStateID() {
		final Integer[] nowIDs = new Integer[queries.length];
		for (int i=0; i< nowIDs.length; i++) {
			Invokeable query = queries[i];
			nowIDs[i] = Converter.toInteger(query.invoke(EMPTY_ARGS));
		}
		return Arrays.deepHashCode(nowIDs);
	}

	
	/**Has any of the contained stateID queries changed?
	 * Returns null if there have been no changes, otherwise it will return an array of the current stateIDs.
	 * */
	public synchronized int[] requiresUpdate() {
		final int[] nowIDs = new int[queries.length];
		if (cachedIDs == null) {
			cachedIDs = new int[queries.length];
			Arrays.fill(cachedIDs, Integer.MAX_VALUE-1);
		}
		
		for (int i=0; i< nowIDs.length; i++) {
			Invokeable inv = queries[i];
			nowIDs[i] = Converter.toInteger(inv.invoke(EMPTY_ARGS));
		}		
				
		boolean matches = true;	//Do the two ID arrays match?
		for (int i=0; matches && i < nowIDs.length; i++) {
			matches = matches && (nowIDs[i] == cachedIDs[i]);
		}
		if (!matches) {return nowIDs;}
		else {return null;}
	}
	
	
	/**Set the stateID collection that constitute the most recent update.
	 * A null will force a run next time. 
	 * @param ids
	 */
	public synchronized void setUpdatePoint(int[] ids) {cachedIDs = ids;}
	
	public synchronized StateQuery viewpoint() {
		final Invokeable[] vps = new Invokeable[queries.length];
		for (int i=0;i<queries.length;i++) {vps[i] = queries[i].viewpoint();}
		StateQuery query = new StateQuery(vps);
		query.cachedIDs = cachedIDs;//Copy is safe because the update method creates a new copy each time it runs
		return query;
	}
}
