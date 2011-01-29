tree grammar OperatorExplicit;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;
	superClass = TreeRewriteSequence;	
  output = AST;
	filter = true;
}

@header {
/**Make all anonymous operators explicit.  Anonymous operators are
 * those used in call chains that do not exist as references.
 *
 * Since all synthetic operator definitions are converted into
 * template/reference pairs, this preserves single-instance semantics
 * for synthetic operators. 
 */

  package stencil.parser.string;

  import stencil.parser.tree.util.*;
  import stencil.parser.tree.*;
  import static stencil.parser.string.util.Utilities.*;
  import static stencil.parser.ParserConstants.NAME_SEPARATOR;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   /**Does the name appear as an operator def/proxy/ref?*/
   private boolean covered(StencilTree function) {
	      StencilTree program = function.getAncestor(PROGRAM);
	      StencilTree operators = program.find(LIST_OPERATORS);
	      MultiPartName name = new MultiPartName(function.getText());
	      
	      for (StencilTree o: operators) {
	          if (o.getText().equals(name.getName())) {return true;}
	      }
	      return false;
   }

   /**Create a cover reference for a given operator IF
    * there is not an operator ref with the given name.
    */
   private String cover(StencilTree target) {
      MultiPartName name = new MultiPartName(target.getText());
      if (covered(target)) {return target.getText() ;} 
	
	  String newName = genSym(name.getName());    	  //create a new name
	  StencilTree program = target.getAncestor(PROGRAM);
	  StencilTree operators = program.find(LIST_OPERATORS);
	  
	  Tree ref = (Tree) adaptor.create(OPERATOR_REFERENCE, newName);     	  //Cover operator
	  adaptor.addChild(ref, adaptor.create(OPERATOR_BASE, name.prefixedName()));
	  adaptor.addChild(ref, adaptor.dupTree(target.find(SPECIALIZER)));
	  
	  adaptor.addChild(operators, ref);  	  //Add new operator to list
	  
	  return newName + NAME_SEPARATOR + name.getFacet();
   } 
   
}

topdown
   : ^(f=FUNCTION s=. rest+=.*)  -> ^(FUNCTION[cover($f)] {adaptor.dupTree(EMPTY_SPECIALIZER)} $rest*);
