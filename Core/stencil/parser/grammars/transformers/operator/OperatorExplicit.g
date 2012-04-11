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

  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.parser.string.util.Utilities;
  import stencil.parser.tree.*;
  import stencil.parser.string.util.TreeRewriteSequence;
  import static stencil.parser.ParserConstants.NAME_SEPARATOR;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER_TREE;
}

@members {
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

  public StencilTree downup(Object t) {
    downup(t, this, "highOrderArgs");          
    downup(t, this, "operatorApplication");    
    return (StencilTree) t;  
  }
  


   /**Does the name appear as an operator def/proxy/ref?*/
   private boolean covered(StencilTree function) {
	      StencilTree program = function.getAncestor(PROGRAM);
	      StencilTree operators = program.find(LIST_OPERATORS);
	      MultiPartName name = Freezer.multiName(function.find(OP_NAME,OPERATOR_BASE));
	      
	      for (StencilTree o: operators) {
	          if (o.getText().equals(name.name())) {return true;}
	      }
	      return false;
   }
   
   /**Create a cover reference for a given operator IF
    * there is not an operator ref with the given name.
    */
   private String cover(StencilTree target) {
       String name = target.getChild(1).getText();
       target = target.getParent();//Move up to the container so the spec can be searched/used 
       if (target.getType() == OPERATOR_REFERENCE) {return name;}
       if (covered(target)) {return Freezer.multiName(target.find(OP_NAME,OPERATOR_BASE)).name();}   //No cover required, if already covered
       return extendOpDefs(target);
   }
        
  /**Cover operator definition passed in the list of definitions for the program**/
  private String extendOpDefs(StencilTree target) {  
     StencilTree opName = target.find(OP_NAME, OPERATOR_BASE);
     MultiPartName name = Freezer.multiName(opName);
     String newName = Utilities.genSym(name.name());       //create a new name
     StencilTree program = target.getAncestor(PROGRAM);
     StencilTree operators = program.find(LIST_OPERATORS);
    
     Tree ref = (Tree) adaptor.create(OPERATOR_REFERENCE, target.getToken(), newName);         //Cover operator
     Tree base = (Tree) adaptor.create(OPERATOR_BASE,target.getToken(),  "OPERATOR_BASE");
     adaptor.addChild(base, adaptor.create(ID, target.getToken(), name.prefix()));
     adaptor.addChild(base, adaptor.create(ID, target.getToken(), name.name()));
     adaptor.addChild(ref, base);
     adaptor.addChild(ref, adaptor.dupTree(target.find(SPECIALIZER)));
    
     adaptor.addChild(operators, ref);      //Add new operator to list
     return newName;
  } 
   
}

//Pull out higher-order arguments, make them explicit
highOrderArgs
   : ^(OP_AS_ARG ^(on=OP_NAME space=. op=. facet=.) .) 
        -> ^(OP_AS_ARG ^(OP_NAME DEFAULT ID[$op.token, cover($on)] $facet));

operatorApplication
   : ^(f=FUNCTION ^(on=OP_NAME space=. op=. facet=.) spec=. rest+=.*)  
         -> ^(FUNCTION ^(OP_NAME DEFAULT ID[$op.token, cover($on)] $facet) $spec $rest*);