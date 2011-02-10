tree grammar DefaultSpecializers;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
  superClass = TreeRewriteSequence;
}

@header{
 /** Make sure all elements with specializers have
  * all properties defined in their default specializer.	  
  **/
  package stencil.parser.string;

  import java.lang.reflect.Field;
    
  import stencil.parser.tree.*;
  import stencil.parser.tree.util.*;
  import stencil.module.*;
  import stencil.module.util.*;
  import stencil.adapters.Adapter;
  import stencil.interpreter.tree.Specializer;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER_TREE;
	import static stencil.parser.ParserConstants.DEFAULT_CANVAS_SPECIALIZER;
	import static stencil.parser.ParserConstants.DEFAULT_LAYER_SPECIALIZER;
	
}

@members{
  public static StencilTree apply (Tree t, ModuleCache modules, Adapter adapter, boolean blend) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules, adapter, blend);
  }

  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
     adapter = (Adapter) args[1];
     blend = (Boolean) args[2];
  }
  
  protected ModuleCache modules;
  protected Adapter adapter;  
  protected boolean blend;
  

  //Be careful of order as some things with specializers are nested inside other things with specializers (e.g. canvas: guide: function can occur)
  private StencilTree getDefault(StencilTree spec) {
    try {
	     StencilTree f = spec.getAncestor(FUNCTION, OP_AS_ARG);
	     if (f != null) {return (StencilTree) adaptor.dupTree(getOperatorDefault(f.getText()));}
	     
	     StencilTree g = spec.getAncestor(GUIDE);
       if (g != null) {return (StencilTree) adaptor.dupTree(getGuideDefault(g.find(ID).getText()));}
	     
       StencilTree c = spec.getAncestor(CANVAS_DEF);
	     if (c != null) {return (StencilTree) adaptor.dupTree(DEFAULT_CANVAS_SPECIALIZER);}
	     	     	     
	     StencilTree ref = spec.getAncestor(OPERATOR_REFERENCE);
	     if (ref != null) {return (StencilTree) adaptor.dupTree(getOperatorDefault(ref.find(OPERATOR_BASE).getText()));}

       StencilTree layer = spec.getAncestor(LAYER);
       if (layer != null) {return (StencilTree) adaptor.dupTree(DEFAULT_LAYER_SPECIALIZER);}
	     
    } catch (Exception e) {return (StencilTree) adaptor.dupTree(EMPTY_SPECIALIZER_TREE);}  //HACK: Is removing the default really the right thing?
	  throw new IllegalArgumentException("Specializer encountered in unexpected context: " + spec.getParent().toStringTree());
  }
  
  /**Work with the graphics adapter to get the default
    * specializer for a given guide type.
    **/
  private StencilTree getGuideDefault(String guideType) {
    Class clss = adapter.getGuideClass(guideType);
    StencilTree defaultSpec;
    
    try {
      Field f = clss.getField("DEFAULT_ARGUMENTS");
      defaultSpec = ((Specializer) f.get(null)).getSource();
    } catch (Exception e) {
      defaultSpec = (StencilTree) adaptor.dupTree(EMPTY_SPECIALIZER_TREE);
    }     
      
    assert defaultSpec != null;
    return  defaultSpec;
  }

  /**Get the default guide for a named operator.*/
  public StencilTree getOperatorDefault(String fullName) {
    MultiPartName name= new MultiPartName(fullName);
    ModuleData md;
    
    try {
        Module m = modules.findModuleForOperator(name.getPrefix(), name.getName());
        md = m.getModuleData();
    } catch (Exception e) {
      throw new RuntimeException("Error getting module information for operator " + name.toString(), e);
    }
    
    try {
        StencilTree source = md.getDefaultSpecializer(name.getName()).getSource();
        assert source != null;
        return  source;
      } catch (Exception e) {
        throw new RuntimeException("Error finding default specializer for " + name.toString(), e);
      } 
  }
  
  /**Combine a given specializer with the appropriate default.*/
  private StencilTree blendWithDefault(StencilTree spec) {
    Specializer specializer = Specializer.blend(spec, getDefault(spec));
    StencilTree root = (StencilTree) adaptor.create(spec.getToken());

    for (String key: specializer.keySet()) {
     Object value = specializer.get(key);
     StencilTree entry = (StencilTree) adaptor.create(MAP_ENTRY, key);
     Const valNode = (Const) adaptor.create(CONST, value == null? null:value.toString());
     valNode.setValue(value);
     
     adaptor.addChild(entry, valNode);
     adaptor.addChild(root, entry);
    }
    return root;
  }

}

topdown
  options{backtrack=true;}
  : ^(s=SPECIALIZER DEFAULT)          -> {getDefault(s)}
  |{blend}?  ^(s=SPECIALIZER .*)      -> {blendWithDefault(s)}
  |{blend}?  s=SPECIALIZER            -> {blendWithDefault(s)};
