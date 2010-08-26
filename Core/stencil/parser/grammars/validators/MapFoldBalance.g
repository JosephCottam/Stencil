tree grammar MapFoldBalance;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
/* In some contexts, every map must be eventually followed
 * by a fold for the Stencil semantics to be clear (default setting, for example).
 * This validates that map and fold passes are paired properly
 * in such contexts. 
 *
 */

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  import stencil.parser.ParseStencil;
  import stencil.parser.string.TreeFilterSequence;
}

@members {
  public static void apply (Tree t) {
     apply(t, new Object(){}.getClass().getEnclosingClass());
  }
}

topdown: (layerDefault | prefilter | view | canvas | local);

layerDefault: ^(LAYER . ^(LIST balancedRule*) .*);

prefilter: ^(CONSUMES . ^(LIST balancedRule*) .*);
local:     ^(CONSUMES . . ^(LIST balancedRule*) .*);
view:      ^(CONSUMES . . . . ^(LIST balancedRule*) .*);
canvas:    ^(CONSUMES . . . . . ^(LIST balancedRule*) .*);


balancedRule: ^(RULE . callChain .);
callChain
   @after{if ($r.r != 0) {throw new ValidationException("Unbalanced map/fold in a context that requires balance.");}}
   : ^(CALL_CHAIN r=chain[0] .);
   
   
chain[int d] returns [int r]
   @init {if (d<0) {throw new ValidationException("Improperly nested map/fold found while checking for balance.");}}
   : ^(PACK .*)  {$r=d;}
   | ^(FUNCTION . . DIRECT_YIELD c=chain[d]) {$r=$c.r;}
   | ^(FUNCTION . . MAP c=chain[d+1]) {$r=$c.r;}
   | ^(FUNCTION . . FOLD c=chain[d-1]) {$r=$c.r;};