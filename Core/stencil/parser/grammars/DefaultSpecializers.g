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
 
 /** Make sure sensible specializers are present on every mapping operator. 
 */
tree grammar DefaultSpecializers;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
	/** Make sure all elements with specializers have
	  * all properties defined in their default specializer.	  
	 **/
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
	import stencil.util.MultiPartName;
	import stencil.operator.module.*;
	import stencil.operator.module.util.*;
	import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
	
}

@members{
	protected ModuleCache modules;
    
  public DefaultSpecializers(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
  }

  private Specializer getDefault(Specializer spec) {
    try {
	     Function f = (Function) spec.getAncestor(FUNCTION);
	     if (f != null) {return  getDefault(f.getName());}
	     
	     CanvasDef c = (CanvasDef) spec.getAncestor(CANVAS_DEF);
	     if (c != null) {return defaultCanvasSpecailizer();}
	     
	     Import i = (Import) spec.getAncestor(IMPORT);
	     if (i != null) {return EMPTY_SPECIALIZER;} 
	     
	     OperatorReference ref = (OperatorReference) spec.getAncestor(OPERATOR_REFERENCE);
	     if (ref != null) {return getDefault(ref.getBase().getName());}
	     
    } catch (Exception e) {return (Specializer) adaptor.dupTree(spec);}
	  throw new IllegalArgumentException("Specializer encountered in unexpected context: " + spec.getParent().toStringTree());
  }
  public Specializer defaultCanvasSpecailizer() {
    try {
      return ParseStencil.parseSpecializer(CanvasDef.DEFAULT_SPECIALIZER);
    } catch (Exception e) {throw new Error("Parse or pre-defined constant failed.", e);}
  }

  public Specializer getDefault(String fullName) {
    MultiPartName name= new MultiPartName(fullName);
    ModuleData md;
    
    try {
        Module m = modules.findModuleForOperator(name.prefixedName()).module;
        md = m.getModuleData();
    } catch (Exception e) {
      throw new RuntimeException("Error getting module information for operator " + name.toString(), e);
    }
    
    try {
        return  (Specializer) adaptor.dupTree(md.getDefaultSpecializer(name.getName()));
      } catch (Exception e) {
        throw new RuntimeException("Error finding default specializer for " + name.toString(), e);
      } 
  }
  
  private Specializer blendWithDefault(Specializer spec) {
     return Specializer.blendMaps(getDefault(spec), spec, adaptor);
  }

}

topdown
  options{backtrack=true;}
  : ^(s=SPECIALIZER DEFAULT) -> {getDefault((Specializer) s)}
  | ^(s=SPECIALIZER .*)      -> {blendWithDefault((Specializer) s)};
