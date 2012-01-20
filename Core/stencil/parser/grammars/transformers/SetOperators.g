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
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.interpreter.tree.Freezer;
  
  import static stencil.parser.string.util.Utilities.counterpart;
  import static stencil.parser.string.util.Utilities.defaultFacet;
  
}

@members { 
  protected ModuleCache modules;
  
  public static StencilTree apply (Tree t, ModuleCache modules) {return (StencilTree) TreeRewriteSequence.apply(t, modules);}
  
  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
  }

  public Object downup(Object p) {
    p=downup(p, this, "resolveDefaultFacets");     //Determine what the default facets are, directly note
    p=downup(p, this, "instantiate");  			//Create the AstInvokeable nodes
    return p;
  }

	
	private StencilOperator getOp(StencilTree func) {
   		MultiPartName name =  Freezer.multiName(func.find(OP_NAME));
  		
  		try {
         Specializer s = Freezer.specializer(func.find(SPECIALIZER));
         //TODO: Do not use modules for this, pull it from a table of operators stored in the AST (and abolish the ad-hoc module)
         return modules.instance(name, null, s, false);   //null context and false is fine BECAUSE all ops should be instantiated in the AST already
    	} catch (Exception e) {
    		String message = String.format("Error creating invokeable instance for function \%1\$s.", name); //TODO: Add path to the point of error...
    		throw new RuntimeException(message, e);
    	}
	}
	
    public AstInvokeable makeInvokeable(StencilTree func) {
       StencilOperator op = getOp(func);
	   MultiPartName name =  Freezer.multiName(func.find(OP_NAME));
	   try {
           AstInvokeable inv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "");
           inv.setOperator(op);
           inv.setInvokeable(op.getFacet(name.facet()));
           return inv;
	   } catch (Exception e) {
		   throw new RuntimeException("Error constructing invokeable for " + name.toString(), e);
	   }
    }
}

resolveDefaultFacets
  : ^(on=OP_NAME p=. o=. f=DEFAULT_FACET) -> ^(OP_NAME $p $o ID[defaultFacet(modules, $on)])
  | ^(on=OP_NAME p=. o=. f=COUNTERPART_FACET) -> ^(OP_NAME $p $o ID[counterpart(modules, $on)]);

instantiate 
  : (FUNCTION AST_INVOKEABLE ) => ^(f=FUNCTION AST_INVOKEABLE .*) 
  | ^(f=FUNCTION name=. spec=. args=. yield=. pack=.) -> ^(FUNCTION {makeInvokeable($f)} $name $spec $args $yield $pack);


