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
 
tree grammar MapFoldBalance;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
/* In some contexts, every map must be eventually followed
 * by a fold for the Stencil semantics to be clear (default setting, for example).
 * This validates that map and fold passes are paired properly
 * in such contexts. 
 *
 * TODO: Don't allow unbalanced map/Fold in dynamic bindings
 *
 */

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.*;
  import stencil.parser.string.ValidationException;
  
}


topdown: (layerDefault | prefilter | view | canvas | local);  //TODO: Should local really be included here?

layerDefault: ^(LAYER . ^(LIST balancedRule*) .*);

prefilter: ^(CONSUMES . ^(LIST balancedRule*) .*);
local:     ^(CONSUMES . . ^(LIST balancedRule*) .*);
view:      ^(CONSUMES . . . . ^(LIST balancedRule*) .*);
canvas:    ^(CONSUMES . . . . . ^(LIST balancedRule*) .*);


balancedRule: ^(RULE . callChain .);
callChain
   @after{if ($r.r != 0) {throw new ValidationException("Unbalanced map/fold in a context that requires balance.");}}
   : ^(CALL_CHAIN r=chain[0] .);
   
   
chain[int d] returns [int r]
   @init {if (d<0) {throw new ValidationException("Improperly nested map/fold found while checking for balance.");}}
   : ^(PACK .*)  {$r=d;}
   | ^(FUNCTION . . DIRECT_YIELD c=chain[d]) {$r=$c.r;}
   | ^(FUNCTION . . MAP c=chain[d+1]) {$r=$c.r;}
   | ^(FUNCTION . . FOLD c=chain[d-1]) {$r=$c.r;};