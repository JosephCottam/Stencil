package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.tuple.Tuple;

/**Holder for invokeables, independent of context.
 * 
 * Text is usually related to the invokeable, but is not
 * necessarily a consistent relationship across contexts.
 * */
public final class AstInvokeable<R> extends StencilTree {
	private StencilOperator op;
	private Invokeable<R> inv;
	
	public AstInvokeable(Token token) {
		super(token);
	}
	
	public void setOperator(StencilOperator op) {this.op = op;}
	public void setInvokeable(Invokeable inv) {this.inv = inv;}
	public StencilOperator getOperator() {return op;}
	public Invokeable getInvokeable() {return inv;}

	public R directInvoke(Object[] args) {return inv.invoke(args);}
	
	public Tuple invoke(Object[] args) {
		return inv.tupleInvoke(args);
	}

	public AstInvokeable dupNode() {
		AstInvokeable n = (AstInvokeable) super.dupNode();
		n.inv = inv;
		n.op = op;
		return n;
	}
	
	public String toString() {
		String rv = super.toString() + "(AST";
		if (inv == null) {return rv + " -NoInvokeable)";}
		if (op == null) {return rv + " -NoOperator)";}
		return rv + ")";
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
	
	public int hashCode() {return inv.hashCode();}
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof AstInvokeable)) {return false;}
		AstInvokeable o = (AstInvokeable) other;
		boolean result = inv.equals(o.inv);
		return result;
	}
	
}
