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
tree grammar GuideInsertSeedOp;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header{
	/** Ensures that a sample operator exists in the call chain.
	 *  Sample operators are inserted where a #-> appears,
	 *  after the last project operator in a chain or 
	 *  at the start of the chain.
	 **/
	 
	package stencil.parser.string;
	 
	import java.util.Set;
	import java.util.HashSet;
	import stencil.util.MultiPartName;
  import stencil.operator.module.*;
  import stencil.util.collections.ArrayUtil;
	import stencil.operator.StencilOperator;
	import stencil.parser.tree.*;
	
	import static stencil.parser.ParserConstants.BIND_OPERATOR;
	import static stencil.tuple.Tuples.stripQuotes;	
	import static stencil.parser.ParserConstants.MAIN_FACET;
	import static stencil.interpreter.guide.Samplers.CATEGORICAL;
	import static stencil.interpreter.guide.Samplers.SAMPLE_KEY;
	 //TODO: Extend so we can handle more than the first field in a mapping definition
}

@members {
  private static final String SEED_PREFIX = "seed.";

  private static final boolean isCategorical(Specializer spec) {
    return !spec.getMap().containsKey(SAMPLE_KEY) ||
           CATEGORICAL.equals(spec.getMap().get(SAMPLE_KEY).getValue());
  }

	protected ModuleCache modules;

  //Mapping from requested guides to descriptor construction strategy.  This is populated by the 'build' pass
  protected HashMap<String, Specializer> requestedGuides = new HashMap();

    
	public GuideInsertSeedOp(TreeNodeStream input, ModuleCache modules) {
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
    
    /**Make sure that things which need guides have minimum necessary operators.
     *
     *@throws Exception Not all requested guides are found for ensuring
     */
    private Object ensure(Object t) throws Exception {
		    fptr down =	new fptr() {public Object rule() throws RecognitionException { return ensure(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    Object r = downup(t, down, up);
   	    return r;
    }
    
    
    private String key(CallTarget target) {return key((Rule) target.getAncestor(RULE));}

    /**Given a tree, how should it be looked up in the guides map?*/
    private String key(Rule rule) {
      Tree layer = rule.getAncestor(LAYER);
      Tree attRef = rule.getTarget().getPrototype().get(0);
      if (attRef == null) {return null;} //rule has no target, happens when the rule is for side effects only
      Tree att = attRef.getChild(0);
      if (layer == null || att==null) {return null;}
      return key(layer.getText(), att.getText());
    }
    
    
    private String key(Tree layer, Tree att) {return key(layer.getText(), att.getText());}
    private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
	  private String key(String layer, String attribute) {
     	MultiPartName att = new MultiPartName(attribute);
    	String key= layer + BIND_OPERATOR + att.getName();	//Trim to just the attribute name
    	return key;
    }
    
    /**Does the given call group already have the appropriate sampling operator?**/ 
    private boolean requiresChanges(CallChain chain) {
      Rule r = (Rule) chain.getAncestor(RULE);
      if (r == null || !requestedGuides.containsKey(key(r))) {return false;}
      Specializer strat = requestedGuides.get(key(r));
      String operatorName = operatorName(strat);
      CallTarget call = chain.getStart();
      while(!(call instanceof Pack)) {
        Function f  = (Function) call;
        if (operatorName.equals(f.getName())) {return false;}
        call = f.getCall();
      }
      return true;
    }
    
    private static final String operatorName(Specializer spec) {
           String operatorName;
      
      if (spec == null || isCategorical(spec)) {
        operatorName = "EchoCategorize";
      } else {
        operatorName = "EchoContinuous";
      }
      
      return String.format("\%1\$s.\%2\$s", operatorName, MAIN_FACET);
    }
 
    /**Construct the arguments section of an echo call block.
     *
     * @param t Call target that will follow the new echo operator.
     */
    private List<Value> echoArgs(Tree target) {return echoArgs((CallTarget) target);}
    private List<Value> echoArgs(CallTarget target) {
    	List<Value> args = (List<Value>) adaptor.create(LIST, "Arguments");
    	
 		  for (Value v: target.getArguments()) {
 			  if (v.isAtom()) {continue;}
 			  adaptor.addChild(args, adaptor.dupTree(v));
 		  }
 		  return args;
    }
    
    private Specializer spec(CommonTree t) {
      CallTarget target = (CallTarget) t;
      List<Value> args = echoArgs(target);
      StringBuilder b = new StringBuilder("[1 .. n,");
      for (Value v: args) {
        if (v instanceof TupleRef) {
	        b.append("\"");
	        b.append(((TupleRef) v).getValue());
	        b.append("\"");
	        b.append(",");
	      }
      }
      
      //Get additional map arguments from the guide declaration
      Specializer spec = requestedGuides.get(key(target));
      for (String k: spec.getMap().keySet()) {
          if (k.startsWith(SEED_PREFIX)) {
             String value = spec.getMap().get(k).toString();
             String key = k.substring(SEED_PREFIX.length());
             
             b.append(key);
             b.append("=");
             b.append(value);
             b.append(",");
          }
      }
      
      b.replace(b.length()-1, b.length(), "]");
      try {return ParseStencil.parseSpecializer(b.toString());}
      catch (Exception e) {throw new Error("Error parsing synthesized specializer: " + b.toString());}
    }
    
     /**Determine which echo operator to use.*/
     public CallTarget getStart(Tree c) {
       return ((CallChain) c).getStart();
     }
    
    private String selectOperator(Tree t) {
      Layer layer = (Layer) t.getAncestor(StencilParser.LAYER);
      Rule r = (Rule) t.getAncestor(StencilParser.RULE);
      String field = r.getTarget().getPrototype().get(0).getFieldName();
      
      Specializer strat = requestedGuides.get(key(layer.getName(), field));
      return operatorName(strat);
    }
    
}

//Identify requested guides from canvas def
listRequirements: ^(g=GUIDE layer=. type=. spec=. actions=.) 
	{requestedGuides.put(key(layer, g), (Specializer) spec);};

//Replace the #-> with an echo operator...
replaceCompactForm:
 ^(f=FUNCTION s=. a=. GUIDE_YIELD t=.) ->
		^(FUNCTION $s $a DIRECT_YIELD ^(FUNCTION[selectOperator($f)] {spec($t)} {echoArgs($t)} DIRECT_YIELD {adaptor.dupTree($t)}));  
		
ensure:
	^(r=RULE t=. c=. b=.)
	    {t.getType()==GLYPH && requiresChanges((CallChain) c)}? ->
		  ^(RULE $t ^(CALL_CHAIN ^(FUNCTION[selectOperator(getStart($c))]  {spec(getStart($c))} {echoArgs(getStart($c))} DIRECT_YIELD {adaptor.dupTree(((CallChain) c).getStart())})) $b); 
		        
		