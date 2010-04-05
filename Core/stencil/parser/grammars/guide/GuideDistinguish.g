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
 

tree grammar GuideDistinguish;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Distinguish between summariziation and direct guides structurally
   * so future passes can use tree pattern matching.
   */

  package stencil.parser.string;

  import java.util.Arrays;
}

@members {
  //TODO: Get the list of direct types from the adaptor
  private static List<String> DIRECT_TYPES = Arrays.asList("AXIS", "SIDEBAR");


  public GuideDistinguish(TreeNodeStream input, TreeAdaptor adaptor) {
    super(input, new RecognizerSharedState());
    this.adaptor = adaptor;
  }


  private boolean isDirect(Tree t) {
    String type = t.getText().toUpperCase();
    return DIRECT_TYPES.contains(type);
  }
  
  private Tree taggedType(Tree t) {
     if (isDirect(t)) {
       return (Tree) adaptor.create(GUIDE_DIRECT, "");
     } else {
       return (Tree) adaptor.create(GUIDE_SUMMARIZATION, "");
     }
  }
}

bottomup: ^(GUIDE t=. s=. p=. r=.) -> ^({taggedType(t)} ^(GUIDE $t $s $p $r));
