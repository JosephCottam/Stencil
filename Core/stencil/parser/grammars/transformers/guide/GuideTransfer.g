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
  import stencil.module.*;
  import stencil.interpreter.guide.MonitorOperator;
  import stencil.parser.ProgramCompileException;
  
  import static stencil.parser.ParserConstants.INVOKEABLE;
  import static stencil.parser.string.util.Utilities.*;
}

@members{
  public static class AutoGuideException extends ProgramCompileException {public AutoGuideException(String message) {super(message);}}

  public static StencilTree apply (Tree t, ModuleCache modules) {
     return (StencilTree) TreeRewriteSequence.apply(t, modules);
  }
  
	protected Map<String, StencilTree> attDefs = new HashMap<String, StencilTree>();
	protected ModuleCache modules;

  protected void setup(Object... args) {this.modules = (ModuleCache) args[0];}
  
  public Object downup(Object p) {
    downup(p, this, "buildMappings");     //Get a listing of layer/attribute definitions
    downup(p, this, "transferMappings");  //Move the needed ones to the guide definitions
    downup(p, this, "fillFragments");     //Convert the transfered parts to rules
    downup(p, this, "trimGuide");         //Throw out the rule parts not required
    downup(p, this, "copyQuery");         //Create the state query
    downup(p, this, "changeFacets");      //Switch the facet invoked
    return p;
  }

    private String key(StencilTree sel) {
      return key(sel.getAncestor(LAYER).getText(), sel.getText());
    }
    
    private String key(String layer, String attribute) {
      return layer + ":" + attribute;	//Trim to just the attribute name
    }

	//EnsureGuideOp guarantees that a sample operator exists; this
	//cuts things down so the generator only includes things after that point
   private Tree trimCall(StencilTree tree) {
      if (tree.getType() == StencilParser.PACK) {throw new RuntimeException("Error trimming (no monitor operator found): " + tree.getAncestor(CALL_CHAIN).toStringTree());}

      if (tree.find(INVOKEABLE).getOperator() instanceof MonitorOperator) {return (Tree) adaptor.dupTree(tree);}
      else {return trimCall(tree.find(FUNCTION, PACK));}
   }
   
   private StencilTree generators(StencilTree selectors) {
      StencilTree generators = (StencilTree) adaptor.create(LIST_GUIDE_GENERATORS, "LIST_GUIDE_GENERATORS");
      for(StencilTree sel:selectors) {
         assert sel.getType() == SELECTOR : "Non selector in selectors list.";
         StencilTree gen = attDefs.get(key(sel));
         if (gen==null) {throw new AutoGuideException("Guide requested for unavailable glyph attribute " + key(sel));}
         
         StencilTree node = (StencilTree) adaptor.create(GEN_FRAGMENT, sel.getText());  //Gen-fragement for a particular output field
         adaptor.addChild(node, adaptor.dupTree(gen));
         adaptor.addChild(generators, node);
      }
      return generators;
   }
}

//Move mappings from the declarations in the consumes block up to the guides section
buildMappings: ^(c=CONSUMES {$c.getAncestor(LAYER) !=null}? . . . ^(RULES_RESULT mapping[$c.getAncestor(LAYER).getText()]*) . .);
mapping[String layerName] 
  : ^(RULE ^(TARGET ^(TARGET_TUPLE ^(TUPLE_FIELD field=.*))) group=. .)
		{attDefs.put(key(layerName, field.getText()), group);};




//Move in the appropriate mappings -----------------------------------------------
//The adaptor.dupTree($selectors) is required to prevent an interesting mid-stream rewrite from destroying the selectors context before it can be used in the generators call
transferMappings
	 :   ^(GUIDE_DIRECT ^(GUIDE type=ID spec=. selectors=. actions=.))
	 	-> ^(GUIDE_DIRECT ^(GUIDE $type   $spec  {adaptor.dupTree($selectors)}  $actions {generators($selectors)}))
                                              
	 | ^(GUIDE_SUMMARIZATION ^(GUIDE type=ID spec=. selectors=. actions=.))
	   -> ^(GUIDE_SUMMARIZATION 
	         ^(GUIDE $type $spec $selectors $actions 
	            ^(RULE ^(TARGET TARGET_TUPLE) ^(CALL_CHAIN PACK))));

//Turn mappings into rules -------------------------------------------------------
fillFragments: ^(gf=GEN_FRAGMENT rule=.) -> ^(RULE ^(TARGET ^(TARGET_TUPLE ^(TUPLE_FIELD ID[$gf.text]))) $rule);


//trimMappings  -----------------------------------------------
trimGuide
  : ^(g=GUIDE . . . . ^(LIST_GUIDE_GENERATORS trimGuideGenRule*));

trimGuideGenRule
  : ^(RULE t=. ^(CALL_CHAIN c=.))
    {t.getAncestor(GUIDE_DIRECT) != null}? -> ^(RULE $t ^(CALL_CHAIN {trimCall(c)}));



//Update query creation -----------------------------------------------
copyQuery: ^(GUIDE type=. spec=. selector=. actions=. lgg=.) ->
        ^(GUIDE $type $spec $selector $actions $lgg {stateQueryList(adaptor, $lgg)});


//Rename mappings -----------------------------------------------
//Pick the 'guide'-related function instead of whatever else
//was selected for each function
changeFacets
   @after{
     StencilTree func = $changeFacets.tree;
     func.find(INVOKEABLE).changeFacet(func.find(OP_NAME).getChild(2).getText());
     //TODO: Remove this @after when no longer relying on copy propagation to keep shared state correct
   }
   : ^(f=FUNCTION i=. ^(OP_NAME pre=. base=. facet=.) spec=. args=. style=. c=. ) {c.getAncestor(LIST_GUIDE_GENERATORS) != null}? -> 
        ^(FUNCTION $i ^(OP_NAME $pre $base ID[counterpart(i, facet)]) $spec $args $style $c);