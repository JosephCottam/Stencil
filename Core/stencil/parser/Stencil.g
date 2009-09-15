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
grammar Stencil;
options {
	language = Java;
	output=AST;
}

tokens {
	ANNOTATION;
	BOOLEAN_OP;		//Boolean operator
	BASIC;			//Marker for specialization (BASIC vs. ORDER)
	CONSUMES;
	CALL_GROUP;
	CALL_CHAIN;
	FUNCTION;
	GUIDE;
	LEGEND_RULE;	//Combination of filter, return and function calls in a legend
	LIST;
	NUMBER;
	POST;
	PRE;
	PREDICATE;
	PROGRAM;
	PACK;
	RULE;
	SIGIL;
	SPECIALIZER;
	TUPLE_PROTOTYPE;
	TUPLE_REF;
	MAP_ENTRY;


	//General Keywords
	ALL			= 'all';	//Default pattern
	BASE		= 'base';	//Refer to the base entity
	CANVAS		= 'canvas';
	COLOR		= 'color';	//Refer to a color
	DEFAULT 	= 'default';//Use the default value (or revert to it...)
	EXTERNAL	= 'external';
	FILTER		= 'filter';
	FROM 		= 'from';
	GLYPH		= 'glyph';
	IMPORT		= 'import';
	LOCAL		= 'local';	//Target to indicate temporary storage
	LAYER		= 'layer';
	LEGEND		= 'legend';
	ORDER		= 'order';
	PYTHON  	= 'python';
	RETURN		= 'return';
	STATIC		= 'static';	//vs. dynamic in legends (this is the default type)
	STREAM		= 'stream';
	VIEW 		= 'view';
	AS			= 'as'; //used in imports
	FACET		= 'facet';

	//Markers
	GROUP		= '(';
	CLOSE_GROUP	= ')';
	ARG			= '[';
	CLOSE_ARG	= ']';
	SEPARATOR	= ',';
	RANGE		= '..';


	//Name manipulation
	NAMESPACE  = '::';
	NAMESPLIT  = '.';
	

	//Bindings
	DEFINE		= ':';
	DYNAMIC	= '<<';//Rules that should be periodically re-evaluated

	//Operators
	YIELDS	= '->';
	FEED	= '>>';
	GUIDE_FEED	= '#>>';
	GUIDE_YIELD	= '#->';

	GATE	= '=>';
	SPLIT	= '|';
	JOIN	= '-->';
	TAG = '@';
}


@header{
	package stencil.parser.string;

	//TODO: Include base types in layers
	//TODO: Remove/delete glyph operation
	//TODO: Replacement of identifiers with numbers in tuples
	//TOOD: Modular color space

	import static stencil.parser.ParserConstants.*;
	import java.util.ArrayList;
	import java.util.List;
	
	
}
@lexer::header{
	package stencil.parser.string;
	import static stencil.util.Tuples.stripQuotes;
}

@members {
	List<String> errors = new ArrayList<String>();

	/**Buried IDs are strings that cannot be input as identifiers according to the
	 * Stencil grammar, but are used internally as IDs.
	 */
	public static String buryID(String input) {return "#" + input;}
	
	public void emitErrorMessage(String msg) {errors.add(msg);}
	public List getErrors() {return errors;}


	public static enum RuleOpts {
		All, 	//Anything is allowed 
		Simple,	//Only argument lists (no split or range)
		Empty}; //Must be empty
}

program	: imports* externals order (streamDef | layerDef | legendDef | pythonDef)*
		-> ^(PROGRAM  ^(LIST["Imports"] imports*) order externals ^(LIST["Layers"] layerDef*) ^(LIST["Legends"] legendDef*) ^(LIST["Pythons"] pythonDef*));



//////////////////////////////////////////// PREAMBLE ///////////////////////////
imports
	: IMPORT name=ID (ARG args=argList CLOSE_ARG)? (AS as=ID)? 
			-> {as==null && args==null}? ^(IMPORT[$name.text] ID[""] LIST["Arguments"])
			-> {as==null && args!=null}? ^(IMPORT[$name.text] ID[""] $args)
			-> {as!=null && args==null}? ^(IMPORT[$name.text] $as LIST["Arguments"])
			-> ^(IMPORT[$name.text] $as $args);	

order
	: ORDER orderRef ('>' orderRef)*
		-> ^(ORDER orderRef+)
	| -> ^(ORDER);

