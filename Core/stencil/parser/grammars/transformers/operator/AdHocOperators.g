tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
/** Creates specific references to all operators in the Stencil program.**/

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
  import stencil.parser.ProgramCompileException;
  import stencil.parser.string.util.TreeRewriteSequence;
  import stencil.parser.string.util.Utilities;
  import stencil.parser.string.validators.NoOperatorReferences;
  import stencil.parser.string.util.Utilities.OperatorNotFoundException;
  
  import static stencil.parser.ParserConstants.STAND_IN_GROUP;
}

@members {
	public static StencilTree apply (StencilTree t, ModuleCache modules, Adapter adapter) {
	   return (StencilTree) TreeRewriteSequence.apply(t, modules, adapter);
	}
	  
	protected void setup(Object... args) {
	   modules = (ModuleCache) args[0];
	   adapter = (Adapter) args[1];
	}
  
	protected Adapter adapter;
	protected ModuleCache modules;
	
	public StencilTree downup(Object p) {	
	   StencilTree r;
	   r = downup(p, this, "prepare");
	   r = downup(r, this, "simple");
	   r = proxyOperators(r);
	   NoOperatorReferences.apply(r);					//Validate the ad-hocs are all created
	   r = downup(r, this, "removePartialList");
	   r = downup(r, this, "relabelOperators");
	   return r;
	}
	
	//Move a stencil native op from the Operators list to the instantiated operators list.
	protected void moveOperator(StencilTree op) {
		StencilTree list = op.getAncestor(PROGRAM).find(LIST_OPERATORS);
		StencilTree newOp = (StencilTree) adaptor.create(OPERATOR_PROXY, op.getToken());

		for (StencilTree t: op) {
			adaptor.addChild(newOp, adaptor.dupTree(t));
		}
		adaptor.addChild(list, newOp);
	}
	
	protected void makeLayerInstance(StencilTree l) {
    	Specializer spec = Freezer.specializer(l.find(SPECIALIZER));
		DisplayLayer dl =adapter.makeLayer(l.getText(), spec); 
		LayerOperator operator = new LayerOperator(STAND_IN_GROUP, dl);
		StencilTree list = l.getAncestor(PROGRAM).find(LIST_OPERATORS);	
		
		Utilities.addToOperators(STAND_IN_GROUP, l.getText(), operator, 
								list, adaptor, l.getToken()); 
	}
	
	
	
	
	//--------------- Proxy operator fixed-point ---------------------
	boolean changed = true;
	public StencilTree proxyOperators(StencilTree p) {
	   while (changed) {
	      changed = false;
	      p = (StencilTree) downup(p, this, "proxies");
	   }
	   return p;
	}
		
  private StencilTree transferProxy(StencilTree ref) {
    OperatorProxy proxy = makeProxy(ref);
    StencilTree list = ref.getAncestor(PROGRAM).findDescendant(LIST_OPERATORS);

	Utilities.addToOperators(STAND_IN_GROUP, proxy.getName(), proxy.getOperator(),
							list, adaptor, ref.getToken()); 
    changed = true; 
    return null;
  } 
  
  private OperatorProxy makeProxy(StencilTree ref) {
      String name = ref.getText();
      StencilOperator op = findBase(ref);
       
      OperatorProxy p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, name);
      p.setOperator(STAND_IN_GROUP, op, op.getOperatorData());
      return p;
  }
  
  //Instantiates an operator, preferentially from the already instantiated list, or from AdHoc
  private StencilOperator findBase(StencilTree ref) {
      Specializer spec = Freezer.specializer(ref.find(SPECIALIZER));
      MultiPartName baseName = Freezer.multiName(ref.find(OPERATOR_BASE));

      if (!baseName.prefixed()) {
          try {return Utilities.findOperator(ref, baseName.name());}
          catch (OperatorNotFoundException onex) {} //Ignore the error and try using the full modules cache
      }

      String useName = ref.getText();      
      StencilTree program = ref.getAncestor(PROGRAM);
      Context context = UseContext.apply(program, useName);

      StencilOperator op;
      try {
          op = modules.instance(baseName, context, spec);
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
  	  if (baseName.prefix() != "") {return true;}//Operator has a prefix, can't be ad-hoc...must be ready or non-existent
  	  
  	  StencilTree ops = ref.getAncestor(LIST_PARTIAL_OPERATORS);
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
    StencilTree ready = ref.getAncestor(PROGRAM).findDescendant(LIST_OPERATORS);
    for(StencilTree op: ops) {
       MultiPartName name = Freezer.multiName(op.find(OP_NAME));
       if (ready.find(OPERATOR_PROXY, name.name()) == null) {return false;}
    }
    return true;
  }


  //TODO: Merge this in with the ViewCanvas operators pass somehow.  Maybe the constructed objects here are passed as constants to the operators through the specializers...Then the ViewCanvas operator pass just inserts references, does not need to add the proxy! (Similar to how layers work)   
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
  
}

prepare : ^(PROGRAM prefix+=.* ^(LIST_OPERATORS ops+=.*)) -> ^(PROGRAM $prefix* LIST_OPERATORS ^(LIST_PARTIAL_OPERATORS $ops*));
 
simple
	: ^(o=OPERATOR .*) {moveOperator($o);}
	| ^(l=LAYER .*) {makeLayerInstance($l);}
	| ^(s=VIEW rest+=.*) -> ^(VIEW $rest* {makeView($s)})
    | ^(s=CANVAS rest+=.*) -> ^(CANVAS $rest* {makeCanvas($s)})
    ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) 
 	{stencilOpReady($r.find(OPERATOR_BASE)) && argsReady($r)}? -> {transferProxy($r)} EMPTY
 ;
 

removePartialList: ^(PROGRAM prefix+=.* ^(LIST_PARTIAL_OPERATORS .*)) -> ^(PROGRAM $prefix*);
relabelOperators: ^(op=OPERATOR_PROXY rest+=.+) -> ^(OPERATOR[$op.token] $rest*);				 
