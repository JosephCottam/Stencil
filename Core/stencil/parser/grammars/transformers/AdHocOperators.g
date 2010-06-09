tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
/** Ensures that stencil native and python operators are defined in the 
 *  ad-hoc module.  Does NOT modify the AST, just populates the ad-hoc module.
 **/

  package stencil.parser.string;

  import stencil.adapters.Adapter;
  import stencil.display.DisplayLayer;
  import stencil.module.operator.wrappers.EncapsulationGenerator;
  import stencil.module.operator.*;
  import stencil.module.*;
  import stencil.module.util.*;    
  import stencil.module.operator.wrappers.*;
  import stencil.parser.tree.*;
}

@members {
	protected MutableModule adHoc;
	protected Adapter adapter;
	protected ModuleCache modules;
	protected final EncapsulationGenerator encGenerator = new EncapsulationGenerator();
	
	public AdHocOperators(TreeNodeStream input, ModuleCache modules, Adapter adapter) {
		super(input, new RecognizerSharedState());
		assert modules != null : "Module cache must not be null.";
		assert adapter != null : "Adapter must not be null.";
		
		this.modules = modules;
		this.adHoc = modules.getAdHoc();
		this.adapter = adapter;				
	}
	
	public Program transform(Program p) {
	   p = simpleOps(p);
	   p = proxyOperators(p);
	   return p;
	}

  private Program simpleOps(Program p) {
    return (Program) downup(p, this, "simple");
  }
	
	

	protected void makeOperator(Operator op) {
		StencilOperator operator = new SyntheticOperator(adHoc.getModuleData().getName(), op);		
		adHoc.addOperator(operator);
	}
	
	protected void makePython(Python p) {
		encGenerator.generate(p, adHoc);
	}
	
	protected void makeLayer(Layer l) {
		DisplayLayer dl =adapter.makeLayer(l); 
		l.setDisplayLayer(dl);
		
		LayerOperator operator = new LayerOperator(adHoc.getName(), dl);
		adHoc.addOperator(operator, operator.getOperatorData());
	}
	
	
	//--------------- Proxy operator fixed-point ---------------------
	boolean changed = true;
	public Program proxyOperators(Program p) {
	   while (changed) {
	      changed = false;
	      p = runOnce(p);
	   }
	   return p;
	}
	
	private Program runOnce(Program p) {
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
          
      StencilOperator op;
      OperatorData od;
      try {
        op = module.instance(baseName, spec);
      } catch (Exception e) {throw new RuntimeException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, name), e);}
      return op;  
  }
  
	 
}
 
simple
	: ^(r=OPERATOR .*) {makeOperator((Operator) $r);}
	| ^(r=PYTHON .*) {makePython((Python) $r);}
	| ^(r=LAYER .*) {makeLayer((Layer) $r);}
  ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) {findBase((OperatorReference) $r) != null}? -> {transferProxy((OperatorReference) $r)}
 ;
	