orderRef
	: ID -> ^(LIST["Streams"] ID)
	| GROUP ID (SPLIT ID)+ CLOSE_GROUP ->  ^(LIST["Streams"] ID+);

externals: externalStream* -> ^(LIST["Externals"] externalStream*);
externalStream: EXTERNAL STREAM name=ID tuple[false] -> ^(EXTERNAL[$name.text] tuple);



//////////////////////////////////////////// STREAM & LAYER ///////////////////////////

streamDef
	: STREAM name=ID tuple[true]  (consumesBlock["return"])+
		-> ^(STREAM[$name.text] tuple ^(LIST["Consumes"] consumesBlock+))*;

layerDef
	: LAYER name=ID implantationDef guidesBlock consumesBlock["glyph"]+
		-> ^(LAYER[$name.text] implantationDef guidesBlock ^(LIST["Consumes"] consumesBlock+));
	
implantationDef
	: ARG type=ID CLOSE_ARG -> GLYPH[$type.text]
	| -> GLYPH[DEFAULT_GLYPH_TYPE];
	
guidesBlock
	: (ID specializer[RuleOpts.Simple] DEFINE ID)* 
		-> ^(LIST["Guides"] ^(GUIDE ID specializer ID)*);
	
consumesBlock[String def]
	: FROM stream=ID filterRule* rule[def]+ 
		-> ^(CONSUMES[$stream.text] ^(LIST["Filters"] filterRule*) ^(LIST["Rules"] rule+));

filterRule
	: FILTER rulePredicate DEFINE callGroup
		-> ^(FILTER rulePredicate callGroup);

rulePredicate
	: GROUP ALL CLOSE_GROUP
		-> ^(LIST["Predicates"] ^(PREDICATE ALL))
	| GROUP value booleanOp value (SEPARATOR value booleanOp value)* CLOSE_GROUP
		-> ^(LIST["Predicates"] ^(PREDICATE value booleanOp value)+);



//////////////////////////////////////////// LEGEND ///////////////////////////

legendDef
	: LEGEND name=ID tuple[false] YIELDS tuple[false] legendRule+
		-> 	^(LEGEND[$name.text] ^(YIELDS tuple tuple) ^(LIST["Rules"] legendRule+));
		
legendRule
	: predicate GATE rule["return"]+
		-> ^(LEGEND_RULE predicate ^(LIST["Rules"] rule+));

predicate
	: GROUP? ALL CLOSE_GROUP?
		-> ^(LIST["Predicates"] ^(PREDICATE ALL))
  | GROUP value booleanOp value (SEPARATOR value booleanOp value)* CLOSE_GROUP
    -> ^(LIST["Predicates"] ^(PREDICATE value booleanOp value)+);
//TODO : Permit call groups in predicates again...or come up with a better mechanism, like the one used in filter targets....
//	| GROUP callGroup booleanOp callGroup (SEPARATOR callGroup booleanOp callGroup)* CLOSE_GROUP
//		-> ^(LIST["Predicates"] ^(PREDICATE callGroup booleanOp callGroup)+);


/////////////////////////////////////////  CALLS  ////////////////////////////////////
rule[String def]
	: target[def] (DEFINE | DYNAMIC) callGroup
		-> ^(RULE target callGroup DEFINE? DYNAMIC?);

callGroup
	: (callChain SPLIT)=> callChain (SPLIT callChain)+ JOIN callChain
		-> ^(CALL_GROUP callChain+)
	| callChain
		-> ^(CALL_GROUP callChain);

callChain: callTarget -> ^(CALL_CHAIN callTarget);

callTarget
	: value -> ^(PACK value)
	| valueList -> ^(PACK valueList)
	| emptySet -> ^(PACK)
    | f1=functionCall -> ^($f1 YIELDS ^(PACK DEFAULT))
	| f1=functionCall passOp f2=callTarget 
	   -> ^($f1 passOp $f2);


functionCall
	: name=callName[MAIN_BLOCK_TAG] specializer[RuleOpts.All] valueList
		-> ^(FUNCTION[((Tree)name.tree).getText()] specializer ^(LIST["args"] valueList));

//Apply defaultCall to functions that have no explicit call
callName[String defaultCall]
	: pre=ID NAMESPACE post=ID 
		-> {post.getText().indexOf(".") > 0}? ID[$pre.text + NAMESPACE + $post.text]
		-> 									  ID[$pre.text + NAMESPACE + $post.text + "." + defaultCall]
	| name=ID
		-> {name.getText().indexOf(".") > 0}? ID[$name.text] 
		-> 									  ID[$name.text + "." + defaultCall];

