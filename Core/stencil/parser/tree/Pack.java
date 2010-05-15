package stencil.parser.tree;

import java.util.List;

import org.antlr.runtime.Token;

import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;

/**End-of-call-chain entity that create a new tuple in its
 * apply with all of the proper names aligned.
 *
 * @author jcottam
 */
public final class Pack extends CallTarget {
	public Pack(Token source) {
		super(source);
	}

	/**Creates a tuple from the values passed and the list of atoms given.
	 * A pack may have literals or repeated names, so synthetic field names may
	 * be created, but existing field names are used whenever possible.
	 *
	 * The field names are guaranteed to be unique
	 * within the context of the tuple and the tuple returned is guaranteed to preserve
	 * the ordering.  The tuple source is given by the value of PACK_SOURCE.
	 */
	public Tuple apply(Tuple valueSource) {
		List<? extends Value> formals = getArguments();
		Object[] values = TupleRef.resolveAll(formals, valueSource);
		return new ArrayTuple(values);
	}

	/**List of values that are being packed.*/
	public List<Value> getArguments() {return new stencil.parser.tree.List.WrapperList<Value>(this);}
	public boolean isTerminal() {return true;}
	
	/**What is the function that immediately precedes this pack?
	 * If there is no function before the pack, then return null.
	 */
	public Function getPriorCall() {
		StencilTree parent = this.getParent();
		if (parent instanceof Rule) {return null;}
		
		return (Function) parent;	
	}
	
	/**What is the root rule this pack is associated with?*/
	public Rule getRule() {
		StencilTree parent = this.getParent();
		while (parent != null && !(parent instanceof Rule)) {parent = parent.getParent();}
		return (Rule) parent;
	}
}