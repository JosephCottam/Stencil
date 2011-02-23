tree grammar EventSinglelyMutates;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
  /** Observe the mutative operations that an event may trigger.
      Check that each mutative operation affects a different state OR 
      that the mutative operations are explicitly sequenced by the programmer.
  **/
  package stencil.parser.string.validators;
  
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.ValidationException;
  import stencil.parser.string.TreeFilterSequence;
  import java.util.ArrayList;
  import java.util.HashSet;
  import java.util.Set;
}

@members {
  public static final class MultimutateException extends ValidationException {
      public MultimutateException(String event, String state) {super("Event " + event + " can cause multiple mutations of " + state + ".");}
  }

  private static final Set<Object> events = new HashSet();
  private static final Set<Object> mutations = new HashSet();
  private static Object eventKey;

  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
    
  public void downup(Object p) {
    events.clear();
    downup(p, this, "gatherEvents");
  
    List events = new ArrayList(); //Gather possible events
    for (Object event: events) {
       mutations.clear();
       downup(p, this, "mutations");
       //Gather mutations, throw MutlimutateException if an unsequenced duplicate is found
    }
    
      
  }
}

gatherEvents: ^(s=STREAM_DEF .*) {events.add($s.getText());};
mutations: FUNCTION;
  