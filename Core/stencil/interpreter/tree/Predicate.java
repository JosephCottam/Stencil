package stencil.interpreter.tree;

import stencil.interpreter.Viewpoint;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.parser.tree.util.Environment;

public class Predicate implements Viewpoint<Predicate> {
	private final Invokeable inv;
	private final Object[] args;

	public Predicate(Invokeable inv, Object[] args) {
		this.inv = inv;
		this.args = args;
	}
	
	public Predicate viewpoint() {
		if (inv.getTarget() == null) {return this;}
		else if (inv.getTarget() instanceof StencilOperator) {
			Invokeable vp = new ReflectiveInvokeable(((ReflectiveInvokeable) inv).getMethod(), ((StencilOperator) inv.getTarget()).viewpoint());
			return new Predicate(vp, args);
		} 
		throw new Error("Could not viewpoint predicate when requested.");
	}


	/**Does the passed environment match this predicate?*/
	public boolean matches(Environment env) {
		Object[] formals = TupleRef.resolveAll(args, env);
		return (Boolean) inv.invoke(formals);
	}

	
	/**Does the passed environment match the given predicates?*/
	public static boolean matches(Predicate[] predicates, Environment env) {
		for (Predicate pred: predicates) {
			if (!pred.matches(env)) {return false;}
		}
		return true;
	}
}
