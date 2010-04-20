package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.operator.StencilOperator;
import stencil.operator.util.Invokeable;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;

/**Holder for invokeables, independent of context.
 * 
 * Text is usually related to the invokeable, but is not
 * necessarily a consistent relationship across contexts.
 * */
public final class AstInvokeable extends StencilTree {
	private StencilOperator op;
	private Invokeable inv;
	
	public AstInvokeable(Token token) {
		super(token);
	}
	
	public void setOperator(StencilOperator op) {this.op = op;}
	public void setInvokeable(Invokeable inv) {this.inv = inv;}
	public StencilOperator getOperator() {return op;}
	public Invokeable getInvokeable() {return inv;}

	public Tuple invoke(Object[] args, ArrayTuple container) {
		return inv.tupleInvoke(args, container);
	}

	public AstInvokeable dupNode() {
		AstInvokeable n = (AstInvokeable) super.dupNode();
		n.inv = inv;
		n.op = op;
		return n;
	}
	
	public String toString() {
		String rv = super.toString();
		if (inv == null) {return rv + " -NoInvokeable";}
		if (op == null) {return rv + " -NoOperator";}
		return rv;
	}
	
	/**If the invokeable being carried contains a
	 * StencilOperator, this method can be used to change
	 * which facet of the StencilOperator the invokeable uses. 
	 * @param name
	 */
	public void changeFacet(String name) {
		if (canChangeFacet()) {
			inv = op.getFacet(name);
		} else {
			throw new UnsupportedOperationException("Facet can only be changed if the contained invokeable is a StencilOperator.");
		}
	}
	
	/**Indicates if a changeFacet operation is possible.*/
	public boolean canChangeFacet() {return op != null;}
	
}
