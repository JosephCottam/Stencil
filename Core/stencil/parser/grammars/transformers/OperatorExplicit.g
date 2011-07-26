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
  import static stencil.parser.ParserConstants.NAME_SEPARATOR;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER_TREE;
  import static stencil.parser.ParserConstants.MAP_FACET;
}

@members {
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

  public StencilTree downup(Object t) {
    downup(t, this, "highOrderArgs");          //Pull out higher-order arguments, make them explicit
    downup(t, this, "operatorApplication");    //Pull out the higher-order ops and make them explicit
    downup(t, this, "simpleArgs");    		   //Remove operator references from argument lists
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
       if (target.find(LIST_ARGS) == null
             || target.find(LIST_ARGS).findAll(OP_AS_ARG).size() == 0) { //No op args?  Simple cover
          if (target.getType() == OPERATOR_REFERENCE) {return name;}
           return simpleCover(target);
       } else {return highOrderCover(target);}                          //Has op args?  Complex cover
   }
      
   private String simpleCover(StencilTree target) {
      if (covered(target)) {return Freezer.multiName(target.find(OP_NAME,OPERATOR_BASE)).name();}   //No cover required, if already covered
      return extendOpDefs(target);
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
      return extendOpDefs(target);
  }
  
  /**Cover operator definition passed in the list of definitions for the program**/
  private String extendOpDefs(StencilTree target) {  
     StencilTree opName = target.find(OP_NAME, OPERATOR_BASE);
     MultiPartName name = Freezer.multiName(opName);
     String newName = Utilities.genSym(name.name());       //create a new name
     StencilTree program = target.getAncestor(PROGRAM);
     StencilTree operators = program.find(LIST_OPERATORS);
    
     Tree ref = (Tree) adaptor.create(OPERATOR_REFERENCE, newName);         //Cover operator
     Tree base = (Tree) adaptor.create(OPERATOR_BASE, "OPERATOR_BASE");
     adaptor.addChild(base, adaptor.create(ID, name.prefix()));
     adaptor.addChild(base, adaptor.create(ID, name.name()));
     adaptor.addChild(ref, base);
     adaptor.addChild(ref, adaptor.dupTree(target.find(SPECIALIZER)));
    
     adaptor.addChild(operators, ref);      //Add new operator to list
     return newName;
  } 
   
}

highOrderArgs
   : ^(OP_AS_ARG ^(on=OP_NAME space=. op=. facet=.) .) 
        -> ^(OP_AS_ARG ^(OP_NAME $space ID[cover($on)] $facet));

operatorApplication
   : ^(f=FUNCTION ^(on=OP_NAME space=. op=. facet=.) spec=. rest+=.*)  
         -> ^(FUNCTION ^(OP_NAME DEFAULT ID[cover($on)] $facet) {adaptor.dupTree(EMPTY_SPECIALIZER_TREE)} $rest*)
   | ^(OPERATOR_REFERENCE base=. spec=. args=.)
   		-> ^(OPERATOR_REFERENCE ^(OPERATOR_BASE {adaptor.dupTree(base.getChild(0))} ID[cover($base)]) $spec);
   
simpleArgs: ^(l=LIST_ARGS .*) -> {filterOpArgs($l)};
        