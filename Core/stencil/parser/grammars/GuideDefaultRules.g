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
 

tree grammar GuideDefaultRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
  superClass = TreeRewriteSequence;
  output = AST;	
}

@header {
  /**Ensures that an Input and Output rule exist in the guide declaration.
   * If either is missing, one is inserted which copies the source values
   * to the results.
   */

	package stencil.parser.string;

  import java.util.Arrays;
	
	import static stencil.parser.ParserConstants.BIND_OPERATOR;
	import stencil.parser.tree.*;
	import stencil.tuple.prototype.*;
	import stencil.tuple.prototype.TuplePrototype;
	
	
}

@members {
   private static final Rule OUTPUT_RULE;
   private static final Rule INPUT_RULE;
   private static final String OUTPUT_FIELD = "Output";
   private static final String INPUT_FIELD = "Input";
   private static final String X_FIELD = "X";
   private static final String Y_FIELD = "Y";
   private static final String ID_FIELD = "ID";
   private static final String TEXT_FIELD = "TEXT";
   
   static {
       OUTPUT_RULE = parseRule(OUTPUT_FIELD + BIND_OPERATOR + OUTPUT_FIELD);
       INPUT_RULE = parseRule(INPUT_FIELD + BIND_OPERATOR + INPUT_FIELD);
   }
   
   private static final Rule parseRule(String input) {
      ANTLRStringStream input1 = new ANTLRStringStream(input);
      StencilLexer lexer = new StencilLexer(input1);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      StencilParser parser = new StencilParser(tokens);
      parser.setTreeAdaptor(ParseStencil.TREE_ADAPTOR);
      
      try {
         return (Rule) parser.rule("glyph").getTree();
      } catch (Exception e) {
         throw new Error("Error constructing default rule for guides.",e);
      }
   }
    
   
   public StencilTree reform(Guide g) {
     TuplePrototype p = g.getPrototype();
     List<String> names = Arrays.asList(TuplePrototypes.getNames(p));
     List<Rule> rules = g.getRules();

     if (g.getGuideType().equals("pointLabels")) {
        if (!names.contains(X_FIELD)) {
           Rule r = parseRule(X_FIELD + BIND_OPERATOR + X_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(Y_FIELD)) {
           Rule r = parseRule(Y_FIELD + BIND_OPERATOR + Y_FIELD); 
           adaptor.addChild(rules, r);
        }
        if (!names.contains(TEXT_FIELD)) {
           Rule r= parseRule(TEXT_FIELD + BIND_OPERATOR + ID_FIELD);
           adaptor.addChild(rules, r);
        }
     } else {
          
	     if (!names.contains(OUTPUT_FIELD)) {
	        adaptor.addChild(rules, adaptor.dupTree(OUTPUT_RULE));   
	     }
	     
	     if (!names.contains(INPUT_FIELD)) {
	        adaptor.addChild(rules, adaptor.dupTree(INPUT_RULE));
	     }
     }
          
     return (StencilTree) adaptor.dupTree(g);   
   }
}

topdown: ^(g=GUIDE .*) -> {reform((Guide) g)};
