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
  import static stencil.parser.string.util.Utilities.isGenSymRoot;
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
  private Specializer getDefault(StencilTree spec) {
    try {
	   StencilTree f = spec.getAncestor(OP_AS_ARG, FUNCTION);
	   if (f != null) {return getOperatorDefault(f.find(OP_NAME));}

	   StencilTree g = spec.getAncestor(GUIDE);
       if (g != null) {return getGuideDefault(g.find(ID).getText());}
	     
       StencilTree c = spec.getAncestor(CANVAS);
	   if (c != null) {return Freezer.specializer(DEFAULT_CANVAS_SPECIALIZER);}

       StencilTree v = spec.getAncestor(VIEW);
       if (v != null) {return Freezer.specializer(DEFAULT_VIEW_SPECIALIZER);}

	     	     	     
	   StencilTree ref = spec.getAncestor(OPERATOR_REFERENCE);
	   if (ref != null) {return getOperatorDefault(ref.find(OPERATOR_BASE));}

       StencilTree layer = spec.getAncestor(LAYER);
       if (layer != null) {return Freezer.specializer(DEFAULT_LAYER_SPECIALIZER);}
    } catch (Exception e) {
	    return Freezer.specializer(EMPTY_SPECIALIZER_TREE);	//Empty because some contexts don't have defaults
    }
	throw new ProgramCompileException("Specializer encountered in unexpected context",spec.getParent());
  }
  
  /**Work with the graphics adapter to get the default
    * specializer for a given guide type.
    **/
  private Specializer getGuideDefault(String guideType) {
    Class clss = adapter.getGuideClass(guideType);
    Specializer defaultSpec;
    
    try {
      Field f = clss.getField("DEFAULT_SPECIALIZER");
      defaultSpec = ((Specializer) f.get(null));
    } catch (Exception e) {
      defaultSpec = Freezer.specializer(EMPTY_SPECIALIZER_TREE);
    }     
      
    assert defaultSpec != null;
    return defaultSpec;
  }

  /**Get the default guide for a named operator.*/
  public Specializer getOperatorDefault(StencilTree opRef) {
    MultiPartName name= Freezer.multiName(opRef);
    ModuleData md;
    
    try {
        Module m = modules.findModuleForOperator(name);
        md = m.getModuleData();
    } catch (Exception e) {
      throw new ProgramCompileException("Error getting module information for operator " + name.toString(), opRef, e);
    }
    
    try {
        Specializer spec = md.getDefaultSpecializer(name.name());
        assert spec != null;
        return  spec;
      } catch (Exception e) {
        throw new ProgramCompileException("Error finding default specializer for " + name.toString(), opRef, e);
      } 
  }
  
  /**Combine a given specializer with the appropriate default.*/
  private StencilTree blendWithDefault(StencilTree spec) {
    Specializer defaults = getDefault(spec);
    Specializer specializer;
    try {specializer= blend(Freezer.specializer(spec), defaults);}
    catch(ProgramCompileException e) {throw new ProgramCompileException(e.getMessage(), spec);}
    return toTree(specializer, spec);
  }
  
  private StencilTree toTree(Specializer specializer, StencilTree replacing) {
     StencilTree root = (StencilTree) adaptor.create(replacing.getToken());

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
  public static Specializer blend(Specializer updates, Specializer defaults) {
    assert updates != null;
    assert defaults != null;

    List<String> keys =new ArrayList();
    List<Object> values = new ArrayList();
    for (int i=0; i<updates.size(); i++) {
      if (isGenSymRoot(POSITIONAL_ARG, updates.prototype().get(i).name())) {
        if (defaults.size() < i) {throw new ProgramCompileException("Specializer with insufficient defaults for positional specializer arguments.");}
        keys.add(defaults.prototype().get(i).name());
      } else {
        keys.add(updates.prototype().get(i).name());        
      }
      values.add(updates.get(i));
    }

    
    for (String key: defaults.keySet()) {
      if (keys.contains(key)) {continue;}
      keys.add(key);
      values.add(defaults.get(key));
    }
    return new Specializer(keys.toArray(new String[keys.size()]), values.toArray());
  }
}

topdown
  : ^(s=SPECIALIZER DEFAULT) -> {toTree(getDefault($s), $s)}
  | ^(s=SPECIALIZER .*)      -> {blendWithDefault($s)}
  |   s=SPECIALIZER          -> {blendWithDefault($s)};
