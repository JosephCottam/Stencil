tree grammar GuideTransfer;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
/** Performs the bulk of automatic guide guide generation:
 *   1) Transfers call chain from layer def to guide def for generator
 *   2) Trims guide generator chain to minimal length
 *   3) Acquires guide or query facets in guide generator chain
 *
 * Precondition: To operate properly, this pass must be run after ensuring 
 * guide operators exist and after annotating function calls with their
 * associated call targets.
 **/

  package stencil.parser.string;
	
  import java.util.Map;
  import java.util.HashMap;

  import org.antlr.runtime.tree.*;

  import stencil.parser.tree.*;
  import stencil.parser.tree.util.*;
  import stencil.module.*;
  import stencil.interpreter.guide.SeedOperator;
  
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.INVOKEABLE;
  import static stencil.parser.string.util.Utilities.*;
}

@members{
  public static class AutoGuideException extends RuntimeException {public AutoGuideException(String message) {super(message);}}

  public static StencilTree apply (Tree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules);
  }
  
	protected Map<String, StencilTree> attDefs = new HashMap<String, StencilTree>();
	protected ModuleCache modules;

  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  public Object downup(Object p) {
    downup(p, this, "buildMappings");
    downup(p, this, "transferMappings");
    downup(p, this, "trimGuide");
    downup(p, this, "copyQuery");
    downup(p, this, "renameMappingsDown");
    return p;
  }

    private String key(StencilTree sel) {
      return key(sel.getChild(0), sel.getChild(1)); //TODO: Extend the range of keys beyond layer/att pairs to full paths
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
   private Tree trimCall(StencilTree tree) {
      if (tree.getType() == StencilParser.PACK) {throw new RuntimeException("Error trimming (no sample operator found): " + tree.toStringTree());}

      if (tree.find(INVOKEABLE).getOperator() instanceof SeedOperator) {return (Tree) adaptor.dupTree(tree);}
      else {return trimCall(tree.find(FUNCTION, PACK));}
   }
}

//Move mappings from the declarations in the consumes block up to the guides section
buildMappings: ^(c=CONSUMES {$c.getAncestor(LAYER) !=null}? . . . ^(RULES_RESULT mapping[$c.getAncestor(LAYER).getText()]*) . .);
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
copyQuery: ^(GUIDE type=. spec=. selector=. actions=. ^(gen=RULE t=. ^(CALL_CHAIN chain=.))) ->
        ^(GUIDE $type $spec $selector $actions {adaptor.dupTree($gen)} {stateQueryList(adaptor, $chain)});


//trimMappings  -----------------------------------------------
trimGuide
  : ^(g=GUIDE layer=. type=. spec=. map=. ^(RULE t=. ^(CALL_CHAIN c=.)))
    {g.getAncestor(GUIDE_DIRECT) != null}?
    -> ^(GUIDE $layer $type $spec $map ^(RULE $t ^(CALL_CHAIN {trimCall(c)})));

//^(CALL_CHAIN call=. size=.) {call.getAncestor(GUIDE) != null}? -> ^(CALL_CHAIN {trimCall(call)});

//Rename mappings -----------------------------------------------
//Pick the 'guide'-related function instead of whatever else
//was selected for each function
renameMappingsDown
   @after{
     StencilTree func = $renameMappingsDown.tree;
     func.find(INVOKEABLE).changeFacet(QUERY_FACET);
     //TODO: Remove when no longer relying on copy propagation to keep shared state correct
   }
   : ^(f=FUNCTION i=. spec=. args=. style=. c=. ) {c.getAncestor(GUIDE) != null}? -> ^(FUNCTION[queryName($f.text)] $i $spec $args $style $c);