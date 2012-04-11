tree grammar OperatorExpandCompactForm;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  /**Takes operator references with YIELD statements and expands them into full operator definitions.**/
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
  import stencil.parser.string.util.TreeRewriteSequence;
}

@members{
  public static StencilTree apply (StencilTree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  
  
    
  private StencilTree resultsList(StencilTree yieldOut) {
        StencilTree root = (StencilTree) adaptor.create(TARGET_TUPLE, yieldOut.token);
        Iterable<StencilTree> children = yieldOut.findAllDescendants(ID);
        
        for (StencilTree c: children) {
            Object fd = adaptor.create(TUPLE_FIELD, c.token);
            adaptor.addChild(fd, adaptor.dupTree(c));
            adaptor.addChild(root, fd);
        }
        return root;
  }
}

topdown 
  : ^(or=OPERATOR_REFERENCE ^(YIELDS in=. out=.) ^(OPERATOR_BASE opNS=. opID=.) s=. args=.)
    -> 
      ^(OPERATOR[$or.text] ^(YIELDS $in $out)
            ^(RULES_PREFILTER)
            ^(RULES_OPERATOR
                ^(OPERATOR_RULE 
                    ^(LIST_PREDICATES ^(PREDICATE ALL))
                    ^(LIST_RULES 
                      ^(RULE
                        ^(RESULT {resultsList($out)})
                        ^(CALL_CHAIN
                            ^(FUNCTION 
                                ^(OP_NAME $opNS $opID DEFAULT_FACET)
                                $s
                                $args
                                DIRECT_YIELD[$opID.token]
                                ^(PACK DEFAULT))    
                            DEFINE))))));
     