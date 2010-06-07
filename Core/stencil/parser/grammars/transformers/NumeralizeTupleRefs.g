tree grammar NumeralizeTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
  
}

@header{
  /** Converts all tuple references to numeric references.
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.ParserConstants;  
  import stencil.parser.tree.*;
  import stencil.module.*;
  import stencil.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototype;
  import static stencil.parser.string.EnvironmentProxy.initialEnv;
  import static stencil.parser.string.EnvironmentProxy.extend;
}

@members {
  private ModuleCache modules;
  
  public NumeralizeTupleRefs(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
  }
}

topdown
	: ^(p=PREDICATE valueE[initialEnv($p, modules)] op=. valueE[initialEnv($p, modules)])
    | ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)]);

callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION . . ^(LIST valueE[env]*) y=. callTarget[extend(env, $y, $f, modules)])
  | ^(PACK valueE[env]+);    
      
valueE[EnvironmentProxy env]
  : ^(t=TUPLE_REF r=ID v=valueP[env.get(env.getFrameIndex($r.text))]) -> ^(TUPLE_REF NUMBER[Integer.toString(env.getFrameIndex($r.text))] $v)
  | (TUPLE_REF ID) => ^(t=TUPLE_REF r=ID) -> ^(TUPLE_REF NUMBER[Integer.toString(env.getFrameIndex($r.text))])
  | ^(t=TUPLE_REF r=NUMBER v=valueP[env.get(((StencilNumber) $r).getValue().intValue())])
  | (TUPLE_REF NUMBER) => ^(t=TUPLE_REF r=NUMBER)
  | .;
    catch[Exception e] {throw new RuntimeException(String.format("Error numeralizing \%1\$s.\n\%2\$s.", $t.toStringTree(), e.toString()));}
  
      
//TODO:Unify envProxy and TuplePrototype (will eliminated the valueE and valueNP variants, requires a way to get a prototype from a prototype...)
valueP[TuplePrototype p]
  : ^(t=TUPLE_REF r=ID) -> ^(TUPLE_REF NUMBER[Integer.toString(p.indexOf($r.text))])
  | ^(t=TUPLE_REF r=ID valueNP) -> ^(TUPLE_REF NUMBER[Integer.toString(p.indexOf($r.text))] valueNP)
  | ^(t=TUPLE_REF r=NUMBER valueNP) 
  | ^(t=TUPLE_REF r=NUMBER);
    catch[Exception e] {throw new RuntimeException("Error numeralizing " + $t.toStringTree());}

valueNP
  : ^(TUPLE_REF ID) {throw new RuntimeException("Numeralize can only handle one level names in nesting");}
  | ^(TUPLE_REF NUMBER valueNP)
  | ^(TUPLE_REF NUMBER);
