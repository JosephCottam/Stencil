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
    
  import stencil.interpreter.tree.Freezer;
  import stencil.parser.tree.*;
  import stencil.module.*;
  import stencil.module.util.*;
  import stencil.adapters.Adapter;
  import stencil.interpreter.tree.Specializer;
  import stencil.interpreter.tree.MultiPartName;
  import stencil.parser.ProgramCompileException;
  import stencil.tuple.Tuple;
  import stencil.tuple.Tuples;
  import stencil.tuple.prototype.TupleFieldDef;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER_TREE;
  import static stencil.parser.ParserConstants.DEFAULT_CANVAS_SPECIALIZER;
  import static stencil.parser.ParserConstants.DEFAULT_VIEW_SPECIALIZER;
  import static stencil.parser.ParserConstants.DEFAULT_LAYER_SPECIALIZER;
  import static stencil.parser.ParserConstants.POSITIONAL_ARG;
}

@members{
  public static StencilTree apply (Tree t, ModuleCache modules, Adapter adapter) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules, adapter);
  }

  protected void setup(Object... args) {
     modules = (ModuleCache) args[0];
     adapter = (Adapter) args[1];
  }
  
  protected ModuleCache modules;
  protected Adapter adapter;  
  

  //Be careful of order as some things with specializers are nested inside other things with specializers (e.g. canvas: guide: function can occur)
  private StencilTree getDefault(StencilTree spec) {
    try {
	   StencilTree f = spec.getAncestor(FUNCTION, OP_AS_ARG);
	   if (f != null) {return (StencilTree) adaptor.dupTree(getOperatorDefault(f.find(OP_NAME)));}
	     
	   StencilTree g = spec.getAncestor(GUIDE);
       if (g != null) {return (StencilTree) adaptor.dupTree(getGuideDefault(g.find(ID).getText()));}
	     
       StencilTree c = spec.getAncestor(CANVAS);
	   if (c != null) {return (StencilTree) adaptor.dupTree(DEFAULT_CANVAS_SPECIALIZER);}

       StencilTree v = spec.getAncestor(VIEW);
       if (v != null) {return (StencilTree) adaptor.dupTree(DEFAULT_VIEW_SPECIALIZER);}

	     	     	     
	   StencilTree ref = spec.getAncestor(OPERATOR_REFERENCE);
	   if (ref != null) {return (StencilTree) adaptor.dupTree(getOperatorDefault(ref.find(OPERATOR_BASE)));}

       StencilTree layer = spec.getAncestor(LAYER);
       if (layer != null) {return (StencilTree) adaptor.dupTree(DEFAULT_LAYER_SPECIALIZER);}
	     
    } catch (Exception e) {return (StencilTree) adaptor.dupTree(EMPTY_SPECIALIZER_TREE);}  //HACK: Is removing the default really the right thing?
	  throw new ProgramCompileException("Specializer encountered in unexpected context: " + spec.getParent().getToken());
  }
  
  /**Work with the graphics adapter to get the default
    * specializer for a given guide type.
    **/
  private StencilTree getGuideDefault(String guideType) {
    Class clss = adapter.getGuideClass(guideType);
    StencilTree defaultSpec;
    
    try {
      Field f = clss.getField("DEFAULT_SPECIALIZER");
      defaultSpec = ((Specializer) f.get(null)).getSource();
    } catch (Exception e) {
      defaultSpec = (StencilTree) adaptor.dupTree(EMPTY_SPECIALIZER_TREE);
    }     
      
    assert defaultSpec != null;
    return  defaultSpec;
  }

  /**Get the default guide for a named operator.*/
  public StencilTree getOperatorDefault(StencilTree opRef) {
    MultiPartName name= Freezer.multiName(opRef);
    ModuleData md;
    
    try {
        Module m = modules.findModuleForOperator(name);
        md = m.getModuleData();
    } catch (Exception e) {
      throw new ProgramCompileException("Error getting module information for operator " + name.toString(), opRef, e);
    }
    
    try {
        StencilTree source = md.getDefaultSpecializer(name.name()).getSource();
        assert source != null;
        return  source;
      } catch (Exception e) {
        throw new RuntimeException("Error finding default specializer for " + name.toString(), e);
      } 
  }
  
  /**Combine a given specializer with the appropriate default.*/
  private StencilTree blendWithDefault(StencilTree spec) {
    Specializer specializer = blend(spec, getDefault(spec));
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

  /**Blend the update with the defaults.  
   * The update values take precedence over the default values.*/
  public static Specializer blend(StencilTree updates, StencilTree defaults) {
    assert updates.getType() == SPECIALIZER;
    assert defaults.getType() == SPECIALIZER;
  
    Specializer updatez = Freezer.specializer(updates);
    Specializer defaultz = Freezer.specializer(defaults);
    
    List<String> keys =new ArrayList();
    List<Object> values = new ArrayList();
    for (int i=0; i<updatez.size(); i++) {
      if (updatez.prototype().get(i).name().equals(POSITIONAL_ARG)) {
        keys.add(defaultz.prototype().get(i).name());
      } else {
        keys.add(updatez.prototype().get(i).name());        
      }
      values.add(updatez.get(i));
    }

    
    for (String key: defaultz.keySet()) {
      if (keys.contains(key)) {continue;}
      keys.add(key);
      values.add(defaultz.get(key));
    }
    return new Specializer(keys.toArray(new String[keys.size()]), values.toArray(), updates);
  }
  

}

topdown
  : ^(s=SPECIALIZER DEFAULT)          -> {getDefault($s)}
  | ^(s=SPECIALIZER .*)      -> {blendWithDefault($s)}
  |   s=SPECIALIZER            -> {blendWithDefault($s)};
