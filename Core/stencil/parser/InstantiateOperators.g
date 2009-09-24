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
 
/* Instantiate operator references and put them in the adhoc module.
 * Operator references are created with the operator keyword.
 */
tree grammar AdHocOperators;

options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree; 
  filter = true;
}

@header {
  package stencil.parser.string;

  import stencil.operator.*;
  import stencil.operator.module.*;
  import stencil.operator.module.util.*;    
  import stencil.operator.wrappers.*;
  import stencil.parser.tree.*;
}

@members {
  public static final UnknownTemplateException extends RuntimeException  {
    private static final String names(List<OperatorTemplate> templates) {
       StringBuilder b = new StringBuilder();
       
       for (OperatorTemplate t: templates) {
         b.append(t.getName());
         b.append(", ");
       }
       b.delete(2);
       return b.toString();
    }
  
    public UnknownTemplateException(String name, List<OperatorTemplate> templates) {
       super("No template named " + name + " found.  Valid names are " + names(templates));
    }
  }

  protected MutableModule adHoc;
  protected Program p;
  
  public AdHocOperators(TreeNodeStream input, Program p, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "Module cache must not be null.";
    assert p != null;
    
    this.adHoc = modules.getAdHoc();
    this.p = p;
  }
  
  protected void makeOperator(OperatorReference ref) { 
    OperatorTemplate t = findTemplate(ref.getName());
    
    StencilLegened instance = t.instantiate(ref.getSpecializer());
    MutableModule adHoc = modules.getAdHoc();
    adHoc.addOperator(instance);
  
  }

  private OperatorTemplate findTemplate(String name) {
    for (OperatorTemplate t:p.getOperatorTemplates()) {
      if (name.equals(t.getName())) {return t;}
    }
    throw new UnknownTemplateException(name, p.getOperatorTemplates());   
  }

  
}

topdown
  : ^(op=OPERATOR_REFERENCE .*) {makeOperator((OperatorReference) $op);};
