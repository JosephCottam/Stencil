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

/** Splits dynamic rules into two parts: UpdateQuery and Base.
 **/
tree grammar DynamicCompleteRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import stencil.operator.StencilOperator;
  import stencil.operator.util.Invokeable;
  
}

@members {
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

duplicate:  ^(DYNAMIC_RULE rule=.) -> ^(DYNAMIC_RULE $rule $rule);
            
transform: ^(DYNMAIC_RULE . compactQuery);

compactQuery: ^(RULE . cc=callChain .) -> {toCompactList($cc.tree)};
callChain: ^(CALL_CHAIN target .) -> target;
target
  @after{
     if ($f != null) {
       StencilOperator op =((Function) f).getTarget().getOperator();
       if (op.getOperatorData().hasFacet(StencilOperator.STATE_FACET)) {
          Invokeable inv2 = op.getFacet(StencilOperator.STATE_FACET);
          ((AstInvokeable) ((CommonTree)$target.tree)).setInvokeable(inv2);
       }
    }
  }
  : ^(f=FUNCTION inv=. . . . target) -> ^(AST_INVOKEABLE[$f.text] target)
  | ^(PACK .*) -> ^(PACK);
  

