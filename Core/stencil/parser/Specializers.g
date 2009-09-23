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
tree grammar Specializers;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
}

@header{
	/**  Make sure that every mapping operator has a specializer.
	 *
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.util.MultiPartName;
	import stencil.operator.module.*;
	import stencil.operator.module.util.*;
	
  import static stencil.parser.ParserConstants.SIMPLE_SPECIALIZER;
	
}

@members{
	protected ModuleCache modules;
    
	public Specializers(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		assert modules != null : "ModuleCache must not be null.";
		this.modules = modules;
	}

	public Specializer getDefault(String fullName) {
		MultiPartName name= new MultiPartName(fullName);
		ModuleData md;
		try{
    		Module m = modules.findModuleForOperator(name.prefixedName()).module;
    		md = m.getModuleData();
		} catch (Exception e) {
			throw new RuntimeException("Error getting module information for operator " + fullName, e);
		}
		    		
		try {
    		return  (Specializer) adaptor.dupTree(md.getDefaultSpecializer(name.getName()));
    	} catch (Exception e) {
    		throw new RuntimeException("Error finding default specializer for " + fullName, e);
    	} 
	}

}

topdown: ^(f=FUNCTION ^(SPECIALIZER DEFAULT) args=. op=. target=.) -> ^(FUNCTION {getDefault($f.getText())} $args $op $target);		

//Instructions at http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality
