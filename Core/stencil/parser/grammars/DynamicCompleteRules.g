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
	superClass = TreeRewriteSequence;
	output = AST;
	filter = true;
}

@header{
  package stencil.parser.string;
	
	import stencil.util.MultiPartName;
  import stencil.parser.tree.*;
  import stencil.operator.StencilOperator;
  import stencil.operator.module.util.FacetData;
  import stencil.operator.module.util.OperatorData;
  import stencil.operator.util.Invokeable;
  import static stencil.parser.string.GuideTransfer.stateQueryList;
  import static stencil.parser.ParserConstants.QUERY_FACET;
}

@members {
  public Object transform(Object t) {
    t = changeType(t);
    t = convertAll(t);
    t = toQuery(t);
    return t;
  } 
  
   private Object changeType(Object t) {
     return downup(t, this, "changeType");
   }

   private Object convertAll(Object t) {
     return downup(t, this, "convert");
   }
   private Object toQuery(Object t) {
     return downup(t, this, "toQuery");
   }
   
   private String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_FACET).toString();}       
}

changeType: ^(CONSUMES f=. pf=. l=. r=. v=. c=. ^(LIST toDynamic*));
toDynamic:  ^(r=RULE rest+=.*) -> ^(DYNAMIC_RULE {adaptor.dupTree($r)} {adaptor.dupTree($r)});
            
            
       
convert: ^(DYNAMIC_RULE . compactQuery);
compactQuery: ^(RULE . cc=callChain .) -> {stateQueryList(adaptor, $cc.tree)};
callChain: ^(CALL_CHAIN target .) -> target;
target
  @after{
     if ($f != null) {
       MultiPartName mpName = new MultiPartName(((Function) f).getName());
       StencilOperator op =((Function) f).getTarget().getOperator();
       OperatorData od = op.getOperatorData();
       FacetData fd = od.getFacet(mpName.getFacet());
       if (fd.isFunction()) { //Do nothing...
       } else if (!fd.isFunction() && od.hasFacet(StencilOperator.STATE_FACET)) {
          Invokeable inv2 = op.getFacet(StencilOperator.STATE_FACET);
          ((AstInvokeable) ((CommonTree)$target.tree)).setInvokeable(inv2);
          ((AstInvokeable) ((CommonTree)$target.tree)).setOperator(op);
       } else {
            System.out.println("Bad situation: Non-function with no state facet and no synthetic state operator: " + mpName.toString());
//          throw new Error("Non-function with no state facet found...implement synthetic state operator.");
       }
    }
  }
  : ^(f=FUNCTION inv=. . . . target) -> ^(AST_INVOKEABLE[$f.text] target)
  | ^(PACK .*) -> ^(PACK);
  

toQuery 
  @after{
    AstInvokeable inv=((Function) $toQuery.tree).getTarget();
    inv.changeFacet(QUERY_FACET);
  }
  : ^(f=FUNCTION rest+=.*) 
          {$f.getAncestor(DYNAMIC_RULE) != null}? ->
          ^(FUNCTION[queryName($f.getText())]  $rest*);

