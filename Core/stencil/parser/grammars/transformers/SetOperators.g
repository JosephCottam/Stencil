tree grammar SetOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
	output = AST;
	superClass = TreeRewriteSequence;
}

@header {
/** Adds references to the concrete operators to each function call tree-node.  
 * Technically, does NOT modify the AST since the field modified is not part
 * of the ANTLR tree; however this does set decorator fields in the AST.
 *
 * Annotate function call tree nodes with Invokeable objects.
 *
 * TODO: Remove ad-hoc module, just search the AST operators list (may require synthetic operators to be proxied)
 **/
	package stencil.parser.string;
	
  import stencil.module.*;
  import stencil.module.operator.StencilOperator;
  import stencil.parser.tree.*;
  import stencil.parser.tree.util.*;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.Freezer;
}

@members { 
  public static StencilTree apply (Tree t, ModuleCache modules) {return (StencilTree) TreeRewriteSequence.apply(t, modules);}
  
  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
  }

	protected ModuleCache modules;
	
    public AstInvokeable makeInvokeable(StencilTree func) {
  		StencilOperator op;
   		MultiPartName name = new MultiPartName(func.getText());
  		
  		try {
         Specializer s = Freezer.specializer(func.find(SPECIALIZER));
         op = modules.instance(name.getPrefix(), name.getName(), null, s);   //null context is fine BECAUSE all ops should be instantiated in the AST already
    	} catch (Exception e) {
    		String message = String.format("Error creating invokeable instance for function \%1\$s.", func.getText()); //TODO: Add path to the point of error...
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


