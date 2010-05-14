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
 
/**Takes references to operator templates and instantiates the corresponding template.**/
tree grammar OperatorInstantiateTemplates;

options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
  import stencil.operator.module.*;
  import stencil.operator.*;
  import stencil.operator.module.util.OperatorData;
}

@members{
  private Program program;
  private ModuleCache modules;
  
  public OperatorInstantiateTemplates(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    this.modules = modules;
    Object p = input.getTreeSource();
    
    assert p instanceof Program : "Input source must be a Program tree.";
    this.program = (Program) p;
  }


  private StencilTree instantiate(OperatorReference opRef) {
    OperatorTemplate t = findTemplate(opRef);
    Specializer spec = (Specializer) opRef.getFirstChildWithType(SPECIALIZER);
    String name = opRef.getName();
    return t.instantiate(name, spec, adaptor);
  }
  
  private OperatorTemplate findTemplate(CommonTree opRef) {
    OperatorBase base = (OperatorBase) opRef.getFirstChildWithType(OPERATOR_BASE);
    String name = base.getName();  

    for (OperatorTemplate t:program.getOperatorTemplates()) {
      if (name.equals(t.getName())) {return t;} 
    }
    return null;
  }
}

topdown 
  : ^(o=OPERATOR_REFERENCE base=. spec=.) 
     {findTemplate($o) != null}? -> {instantiate((OperatorReference) o)};