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
 

tree grammar GuideExtendQuery;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;	
  filter = true;
  output = AST;	
}

@header {
  /**Ensure the query guide includes the seed, sample and any stateful action operations.*/
  
  package stencil.parser.string; 
  
  import stencil.operator.StencilOperator;
  import stencil.operator.util.Invokeable;
  import stencil.operator.util.ReflectiveInvokeable;
  import stencil.parser.tree.*;
  import static stencil.operator.StencilOperator.STATE_FACET;
}

topdown:
  ^(g=GUIDE type=. spec=. selector=. actions=. gen=. query[(Guide) g])
     -> ^(GUIDE $type $spec $selector $actions $gen query);
     
     
query[Guide g]
   @after{
      Invokeable seedInv = new ReflectiveInvokeable(STATE_FACET, g.getSeedOperator());
      AstInvokeable seedAInv = (AstInvokeable) adaptor.create(AST_INVOKEABLE, "seed");
      seedAInv.setInvokeable(seedInv);
      seedAInv.setOperator((StencilOperator) seedInv.getTarget());
      adaptor.addChild(gq, seedAInv);
   }
   : ^(gq=STATE_QUERY .*); 