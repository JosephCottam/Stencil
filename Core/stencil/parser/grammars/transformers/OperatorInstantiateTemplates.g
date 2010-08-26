tree grammar OperatorInstantiateTemplates;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  /**Takes references to operator templates and instantiates the corresponding template.**/
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
  import stencil.module.*;
  import stencil.module.operator.*;
  import stencil.module.util.OperatorData;
}

@members{
  public static Program apply (Tree t, ModuleCache modules) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass(), modules);
  }
  
  protected void setup(Object... args) {modules = (ModuleCache) args[0];}

  private ModuleCache modules;

  private StencilTree instantiate(OperatorReference opRef) {
    OperatorTemplate t = findTemplate(opRef);
    Specializer spec = (Specializer) opRef.getFirstChildWithType(SPECIALIZER);
    String name = opRef.getName();
    return t.instantiate(name, spec, adaptor);
  }
  
  private OperatorTemplate findTemplate(CommonTree opRef) {
    OperatorBase base = (OperatorBase) opRef.getFirstChildWithType(OPERATOR_BASE);
    Program program = (Program) opRef.getAncestor(PROGRAM);
    String name = base.getName();  

    for (OperatorTemplate t:program.getOperatorTemplates()) {
      if (name.equals(t.getName())) {return t;} 
    }
    return null;
  }
}

topdown 
  : ^(o=OPERATOR_REFERENCE base=. spec=.) 
     {findTemplate($o) != null}? -> {instantiate((OperatorReference) o)};