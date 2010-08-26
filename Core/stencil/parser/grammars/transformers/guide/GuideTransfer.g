tree grammar GuideTransfer;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Performs the bulk of automatic guide mark generation:
 *   1) Transfers call chain from layer def to guide def
 *   2) Trims guide generator chain to minimal length
 *   3) Acquires guide or query facets in guide generator chain
 *
 * Precondition: To operate properly, this pass must be run after ensuring 
 * guide operators exist and after annotating function calls with their
 * associated call targets.
 *  
 *
 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality    
 **/

  package stencil.parser.string;
	
  import java.util.Map;
  import java.util.HashMap;

  import org.antlr.runtime.tree.*;

  import stencil.parser.tree.*;
  import stencil.parser.tree.util.*;
  import stencil.module.*;
  import stencil.module.util.*;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.operator.StencilOperator;
  import stencil.interpreter.guide.SeedOperator;
  
  import static stencil.module.util.OperatorData.TYPE_CATEGORIZE;
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.STATE_ID_FACET;
  import static stencil.parser.string.util.Utilities.*;
}

@members{
  public static class AutoGuideException extends RuntimeException {public AutoGuideException(String message) {super(message);}}

  public static Program apply (Tree t, ModuleCache modules) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass(), modules);
  }
  
	protected Map<String, CommonTree> attDefs = new HashMap<String, CommonTree>();
	protected ModuleCache modules;

  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  public Object downup(Object p) {
    downup(p, this, "buildMappings");
    downup(p, this, "transferMappings");
    downup(p, this, "copyQuery");
    downup(p, this, "trimGuide");
    downup(p, this, "renameMappingsDown");
    return p;
  }

    private String key(Tree selector) {
      Selector sel=(Selector) selector;
      List<Id> path = sel.getPath();
      return key(path.get(0).getText(), path.get(1).getText()); //TODO: Extend the range of keys beyond layer/att pairs to full paths
    }
    
    private String key(Tree layer, Tree attribute) {return key(layer.getText(), attribute.getText());}
    private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
    private String key(String layer, String attribute) {
      MultiPartName att = new MultiPartName(attribute);
      String key = layer + ":" + att.getName();	//Trim to just the attribute name
      return key;
    }

	//EnsureGuideOp guarantees that a sample operator exists; this
	//cuts things down so the generator only includes things after that point
   private Tree trimCall(CallTarget tree) {
      if (tree instanceof Pack) {throw new RuntimeException("Error trimming (no sample operator found): " + tree.toStringTree());}
      Function f = (Function) tree;

      if (f.getTarget().getOperator() instanceof SeedOperator) {return (Tree) adaptor.dupTree(f);}
      else {return trimCall(f.getCall());}
   }
    	
	
	private boolean isCategorize(Function f) {
   		MultiPartName name = new MultiPartName(f.getName());
   		Module m = modules.findModuleForOperator(name.prefixedName());
   		try {
   			String opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacet(name.getFacet()).getType();;
   			return TYPE_CATEGORIZE.equals(opType);
   		} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.",e);}
	}
}

//Move mappings from the declarations in the consumes block up to the guides section
buildMappings: ^(c=CONSUMES . . . ^(LIST mapping[((Consumes)$c).getContext().getName()]*) . .);
mapping[String layerName] 
  : ^(RULE ^(RESULT ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF field=. type=.))) group=. .)
		{attDefs.put(key(layerName, field), group);};

//Move in the appropriate mappings -----------------------------------------------
transferMappings
	 : ^(GUIDE_DIRECT ^(GUIDE type=ID spec=. selector=. actions=.))
	 	{
	 	 if (!attDefs.containsKey(key(selector))) {throw new AutoGuideException("Guide requested for unavailable glyph attribute " + key(selector));}
	 	}
	 	-> ^(GUIDE_DIRECT 
	 	     ^(GUIDE $type $spec $selector $actions 
	 	        ^(RULE ^(RESULT ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF STRING["Output"] DEFAULT))) {adaptor.dupTree(attDefs.get(key(selector)))})))
	 | ^(GUIDE_SUMMARIZATION ^(GUIDE type=ID spec=. selector=. actions=.))
	   -> ^(GUIDE_SUMMARIZATION 
	         ^(GUIDE $type $spec $selector $actions 
	            ^(RULE ^(RESULT TUPLE_PROTOTYPE) ^(CALL_CHAIN PACK))));


//Update query creation -----------------------------------------------
copyQuery: ^(GUIDE type=. spec=. selector=. actions=. ^(gen=RULE t=. ^(CALL_CHAIN chain=. .*))) ->
        ^(GUIDE $type $spec $selector $actions {adaptor.dupTree($gen)} {stateQueryList(adaptor, $chain)});


//trimMappings  -----------------------------------------------
trimGuide
  : ^(g=GUIDE layer=. type=. spec=. map=. ^(RULE t=. ^(CALL_CHAIN c=. .*)) query=.)
    {g.getAncestor(GUIDE_DIRECT) != null}?
    -> ^(GUIDE $layer $type $spec $map ^(RULE $t ^(CALL_CHAIN {trimCall((CallTarget) c)})) $query);

//^(CALL_CHAIN call=. size=.) {call.getAncestor(GUIDE) != null}? -> ^(CALL_CHAIN {trimCall((CallTarget) call)});

//Rename mappings -----------------------------------------------
//Pick the 'guide'-related function instead of whatever else
//was selected for each function
renameMappingsDown
   @after{
     Function func = ((Function) $renameMappingsDown.tree);
     func.getTarget().changeFacet(QUERY_FACET);
   }
   : ^(f=FUNCTION i=. spec=. args=. style=. c=. ) {c.getAncestor(GUIDE) != null}? -> ^(FUNCTION[queryName($f.text)] $i $spec $args $style $c);