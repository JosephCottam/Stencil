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
	ASTLabelType = CommonTree;	
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
		assert adapter != null : "Adaptor must not be null.";
		this.adapter = adapter;
	}

  public Specializer makeSpec(String guideType, Specializer spec) {
     if (spec.getChild(0).getType() == DEFAULT) {return getDefault(guideType);}
     else {return blendMaps(getDefault(guideType), spec);}
  }

	private Specializer getDefault(String guideType) {
		Class clss = adapter.getGuideClass(guideType);
		Specializer defaultSpec;
		
		try {
			Field f = clss.getField(DEFAULT_FIELD_NAME);
    	defaultSpec = (Specializer) f.get(null);
		} catch (Exception e) {
  		try {defaultSpec = ParseStencil.parseSpecializer("[]");}
  		catch (Exception e2) {throw new Error("Error in parsing of constant specializer...");}
		}			
			
    return  (Specializer) adaptor.dupTree(defaultSpec);
	}

  private Specializer blendMaps(Specializer defaults, Specializer update) {
    Specializer result = (Specializer) adaptor.dupTree(update);
    CommonTree mapList = (CommonTree) adaptor.create(LIST, "<map args>");
    
    Map<String, Atom> entries = new HashMap();
    entries.putAll(defaults.getMap());
    entries.putAll(update.getMap());
    
    for (String key: entries.keySet()) {
      MapEntry entry = (MapEntry) adaptor.create(MAP_ENTRY, key);
      adaptor.addChild(entry, adaptor.dupTree(entries.get(key)));
      adaptor.addChild(mapList, entry);
    }

    int mapIdx = result.getMap().getSource().getChildIndex();    
    adaptor.replaceChildren(result, mapIdx, mapIdx, mapList);
    return result;
  }
}

topdown
  : ^(GUIDE type=. spec=. sel=.  rules=.) 
    -> ^(GUIDE $type {makeSpec(type.getText(), (Specializer) spec)} $sel $rules);

//Instructions at http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality
