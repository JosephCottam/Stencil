tree grammar ReplaceConstantOps;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header {
  /**Identifies operations that are constant and replaces them with "Const" nodes.
   **/
	package stencil.parser.string;
	
	import stencil.module.*;
	import stencil.module.util.*;
	import stencil.parser.tree.*;
	import stencil.parser.tree.util.*;
	import stencil.tuple.Tuple;
  import static stencil.tuple.Tuples.EMPTY_TUPLE;
}

@members {
   public static Program apply (Tree t, ModuleCache modules) {
     return (Program) TreeRewriteSequence.apply(t, modules);
   }
  
   protected void setup(Object... args) {modules = (ModuleCache) args[0];}
   
   protected ModuleCache modules;
  
  //Fixed point for updating constants
  private boolean changed; 
  public Object downup(Object p) {
     
     changed = true;
     while(changed) {
       changed = false;
       downup(p, this, "replaceOps");
       downup(p, this, "propogateValues");
     }
     
     changed= true;   //Doing this as a fixed point makes the grammar easier
     while (changed) {
       changed = false;     
       p=(Program) downup(p, this, "removeConsts");
     }
     
     return p;
  }
    
	  //Is the give operator call Constant?
	  //Constant operator calls are functions with no tuple refs
    private boolean isConstant(Function func) {
       return isTrueFunction(func) && constArgs(func.getArguments());
    }
    
    private boolean constArgs(List<Value> args) {
       for (Value v: args) {
          if (v instanceof TupleRef
              || v instanceof All
              || v instanceof Last) {return false;}  
       }
       return true;
    }

    //Is the passed Function using a facet that is a mathematical function?
    private boolean isTrueFunction(Function f) {
    	MultiPartName name= new MultiPartName(f.getName());
      try{
    		Module m = modules.findModuleForOperator(name.prefixedName());
    		OperatorData od = m.getOperatorData(name.getName(), f.getSpecializer());
    		FacetData fd=od.getFacet(name.getFacet());
    		return fd.isFunction();
   		} catch (Exception e) {
   			throw new RuntimeException("Error getting module information for operator " + name, e);
   	  }
		}

    private Tuple evaluate(Function f) {
        assert isConstant(f) : "Reached evaluate for non-const operator application.";
        try {return f.getTarget().invoke(f.getArguments().toArray());}
        catch (Exception e) {throw new RuntimeException("Error evaluating constant oeprator durring constant propagation; " + f.toStringTree());}
    }
  
  private static final Environment defaultEnvironment(Object chain) {
     int depth = ((CallChain) chain).getDepth();
     Environment env = Environment.getDefault();
     return env.ensureCapacity(env.size() + depth);
  }
  
  private static final Object NO_RESOLUTION = new Object();
  
  private Pack updatePack(Pack pack, Environment env) {
     List newArgs = updateArgs(pack.getArguments(), env);
     Pack newPack = (Pack) adaptor.create(PACK, "args");
     for (Object v: newArgs) {
        adaptor.addChild(newPack, adaptor.dupTree(v));
     }
     return newPack;
  }
  
  
  
  private List updateArgs(List<Value> args, Environment env) {
     List newArgs = (List) adaptor.create(LIST, "args");
     
     for (Value arg: args) {
        Object value = NO_RESOLUTION;
        if (arg instanceof TupleRef) {
           try {value = TupleRef.resolve((TupleRef) arg, env);            
           } catch (Exception e) {/**Ignore**/}
        }
        
        if (value == NO_RESOLUTION || value == EMPTY_TUPLE) {
           adaptor.addChild(newArgs, adaptor.dupTree(arg));
        } else{
          adaptor.addChild(newArgs, Atom.Literal.instance(value));
        }
     }
     
     return newArgs;
  }
}

//Replace constant operations in call chains with CONST entities holding the return tuple
replaceOps
  @after{
    Const c=((Const) $replaceOps.tree);
    c.setTuple(evaluate((Function) $f));
    changed=true;
  }
  : ^(f=FUNCTION inv=. spec=. args=. yield=. target=.)
        {isConstant((Function) $f) && f.getAncestor(CALL_CHAIN) != null}? -> ^(CONST $yield $target);

//Propogate constants through call chains        
propogateValues
  : ^(c=CALL_CHAIN chain[defaultEnvironment($c)] depth=.);        

chain[Environment env]
  : ^(f=FUNCTION inv=. spec=. args=. yield=. target=chain[env])
    -> ^(FUNCTION[$f.text] $inv $spec {updateArgs((List<Value>) $args, env)} $yield $target)
  | ^(c=CONST yield=. target=chain[env.extend(((Const) c).getTuple())])
  | ^(p=PACK .*)
    -> {updatePack((Pack) $p, env)};
    
//Remove all CONST entities with their targets    
removeConsts
  @after{changed=true;}
  : ^(CALL_CHAIN ^(CONST . target=.) depth=.) -> ^(CALL_CHAIN $target $depth) 
  | ^(FUNCTION inv=. spec=. args=. yield=. ^(CONST constYield=. target=.)) -> ^(FUNCTION $inv $spec $args $constYield $target);