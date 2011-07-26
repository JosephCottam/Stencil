tree grammar OperatorToOpTemplate;
options {
    tokenVocab = Stencil;
    ASTLabelType = StencilTree; 
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
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}

   private String newName;
   
   /** @arg ops Operator parts list
    * @arg keep True -- return list of templates; False -- return list of non-templates
    */
   private StencilTree siftTemplates(StencilTree ops, int type, boolean keepTemplates) {
      StencilTree list = (StencilTree) adaptor.create(type, StencilTree.typeName(type)); 
      for (StencilTree t: ops) {
         if (keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.find(OPERATOR_TEMPLATE)));
         } else if (!keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.find(OPERATOR_REFERENCE)));
         } else if (!keepTemplates && t.getType() != OPERATOR) {
              adaptor.addChild(list, adaptor.dupTree(t));
         }
      }
      return list;   
   }

   protected StencilTree templates(StencilTree source)     {return siftTemplates(source, LIST_TEMPLATES, true);}
   protected StencilTree nonTemplates(StencilTree source)  {return siftTemplates(source, LIST_OPERATORS, false);}
   
}

topdown:  ^(o=OPERATOR rest+=.*) {newName=genSym($o.text);}
  ->  ^(OPERATOR ^(OPERATOR_REFERENCE[$o.text] ^(OPERATOR_BASE DEFAULT ID[newName] DEFAULT) SPECIALIZER)  //Specializer not required in op references   
                 ^(OPERATOR_TEMPLATE[newName] $rest*));
                 
bottomup:
    ^(PROGRAM i=. g=. s=. o=. cd=. vd=. sd=. l=. ops=. j=.)
        -> ^(PROGRAM $i $g $s $o $cd $vd $sd $l {nonTemplates(ops)} $j {templates(ops)});
                 
