tree grammar FrameTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Makes all tuple refs qualified by their frame offsets.
   ** 
   ** Since frame references return values, framing also removes ALL from tuple references by truncating them.  
   **
   **/
   
  package stencil.parser.string;
  
  import stencil.module.*;
  import stencil.parser.string.util.EnvironmentProxy;  
  import stencil.parser.string.util.GlobalsTuple;
  import stencil.parser.tree.*;
  import static stencil.parser.string.util.EnvironmentProxy.extend;
  import static stencil.parser.ParserConstants.GLOBALS_FRAME;
  import static stencil.parser.string.util.EnvironmentProxy.initialEnv;
  
}

@members {
  private static final class FramingException extends RuntimeException {
      public FramingException(Tree at, EnvironmentProxy.FrameException fe) {
        super("Error Framing" + at.toStringTree() + "\n" + fe.getMessage(), fe.getCause());}
  }

  private static boolean ignoreErrors;
  public static StencilTree apply (StencilTree t, ModuleCache modules, boolean ignoreErrors) {
     FrameTupleRefs.ignoreErrors= ignoreErrors;
     return (StencilTree) TreeRewriteSequence.apply(t, modules, new GlobalsTuple(t.find(LIST_GLOBALS)));
  }
  
  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
     globals = (GlobalsTuple) args[1];
  }

  private ModuleCache modules;
  private GlobalsTuple globals;
}

topdown
  : ^(p=PREDICATE value[initialEnv($p, modules)] op=. value[initialEnv($p, modules)])
  | ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)] .?);
	catch [EnvironmentProxy.FrameException fe] {
	 if (c != null) {throw new FramingException(c, fe);}
     else if (p != null) {throw new FramingException(p, fe);}
	 else {throw new Error("Error in framing: No root.");}
  }
  catch [RuntimeException ex] {
     if (!ignoreErrors) {throw ex;}
  }
	
callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION .? . ^(LIST_ARGS value[env]*) y=. callTarget[extend(env, $y, $f, modules)])
  | ^(p=PACK value[env]*);
          
value[EnvironmentProxy env]
  options{backtrack=true;}
  : ^(TUPLE_REF n=ID v+=.+)
      -> {env.isFrameRef($n.text)}? ^(TUPLE_REF $n $v*)		//Already is a frame ref, no need to extend 
      -> ^(TUPLE_REF NUMBER[Integer.toString(env.frameRefFor($n.text))] $n $v*)
  | ^(TUPLE_REF NUMBER) -> ^(TUPLE_REF ID[env.getLabel()] NUMBER) 
  | ^(TUPLE_REF ALL) -> ^(TUPLE_REF ID[env.getLabel()])
  | ^(TUPLE_REF n=ID) 
      -> {env.isFrameRef($n.text)}? ^(TUPLE_REF $n)
      -> {env.canFrame($n.text)}? ^(TUPLE_REF ID[env.frameNameFor($n.text)] $n)
  	  -> {globals.getPrototype().contains($n.text)}? ^(TUPLE_REF ID[GLOBALS_FRAME] $n)
  	  -> {env.frameNameFor($n.text)} //invoked to get a good exception out of it...
  | (STRING | NUMBER);
