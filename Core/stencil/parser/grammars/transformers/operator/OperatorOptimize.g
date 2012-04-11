tree grammar OperatorOptimize;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
/**Substitute in context-specific operator optimizations, if applicable.
 * This must be run after all operators have been instantiated.
**/

  package stencil.parser.string;

  import stencil.module.operator.*;
  import stencil.module.*;
  import stencil.module.util.*;    
  import stencil.module.operator.wrappers.*;
  import stencil.interpreter.tree.*;
  import stencil.parser.tree.*;
  import stencil.parser.string.util.Context;
  import stencil.parser.ProgramCompileException;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.parser.string.util.Utilities;
  import stencil.parser.ParserConstants;
}

@members {
	protected ModuleCache modules;

	public static StencilTree apply (StencilTree t, ModuleCache modules) {
	   return TreeRewriteSequence.apply(t, modules);
	}
	  
	protected void setup(Object... args) {
	   modules = (ModuleCache) args[0];
	}

	public OperatorProxy optimizeOp(StencilTree operator) {
		StencilOperator op = ((OperatorProxy) operator).getOperator();
		OperatorData od = op.getOperatorData();
		
		Module m = modules.getModule(od.module());
		Context context = UseContext.apply(operator.getAncestor(PROGRAM), operator.getText());
		
		//IFF the context includes operators as arguments, get each operator and call optimize on it too		
		int opArgCount =0;
		StencilTree opArg = null; 
		for (StencilTree arg: context.args()) {
		   if(arg.is(OP_AS_ARG)) {
		      opArg = arg;
		   	  opArgCount++;
		   }
		}
		
		OperatorProxy p;
		if (opArgCount == 1) {
			p = optimizeOp(opArg);
		} else {
		  StencilOperator op2;
		  p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, operator.token);
		  op2 = m.optimize(op, context);
		  p.setOperator(ParserConstants.STAND_IN_GROUP, op2, op2.getOperatorData());
		} 
		return p;
	}
	
	public StencilTree cleanup(StencilTree operator) {
        Context context = UseContext.apply(operator.getAncestor(PROGRAM), operator.getText());
        if (context.callSites().size() >0) {return operator;}
        else {return (StencilTree) adaptor.create(EMPTY_NODE, operator.token);}
	}

}

optimize: op=OPERATOR_PROXY -> {optimizeOp(op)};
remove: op=OPERATOR_PROXY -> {cleanup(op)};
cleanup: ^(LIST_OPERATORS (EMPTY_NODE|op+=OPERATOR_PROXY)*) -> ^(LIST_OPERATORS $op*);  