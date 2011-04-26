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
  import static stencil.parser.ParserConstants.MAP_FACET;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

  public StencilTree downup(Object t) {
    downup(t, this, "highOrderArgs");          //Pull out higher-order arguments, make them explicit
    downup(t, this, "operatorApplication");    //Pull out the higher-order ops and make them explicit
    downup(t, this, "simpleArgs");             //Remove operator references from argument lists
    return (StencilTree) t;  
  }
  
  private StencilTree filterOpArgs(StencilTree args) {
     assert args.getType() == LIST_ARGS;
     
     StencilTree newArgs = (StencilTree) adaptor.create(LIST_ARGS, "LIST_ARGS");
     for (StencilTree arg: args) {
        if (arg.getType() != OP_AS_ARG) {adaptor.addChild(newArgs, adaptor.dupTree(arg));}
     }
     return newArgs;
  }


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
   
   private List<StencilTree> removeOpAsArgs(List<StencilTree> args) {
    List<StencilTree> newArgs = new ArrayList<StencilTree>(args.size());
    for (StencilTree arg: args) {
       if (arg.getType() != OP_AS_ARG) {newArgs.add(arg);}
    }
    return newArgs;
   
   }

   /**Create a cover reference for a given operator IF
    * there is not an operator ref with the given name.
    */
   private String cover(StencilTree target) {
       if (target.find(LIST_ARGS) == null || target.find(LIST_ARGS).findAll(OP_AS_ARG).size() == 0) { //No op args?  Simple cover
         return simpleCover(target);
       } else {return highOrderCover(target);}                             //Has op args?  Complex cover
   }
      
   private String simpleCover(StencilTree target) {
      if (covered(target)) {return target.getText();}   //No cover required, if already covered
      MultiPartName name = new MultiPartName(target.getText()); 
      String newName = extendOpDefs(target);
      newName = newName +  (name.hasFacet() ? NAME_SEPARATOR + name.getFacet() : "");
      
 	    return newName;
  }
  
  /**A high order cover goes like:
   *  ... -> Map[...](@Op,...) -> ... 
   *  becomes
   *  ... -> Map#Reform1(*) -> ...
   *   operator Map#Reform1 : Map[..., op1:@Reform]
  **/
  private String highOrderCover(StencilTree target) {
      //Extract high-order args into specializer
      StencilTree spec = target.find(SPECIALIZER);
      List<StencilTree> ops = target.find(LIST_ARGS).findAll(OP_AS_ARG);

      assert ops.size() > 0;
      assert spec != null;
  
      for (int i=0; i<ops.size(); i++) {
         Object key = adaptor.create(MAP_ENTRY, "Op" + i);
         adaptor.addChild(key, adaptor.dupTree(ops.get(i)));
         adaptor.addChild(spec, key); 
      }
      
      //Cover as normal (will always require cover)
      return extendOpDefs(target) + NAME_SEPARATOR + MAP_FACET; //TODO: Should this be MAP_FACET??? Or QUERY_FACET??? or WHAT?????
  }
  
  /**Cover operator definition passed in the list of definitions for the program**/
  private String extendOpDefs(StencilTree target) {
     MultiPartName name = new MultiPartName(target.getText());
  
     String newName = genSym(name.getName());       //create a new name
     StencilTree program = target.getAncestor(PROGRAM);
     StencilTree operators = program.find(LIST_OPERATORS);
    
     Tree ref = (Tree) adaptor.create(OPERATOR_REFERENCE, newName);         //Cover operator
     adaptor.addChild(ref, adaptor.create(OPERATOR_BASE, name.prefixedName()));
     adaptor.addChild(ref, adaptor.dupTree(target.find(SPECIALIZER)));
    
     adaptor.addChild(operators, ref);      //Add new operator to list
     return newName;
  } 
   
}

highOrderArgs
   : ^(a=OP_AS_ARG .) -> OP_AS_ARG[cover($a)];

operatorApplication
   : ^(f=FUNCTION spec=. rest+=.*)  -> ^(FUNCTION[cover($f)] SPECIALIZER $rest*)
   | ^(OPERATOR_REFERENCE highOrderBase) -> ^(OPERATOR_REFERENCE highOrderBase SPECIALIZER);
   
highOrderBase
   : ^(b=OPERATOR_BASE spec=. op=.) -> OPERATOR_BASE[cover($b)];

simpleArgs: ^(l=LIST_ARGS .*) -> {filterOpArgs($l)};
        