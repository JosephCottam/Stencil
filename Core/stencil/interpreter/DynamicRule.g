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
tree grammar DynamicRule;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
	/** Converts a rule into a rule suitable for dynamic bindings.
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/

	package stencil.interpreter;
		
	import stencil.util.MultiPartName;
	import stencil.parser.tree.Function;
	import stencil.parser.tree.StencilTreeAdapter;
	import stencil.parser.tree.Rule;
	import static stencil.parser.ParserConstants.QUERY_BLOCK_TAG;

	import java.util.Map;
	import java.util.HashMap;
}

@members {
	/**Tree adaptor for translation and duplication.**/
	private static StencilTreeAdapter treeAdaptor = new StencilTreeAdapter(); 
	
	/**Cache of rules that the dynamic representation has already been constructed for.**/
	private static Map<Rule, Rule> mapCache  = new HashMap<Rule, Rule>();
	
	/**Convert a rule to its dynamic for; this is the preferred method
	  *for creating a dynamic rule.
	  *
	  *This method is memoized.
	 **/
	public static Rule toDynamic(Rule original) {
		if (mapCache.containsKey(original)) {return mapCache.get(original);}
	
		Rule rule = (Rule) treeAdaptor.dupTree(original);
		
		CommonTreeNodeStream treeTokens = new CommonTreeNodeStream(rule);
		DynamicRule dr = new DynamicRule(treeTokens);
		dr.setTreeAdaptor(treeAdaptor);
		rule = (Rule) dr.downup(rule);
		
		mapCache.put(original, rule);
		return rule;
	}
    
    
    private String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_BLOCK_TAG).toString();}       
}

topdown 
	@after{((Function) retval.tree).setOperator(((Function) f).getOperator());}
	:^(f=FUNCTION spec=. args=. yield=. target=.) -> ^(FUNCTION[queryName($f.getText())] $spec $args $yield $target);

