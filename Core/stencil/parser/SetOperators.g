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
 
/* Adds references to the concrete operators to each function call tree-node.  
 * Technically, does NOT modify the AST since the field modified is not part
 * of the ANTLR tree; however this does set decorator fields in the AST.
 */
tree grammar SetOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
}

@header {
	/** Annotate function call tree nodes with Invokeable objects. 
	 *
	 *
	 **/
	 

	package stencil.parser.string;
	
    import stencil.operator.*;
    import stencil.operator.module.*;
    import stencil.operator.module.util.*;
    import stencil.operator.util.*;
    import stencil.parser.tree.Function;
    import stencil.parser.tree.Specializer;
    import stencil.parser.tree.StencilTree;
    import stencil.util.*;

}

@members { 
	protected ModuleCache modules;

	public SetOperators(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		this.modules = modules;
	}
	
  	public void setOperator(Function func) {
    	try {
    		MultiPartName name = new MultiPartName(func.getName());
    		Specializer s = func.getSpecializer();

    		StencilOperator op = modules.instance(name.prefixedName(), s);
    		func.setOperator(op);
    	} catch (Exception e) {
    		String message = String.format("Error creating invokeable instance for function \%1\$s.", func.getName()); //TODO: Add line number report...tree.getLine() doesn't work!
    		throw new RuntimeException(message, e);
    	}
    }
}

topdown: ^(f=FUNCTION .*) {setOperator((Function) $f);};


