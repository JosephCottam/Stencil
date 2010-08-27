tree grammar FrameTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Makes all tuple refs qualified by their frame offsets.
   *  References in the current frame are offset by [0], others
   *  must be reference back to an earlier frame.
   **/
   
  package stencil.parser.string;
  
  import stencil.module.*;
  import stencil.parser.string.util.EnvironmentProxy;  
  import stencil.parser.string.util.GlobalsTuple;
  import stencil.parser.tree.Program;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.ParseStencil;
  import static stencil.parser.string.util.EnvironmentProxy.extend;
  import static stencil.parser.ParserConstants.GLOBALS_FRAME;
  import static stencil.parser.string.util.EnvironmentProxy.initialEnv;
  
}

@members {
  public static Program apply (Tree t, ModuleCache modules) {
     return (Program) TreeRewriteSequence.apply(t, modules, new GlobalsTuple(((Program) t).getGlobals()));
  }
  
  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
     globals = (GlobalsTuple) args[1];
  }

  private ModuleCache modules;
  private GlobalsTuple globals;
  
}

//TODO: Try to get away from the env proxy...its hard to work with.  What pre-processing would make the default environment construction easier?  What would make handling globals easier? 
topdown
  : ^(p=PREDICATE value[initialEnv($p, modules)] op=. value[initialEnv($p, modules)])
  | ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)]);
	catch [EnvironmentProxy.FrameException fe] {
	 if (c != null) {throw new RuntimeException("Error framing: " + c.toStringTree(), fe);}
	 else if (p != null) {throw new RuntimeException("Error framing: " + p.toStringTree(), fe);}
	 else {throw new Error("Error in framing: No root.");}
  }
	
callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION . . ^(LIST value[env]*) y=. callTarget[extend(env, $y, $f, modules)])
  | ^(PACK value[env]*);
          
value[EnvironmentProxy env]
  options{backtrack=true;}
  : ^(TUPLE_REF n=ID v=.) 
      -> {env.isFrameRef($n.text)}? ^(TUPLE_REF $n $v)		//Already is a frame ref, no need to extend 
      -> ^(TUPLE_REF NUMBER[Integer.toString(env.frameRefFor($n.text))] ^(TUPLE_REF $n $v))
  | ^(TUPLE_REF NUMBER) -> ^(TUPLE_REF ID[env.getLabel()] ^(TUPLE_REF NUMBER))
  | ^(TUPLE_REF ALL) -> ^(TUPLE_REF ID[env.getLabel()])
  | ^(TUPLE_REF n=ID)  
  		-> {env.canFrame($n.text)}? ^(TUPLE_REF ID[env.frameNameFor($n.text)] ^(TUPLE_REF $n))
  	  -> {globals.getPrototype().contains($n.text)}? ^(TUPLE_REF ID[GLOBALS_FRAME] ^(TUPLE_REF $n))
  	  -> {env.frameNameFor($n.text)} //invoked to get a good exception out of it...
  | .;
