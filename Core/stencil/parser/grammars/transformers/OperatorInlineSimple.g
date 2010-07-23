tree grammar OperatorInlineSimple;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
  superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header {
	/**Moves synthetic operators that only have one branch (the ALL branch)
	 * up to their call site.  Requires that the OperatorExplicit has already
	 * been run to ensure that state sharing is still correct.
	 **/

	package stencil.parser.string;

	import stencil.parser.tree.*;
}

@members {	
   private final Map<String, StencilTree> trivials = new HashMap();

  public Object transform(Object t) {
    t = downup(t, this, "search");
    t = downup(t, this, "replace");
    return t;
  }   
   private Tree replaceDef(String refName, StencilTree simpleOpRef) {
      Operator op = trivials.get(refName);
      StencilTree core = (StencilTree) adaptor.dupTree(getCoreCall(op));
      StencilTree tail = findTail(core);
      StencilTree target = (StencilTree) simpleOpRef.getFirstChildWithType(FUNCTION);
      if (target == null) {target = (StencilTree) simpleOpRef.getFirstChildWithType(PACK);}
      
      TODO: INSERT "Rename" ops before and after core based on input/output prototypes, pack and original call
      
      adaptor.setChild(splice, target.getChildIndex(), adaptor.dupTree(target));
      return splice;
   }
   
   public StencilTree findTail(StencilTree head) {
     while (head != null) {
        Tree tail = head.getFirstChildWithType(PACK);
        if (tail == null) {tail = head.getFirstChildWithType(FUNCTION);}
        else {return (StencilTree) tail;}
     }
     throw new Error("Could not find splice tail when required...");
   }
      
   public String getName(Object t) {
      return ((Tree) t).getText().substring(0, ((Tree) t).getText().indexOf("."));
   }
   
   public StencilTree getCoreCall(StencilTree operator) {
      return (StencilTree) operator.getChild(2).getChil(0).getChild(1).getChild(0).getChild(1).getChild(0);
   }
   
   
}

//Identify operators that only have one branch
search: ^(OPERATOR . . rules);
rules: ^(LIST ^(OPERATOR_RULE ^(LIST predicate) .*));
predicate: ^(PREDICATE ^(RULE . ^(CALL_CHAIN f=.)))
  {if ($f.getText().startsWith("#TrivialTrue")) {
     Tree op = f.getAncestor(OPERATOR);
     trivials.put(op.getText(), op);
  }};


replace  
  : ^(CALL_CHAIN ^(f=FUNCTION spec=. args=. pass=. target=replace))
      {trivials.containsKey(getName($f))}? ->
      ^(CALL_CHAIN {replaceDef(getName($f), (StencilTree) $f)})
  | ^(f1=FUNCTION spec1=. args1=. pass1=. ^(f=FUNCTION spec=. args=. pass=. target=replace))
      {trivials.containsKey(getName($f))}? ->
      ^(FUNCTION $spec1 $args1 $pass1 {replaceDef(getName($f), (StencilTree) $f)})
  | ^(PACK .*);
