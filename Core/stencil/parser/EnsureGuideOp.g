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
tree grammar EnsureGuideOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}
@header{
	/** Performs the built of automatic guide mark generation.
	 *
	 * Precondition: To operate properly, this pass must be run after ensuring 
	 * guide operators exist and after annotating function calls with their
	 * associated call targets.
	 *  
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching\%2C+rewriting+a+reality	  
	 **/
	 
	package stencil.parser.string;
	 
	import java.util.Set;
	import java.util.HashSet;
	import stencil.util.MultiPartName;
  import stencil.operator.module.*;
	import stencil.operator.StencilOperator;
	import stencil.parser.tree.*;
	import stencil.rules.ModuleCache;
	
	import static stencil.operator.module.OperatorData.OpType;
	import static stencil.util.Tuples.stripQuotes;	 
	 //TODO: Extend so we can handle more than the first field in a mapping definition

}

@members {
	protected Set<String> requestedGuides = new HashSet<String>();
	protected ModuleCache modules;
    
	public EnsureGuideOp(TreeNodeStream input, ModuleCache modules) {
		super(input, new RecognizerSharedState());
		assert modules != null : "Module cache must not be null.";
		this.modules = modules;
	}

   	public Object transform(Object t) throws Exception {
		build(t);
		t = replace(t);
		t = ensure(t);
		return t;
	}	

	/**Build a list of things that need guides.**/
	private void build(Object t) {
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return listRequirements(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    downup(t, down, up);
    }
    
    /**Replace the auto-categorize operator.**/
    private Object replace(Object t) throws Exception {
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return replaceCompactForm(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    Object r = downup(t, down, up);
		return r;
    }
    
    /**Make sure that things which need guides have minimum necessary operators
     *
     *@throws Exception Not all requested guides are found for ensuring
     */
    private Object ensure(Object t) throws Exception {
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return ensure(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    Object r = downup(t, down, up);
   	    return r;
    }


    private Object downup(Object t, final fptr down, final fptr up) {
        TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(Object t)  { return applyOnce(t, down); }
            public Object post(Object t) { return applyRepeatedly(t, up); }
        };
        t = v.visit(t, actions);
        return t;    
    }
    
    
    private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
	private String key(String layer, String attribute) {
    	MultiPartName att = new MultiPartName(attribute);
    	String key= layer + ":" + att.getName();	//Trim to just the attribute name
    	return key;
    } 

    private boolean requiresChanges(CallGroup group) {
    	if (group.getChildCount() >1) {throw new RuntimeException("Cannot support auto-guide ensure for compound call groups.");}
       	CallChain chain = group.getChains().get(0);
        CallTarget call = chain.getStart();

		//Check if there is a categorize operator
    	boolean hasCategorize = false;
    	while (!(call instanceof Pack) && !hasCategorize) {
    		Function f = (Function) call;
    		MultiPartName name = new MultiPartName(f.getName());
    		Module m = modules.findModuleForOperator(name.prefixedName()).module;
       		try {
        		OpType opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacetData(name.getFacet()).getFacetType();
        		hasCategorize = (opType == OpType.CATEGORIZE);
        	} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.");}
			call = f.getCall();
       	}    	
    	return !hasCategorize;
    }
    
    //Given a call group, what are the values retrieved from the tuple in the first round
    //of calls?
    private String findInitialArgs(CallGroup call) {
    	StringBuilder args= new StringBuilder();
    	for (CallChain chain: call.getChains()) {
    		CallTarget t = chain.getStart();
    		for (Value v: t.getArguments()) {
    			if (v.isTupleRef()) {
    				args.append("\"");
    				args.append(v.getChild(0).toString());//TODO: HACK...won't admit indexed tuple-refs
    				args.append("\"");
    				args.append(",");
    			}
    		}
    	}
    	if (args.length() ==0) {throw new RuntimeException("No tuple-dependent arguments found when creating guide operator.");}
    	args.deleteCharAt(args.length()-1); //Remove trailing comma
    	
    	return args.toString();
    }
                
	private Tree newCall(String layer, String field, CommonTree c) {
	 	CallGroup call = (CallGroup) c; 
    	String key = key(layer, field);
    	if (!requestedGuides.contains(key)) {return call;}
    	if (!requiresChanges(call)) {return call;} 
    	String intialArgs = findInitialArgs(call);
    	
    	String specSource = String.format("[1 .. n, \%1\$s]", intialArgs);
    	Specializer specializer;
    	StencilOperator op;
    
    	try {
	    	specializer =ParseStencil.parseSpecializer(specSource); 
    	} catch (Exception e) {
    		throw new Error("Error creating auto-guide required categorize operator.",e);
    	}
    	
    	
    	stencil.parser.tree.List args = (stencil.parser.tree.List) adaptor.create(LIST, "args");
    	try {
    		String[] argNames = intialArgs.split(",");
    		for (String name: argNames) {
    			name= stripQuotes(name);
    			TupleRef ref = (TupleRef) adaptor.create(TUPLE_REF, "TUPLE_REF");
    			adaptor.addChild(ref, adaptor.create(ID, name));
    			adaptor.addChild(args,ref);
    		}
    	} catch (Exception e) {
    		throw new Error("Error creating auto-guide required argument list.",e);
    	}

		//Construct function node
    	Function functionNode = (Function) adaptor.create(FUNCTION, "EchoCategorize.Map");
    	adaptor.addChild(functionNode, specializer);
    	adaptor.addChild(functionNode, args);
    	adaptor.addChild(functionNode, adaptor.create(YIELDS, "->"));
		
		//Construct chain node
		CallChain chainNode = (CallChain) adaptor.create(CALL_CHAIN, "CALL_CHAIN");
		adaptor.addChild(chainNode, functionNode);
		
		CallGroup groupNode;
		if (call.getChains().size() == 1) {
			adaptor.addChild(functionNode, call.getChains().get(0).getStart());
			groupNode = (CallGroup) adaptor.create(CALL_GROUP, "CALL_GROUP");
			adaptor.addChild(groupNode, chainNode);
		} else {
			throw new Error("Auto guide with joined call chains not supported.");
		}
		
		
    	return groupNode;
    }
    
	public Specializer autoEchoSpecializer(CommonTree t) {
    	//Switch on the target type
    	//Get the names out of its arguments list
    	//Remember those names in the echo categorize
    
    	String specializerTemplate = "[1 .. n, \%1\$s]";
    	StringBuilder refs = new StringBuilder();
    	 
    	if (t instanceof Pack || t instanceof Function) {
    		CallTarget target = (CallTarget) t;
    		for (Value v:target.getArguments()) {
    			if (v.isAtom()) {continue;} //Skip all the atoms, we only want tuple-refs
    			refs.append("\"");
    			refs.append(v.getValue());
    			refs.append("\"");
    			refs.append(",");
    		}
    		refs.deleteCharAt(refs.length()-1); //Remove the last comma
    	} else {
    		throw new IllegalArgumentException("Attempt to use target of uknown type: " + t.getClass().getName());
    	}
    	
    	
		String specSource =String.format(specializerTemplate, refs);
    	try {
    		Specializer spec = ParseStencil.parseSpecializer(specSource);
    		return spec;
    	} catch (Exception e) {
    		throw new RuntimeException("Error creating default catgorical operator with specialzier " + specSource, e);
    	}
    }
    
    public List<Value> autoEchoArgs(CommonTree t) {
    	CallTarget target = (CallTarget) t;
    	List<Value> args = (List<Value>) adaptor.create(LIST, "Arguments");
    	
 		for (Value v: target.getArguments()) {
 			if (v.isAtom()) {continue;}
 			adaptor.addChild(args, adaptor.dupTree(v));
 		}
 		return args;
    }

}

//Identify requested guides for guides section
//Scan associated mapping chains
//  If a categorical exists, celebrate
//  If a categorical does not exist, place one at start

listRequirements: ^(name=LAYER . ^(LIST guide[$name.text]*) .);
guide[String layer]: ^(GUIDE . . field=ID) 	{requestedGuides.add(key(layer, field));};



ensure: ^(name=LAYER . . ^(LIST ^(CONSUMES . ^(LIST rule[$name.text]*))));
rule[String layer]: 
	^(RULE field=glyphField  call=. bind=.)
		->  ^(RULE $field {newCall(layer, $glyphField.field, call)} $bind);

//First field in a prototype list
glyphField returns [String field]: ^(GLYPH ^(TUPLE_PROTOTYPE f=ID .*)) {$field=$f.text;};

//Replace the #-> with an actual categorical operator...
replaceCompactForm:
 ^(FUNCTION s=. a=. GUIDE_YIELD t=.) ->
		^(FUNCTION $s $a YIELDS ^(FUNCTION["EchoCategorize.Map"] {autoEchoSpecializer($t)} {autoEchoArgs($t)} YIELDS {adaptor.dupTree($t)}));  
