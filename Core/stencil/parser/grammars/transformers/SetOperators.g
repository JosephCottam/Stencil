tree grammar SetOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
	output = AST;
}

@header {
/** Adds references to the concrete operators to each function call tree-node.  
 * Technically, does NOT modify the AST since the field modified is not part
 * of the ANTLR tree; however this does set decorator fields in the AST.
 *
 * Annotate function call tree nodes with Invokeable objects.
 **/
	package stencil.parser.string;
	
    import stencil.module.*;
    import stencil.module.util.*;
    import stencil.module.operator.StencilOperator;
    import stencil.module.operator.util.*;
    import stencil.parser.tree.Function;
    import stencil.parser.tree.Specializer;
    import stencil.parser.tree.StencilTree;
    import stencil.parser.tree.AstInvokeable;
    import stencil.util.*;

}

@members { 
	protected ModuleCache modules;

	public SetOperators(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		this.modules = modules;
	}
	
    public AstInvokeable makeInvokeable(Tree t) {
    	Function func = (Function) t;
  		StencilOperator op;
   		MultiPartName name = new MultiPartName(func.getName());
  		
  		try {
    		Specializer s = func.getSpecializer();
            op = modules.instance(name.prefixedName(), s);
    	} catch (Exception e) {
    		String message = String.format("Error creating invokeable instance for function \%1\$s.", func.getName()); //TODO: Add path to the point of error...
    		throw new RuntimeException(message, e);
    	}
    	
       AstInvokeable inv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "");
       inv.setOperator(op);
       inv.setInvokeable(op.getFacet(name.getFacet()));
       return inv;
    }
}

topdown 
  : (FUNCTION AST_INVOKEABLE ) => ^(f=FUNCTION AST_INVOKEABLE .*) 
  | ^(f=FUNCTION spec=. args=. yield=. pack=.) -> ^(FUNCTION {makeInvokeable($f)} $spec $args $yield $pack);
//  | ^(f=FUNCTION rest+=.*) -> ^(FUNCTION {makeInvokeable($f)} $rest*);


