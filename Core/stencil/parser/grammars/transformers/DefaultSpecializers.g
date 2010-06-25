tree grammar DefaultSpecializers;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
 /** Make sure all elements with specializers have
  * all properties defined in their default specializer.	  
  **/
  package stencil.parser.string;

  import java.lang.reflect.Field;
    
  import stencil.parser.ParseStencil;
  import stencil.parser.tree.*;
  import stencil.parser.tree.util.*;
  import stencil.module.*;
  import stencil.module.util.*;
  import stencil.adapters.Adapter;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
	
}

@members{
  protected ModuleCache modules;
  protected Adapter adapter;
  
  public boolean BLEND = true;
    
  public DefaultSpecializers(TreeNodeStream input, ModuleCache modules, Adapter adapter) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    assert adapter != null : "Adaptor must not be null.";
    this.modules = modules;
    this.adapter = adapter;    
  }


  //Be careful of order as some things with specializers are nested inside other things with specializers (e.g. canvas: guide: function can occur)
  private Specializer getDefault(Specializer spec) {
    try {
	     Function f = (Function) spec.getAncestor(FUNCTION);
	     if (f != null) {return  getOperatorDefault(f.getName());}
	     
	     Guide g = (Guide) spec.getAncestor(GUIDE);
       if (g != null) {return getGuideDefault(g.getGuideType());}
	     
	     CanvasDef c = (CanvasDef) spec.getAncestor(CANVAS_DEF);
	     if (c != null) {return ParseStencil.parseSpecializer(CanvasDef.DEFAULT_SPECIALIZER);}
	     	     
	     Import i = (Import) spec.getAncestor(IMPORT);
	     if (i != null) {return (Specializer) adaptor.dupTree(EMPTY_SPECIALIZER);} 
	     
	     OperatorReference ref = (OperatorReference) spec.getAncestor(OPERATOR_REFERENCE);
	     if (ref != null) {return getOperatorDefault(ref.getBase().getName());}
	     
    } catch (Exception e) {return (Specializer) adaptor.dupTree(spec);}
	  throw new IllegalArgumentException("Specializer encountered in unexpected context: " + spec.getParent().toStringTree());
  }
  
  /**Work with the graphics adapter to get the default
    * specializer for a given guide type.
    **/
  private Specializer getGuideDefault(String guideType) {
    Class clss = adapter.getGuideClass(guideType);
    Specializer defaultSpec;
    
    try {
      Field f = clss.getField("DEFAULT_ARGUMENTS");
      defaultSpec = (Specializer) f.get(null);
    } catch (Exception e) {
      try {defaultSpec = (Specializer) adaptor.dupTree(EMPTY_SPECIALIZER);}
      catch (Exception e2) {throw new Error("Error in parsing of constant specializer...");}
    }     
      
    return  (Specializer) adaptor.dupTree(defaultSpec);
  }

  /**Get the default guide for a named operator.*/
  public Specializer getOperatorDefault(String fullName) {
    MultiPartName name= new MultiPartName(fullName);
    ModuleData md;
    
    try {
        Module m = modules.findModuleForOperator(name.prefixedName());
        md = m.getModuleData();
    } catch (Exception e) {
      throw new RuntimeException("Error getting module information for operator " + name.toString(), e);
    }
    
    try {
        return  (Specializer) adaptor.dupTree(md.getDefaultSpecializer(name.getName()));
      } catch (Exception e) {
        throw new RuntimeException("Error finding default specializer for " + name.toString(), e);
      } 
  }
  
  /**Combine a given specializer with the appropriate default.*/
  private Specializer blendWithDefault(Specializer spec) {
     return Specializer.blendMaps(getDefault(spec), spec, adaptor);
  }

}

topdown
  options{backtrack=true;}
  : ^(s=SPECIALIZER DEFAULT) -> {getDefault((Specializer) s)}
  |{BLEND}?  ^(s=SPECIALIZER .*)      -> {blendWithDefault((Specializer) s)};