target[String def]
	: GLYPH^ tuple[false]
	| RETURN^ tuple[false]
	| CANVAS^ tuple[false]
	| LOCAL^ tuple[false]
	| VIEW^ tuple[false]
	| tuple[true]
		-> {def.equals("glyph")}? ^(GLYPH tuple)
		-> {def.equals("return")}? ^(RETURN tuple)
		-> ^(DEFAULT tuple);

//////////////////////////////////////////// PYTHON ///////////////////////////

pythonDef
	: (PYTHON ARG) => PYTHON ARG env=ID CLOSE_ARG name=ID pythonBlock+
		-> ^(PYTHON[$name.text] ID pythonBlock+)
	| PYTHON name=ID pythonBlock+
		-> ^(PYTHON[$name.text] ID[buryID($name.text)] pythonBlock+);
	
pythonBlock
	: FACET 'Init' CODE_BLOCK
		-> ^(FACET["Init"] ^(YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE) ^(LIST["Annotations"] ^(ANNOTATION["Type"] STRING["NA"])) CODE_BLOCK)
	| annotations FACET name=ID tuple[true] YIELDS tuple[false] CODE_BLOCK
		-> ^(FACET[name] ^(YIELDS tuple tuple) annotations CODE_BLOCK);
	
annotations
	: a=annotation -> ^(LIST["Annotations"] ^(ANNOTATION["TYPE"] STRING[$a.text.toUpperCase().substring(1)]))
	| -> ^(LIST["Annotations"] ^(ANNOTATION["TYPE"] STRING["CATEGORIZE"]));

annotation: t=TAGGED_ID -> ANNOTATION["JUNK"]; //Remove the tag and convert to upper case

//////////////////////////////////////////// GENERAL OBJECTS ///////////////////////////

specializer[RuleOpts opts]
	: ARG range sepArgList CLOSE_ARG
		{opts == RuleOpts.All}? -> ^(SPECIALIZER range ^(SPLIT BASIC PRE ID[(String) null]) sepArgList)
	| ARG split[false] sepArgList CLOSE_ARG
		{opts == RuleOpts.All}? -> ^(SPECIALIZER ^(RANGE NUMBER[RANGE_END] NUMBER[RANGE_END]) split sepArgList)
	| ARG range SPLIT split[false] sepArgList CLOSE_ARG
		{opts == RuleOpts.All}? ->  ^(SPECIALIZER range split sepArgList)
	| ARG split[true] SPLIT range sepArgList CLOSE_ARG
		{opts == RuleOpts.All}? -> ^(SPECIALIZER range split sepArgList)
	| ARG argList CLOSE_ARG
		{opts != RuleOpts.Empty}? -> ^(SPECIALIZER ^(RANGE NUMBER[RANGE_END] NUMBER[RANGE_END]) ^(SPLIT BASIC PRE ID[(String) null]) argList)
	| -> ^(SPECIALIZER DEFAULT);

sepArgList
	: SEPARATOR! argList
	| -> ^(LIST["Values Arguments"]) ^(LIST["Map Arguments"]);
	 
argList
	: -> ^(LIST["Values Arguments"]) ^(LIST["Map Arguments"])
	| values -> values ^(LIST["Map Arguments"])
	| mapList -> ^(LIST["Value Arguments"]) mapList
	| values SEPARATOR! mapList;

values
	: atom (SEPARATOR atom)* -> ^(LIST["Value Arguments"] atom*);

mapList
	: mapEntry (SEPARATOR mapEntry)* -> ^(LIST["Map Arguments"] mapEntry*);
	
mapEntry 
	: k=ID '=' v=atom -> ^(MAP_ENTRY[$k.text] $v);

tuple[boolean allowEmpty]
	: emptySet {allowEmpty}?
		-> ^(TUPLE_PROTOTYPE)
	| ID
		-> ^(TUPLE_PROTOTYPE ID)
	| GROUP ID (SEPARATOR ID)* CLOSE_GROUP
		-> ^(TUPLE_PROTOTYPE ID+);


emptySet:	GROUP! CLOSE_GROUP!;

valueList:	GROUP! value (SEPARATOR! value)* CLOSE_GROUP!;  //TODO: combine with 'values' above...

