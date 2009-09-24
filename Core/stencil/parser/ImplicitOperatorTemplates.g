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
 
tree grammar ImplicitOperatorTemplates;

options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  /**Takes operators with an implicit template and creates the explicit template
   *
   * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality    
   **/
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
}

@members{
  private String derivedName(StencilTree name) {return "$" + name.getText();}
  
  public Program transform(Program t) {
    t = createTemplates(t);
    List operators = (List) adaptor.create(LIST, "Operators");
    List templates = (List) adaptor.create(LIST,  "Templates");
    
    getOperators(t, operators);
    getTemplates(t, templates);
    
    setOperators(t, operators);
    setTemplates(t, templates);
    
    return t;
  }
  
  //Rename functions to use the guide channel
  private Program createTemplates(Object t) {
      fptr down = new fptr() {public Object rule() throws RecognitionException { return topdown(); }};
      fptr up = new fptr() {public Object rule() throws RecognitionException { return createTemplates(); }};
      return (Program) downup(t, down, up);   
  }
  
  //Rename functions to use the guide channel
  private void getOperators(Object t, final List operators) {
      fptr down = new fptr() {public Object rule() throws RecognitionException { return getOperators(operators); }};
      fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
      downup(t, down, up);   
  }

  private void getTemplates(Object t, final List templates) {
      fptr down = new fptr() {public Object rule() throws RecognitionException { return getTemplates(templates); }};
      fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
      downup(t, down, up);   
  }
  
  
  private void setOperators(Program program, List operators) {
    int i =0;
    for (Object t: program.getChildren()) {
      if (((StencilTree) t).getText().equals("Operators")) {break;}
      i++;
    }
        
    if (i == program.getChildCount()) {
      adaptor.addChild(program, operators);
    } else {
      adaptor.replaceChildren(program, i, i, operators);
    }
  }
  
  private void setTemplates(Program program, List templates) {
    adaptor.addChild(program, templates);
  }
}

getOperators[List l]:
  ^(o=OPERATOR .*) {adaptor.addChild(l, o);};
   
getTemplates[List l]:
  ^(t=OPERATOR_TEMPLATE .*) {adaptor.addChild(l, t);};

createTemplates:
  ^(o=OPERATOR yeilds=. ops=.) 
    -> ^(LIST ^(OPERATOR BASE[derivedName($o)] ^(SPECIALIZER DEFAULT))
              ^(OPERATOR_TEMPLATE[derivedName($o)] ^(SPECIALIZER DEFAULT) $yeilds $ops));