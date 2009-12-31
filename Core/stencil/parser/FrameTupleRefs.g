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
tree grammar FrameTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Makes all tuple refs qualified by their frame offsets.
   *  References in the current frame are offset by [0], others
   *  must be reference back to an earlier frame.
   **/
   
  package stencil.parser.string;
  
  import java.util.Arrays;
  
  import stencil.parser.ParserConstants;
  import stencil.parser.tree.*;
  import stencil.operator.module.*;
  import stencil.util.MultiPartName;
  import stencil.util.AutoguidePair;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.tuple.prototype.SimplePrototype;
  import stencil.tuple.prototype.TuplePrototypes;
  import static stencil.parser.string.EnvironmentProxy.initialEnv;
  import static stencil.parser.string.EnvironmentProxy.extend;
}

@members {
  private ModuleCache modules;
  
  public FrameTupleRefs(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
  }

  protected TupleRef frame(CommonTree t, EnvironmentProxy env) {
    TupleRef ref = (TupleRef) t;
    
    int frameIdx;
    if (!ref.isNumericRef()) {
		String name = ((Id) ref.getValue()).getName();
		if (env.isFrameRef(name)) {return ref;}	//No need to frame, already uses a frame ref
		
		frameIdx = env.frameRefFor(name);
    } else {
    	frameIdx =env.currentIndex();
    }
	
	TupleRef newRef = (TupleRef) adaptor.create(StencilParser.TUPLE_REF, "<frame autogen>");
	StencilNumber frame = (StencilNumber) adaptor.create(StencilParser.NUMBER, Integer.toString(frameIdx));
	adaptor.addChild(newRef, frame);
	adaptor.addChild(newRef, adaptor.dupTree(ref));
	return newRef;
  }
}


topdown: action | predicate;

predicate: ^(p=PREDICATE value[initialEnv($p, modules)] op=. value[initialEnv($p, modules)]);

action : ^(c=CALL_CHAIN callTarget[initialEnv($c, modules)]);

callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION s=. ^(LIST value[env]*) c=. callTarget[extend(env, $c, $f, modules)])
  | ^(PACK value[env]+);
          
value[EnvironmentProxy env] 
  : (TUPLE_REF) => ^(t=TUPLE_REF .+) -> {frame($t, env)}
  | .;
