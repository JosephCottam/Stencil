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
 
/*Perform import operations specified in the stencil program.
 *This builds the fully populated module cache, ready to be used to instantiate
 *methods for use in the interpreter.
 */ 
tree grammar Imports;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
}

@header{
	package stencil.parser.string;
	
	import java.util.Map;
	import java.util.HashMap;
	import stencil.parser.tree.*;
	import stencil.operator.module.*;
	import stencil.operator.module.util.*;
	import org.antlr.runtime.tree.*;
	
}

@members{
	protected ModuleCache modules;
    
	public void doImport(String name, String prefix, StencilTree spec) {
		//TODO: handle arg list on import (currently just ignored)
		try {modules.importModule(name, prefix);}
		catch (Exception e) {throw new RuntimeException(String.format("Error importing \%1\$s (with prefix '\%2\$s').", name, prefix), e);} 
	}
	
	public ModuleCache processImports(Object t) {
		modules = new ModuleCache(); 
		super.downup(t);
		return modules;
	}
}

/**Build up imports (used to decide the operation type)*/
topdown	: ^(name=IMPORT prefix=ID spec=.) {doImport($name.getText(), $prefix.getText(), spec);};
