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

	
	/**Has any of the contained stateID queries changed?*/
	public boolean requiresUpdate() {
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
		cachedIDs = nowIDs;
		return !matches;
	}
	
	public StateQuery viewpoint() {
		final Invokeable[] vps = new Invokeable[queries.length];
		for (int i=0;i<queries.length;i++) {vps[i] = queries[i].viewpoint();}
		StateQuery query = new StateQuery(queries);
		query.cachedIDs = cachedIDs;//Safe because the update method creates a new copy each time it runs
		return query;
	}
}
