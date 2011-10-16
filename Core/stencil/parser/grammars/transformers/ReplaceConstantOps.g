tree grammar ReplaceConstantOps;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
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
  import stencil.interpreter.Environment;
  import stencil.tuple.Tuple;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.parser.ProgramCompileException;
  
  import static stencil.tuple.Tuples.EMPTY_TUPLE;
  import static stencil.parser.ParserConstants.INVOKEABLE;
}

@members {
	public static final class ConstantOpError extends ProgramCompileException {
	   public ConstantOpError(String message) {super(message);}
	   public ConstantOpError(String message, Exception cause) {super(message, cause);}
	}

   public static StencilTree apply (StencilTree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules);
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
     
     return downup(p, this, "topdown", "removeConsts");
  }
    
	  //Is the give operator call Constant?
	  //Constant operator calls are functions with no tuple refs
    private boolean isConstant(StencilTree func) {
       return isTrueFunction(func) && (func.find(LIST_ARGS).find(TUPLE_REF) == null);
    }
    
    //Is operator wrapped by the invokeable a mathematical function?
    private boolean isTrueFunction(StencilTree func) {
      MultiPartName name= Freezer.multiName(func.find(OP_NAME));
      Specializer spec = Freezer.specializer(func.find(SPECIALIZER));
      
      try{
    		Module m = modules.findModuleForOperator(name);
            OperatorData od = m.getOperatorData(name.name(), spec);
    		FacetData fd=od.getFacet(name.facet());
    		return fd.getMemUse() == FacetData.MemoryUse.FUNCTION;
   		} catch (Exception e) {
   			throw new ConstantOpError("Error getting module information for operator " + name, e);
   	  }
		}

    private Tuple evaluate(StencilTree f) {
        assert isConstant(f) : "Reached evaluate for non-const operator application.";
        try {
          Object[] args = Freezer.valueList(f.find(LIST_ARGS));
          return f.find(INVOKEABLE).invoke(args);}
        catch (Exception e) {throw new ConstantOpError("Error evaluating constant operator during constant propagation; " + f.toStringTree(), e);}
    }
  
  private static final Environment defaultEnvironment(StencilTree chain) {
     int depth = chain.findAllDescendants(FUNCTION).size() + chain.findAllDescendants(CONST).size();
     Environment env = Environment.getDefault(true);
     return env.ensureCapacity(true, env.size() + depth);
  }
  
  private static final Object NO_RESOLUTION = new Object();
  
  private StencilTree updatePack(StencilTree pack, Environment env) {
     StencilTree newArgs = updateArgs(pack, env);
     StencilTree newPack = (StencilTree) adaptor.create(PACK, "args");
     for (Object v: newArgs) {
        adaptor.addChild(newPack, adaptor.dupTree(v));
     }
     return newPack;
  }
  
  
  
  private StencilTree updateArgs(StencilTree args, Environment env) {
     StencilTree newArgs = (StencilTree)adaptor.create(LIST_ARGS, StencilTree.typeName(LIST_ARGS));
     
     for (StencilTree arg: args) {
        Object value = NO_RESOLUTION;
        if (arg.getType() == TUPLE_REF) {
           try {value = Freezer.tupleRef(arg).resolve(env);            
           } catch (Exception e) {/**Ignore**/}
        }
        
        if (value == NO_RESOLUTION || value == EMPTY_TUPLE) {
           adaptor.addChild(newArgs, adaptor.dupTree(arg));
        } else if (value instanceof StencilTree) {
           adaptor.addChild(newArgs, adaptor.dupTree(value));
        } else {
           adaptor.addChild(newArgs, Const.instance(value));
        }
     }
     
     return newArgs;
  }
  
  private int afterFrame; 
  private Object reframe(Object tree) {

     Tree cursor = (Tree) tree;
     afterFrame = Environment.DEFAULT_FRAME_NAMES.length-1;   //-1 is required becuase the loop will always execute at least once.
     while (cursor.getType() != CALL_CHAIN) {afterFrame++; cursor = cursor.getParent();}
     
     return downup(adaptor.dupTree(tree), this, "frameSub1");
  }
  
  private String maybeSub1(Object number) {
     int frameRef = Integer.parseInt(number.toString());
     if (frameRef >= afterFrame) {frameRef = frameRef-1;}
     return Integer.toString(frameRef);
  }  
}

//Replace constant operations in call chains with CONST entities holding the return tuple
replaceOps
  @after{
	  Const c=((Const) $replaceOps.tree);
	  c.setValue(evaluate($f));
      changed=true;
  }
  : ^(f=FUNCTION inv=. name=. spec=. args=. yield=. target=.)
        {isConstant($f) && f.getAncestor(CALL_CHAIN) != null}? -> ^(CONST["CONST"] $yield $target);

//Propogate constants through call chains        
propogateValues
  : ^(c=CALL_CHAIN chain[defaultEnvironment($c)] depth=.);        

chain[Environment env]
  : ^(f=FUNCTION inv=. name=. spec=. args=. yield=. target=chain[env.extend(EMPTY_TUPLE)])
    -> ^(FUNCTION $inv $name $spec {updateArgs($args, env)} $yield $target)
  | ^(c=CONST yield=. target=chain[env.extend((Tuple) ((Const) c).getValue())])
  | ^(p=PACK .*)
    -> {updatePack($p, env)};
    
//Remove all CONST entities with their targets
removeConsts
  : ^(CALL_CHAIN ^(CONST . target=.)) -> ^(CALL_CHAIN {reframe($target)}) 
  | ^(FUNCTION inv=. name=. spec=. args=. yield=. ^(CONST constYield=. target=.)) -> ^(FUNCTION $inv $name $spec $args $constYield {reframe($target)});
  
  
frameSub1: ^(TUPLE_REF frame=. rest+=.*) -> ^(TUPLE_REF NUMBER[maybeSub1($frame)] $rest*);
 