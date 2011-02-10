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
  import stencil.parser.string.info.UseContext;
  import stencil.parser.string.util.Context;
  import stencil.parser.tree.util.MultiPartName;
  import stencil.parser.string.util.JavaCompiler;
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
	
	protected StencilTree makeLayer(StencilTree l) {
		DisplayLayer dl =adapter.makeLayer(l); 
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
  
  private StencilOperator findBase(StencilTree ref) {
      Specializer spec = Freezer.specializer(ref.find(SPECIALIZER));
      MultiPartName baseName = new MultiPartName(ref.find(OPERATOR_BASE).getText());
      String useName = ref.getText();  
      boolean higherOrder = ref.find(SPECIALIZER).findAllDescendants(OP_AS_ARG).size() != 0;
  
  
      StencilTree program = ref.getAncestor(PROGRAM);
      Context context = UseContext.apply(program, useName);

      StencilOperator op;
      try {
          op = modules.instance(baseName.getPrefix(), baseName.getName(), context, spec, higherOrder);
      } catch (Exception e) {
        throw new RuntimeException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, useName), e);
      }
      return op;
  }
  
  /**Are the operators arguments ready for higher-order ops?**/
  private boolean argsReady(StencilTree ref) {
    List<StencilTree> ops = ref.findAllDescendants(OP_AS_ARG);
    for(StencilTree op: ops) {
       MultiPartName name = new MultiPartName(op.getText());
       try {modules.findModuleForOperator(name.getPrefix(), name.getName());}
       catch (IllegalArgumentException e) {return false;}
    }
    return true;
  }
}
 
simple
	: ^(r=OPERATOR .*) {makeOperator($r);}
	| ^(r=LAYER rest+=.*) -> ^(LAYER $rest* {makeLayer($r)})
	| ^(r=JAVA .*) {makeJava($r);}
  ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) {argsReady($r) && findBase($r) != null}? -> {transferProxy($r)}
 ;
	