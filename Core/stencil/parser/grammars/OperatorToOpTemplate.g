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
 
/**Convert operator definition to template/reference pairs.
 */
tree grammar OperatorToOpTemplate;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
  package stencil.parser.string;
	
  import stencil.parser.tree.*;
  import static stencil.parser.string.Utilities.genSym;
}

@members{
   String newName;
   
   /** @arg ops Operator parts list
    * @arg keep True -- return list of templates; False -- return list of non-templates
    */
   private StencilTree siftTemplates(List<StencilTree> ops, String label, boolean keepTemplates) {
      StencilTree list = (StencilTree) adaptor.create(LIST, label); 
      for (StencilTree t: ops) {
		if (keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.getFirstChildWithType(OPERATOR_TEMPLATE)));
         } else if (!keepTemplates && t.getType() == OPERATOR) {
            adaptor.addChild(list, adaptor.dupTree(t.getFirstChildWithType(OPERATOR_REFERENCE)));
         } else if (!keepTemplates && t.getType() != OPERATOR) {
         	adaptor.addChild(list, adaptor.dupTree(t));
         }
      }
      return list;   
   }

   protected StencilTree templates(CommonTree source)     {return siftTemplates((List<StencilTree>) source, "Operator Templates", true);}
   protected StencilTree nonTemplates(CommonTree source)  {return siftTemplates((List<StencilTree>) source, "Operators", false);}
   
}

topdown:  ^(o=OPERATOR rest+=.*) {newName=genSym($o.text);}
  ->  ^(OPERATOR ^(OPERATOR_REFERENCE[$o.text] OPERATOR_BASE[newName] ^(SPECIALIZER DEFAULT))
                 ^(OPERATOR_TEMPLATE[newName] $rest*));
                 
bottomup:
    ^(PROGRAM i=. g=. s=. o=. cl=. sd=. l=. ops=. p=.) 
        -> ^(PROGRAM $i $g $s $o $cl $sd $l {nonTemplates(ops)} $p {templates(ops)});
                 