range
	: number RANGE number
		-> ^(RANGE number number)
	| number RANGE 'n'
		-> ^(RANGE number NUMBER[RANGE_END])
	| 'n' RANGE 'n'
		-> ^(RANGE NUMBER[RANGE_END] NUMBER[RANGE_END]);


split[boolean pre]
	: ID  -> {pre}? ^(SPLIT BASIC PRE ID)
		  ->       ^(SPLIT BASIC POST ID)
    | ORDER ID
    	-> {pre}? ^(SPLIT ORDER PRE ID)
		->       ^(SPLIT ORDER  POST ID);

value	: tupleRef |  atom;
atom 	: sigil | number | STRING | DEFAULT	| ALL; 	//TODO: Does this need to be here, now that there is separate filterRule branch?

tupleRef
	: ID -> ^(TUPLE_REF ID)
	| '_' -> ^(TUPLE_REF NUMBER["0"])
//	| qualifiedID ->  ^(TUPLE_REF qualifiedID)		//TODO: Implement sequences ([1][1][2]) and named (LOCAL[1])
	| ARG number CLOSE_ARG -> ^(TUPLE_REF number);

qualifiedID : ID^ ARG! number CLOSE_ARG!;

sigil: t=TAGGED_ID sValueList -> ^(SIGIL[$t.text] sValueList);
private sValueList:  GROUP! sValue (SEPARATOR! sValue)* CLOSE_GROUP!;
private sValue : tupleRef | number | STRING;


booleanOp
	: t= '>'  -> BOOLEAN_OP[t]
	| t= '>=' -> BOOLEAN_OP[t]
	| t= '<'  -> BOOLEAN_OP[t]
	| t= '<=' -> BOOLEAN_OP[t]
	| t= '='  -> BOOLEAN_OP[t]
	| t= '!=' -> BOOLEAN_OP[t]
	| t= '=~' -> BOOLEAN_OP[t]
	| t= '!~' -> BOOLEAN_OP[t];


passOp	
  : YIELDS
	| GUIDE_YIELD; 
		//TODO: Add feed


//Numbers may be integers or doubles, signed or unsigned.  These rules turn number parts into a single number.
number	:  doubleNum | intNum;

intNum
	: (n='-' | p='+') d=DIGITS -> ^(NUMBER[p!=null?"+":"-" + $d.text])
	| d=DIGITS -> ^(NUMBER[$d.text]);

doubleNum
	: '.' d2=DIGITS -> ^(NUMBER["0." + $d2.text])
	| d=DIGITS '.' d2=DIGITS -> ^(NUMBER[$d.text + "." + $d2.text])
	| (n='-' | p='+') d=DIGITS '.' d2=DIGITS -> ^(NUMBER[p!=null?"+":"-" + $d.text + "." + $d2.text]);


TAGGED_ID: TAG ID;

ID 		: ('a'..'z' | 'A'..'Z' | '_') ('.'? ('a'..'z' | 'A'..'Z' | '_' | '0'..'9'))*;

DIGITS 	: '0'..'9'+;


CODE_BLOCK
		: NESTED_BLOCK {setText($text.substring(1, $text.length()-1));}; //Strip braces

fragment 
NESTED_BLOCK
    : '{' (options {greedy=false;k=2;}: NESTED_BLOCK | .)* '}';


STRING
    	:  '"' ( ESCAPE_SEQUENCE | ~('\\'|'"') )* '"'
    		{setText(stripQuotes($text));}; //Strip the quotes

fragment
ESCAPE_SEQUENCE
		: '\\b' {setText($text.substring(0, $text.length()-2) + "\bp");} //'p' is for pad, which seems striped of at return...don't know why
		| '\\t' {setText($text.substring(0, $text.length()-2) + "\tp");} //TODO: Figure out how to do this without the pad character (or the reason why its required)
		| '\\n' {setText($text.substring(0, $text.length()-2) + "\np");}
		| '\\f' {setText($text.substring(0, $text.length()-2) + "\fp");}
		| '\\r' {setText($text.substring(0, $text.length()-2) + "\rp");}
		| '\\\"'{setText($text.substring(0, $text.length()-2) + "\"p");}
		| '\\\''{setText($text.substring(0, $text.length()-2) + "\'p");}
		| '\\\\'{setText($text.substring(0, $text.length()-2) + "\\p");};


WS	:	(' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();};
COMMENT :  	'/*' (options {greedy=false;} :.)* '*/' {skip(); };
