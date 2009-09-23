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
tree grammar GuideSpecializers;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
}

@header{
	/**  Make sure that every guide a specializer.
	 *
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.adapters.Adapter;
	import java.lang.reflect.Field;
	import java.util.Arrays;
}

@members{
	
	public static final String DEFAULT_FIELD_NAME = "DEFAULT_ARGUMENTS";
	protected Adapter adapter;
    
	public GuideSpecializers(TreeNodeStream input, Adapter adapter) {
		super(input, new RecognizerSharedState());
		assert adapter != null : "ModuleCache must not be null.";
		this.adapter = adapter;
	}

	public Specializer getDefault(String guideType) {
		Class clss = adapter.getGuideClass(guideType);
				    		
		try {
			Specializer defaultSpec;
			
			if (Arrays.asList(clss.getFields()).contains(DEFAULT_FIELD_NAME)) {
				Field f = clss.getField(DEFAULT_FIELD_NAME);
    			defaultSpec = (Specializer) f.get(null);
			} else {
				defaultSpec = ParseStencil.parseSpecializer("[]");
			}
			
			
    		return  (Specializer) adaptor.dupTree(defaultSpec);
    	} catch (Exception e) {
    		throw new RuntimeException("Error finding default specializer for guide " + guideType, e);
    	} 
	}

}

topdown:  ^(GUIDE type=ID ^(SPECIALIZER DEFAULT) target=ID) -> ^(GUIDE $type {getDefault($type.getText())} $target); 

//Instructions at http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality
