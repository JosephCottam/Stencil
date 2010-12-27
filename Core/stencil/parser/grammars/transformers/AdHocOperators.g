
tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
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
  import stencil.parser.tree.*;
  import stencil.parser.tree.util.MultiPartName;
}

@members {
  public static Program apply (Tree t, ModuleCache modules, Adapter adapter) {
     return (Program) TreeRewriteSequence.apply(t, modules, adapter);
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
	   proxyOperators((Program) p);
	   return p;
	}
	
	

	protected void makeOperator(Operator op) {
		StencilOperator operator = new SyntheticOperator(adHoc.getModuleData().getName(), op);		
		adHoc.addOperator(operator);
	}
	
	protected void makeLayer(Layer l) {
		DisplayLayer dl =adapter.makeLayer(l); 
		l.setDisplayLayer(dl);
		
		LayerOperator operator = new LayerOperator(adHoc.getName(), dl);
		adHoc.addOperator(operator, operator.getOperatorData());
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
	
	private Program runOnce(Tree p) {
	  return (Program) downup(p, this, "proxies");
	}
	
  private Tree transferProxy(OperatorReference ref) {
    OperatorProxy proxy = makeProxy(ref);
     
    if (adHoc.getModuleData().getOperatorNames().contains(proxy.getName())) {return ref;}
    adHoc.addOperator(proxy.getName(), proxy.getOperator(), proxy.getOperatorData());
    changed = true; 
    return proxy;
  } 
  
  private OperatorProxy makeProxy(OperatorReference ref) {
      String name = ref.getName();
      StencilOperator op = findBase(ref);
       
      OperatorProxy p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, name);
      p.setOperator(op, op.getOperatorData());
      return p;
  }
  
  private StencilOperator findBase(OperatorReference ref) {
      OperatorBase base = (OperatorBase) ref.getFirstChildWithType(OPERATOR_BASE);
      Specializer spec = (Specializer) ref.getFirstChildWithType(SPECIALIZER);
      String baseName = base.getName();
      String name = ref.getName();  
  
      Module module; 
      try {module = modules.findModuleForOperator(baseName);}
      catch (Exception e) {return null;}
      
      
      String opName  = (new MultiPartName(baseName)).getName();
      StencilOperator op;
      try {
          op = module.instance(opName, spec);
      } catch (Exception e) {throw new RuntimeException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, name), e);}
      return op;  
  }
  
	private void makeJava(Java java) {
	    adHoc.addOperator(java.name(), java.operator(), java.operatorData());
	}
}
 
simple
	: ^(r=OPERATOR .*) {makeOperator((Operator) $r);}
	| ^(r=LAYER .*) {makeLayer((Layer) $r);}
	| ^(r=JAVA .*) {makeJava((Java) $r);}
  ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) {findBase((OperatorReference) $r) != null}? -> {transferProxy((OperatorReference) $r)}
 ;
	