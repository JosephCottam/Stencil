tree grammar OperatorToOpTemplate;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
/**Convert operator definition to template/reference pairs. */

  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.string.util.Utilities.genSym;
}

@members{
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }

   private String newName;
   
   /** @arg ops Operator parts list
    * @arg keep True -- return list of templates; False -- return list of non-templates
    */
   private StencilTree siftTemplates(List<StencilTree> ops, String label, boolean keepTemplates) {
      StencilTree list = (StencilTree) adaptor.create(LIST, label); 
      for (StencilTree t: ops) {
         if (keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.getFirstChildWithType(OPERATOR_TEMPLATE)));
         } else if (!keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.getFirstChildWithType(OPERATOR_REFERENCE)));
         } else if (!keepTemplates && t.getType() != OPERATOR) {
         	  adaptor.addChild(list, adaptor.dupTree(t));
         }
      }
      return list;   
   }

   protected StencilTree templates(CommonTree source)     {return siftTemplates((List<StencilTree>) source, "Operator Templates", true);}
   protected StencilTree nonTemplates(CommonTree source)  {return siftTemplates((List<StencilTree>) source, "Operators", false);}
   
}

topdown:  ^(o=OPERATOR rest+=.*) {newName=genSym($o.text);}
  ->  ^(OPERATOR ^(OPERATOR_REFERENCE[$o.text] OPERATOR_BASE[newName] ^(SPECIALIZER DEFAULT))
                 ^(OPERATOR_TEMPLATE[newName] $rest*));
                 
bottomup:
    ^(PROGRAM i=. g=. s=. o=. cl=. sd=. l=. ops=. p=.) 
        -> ^(PROGRAM $i $g $s $o $cl $sd $l {nonTemplates(ops)} $p {templates(ops)});
                 
