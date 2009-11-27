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
grammar ModuleData;
options {
	language = Java;
	ASTLabelType = CommonTree;	
	
	output=AST;
}

tokens {
	CLASS = 'Class';
	DEFAULTS = 'Defaults';
	DESC = 'Description';
	MODULE_DATA = 'ModuleData';
	OPERATOR = 'Operator';
	FACET = 'Facet';
	FACETS = 'Facets';
	OPT = 'Opt';
	OPTS = 'Opts';
	
	NAME = 'name';
	REV = 'grammarRev';
	TARGET = 'target';
		
	OPEN = '<';
	CLOSE ='\>';
	TERM = '/>';
	TERM_OPEN = '</';
	
	DEF = '=';
}

@header{
	package stencil.operator.module.util;
	
	import java.io.*;
	import stencil.parser.string.ParseStencil;	
	import stencil.operator.module.ModuleData;
	import static stencil.tuple.Tuples.stripQuotes;	
}

@lexer::header{
  package stencil.operator.module.util;
}

@members {
	public static final String REVISION = "\"2\"";
	
	protected Tree defaultFacets;
	protected Tree defaultOpts;
		
	/**Parse the contents of the passed reader.**/
	public static ModuleData parse(BufferedReader reader) throws Exception {
    String line = reader.readLine();
    StringBuilder source = new StringBuilder();
    while (line !=null) {source.append(line); line = reader.readLine();}

  
		ANTLRStringStream input = new ANTLRStringStream(source.toString());
    
		ModuleDataLexer lexer = new ModuleDataLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		ModuleDataParser parser = new ModuleDataParser(tokens);
		parser.setTreeAdaptor(new MDTreeAdapter());
		ModuleData t= (ModuleData) parser.moduleData().getTree();
		return t;
	}
	
	
	//TODO: Remove when all references to this are removed.  Resolution is by absolute file name only...BAD!!!!
	public static ModuleData parse(String filename) throws Exception {
		StringBuilder source = new StringBuilder();
		BufferedReader file = new BufferedReader(new FileReader(filename));
		return parse(file);
	}

	protected void setDefaultFacets(Tree defaultFacets) {this.defaultFacets=defaultFacets;}
	protected Tree getDefaultFacets() {return (Tree) adaptor.dupTree(defaultFacets);}
	
	protected void setDefaultOperatorOpts(Tree defaultOpts) {this.defaultOpts = defaultOpts;}
	
	//Get the default operator options, but splice in the given child node to the options as well.
	protected Tree getDefaultOperatorOpts(Tree... children) {
		Tree t = (Tree) adaptor.dupTree(defaultOpts);
        if (children != null) {
        	for (Tree child: children) {adaptor.addChild(t, child);}
        }		
		return t;
	}

	protected void mismatch(IntStream input, int type, BitSet follow) 
		throws RecognitionException 
	{
		throw new MismatchedTokenException(type, input);
	}
	
	public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) 
		throws RecognitionException
	{
		throw e;	
	}

}

//@rulecatch {catch (RecognitionException e) {throw e;}}

moduleData
	: OPEN MODULE_DATA r=rev n=name CLOSE 
	  {if (!r.ver.equals(REVISION)) {
		String message = "Revision number not recognized. Found "+ r.ver + ", expected " + REVISION + ".";
		throw new RuntimeException(message);}
	  } 
	  clss description defaults operator+ TERM_OPEN MODULE_DATA CLOSE
		-> ^(MODULE_DATA[$n.name] clss defaults description operator+ );


rev returns[String ver]: REV DEF v=VAL {$ver = $v.text;};

clss: OPEN CLASS n=name TERM -> CLASS[$n.name];

description: OPEN DESC CLOSE d=CDATA TERM_OPEN DESC CLOSE -> DESC[$d.text]
			| -> DESC[""];

defaults: OPEN! DEFAULTS^ CLOSE! o=operatorOpts f=facets TERM_OPEN! DEFAULTS! CLOSE!
		{setDefaultOperatorOpts(o.tree); setDefaultFacets(f.tree);};

operator
	: OPEN OPERATOR n=name t=target[$n.name] TERM 
		-> ^(OPERATOR[$n.name] {getDefaultOperatorOpts((Tree) $t.tree)} {getDefaultFacets()})
	
	| OPEN OPERATOR n=name t=target[$n.name] CLOSE facet+ TERM_OPEN OPERATOR CLOSE
		-> ^(OPERATOR[$n.name] {getDefaultOperatorOpts((Tree) $t.tree)} ^(FACETS facet+))
	
	| OPEN OPERATOR n=name target[$n.name] CLOSE operatorOpt+ TERM_OPEN OPERATOR CLOSE
		-> ^(OPERATOR[$n.name] ^(OPTS target operatorOpt+) {getDefaultFacets()})
	
	| OPEN OPERATOR n=name target[$n.name] CLOSE operatorOpt+ facet+ TERM_OPEN OPERATOR CLOSE
		-> ^(OPERATOR[$n.name] ^(OPTS target operatorOpt+) ^(FACETS facet+));

operatorOpts: OPEN OPTS CLOSE operatorOpt+ TERM_OPEN OPTS CLOSE -> ^(OPTS operatorOpt+);
operatorOpt: OPEN OPT vp=valuePair TERM -> ^(OPT[vp.tree.getText()] {vp.tree.getChild(0)});

facets: OPEN FACETS CLOSE facet+ TERM_OPEN FACETS CLOSE -> ^(FACETS facet+);
facet : OPEN FACET n=name valuePair+ TERM -> ^(FACET[$n.name] valuePair+);

target[String name]
	: TARGET '=' id=VAL -> ^(OPT["Target"] VAL[stripQuotes($id.text.toUpperCase())])
	| -> ^(OPT["Target"] VAL[name.toUpperCase()]);
	
name returns [String name]: NAME DEF^ v=VAL {$name=stripQuotes($v.text);};

valuePair : id=ID DEF v=VAL -> ^(DEF[stripQuotes($id.text)] VAL[stripQuotes($v.text)]);
	
VAL   : '"' (options {greedy=false;} :.)* '"';

ID    : ('a'..'z' | 'A'..'Z' | '_') ('.'? ('a'..'z' | 'A'..'Z' | '_' | '0'..'9'))*;

CDATA : '<![CDATA[' (options {greedy=false;} :.)* ']]>';

WS	:	(' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();};
COMMENT :  	'<!--' (options {greedy=false;} :.)* '-->' {skip(); };