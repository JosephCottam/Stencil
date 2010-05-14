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

/**Takes custom-argument blocks and converts them 
 *  into either normal argument blocks (when they contain no tuple refs)
 *  or into format statements with an associated normal block (when they contain refs).
 *
 * This expects the blocks to still include surrounding markers, which are stripped away here.
 */ 
tree grammar PrepareCustomArgs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
}

@header{
	package stencil.parser.string;

	import java.util.regex.Pattern;
	import stencil.parser.tree.StencilString;
}

@members{
	public static final String PRINTF_OP = "Format.map";
	public static final String VALIDATE_PATTERN = "([^\\{\\}]*(\\{.+\\})?)*";
	public static final String SPLIT_PATTERN = "\\{|\\}";
	private static final Pattern SPLITTER = Pattern.compile(SPLIT_PATTERN);
	
	public static String[] split(String input) {
		if(!input.matches(VALIDATE_PATTERN)) {
			throw new RuntimeException("Error in sigil arguments: " + input);
		}
		return SPLITTER.split(input);
	}

   private String sigilCall(Tree call) {
    return call.getText().substring(1) + ".customParser";
   }
   
   /**Divide the input up into string and tuple refs.  
    * The input text MUST NOT include the island grammar markers (e.g. the quotes or braces...or whatever they end up being)
    **/
   public static Tree splitArgs(String input, TreeAdaptor adaptor) {
      boolean inRef = false;//Always starts outside of the ref
      String[] parts = split(input);
 	  
 	  Tree root = (Tree) adaptor.create(SIGIL_ARGS, "SIGIL_ARGS");
 	  for (String part: parts) {
 	     if (!part.equals("")) {
	 	     Tree leaf;
	 	     if (inRef) {
          //TODO: Figure out how to re-use the lexer...
          StencilLexer lexer = new StencilLexer(new ANTLRStringStream(part));
	 	     	CommonTokenStream tokens = new CommonTokenStream(lexer);
	 	     	StencilParser parser = new StencilParser(tokens);
	 	     	parser.setTreeAdaptor(adaptor);
	 	     	try {leaf = (Tree) parser.tupleRef().getTree();}
	 	     	catch (Exception e) {throw new RuntimeException("Error parsing tuple ref in sigil: " + part);}
	 	     	leaf = (Tree) adaptor.dupTree(leaf);
	 	     } else {
	 	        leaf = (Tree) adaptor.create(STRING, part); 	     
	 	     }
         adaptor.addChild(root, leaf);
       }
 	     inRef = !inRef;
    }
    return root;
   }

   public Tree printfArgs(Tree args) {return printfArgs(args, adaptor);}
   public static Tree printfArgs(Tree args, TreeAdaptor adaptor) {
     List<Tree> splitArgs = new stencil.parser.tree.List.WrapperList(splitArgs(stripBraces(args), adaptor), 0);
     StringBuilder format = new StringBuilder();
     List<Tree> refArgs = new ArrayList();
     
     for (Tree t: splitArgs) {
        if (t instanceof StencilString) {
           format.append(t.getText());
        } else {
        	refArgs.add(t);
			format.append("\%");
			format.append(refArgs.size());
			format.append("\$s");     
        }
     }
     
     Tree printfArgs = (Tree) adaptor.create(LIST, "<args>");
     adaptor.addChild(printfArgs, adaptor.create(STRING, format.toString()));
     for (Tree t: refArgs) {
        adaptor.addChild(printfArgs, adaptor.dupTree(t));
     }
     return printfArgs;
   }
   
   /**Only needs to do the printf stuff if there is a tuple ref.**/
   private static boolean doPrintf(Tree t) {return stripBraces(t).contains("{");}
   private static String stripBraces(Tree t) {return t.getText().substring(1, t.getText().length()-1);}
}

topdown: ^(FUNCTION s=. b=ISLAND_BLOCK y=. c=.) 
			 -> {doPrintf($b)}? ^(FUNCTION[PRINTF_OP] ^(SPECIALIZER DEFAULT) {printfArgs($b)} $y ^(FUNCTION $s ^(LIST ^(TUPLE_REF ^(NUMBER["0"]))) $y $c))
			 -> ^(FUNCTION $s ^(LIST ^(STRING[stripBraces($b)])) $y $c);
