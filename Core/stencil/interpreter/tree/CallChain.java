package stencil.interpreter.tree;

import stencil.interpreter.NoOutput;
import stencil.interpreter.Viewpoint;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.instances.MapMergeTuple;
import stencil.types.Converter;

public class CallChain implements Viewpoint<CallChain> {
	private final Function[] targets;
	private final Object[] pack; 	//TODO: Convert pack to a function call on freeze
	
	public CallChain(Function[] targets, Object[] pack) {
		this.targets = targets;
		this.pack = pack;
	}
	
	public int depth() {return targets.length;}
	
	public CallChain viewpoint() {
		final Function[] vp = new Function[targets.length];
		for (int i=0; i< vp.length; i++) {
			vp[i] = targets[i].viewpoint();
		}
		return new CallChain(vp, pack);
	}
	
	/**Execute the call chain, all the way through the pack.
	 * 	 
	 * Short-circuiting occurs when the method invoked returns null.  If this is the case,
	 * a null is immediately returned from the function chain.  This means that no further
	 * actions will be taken in the chain.  If a 'null' is a valid return value from a given
	 * function, then you must wrap it in a tuple and give it an appropriate key.
	 *
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public Tuple apply(Environment rootEnv) throws Exception {		
		Environment[] envs = new Environment[]{rootEnv};
		
		for (int target=0; target< targets.length; target++) {
			final Function func = targets[target];
			Tuple[] results = new Tuple[envs.length];
			for (int i=0; i< results.length; i++) {
				try {results[i] = func.apply(envs[i]);}
				catch (NoOutput.Signal s) {results[i] = NoOutput.TUPLE;}
			}
			
			//Depending on the pass operator, there may be 1 or many results
			switch (func.passType()) {
				case DIRECT_YIELD :
					for (int i=0; i<results.length; i++) {
						envs[i].extend(results[i]);
					}
					break;
				case MAP :
					Environment[] newEnvs = new Environment[results[0].size()];  //HACK: The 0 here is because only one map is allowed to be used at a time...nesting them will require this to be in a loop AT LEAST
					for (int i=0; i< newEnvs.length; i++) {
						newEnvs[i] = envs[0].clone();								//make new env
						newEnvs[i].extend(Converter.toTuple(results[0].get(i)));	//Add part of the most recent result to new env
					}
					envs = newEnvs;
					break;
				case FOLD :
					Tuple folded = new MapMergeTuple(results);
					envs = new Environment[]{envs[0]};
					envs[0].extend(folded);
					break;
			}
		}

		
		Tuple[] results = new Tuple[envs.length];
		for (int i=0; i< results.length; i++) {
			try {
				Object[] values = TupleRef.resolveAll(pack, envs[i]);
				results[i] = new ArrayTuple(values);
			} catch (NoOutput.Signal s) {results[i] = NoOutput.TUPLE;}
		}
		
		if (results.length >1) {return new MapMergeTuple(results);}
		else if (results.length ==1) {return results[0];}
		else {return null;}
	}}
