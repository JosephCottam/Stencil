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
 
/* Make sure sensible specializers are present on every
 * mapping operator. 
 */
tree grammar DefaultPack;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
}

@header{
	/**  Convert default PACKs to fully fledged PACKs.
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/
	package stencil.parser.string;
	
	import stencil.util.MultiPartName;
	import stencil.operator.module.*;
	import stencil.operator.module.util.*;
	import stencil.parser.tree.*;
	
}

@members{
  public static final class DefaultPackExpansionException extends RuntimeException {
    public DefaultPackExpansionException(String msg) {super(msg);}
  }

	protected ModuleCache modules;
    
	public DefaultPack(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		assert modules != null : "ModuleCache must not be null.";
		this.modules = modules;
	}

	public Pack fromDefault(Pack pack) {
	  Rule rule = pack.getRule();
	  Target target = rule.getTarget();
    TuplePrototype targetPrototype = target.getPrototype();
    
    Function call = pack.getPriorCall();
	  if (call == null) {throw new DefaultPackExpansionException("Cannot use implicit pack in a call-free chain.");}
    TuplePrototype callPrototype = target.getPrototype();
    
    if (callPrototype.size() != targetPrototype.size()) {throw new DefaultPackExpansionException("Default pack cannot be created because tuple prototypes are of different lengths.");}

    Pack newPack = (Pack) adaptor.dupNode(pack);
        
    for (int i=0; i< targetPrototype.size(); i++) {
       TupleRef ref = (TupleRef) adaptor.create(TUPLE_REF,"");
       adaptor.addChild(ref, adaptor.create(NUMBER, Integer.toString(i)));
       adaptor.addChild(newPack, ref);
    }
    
    return newPack;
	}

}

topdown: ^(r=PACK DEFAULT) -> {fromDefault((Pack) $r)};		
