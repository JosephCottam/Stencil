tree grammar FrameTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Makes all tuple refs qualified by their frames.
   **  Rules are:
   **     *,_,LAST,etc are take to the most recent frame
   **     Constants are resolved to the constants frame
   **     Anything else whose first part is not a valid frame are set to the stream frame
   ** 
   ** In source code, if a frame reference is "bare", it is taken as "default value"; to get the whole frame, a * must have been applied.
   ** However, after this step a bare reference is a reference to the whole frame value
   **/
   
  package stencil.parser.string;
  
  import java.util.Arrays;

  import stencil.module.*;
  import stencil.parser.string.util.GlobalsTuple;
  import stencil.parser.tree.*;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.parser.string.util.TreeRewriteSequence;  
  import stencil.parser.ProgramCompileException;

  import static stencil.parser.ParserConstants.*;
  import static stencil.parser.string.util.EnvironmentUtil.*;

}

@members {
  private static final class FramingException extends ProgramCompileException {
      public FramingException(StencilTree at) {
         super("Could not find a frame for tuple reference '" + at.toStringTree() + "'",  at);}
  }
  
  public StencilTree downup(Object p) {
    StencilTree r;
    r = downup(p, this, "ensureFrames");
    r = downup(r, this, "removeAlls");
    r = downup(r, this, "generalizeStreamFrames");
    return r;
  }  

  private GlobalsTuple globals;
  
  public static StencilTree apply (StencilTree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, new GlobalsTuple(t.find(LIST_GLOBALS)));
  }
  
  //Called by the instance apply function
  protected void setup(Object... args) {
     globals = (GlobalsTuple) args[0];
  }
  
  //If it has been determined that a frame name is required....
  public String getFrame(StencilTree toPrefix) {
     String name = toPrefix.getText();
     TuplePrototype stream = streamFor(toPrefix);
     if (stream.indexOf(name)>=0) {return STREAM_FRAME;}
     if (globals.prototype().indexOf(name) >= 0) {return GLOBALS_FRAME;}
     throw new FramingException(toPrefix);
  }
  
  public List<String> initialFrames(StencilTree target) {
      List<String> frames = new ArrayList<String>(Arrays.asList(GLOBALS_FRAME, STREAM_FRAME, PREFILTER_FRAME, LOCAL_FRAME));
      StencilTree consumes = target.getAncestor(CONSUMES);
      if (consumes != null) {extend(frames, consumes);}
      return frames;
  }
  
  public static List<String> extend(List<String> known, StencilTree nameSource) {
      known.add(0, nameSource.getText());
      return known;
  }
  
  public static String mostRecent(List<String> frames) {return frames.get(0);}
  public StencilTree trimAll(StencilTree tr) {
     tr = (StencilTree) adaptor.dupTree(tr);
     int lastChild = tr.getChildCount()-1;
     if (tr.getChild(lastChild).is(ALL)) {
        adaptor.deleteChild(tr, lastChild);
     }
     return tr;
  }
  
  private String deStream(StencilTree maybeFrame) {
      String maybeFrameName = maybeFrame.getText();
      if (maybeFrameName.equals(STREAM_FRAME)) {return maybeFrameName;} //No work to be done

      StencilTree consumes = maybeFrame.getAncestor(CONSUMES);
      if (consumes == null) {return maybeFrameName;}  //No consumes->no stream!
      
      StencilTree cursor = maybeFrame.getAncestor(FUNCTION);
      while(cursor != null) {
         if (cursor.find(DIRECT_YIELD) == null || cursor.find(DIRECT_YIELD).getText().equals(maybeFrameName)) {return maybeFrameName;}
         cursor = cursor.getAncestor(FUNCTION);
      }
      
      
      
      if (consumes.getText().equals(maybeFrameName)) {return STREAM_FRAME;}  //If the stream matched, replace it
      return maybeFrameName;
  }

}

//Make sure all tuple refs are composed of frame references
ensureFrames
  : ^(p=PREDICATE value[initialFrames($p)] op=. value[initialFrames($p)])
  | ^(c=CALL_CHAIN callTarget[initialFrames($c)] .?);
	
callTarget[List<String> frames]
  : ^(f=FUNCTION n=. s=. ^(LIST_ARGS value[frames]*) y=. callTarget[extend(frames, $y)])
  | ^(p=PACK value[frames]*);
          
value[List<String> frames]
  : ^(TUPLE_REF n=ID v+=.+)
      -> {frames.contains($n.text)}? ^(TUPLE_REF $n $v*)        		//Already is a frame ref, no need to extend 
      -> ^(TUPLE_REF ID[getFrame($n)] $n $v*)
  | ^(TUPLE_REF NUMBER) -> ^(TUPLE_REF ID[mostRecent(frames)] NUMBER) 
  | ^(TUPLE_REF ALL) -> ^(TUPLE_REF ID[mostRecent(frames)])
  | ^(TUPLE_REF n=ID) 
      -> {frames.contains($n.text)}? ^(TUPLE_REF $n NUMBER["0"])        //By default, frame refs are to the default element of the frame; must be <frame>.* to be whole frame 
      -> {globals.prototype().contains($n.text)}? ^(TUPLE_REF ID[GLOBALS_FRAME] $n)
      -> ^(TUPLE_REF ID[getFrame($n)] $n)
  | (STRING | NUMBER | NULL | ^(OP_AS_ARG .));



//Remove trailing "all" statements; ref of just frame name is now sufficient to return the tuple 
removeAlls:  ^(tr=TUPLE_REF parts+=.*) -> {trimAll($tr)};

//Replace all references to the stream that use the stream name itself.  
//Instead use the context independent "stream frame" name
generalizeStreamFrames : ^(TUPLE_REF f=. parts+=.*) -> ^(TUPLE_REF ID[deStream($f)] $parts*);    