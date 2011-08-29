tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
/** Ensures that stencil native and java operators are defined in the ad-hoc module.  
 **/

  package stencil.parser.string;

  import stencil.adapters.Adapter;
  import stencil.display.DisplayLayer;
  import stencil.module.operator.*;
  import stencil.module.*;
  import stencil.module.util.*;    
  import stencil.module.operator.wrappers.*;
  import stencil.interpreter.tree.*;
  import stencil.parser.tree.*;
  import stencil.parser.string.util.Context;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.parser.string.util.JavaCompiler;
  import stencil.parser.ProgramCompileException;
}

@members {
  public static StencilTree apply (Tree t, ModuleCache modules, Adapter adapter) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules, adapter);
  }
  
  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
     adapter = (Adapter) args[1];
     adHoc = modules.getAdHoc();
  }
  
	protected MutableModule adHoc;
	protected Adapter adapter;
	protected ModuleCache modules;
	
	public Object downup(Object p) {	
	   downup(p, this, "simple");
	   proxyOperators((StencilTree) p);
	   return p;
	}
	
	

	protected void makeOperator(StencilTree op) {
		StencilOperator operator = new SyntheticOperator(adHoc.getModuleData().getName(), op);		
		adHoc.addOperator(operator);
	}
	
	protected StencilTree makeView(StencilTree vd) {
        Specializer spec = Freezer.specializer(vd.find(SPECIALIZER));
        Object canvas = adapter.makeView(spec);
        Const c = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
        c.setValue(canvas); 
        return c;
	}
	
	protected StencilTree makeCanvas(StencilTree cd) {
	    Specializer spec = Freezer.specializer(cd.find(SPECIALIZER));
	    Object canvas = adapter.makeCanvas(spec);
	    Const c = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
        c.setValue(canvas); 
        return c;
	}
	
	protected StencilTree makeLayer(StencilTree l) {
    	Specializer spec = Freezer.specializer(l.find(SPECIALIZER));
		
		DisplayLayer dl =adapter.makeLayer(l.getText(), spec); 
		LayerOperator operator = new LayerOperator(adHoc.getName(), dl);
		adHoc.addOperator(operator, operator.getOperatorData());
		
		Const c = (Const) adaptor.create(CONST, StencilTree.typeName(CONST));
		c.setValue(dl); 
		return c;
	}
	
	private void makeJava(StencilTree java) {
     String name = java.getText();
     String superClass = java.getChild(0).getText();
     String header = java.getChild(1).getText();
     String body = java.getChild(2).getText();
     
     adHoc.addOperator(JavaCompiler.compile(name, superClass, header, body));
  }
	
	
	
	//--------------- Proxy operator fixed-point ---------------------
	boolean changed = true;
	public Tree proxyOperators(Tree p) {
	   while (changed) {
	      changed = false;
	      p = runOnce(p);
	   }
	   return p;
	}
	
	private StencilTree runOnce(Tree p) {
	  return (StencilTree) downup(p, this, "proxies");
	}
	
  private Tree transferProxy(StencilTree ref) {
    OperatorProxy proxy = makeProxy(ref);
     
    if (adHoc.getModuleData().getOperatorNames().contains(proxy.getName())) {return ref;}
    adHoc.addOperator(proxy.getName(), proxy.getOperator(), proxy.getOperatorData());
    changed = true; 
    return proxy;
  } 
  
  private OperatorProxy makeProxy(StencilTree ref) {
      String name = ref.getText();
      StencilOperator op = findBase(ref);
       
      OperatorProxy p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, name);
      p.setOperator(op, op.getOperatorData());
      return p;
  }
  
  //Instantiates an operator from AdHoc
  private StencilOperator findBase(StencilTree ref) {
      Specializer spec = Freezer.specializer(ref.find(SPECIALIZER));
      MultiPartName baseName = Freezer.multiName(ref.find(OPERATOR_BASE));
      String useName = ref.getText();  
      boolean higherOrder = ref.find(SPECIALIZER).findAllDescendants(OP_AS_ARG).size() != 0;
  
      StencilTree program = ref.getAncestor(PROGRAM);
      Context context = UseContext.apply(program, useName);

      StencilOperator op;
      try {
          op = modules.instance(baseName, context, spec, higherOrder);
      } catch (Exception e) {
        throw new ProgramCompileException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, useName), ref, e);
      }
      return op;
  }
  
  /**Check to see if the base is a reference to an stencil-defined operator (may be synthetic or a reference).
   *  If it is AND that op is not yet instantiated THEN return false.
   **/
  private boolean stencilOpReady(StencilTree opName) {
  	  MultiPartName baseName = Freezer.multiName(opName);
  	  StencilTree ref = opName.getAncestor(OPERATOR_REFERENCE);
  	  if (baseName.prefix() != "") {return true;}//Operator has a prefix, can't be ad-hoc...must be ready or non-existant
  	  
  	  StencilTree ops = ref.getAncestor(LIST_OPERATORS);
  	  for (StencilTree op: ops) {
  	     if (ref != op && baseName.name().equals(op.getText())) {
  	        return op.getType() == OPERATOR_PROXY
  	        		|| op.getType() == OPERATOR;
  	     }
  	  }
  	  return true;		//Time to look into the other modules, won't hurt
  }
  
  /**Are the operators arguments ready for higher-order ops?**/
  private boolean argsReady(StencilTree ref) {
    List<StencilTree> ops = ref.findAllDescendants(OP_AS_ARG);
    for(StencilTree op: ops) {
       MultiPartName name = Freezer.multiName(op.find(OP_NAME));
       try {
          if (stencilOpReady(op.find(OP_NAME))) {
	          modules.findModuleForOperator(name);
	      } else {return false;}
       } catch (IllegalArgumentException e) {return false;}
    }
    return true;
  }
}
 
simple
	: ^(s=OPERATOR .*) {makeOperator($s);}
	| ^(s=LAYER rest+=.*) -> ^(LAYER $rest* {makeLayer($s)})
	| ^(s=JAVA .*) {makeJava($s);}
	| ^(s=VIEW rest+=.*) -> ^(VIEW $rest* {makeView($s)})
	| ^(s=CANVAS rest+=.*) -> ^(CANVAS $rest* {makeCanvas($s)})
  ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) {stencilOpReady($r.find(OPERATOR_BASE)) && argsReady($r)}? -> {transferProxy($r)}
 ;
	