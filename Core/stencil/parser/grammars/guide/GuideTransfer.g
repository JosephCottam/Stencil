/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
tree grammar GuideTransfer;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
  package stencil.parser.string;
	
  import java.util.Map;
  import java.util.HashMap;

  import org.antlr.runtime.tree.*;

  import stencil.parser.tree.*;
  import stencil.util.MultiPartName;
  import stencil.operator.module.*;
  import stencil.operator.module.util.*;
  import stencil.operator.util.Invokeable;
  import stencil.operator.StencilOperator;

  import static stencil.operator.module.util.OperatorData.TYPE_CATEGORIZE;
  import static stencil.parser.ParserConstants.GUIDE_FACET;
  import static stencil.parser.ParserConstants.MAIN_FACET;
  import static stencil.parser.ParserConstants.QUERY_FACET;
}

@members{
	protected Map<String, CommonTree> attDefs = new HashMap<String, CommonTree>();
	protected ModuleCache modules;
 
	public static class AutoGuideException extends RuntimeException {public AutoGuideException(String message) {super(message);}}
    
	public GuideTransfer(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		this.modules = modules;
	}
		
	public Object transform(Object t) {
		t = build(t);
		t = transfer(t);
		t = copyQuery(t);
		t = foldQuery(t);
		t = trim(t);
		t = rename(t);
		return t;
	}	
	
	 //Build a mapping from the layer/attribute names to mapping trees
	 private Object build(Object t) {
	   return downup(t, this, "buildMappings");
	 }

  private Object copyQuery(Object t) {
    return downup(t, this, "copyQuery");
  }
  private Object foldQuery(Object t) {
    return downup(t, this, "foldQuery");
  }
    
    //Transfer appropriate mapping tree to the guide clause
    private Object transfer(Object t) {
      return downup(t, this, "transferMappings");
    }
    
    //Trim each mapping chain to its last categorical operator
    private Object trim(Object t) {
      return downup(t, this, "trimGuide");
    }
    
    //Rename functions to use the guide channel
    private Object rename(Object t) {
      return downup(t, this, "renameMappingsDown");
    }    
    
    private String key(Tree selector) {
      Selector sel=(Selector) selector;
      return key(sel.get(0), sel.get(1));
    }
    private String key(Tree layer, Tree attribute) {return key(layer.getText(), attribute.getText());}
    private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
    private String key(String layer, String attribute) {
      MultiPartName att = new MultiPartName(attribute);
      String key = layer + ":" + att.getName();	//Trim to just the attribute name
      return key;
    }
    
    private String guideName(String name) {
      //TODO: Add switch here to check if guide exists and only use Query if it doesn't
      return new MultiPartName(name).modSuffix(QUERY_FACET).toString();
    }       


	//EnsureGuideOp guarantees that a sample operator exists; this
	//cuts things down so the generator only includes things after that point
	private Tree trimCall(CallTarget tree) {
    if (tree instanceof Pack) {throw new RuntimeException("Error trimming (no sample operator found): " + tree.toStringTree());}
    Function f = (Function) tree;

    //TODO: This is a crapy test for the sample operator...really needs to be fixed to something that can't be confused with user input
    if (f.getText().startsWith("Echo")) {return (Tree) adaptor.dupTree(f);}
    else {return trimCall(f.getCall());}
	}
    	
	
	private boolean isCategorize(Function f) {
   		MultiPartName name = new MultiPartName(f.getName());
   		Module m = modules.findModuleForOperator(name.prefixedName()).module;
   		try {
   			String opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacet(name.getFacet()).getType();;
   			return TYPE_CATEGORIZE.equals(opType);
   		} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.",e);}
	}
	
	
	/**Turns a chain into a list, discarding anything
	 * that is not an AST_INVOKEABLE with properly set operator.
	 */
	private Tree toCompactList(Tree tree) {
	   Tree rv = (Tree) adaptor.create(GUIDE_QUERY, "");
	   while (tree != null) {
	     if (tree.getType() == AST_INVOKEABLE &&
	        ((AstInvokeable) tree).getInvokeable() != null) {
         adaptor.addChild(rv, adaptor.dupNode(tree));
	     }
       tree = tree.getChild(0);
	   }
	   return rv;
	}
}

//Move mappings from the declarations in the consumes block up to the guides section
buildMappings: ^(c=CONSUMES . . . ^(LIST mapping[((Consumes)$c).getLayer().getName()]*) . .);
mapping[String layerName] 
  : ^(RULE ^(GLYPH ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF field=. type=.))) group=. .)
		{attDefs.put(key(layerName, field), group);};

//Move in the appropriate mappings -----------------------------------------------
transferMappings
	 : ^(GUIDE_DIRECT ^(GUIDE type=ID spec=. selector=. actions=.))
	 	{
	 	 if (!attDefs.containsKey(key(selector))) {throw new AutoGuideException("Guide requested for unavailable glyph attribute " + key(selector));}
	 	}
	 	-> ^(GUIDE_DIRECT 
	 	     ^(GUIDE $type $spec $selector $actions 
	 	        ^(RULE ^(RETURN ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF STRING["Output"] DEFAULT))) {adaptor.dupTree(attDefs.get(key(selector)))})))
	 | ^(GUIDE_SUMMARIZATION ^(GUIDE type=ID spec=. selector=. actions=.))
	   -> ^(GUIDE_SUMMARIZATION 
	         ^(GUIDE $type $spec $selector $actions 
	            ^(RULE ^(RETURN TUPLE_PROTOTYPE) ^(CALL_CHAIN PACK))));


//Update query creation -----------------------------------------------
//TODO: Extend to include actions and sampler
copyQuery: ^(GUIDE type=. spec=. selector=. actions=. ^(gen=RULE t=. ^(CALL_CHAIN chain=. .*))) ->
        ^(GUIDE $type $spec $selector $actions {adaptor.dupTree($gen)} ^(GUIDE_QUERY {adaptor.dupTree($chain)}));


foldQuery: ^(GUIDE_QUERY qc=foldQueryChain) -> {toCompactList($qc.tree)};
foldQueryChain
  @after{
     if ($f != null) {
       StencilOperator op =((Function) f).getOperator();
       if (op.getOperatorData().hasFacet(StencilOperator.STATE_FACET)) {
          Invokeable inv = op.getFacet(StencilOperator.STATE_FACET);
          ((AstInvokeable) ((CommonTree)$foldQueryChain.tree)).setInvokeable(inv);
       }
    }
  }
  : ^(f=FUNCTION . . . foldQueryChain) -> ^(AST_INVOKEABLE[$f.text] foldQueryChain)
  | ^(PACK .*) -> ^(PACK);
	

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
   @after{((Function) $renameMappingsDown.tree).setInvokeable(((Function) f).getOperator(), QUERY_FACET);}
   : ^(f=FUNCTION spec=. args=. style=. c=.) {c.getAncestor(GUIDE) != null}? -> ^(FUNCTION[guideName($f.text)] $spec $args $style $c);