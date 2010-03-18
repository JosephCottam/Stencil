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
 

tree grammar GuideLiftGenerator;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Removes the seed operator from the generator chain but adds
   * its invokeable to the guide node as the seed operator.
   */

	package stencil.parser.string;
	
	import stencil.parser.tree.*;
}

topdown 
  @after{((Guide) retval.tree).setSeedOperator(((Function) seed).getInvokeable());}
  : ^(g=GUIDE layer=. type=. spec=. map=. 
              ^(RULE target=.
                 ^(CALL_CHAIN ^(seed=FUNCTION s=. a=. y=. c=.))) query=.)
          -> ^(GUIDE[$g.text] $layer $type $spec $map ^(GUIDE_GENERATOR  ^(RULE $target ^(CALL_CHAIN $c))) $query);

bottomup
  : ^(GUIDE_GENERATOR ^(RULE retarget ^(CALL_CHAIN repack)));

retarget
  : ^(RETURN  ^(TUPLE_PROTOTYPE p=.+)) 
      -> ^(RETURN ^(TUPLE_PROTOTYPE $p ^(TUPLE_FIELD_DEF STRING["Input"] STRING["DEFAULT"])));
      
repack
  : ^(FUNCTION . . . repack)
  | ^(PACK f=.*) -> ^(PACK $f ^(TUPLE_REF ID["**stream**"] ^(TUPLE_REF NUMBER["0"])));
  //TODO: Is there a better way to reference to root source?  **stream** is a little obscure...
  //HACK: Only works if there is only one input
      