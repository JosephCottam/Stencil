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
 
/* Make sure a query and StateID facet exist for each operator.
 */
tree grammar OperatorExtendFacets;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	superClass = TreeRewriteSequence;	
    output = AST;
	filter = true;
}

@header {
  package stencil.parser.string;

  import stencil.util.MultiPartName;
  import stencil.parser.tree.*;
  import stencil.operator.StencilOperator;
  import stencil.operator.module.util.FacetData;
  import stencil.operator.module.util.OperatorData;
  import stencil.operator.util.Invokeable;
  import static stencil.parser.string.Utilities.*;
  import static stencil.parser.ParserConstants.QUERY_FACET;
  import static stencil.parser.ParserConstants.MAP_FACET;
  import static stencil.parser.ParserConstants.STATE_ID_FACET;
}

@members {
	public Object transform(Object t) {
		t = replicate(t);
		t = toQuery(t);
		t = toStateQuery(t);
		return t;
	}	
	
	 //Build a mapping from the layer/attribute names to mapping trees
	 private Object replicate(Object t) {
	   return downup(t, this, "replicate");
	 }
	 
	 private Object toQuery(Object t) {
	   return downup(t, this, "toQuery");
	 }
	 
	 private Object toStateQuery(Object t) {
	   return downup(t, this, "toStateQuery");
	 }
}
 
replicate: ^(r=OPERATOR proto=. prefilter=. rules=.) 
	   -> ^(OPERATOR 
	          ^(OPERATOR_FACET[MAP_FACET] $proto $prefilter $rules) 
	          ^(OPERATOR_FACET[QUERY_FACET] $proto $prefilter $rules)
	          ^(STATE_QUERY $prefilter $rules));
	          

toQuery: ^(f=FUNCTION rest+=.*) 
          {$f.getAncestor(OPERATOR_FACET) != null && $f.getAncestor(OPERATOR_FACET).getText().equals("query")}? ->
          ^(FUNCTION[queryName($f.getText())]  $rest*);


toStateQuery: ^(OPERATOR . . compactQuery);
compactQuery: ^(STATE_QUERY ^(LIST or+=opRule*)) -> {stateQueryList(adaptor, $or.tree)}; //TODO: make a 'gather functions' operator....
opRule: ^(OPERATOR_RULE pred=. rule) -> rule;											 //TODO: figure out how to tie operator instances across facets... 
rule: ^(RULE . callChain .) -> callChain;
callChain: ^(CALL_CHAIN target .) -> target;
target
  : ^(f=FUNCTION inv=. . . . target) -> ^(AST_INVOKEABLE[$f.text] target)
  | ^(PACK .*) -> ^(PACK);



