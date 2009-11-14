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
tree grammar AutoGuide;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
	/** Performs the built of automatic guide mark generation.
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
	import stencil.util.MultiPartName;
  import stencil.operator.module.*;
  import stencil.operator.module.util.*;
	import stencil.operator.module.OperatorData.OpType;

  import static stencil.parser.ParserConstants.GUIDE_BLOCK_TAG;

	
}

@members{
	protected Map<String, CommonTree> attDefs = new HashMap<String, CommonTree>();
	protected ModuleCache modules;
 
  public static class AutoGuideException extends RuntimeException {public AutoGuideException(String message) {super(message);}}
    
	public AutoGuide(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		this.modules = modules;
	}
		
	public Object transform(Object t) {
		t = build(t);
		t = transfer(t);
		t = trim(t);
		t = rename(t);
		return t;
	}	
	
	 //Build a mapping from the layer/attribute names to mapping trees
	 private Object build(Object t) {
      fptr down =	new fptr() {public Object rule() throws RecognitionException { return buildMappings(); }};
    	fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
    	return downup(t, down, up);
   }
    
    //Transfer appropriate mapping tree to the guide clause
    private Object transfer(Object t) {
		  fptr down =	new fptr() {public Object rule() throws RecognitionException { return transferMappings(); }};
   	  fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	  return downup(t, down, up);
    }
    
    //Trim each mapping chain to its last categorical operator
    private Object trim(Object t) {
      fptr down = new fptr() {public Object rule() throws RecognitionException { return trimGuide(); }};
      fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
      return downup(t, down, up);
    }
    
    //Rename functions to use the guide channel
    private Object rename(Object t) {
			fptr down =	new fptr() {public Object rule() throws RecognitionException { return renameMappingsDown(); }};
	   	fptr up = new fptr() {public Object rule() throws RecognitionException { return  bottomup(); }};
	   	return downup(t, down, up);		
    }
    
    
    private String key(Tree layer, Tree attribute) {return key(layer.getText(), attribute.getText());}
    private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
    private String key(String layer, String attribute) {
    	MultiPartName att = new MultiPartName(attribute);
		String key = layer + ":" + att.getName();	//Trim to just the attribute name
		return key;
    }
    
    private String guideName(String name) {return new MultiPartName(name).modSuffix(GUIDE_BLOCK_TAG).toString();}       


	//EnsureGuideOp guarantees that one categorize exists...we move on from there!
	private Tree trimCall(CallTarget tree) {
    	if (tree instanceof Pack) {return null;}

    	try {
    		Tree trimmed = trimCall(((Function) tree).getCall());
        	if (trimmed != null) {return trimmed;}
    	} catch (Exception e) {
    		throw new RuntimeException("Error trimming: " + tree.getText(),e);
    	}

    	if (isCategorize((Function) tree)) {return tree;}
    	else {return null;}
	}
    	
	
	private boolean isCategorize(Function f) {
   		MultiPartName name = new MultiPartName(f.getName());
   		Module m = modules.findModuleForOperator(name.prefixedName()).module;
   		try {
   			OpType opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacetData(name.getFacet()).getFacetType();;
   			return (opType == OpType.CATEGORIZE);
   		} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.");}

	}

}

//Move mappings from the declarations in the consumes block up to the 
//guides section
buildMappings: ^(c=CONSUMES . . ^(LIST mapping[((Consumes)$c).getLayer().getName()]*) . .);
mapping[String layerName] 
  : ^(RULE ^(GLYPH ^(TUPLE_PROTOTYPE field=.)) group=. .)
		{attDefs.put(key(layerName, field), group);};

transferMappings
	 : ^(field=GUIDE layerName=ID type=ID spec=. rules=.)
	 	{
	 	 if (!attDefs.containsKey(key(layerName,field))) {throw new AutoGuideException("Guide requested for unavailable glyph attribute " + key(layerName, field));}
	 	}
	 	-> ^($field $layerName $type $spec $rules {adaptor.dupTree(attDefs.get(key(layerName,field)))});
	

//trimMappings
trimGuide : ^(GUIDE . . . . trimCallChain);
trimCallChain: ^(CALL_CHAIN call=.) -> ^(CALL_CHAIN {trimCall((CallTarget) call)});


//Pick the 'guide' function instead of whatever else
//was selected for each function
renameMappingsDown: ^(GUIDE . . . . ^(CALL_CHAIN renameGuideMapping));
renameGuideMapping
	 @after{if (retval.tree instanceof Function) {((Function) retval.tree).setOperator(((Function) f).getOperator());}}
	 : ^(f=FUNCTION spec=. args=. style=. call=renameGuideMapping) -> ^(FUNCTION[guideName($f.text)] $spec $args $style $call)
	 | ^(PACK .*);
