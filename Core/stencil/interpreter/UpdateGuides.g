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
 
/**Operators over a properly formed-AST and ensures that
 * all guides are up-to date.
 */ 
tree grammar UpdateGuides;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	filter = true;
}

@header{
	package stencil.interpreter;
	
	import java.util.Arrays;
	
	import stencil.util.AutoguidePair;
	import stencil.parser.tree.*;	
	import stencil.util.MultiPartName;
	import stencil.display.*;
	import stencil.rules.ModuleCache;
	import stencil.legend.module.*;
}

@members{
	private String layerName; //What layer is currently being worked on
	private String attribute; //What attribute is currently being considered
	private StencilPanel panel; //Panel to take elements from
	
	private ModuleCache cache;
		
	public void updateGuides(StencilPanel panel) {
		this.panel = panel;
		layerName = null;
		attribute = null;
		
		downup(panel.getProgram());
	}
	
	//TODO: Remove when all tuple references are positional
	public void setModuleCache(ModuleCache c) {this.cache = c;}

	/**Update an actual guide on the current layer using the passed panel.*/
    private void update(List<Object[]> categories, List<Object[]> results) {
    	try {
       		List<AutoguidePair> pairs = zip(categories, results);
       		DisplayLayer l = panel.getLayer(layerName);
       		DisplayGuide g = l.getGuide(attribute);
       		g.setElements(pairs);
    	} catch (Exception e) {
    		throw new RuntimeException(String.format("Error creating guide for attribute \%1\$s", attribute), e);
    	}
   	}
	
	/**Turn a pair of lists into a list of AutoguidePairs.*/
    private List<AutoguidePair> zip(List<? extends Object[]> categories, List<? extends Object[]> results) {
		assert categories.size() == results.size() : "Category and result lists must be of the same length";
		AutoguidePair[] pairs = new AutoguidePair[categories.size()]; 
		
		for (int i=0; i < categories.size(); i++) {
   			pairs[i] = new AutoguidePair<Object, Object>(categories.get(i), results.get(i));
		}
		return Arrays.asList(pairs);	
	}	
	
	private final void setAttribute(String att) {attribute = att;}
	
	private final void setLayer(String layer) {layerName = layer;}
	
	private final List<String> getPrototype(Function f) {
		MultiPartName name = new MultiPartName(f.getName());
		Specializer spec = f.getSpecializer();
		try {
   			Module m = cache.findModuleForLegend(name.prefixedName()).module;
   			LegendData ld = m.getOperatorData(name.getName(), spec);
   			FacetData fd = ld.getFacetData("Query");//TODO: This is not always query...we need to add guide facet data
   			assert fd.tupleFields() != null : "Unexpected null prototype tuple.";
   			return fd.tupleFields();
   		} catch (Exception e) {throw new RuntimeException("Error Specailizing", e);}
	}
	
	private final List<Object[]> invokeGuide(Function f, List<Object[]> vals, List<String> prototype) {
		return f.getOperator().guide(f.getArguments(), vals, prototype);
	}
	
  	private final List<Object[]> packGuide(Pack p, List<Object[]> vals, List<String> prototype) {
		Object[][] results = new Object[vals.size()][];
		
		int i=0;
		for (Object[] val: vals) {
			results[i] = new Object[p.getArguments().size()];
			int j=0;
			Value arg = p.getArguments().get(j); //TODO: Really need to handle the case where chain is setting more than one value
   			if (arg instanceof TupleRef) {results[i][j] = val[prototype.indexOf(((TupleRef) arg).getChild(0).getText())];} //HACK: This is horrible!  Assumes the tuple ref is a name...
			else {results[i][j] = arg.getValue();}
   			i++;
		}
		return Arrays.asList(results);
	}
}

topdown: ^(l=LAYER {setLayer($l.text);} . ^(LIST guide*) .);
guide: ^(GUIDE ID . att=ID {setAttribute($att.text);} callGroup);
	
callGroup: ^(CALL_GROUP callChain);
callChain: ^(CALL_CHAIN categorize);  //Chains always start with prototypes by now
categorize
	@init{List<Object[]> cats = null;}
	: ^(f=FUNCTION {cats = invokeGuide((Function) f, null, null);} . . . target[cats, cats, getPrototype((Function) f)]);
	
target[List<Object[\]> cats, List<Object[\]> vals, List<String> prototype]  //TODO: Remove prototype when all tuple-references are positional
	: ^(f=FUNCTION . . . target[cats, invokeGuide((Function) f, vals, prototype), getPrototype((Function) f)])
	| ^(p=PACK .) {update(cats, packGuide((Pack) p, vals, prototype));};

