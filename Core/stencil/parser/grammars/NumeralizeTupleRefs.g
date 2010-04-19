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
tree grammar NumeralizeTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
  
}

@header{
  /** Converts all tuple references to numeric references.
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.ParserConstants;  
  import stencil.parser.tree.*;
  import stencil.operator.module.*;
  import stencil.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototype;
  import static stencil.parser.string.EnvironmentProxy.initialEnv;
  import static stencil.parser.string.EnvironmentProxy.extend;
}

@members {
  private ModuleCache modules;
  
  public NumeralizeTupleRefs(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
  }
}

topdown
	: ^(p=PREDICATE valueE[initialEnv($p, modules)] op=. valueE[initialEnv($p, modules)])
    | ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)]);

callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION . . ^(LIST valueE[env]*) y=. callTarget[extend(env, $y, $f, modules)])
  | ^(PACK valueE[env]+);    
      
valueE[EnvironmentProxy env]
  : ^(t=TUPLE_REF r=ID v=valueP[env.get(env.getFrameIndex($r.text))]) -> ^(TUPLE_REF NUMBER[Integer.toString(env.getFrameIndex($r.text))] $v)
  | (TUPLE_REF ID) => ^(t=TUPLE_REF r=ID) -> ^(TUPLE_REF NUMBER[Integer.toString(env.getFrameIndex($r.text))])
  | ^(t=TUPLE_REF r=NUMBER v=valueP[env.get(((StencilNumber) $r).getNumber().intValue())])
  | (TUPLE_REF NUMBER) => ^(t=TUPLE_REF r=NUMBER)
  | .;
    catch[Exception e] {throw new RuntimeException("Error numeralizing " + $t.toStringTree());}
  
      
//TODO:Unify envProxy and TuplePrototype (will eliminated the valueE and valueNP variants, requires a way to get a prototype from a prototype...)
valueP[TuplePrototype p]
  : ^(t=TUPLE_REF r=ID) -> ^(TUPLE_REF NUMBER[Integer.toString(p.indexOf($r.text))])
  | ^(t=TUPLE_REF r=ID valueNP) -> ^(TUPLE_REF NUMBER[Integer.toString(p.indexOf($r.text))] valueNP)
  | ^(t=TUPLE_REF r=NUMBER valueNP) 
  | ^(t=TUPLE_REF r=NUMBER);
    catch[Exception e] {throw new RuntimeException("Error numeralizing " + $t.toStringTree());}

valueNP
  : ^(TUPLE_REF ID) {throw new RuntimeException("Numeralize can only handle one level names in nesting");}
  | ^(TUPLE_REF NUMBER valueNP)
  | ^(TUPLE_REF NUMBER);
