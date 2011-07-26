tree grammar OperatorInstantiateTemplates;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  /**Takes references to operator templates and instantiates the corresponding template.**/
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
  import stencil.module.*;
  import stencil.interpreter.tree.Freezer;
  import stencil.interpreter.tree.MultiPartName;
}

@members{
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
  
  private Object instantiate(StencilTree opRef) {
    StencilTree t = findTemplate(opRef);
    
    Object op = adaptor.create(OPERATOR, opRef.getText());
    for (int i=0; i<t.getChildCount(); i++) {
      adaptor.addChild(op, adaptor.dupTree(t.getChild(i)));
    }
    
    return op;    
  }
  
  private StencilTree findTemplate(StencilTree opRef) {
    StencilTree base = opRef.find(OPERATOR_BASE);
    StencilTree program = opRef.getAncestor(PROGRAM);
    
    MultiPartName name = Freezer.multiName(base);
    if (name.prefixed()) {return null;}

    for (StencilTree t:program.find(LIST_TEMPLATES)) {
      if (name.name().equals(t.getText())) {return t;} 
    }
    return null;
  }
  
}

topdown 
  : ^(o=OPERATOR_REFERENCE .*) 
     {findTemplate($o) != null}? -> {instantiate(o)};
     
//Remove templates on the way out (they are no longer needed)     
bottomup
  : ^(p=PROGRAM .*)
    {adaptor.deleteChild(p, p.find(LIST_TEMPLATES).getChildIndex());};
     