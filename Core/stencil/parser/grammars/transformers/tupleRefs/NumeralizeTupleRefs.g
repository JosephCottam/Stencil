tree grammar NumeralizeTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Converts all tuple references to numeric references.
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import stencil.module.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.string.util.EnvironmentProxy;
  import static stencil.parser.string.util.EnvironmentProxy.initialEnv;
  import static stencil.parser.string.util.EnvironmentProxy.extend;
}

@members {
  public static StencilTree apply (StencilTree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules);
  }
  
  protected void setup(Object... args) {modules = (ModuleCache) args[0];}

  private static ModuleCache modules;  
}

//In a more ideal world (where environment-proxy-like entity construction coud be efficienlty implemented as a few methods)
//Maybe if we tucked reutnr prototypes into the invokeables or something.  That would take care of all chains, then only predicates and special frames would need extra work
//topdown
//  : ^(TARGET .*) -> ^(TARGET .*)  //Skip all tuple refs in targets
//  | ^(r=TUPLE_REF id=ID topdown?) -> ^(TUPLE_REF numeralize($id) topdown?)
//  | ^(TUPLE_REF r=NUMBER topdown) //No change needed
//  | ^(TUPLE_REF NUMBER);  //No change needed


topdown
	: ^(p=PREDICATE valueE[initialEnv($p, modules)] op=. valueE[initialEnv($p, modules)])
  | ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)] .)
  | ^(CONSUMES (options {greedy=false;} :.)* ^(DYNAMIC_REDUCER ^(p=PACK valueE[initialEnv($p, modules)]*))); 

callTarget[EnvironmentProxy env]
  : ^(f=FUNCTION . . ^(LIST_ARGS valueE[env]*) y=. callTarget[extend(env, $y, $f, modules)])
  | ^(PACK valueE[env]*);    
      
      
valueE[EnvironmentProxy env]
  : ^(t=TUPLE_REF r=ID valueP[env.get(env.getFrameIndex($r.text))]?)
      -> ^(TUPLE_REF NUMBER[Integer.toString(env.getFrameIndex($r.text))] valueP?)
  | (STRING | NUMBER); //Literals don't have tuple refs, just match and continue
    catch[Exception e] {throw new RuntimeException(String.format("Error numeralizing \%1\$s.\n\%2\$s.", $t.toStringTree(), e.toString()));}
  
      
//TODO:Unify envProxy and TuplePrototype (will eliminated the valueE and valueNP variants, requires a way to get a prototype from a prototype...)
valueP[TuplePrototype p]
  : r=ID value? -> NUMBER[Integer.toString(p.indexOf($r.text))] value?
  | r=NUMBER value?;
    catch[Exception e] {throw new RuntimeException("Error numeralizing " + $r.getAncestor(TUPLE_REF).toStringTree());}

value
  : ID {throw new RuntimeException("Numeralize can only handle one level names in nesting");}
  | NUMBER value?;
