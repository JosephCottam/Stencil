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
 
/**Make all anonymous operators explicit.  Anonymous operators are
 * those used in call chains that do not exist as references.
 *
 * Since all synthetic operator definitions are converted into
 * template/reference pairs, this preserves single-instance semantics
 * for synthetic operators. 
 */
tree grammar OperatorExplicit;
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
  import static stencil.parser.ParserConstants.NAME_SEPARATOR;
  import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
}

@members {
   /**Does the name appear as an operator def/proxy/ref?*/
   private boolean covered(Tree target) {
	  Program program = (Program) target.getAncestor(PROGRAM);
	  List<? extends Tree> operators = program.getOperators();
	  MultiPartName name = new MultiPartName(target.getText());
	  for (Tree o: operators) {
	      if (o.getText().equals(name.getName())) {return true;}
	  }
	  return false;
   }

   /**Create a cover reference for a given operator IF
    * there is not an operator ref with the given name.
    */
   private String cover(CommonTree target) {
      MultiPartName name = new MultiPartName(target.getText());
      if (covered(target)) {return target.getText() ;} 
	
	  String newName = genSym(name.getName());    	  //create a new name
	  Program program = (Program) target.getAncestor(PROGRAM);
	  List operators = program.getOperators();
	  
	  Tree ref = (Tree) adaptor.create(OPERATOR_REFERENCE, newName);     	  //Cover operator
	  adaptor.addChild(ref, adaptor.create(OPERATOR_BASE, name.prefixedName()));
	  adaptor.addChild(ref, adaptor.dupTree(target.getFirstChildWithType(SPECIALIZER)));
	  
	  adaptor.addChild(operators, ref);  	  //Add new operator to list
	  
	  return newName + NAME_SEPARATOR + name.getFacet();
   } 
   
}

topdown
   : ^(f=FUNCTION s=. rest+=.*)  -> ^(FUNCTION[cover($f)] {adaptor.dupTree(EMPTY_SPECIALIZER)} $rest*);
