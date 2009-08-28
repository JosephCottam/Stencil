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
 
/* Ensures that stencil native and python operators are defined in the 
 * ad-hoc module.  Does NOT modify the AST, just populates the ad-hoc module.
 */
tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
}

@header {
	package stencil.parser.string;

	import stencil.adapters.Adapter;
	import stencil.display.DisplayLayer;
    import stencil.rules.ModuleCache;
    import stencil.rules.EncapsulationGenerator;
    import stencil.legend.StencilLegend;
    import stencil.legend.DynamicStencilLegend;
    import stencil.parser.tree.Legend;
    import stencil.parser.tree.Python;
    import stencil.parser.tree.Layer;
    import stencil.parser.tree.StencilTree;
    import stencil.legend.module.*;
    import stencil.legend.module.util.*;
	import stencil.legend.wrappers.*;
}

@members {
	protected ModuleCache modules;
	protected Adapter adapter;
	EncapsulationGenerator encGenerator = new EncapsulationGenerator();
	
	public AdHocOperators(TreeNodeStream input, ModuleCache modules, Adapter adapter) {
		super(input, new RecognizerSharedState());
		assert modules != null : "Module cache must not be null.";
		assert adapter != null : "Adapter must not be null.";
		
		this.modules = modules;
		this.adapter = adapter;		
	}

	protected void makeOperator(Legend op) {
		MutableModule adHoc = modules.getAdHoc();
		DynamicStencilLegend operator = new SyntheticLegend(adHoc.getModuleData().getName(), op);
		
		adHoc.addOperator(operator);
	}	
	
	protected void makePython(Python p) {
		encGenerator.generate(p, modules.getAdHoc());
	}
	
	protected void makeLayer(Layer l) {
		MutableModule adHoc = modules.getAdHoc();
		DisplayLayer dl =adapter.makeLayer(l); 
		l.setDisplayLayer(dl);
		
		DisplayLegend legend = new DisplayLegend(dl);
		adHoc.addOperator(legend, legend.getLegendData(adHoc.getName()));
	}

	
}

topdown
	: ^(op=LEGEND .*) {makeOperator((Legend) $op);}
	| ^(py=PYTHON .*) {makePython((Python) $py);}
	| ^(lay=LAYER .*) {makeLayer((Layer) $lay);};
