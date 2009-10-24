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
 
tree grammar DereferenceOperators;

options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;
  superClass = TreeRewriteSequence; 
  output = AST;
  filter = true;
}

@header{
  /**Takes operator references and creates operator instances out of them
   * 
   * Operators can be based off o either operator templates or operator instances from
   * an imported module.  If it is based on an operator template, the template is instantiated
   * and the operator reference is replaced with a new operator object that has
   * a body based on the template plus the specializer.  If the operator is
   * based on an instance, an instance of that operator is created and given the appropriate name.
   *
   * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality    
   **/
  package stencil.parser.string;
  
  import stencil.parser.tree.*;  
  import stencil.operator.module.*;
  import stencil.operator.*;
}

@members{
  private Program program;
  private ModuleCache modules;
  
  public DereferenceOperators(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    this.modules = modules;
    Object p = input.getTreeSource();
    
    assert p instanceof Program : "Input source must be a Program tree.";
    this.program = (Program) p;
  }


  public StencilTree instantiate(OperatorReference opRef) {
    OperatorBase base = opRef.getBase();
    Specializer spec = opRef.getSpecializer();
    String name = opRef.getName();
    String baseName = base.getName();  
    OperatorTemplate t = findTemplate(baseName);
    
    if (t==null) {
          Module module; 
          try {module = modules.findModuleForOperator(baseName).module;}
          catch (Exception e) {throw new RuntimeException(String.format("Could un-base operator \%1\$s, no template or instance named \%2\$s known.", name, baseName));}
              
          StencilOperator l;
          OperatorData od;
          try {
            l = module.instance(baseName, spec);
            od = module.getOperatorData(baseName, spec); 
          } catch (Exception e) {throw new RuntimeException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, name), e);}
           
          OperatorProxy p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, name);
          p.setOperator(l, od);
          return p;
    } else {
      return t.instantiate(name, spec, adaptor);
    }
  }
  
  public OperatorTemplate findTemplate(String name) {
    for (OperatorTemplate t:program.getOperatorTemplates()) {
      if (name.equals(t.getName())) {return t;} 
    }
    return null;
  }
}

topdown 
  : ^(o=OPERATOR_REFERENCE base=. spec=.) 
      -> {instantiate((OperatorReference) o)};