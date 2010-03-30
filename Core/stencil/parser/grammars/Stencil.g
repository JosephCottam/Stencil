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
  AST_INVOKEABLE; //Holder for invokeables
  ANNOTATION;
  BOOLEAN_OP;    //Boolean operator
  BASIC;         //Marker for specialization (BASIC vs. ORDER)
  CONSUMES;
  CALL_CHAIN;
  DIRECT_YIELD;
  FUNCTION;
  GLYPH;        //Indicates the target is a layer's glyph (can only be derived, not specified)
  GUIDE_QUERY;
  GUIDE_GENERATOR;
  LIST;
  CANVAS_DEF;
  NUMBER;
  OPERATOR_INSTANCE; //Operator, fully specified in stencil (either directly or through a template/specializer)
  OPERATOR_PROXY;    //Operator, specfied by base reference to an instance of an imported operator 
  OPERATOR_REFERENCE;//Operator, specified by base reference to a template or imported operator (instantiates to either a proxy or an instance)
  OPERATOR_TEMPLATE; //Template used to create an operator instance
  OPERATOR_RULE;     //Combination of filter, return and function calls in a operator
  OPERATOR_BASE;
  POST;
  PRE;
  PREDICATE;
  PROGRAM;
  PACK;
  PYTHON_FACET;
  RETURN;       //Inidicates the target is an operator return value (can only be derived, not specified)
  RULE;
  SIGIL_ARGS;
  SPECIALIZER;
  TUPLE_PROTOTYPE;
  TUPLE_FIELD_DEF;
  TUPLE_REF;
  MAP_ENTRY;

  //General Keywords
  ALL = 'all'; //Default pattern
  AS  = 'as'; //used in imports
  BASE  = 'base'; //Refer to the base entity
  CANVAS  = 'canvas';
  DEFAULT = 'default';
  EXTERNAL= 'external';
  FACET = 'facet';
  FILTER  = 'filter';
  FROM  = 'from';
  GUIDE = 'guide';
  IMPORT  = 'import';
  LOCAL = 'local';  //Target to indicate temporary storage after filtering
  LAYER = 'layer';
  OPERATOR= 'operator';
  ORDER = 'order';
  PREFILTER = 'prefilter'; //Target to indicate actions that occure before filters
  PYTHON  = 'python';
  TEMPLATE= 'template';
  STREAM  = 'stream';
  VIEW  = 'view';

  //Markers
  GROUP   = '(';
  CLOSE_GROUP = ')';
  ARG     = '[';
  CLOSE_ARG = ']';
  SEPARATOR = ',';
  RANGE   = '..';


  //Name manipulation
  NAMESPACE  = '::';
  

  //Bindings
  DEFINE    = ':';
  DYNAMIC = ':*';//Rules that should be periodically re-evaluated
  ANIMATED = '<:';
  ANIMATED_DYNAMIC = '<*';


  //Operators
  YIELDS  = '->';
  FEED  = '>>';
  GUIDE_FEED  = '#>>';
  GUIDE_YIELD = '#->';

  GATE  = '=>';
  SPLIT = '|';
  JOIN  = '-->';
  TAG = '@';
}

@header{
  package stencil.parser.string;

  //TODO: Remove/delete glyph operation

  import static stencil.parser.ParserConstants.*;
  import java.util.ArrayList;
  import java.util.List;
  import stencil.parser.ParserConstants;
    
}

@lexer::header{
	package stencil.parser.string;
	import static stencil.tuple.Tuples.stripQuotes;	
}

@members {
  List<String> errors = new ArrayList<String>();
  boolean poolErrors = false;
  
  /**Buried IDs are strings that cannot be input as identifiers according to the
   * Stencil grammar, but are used internally as IDs.
   */
  public static String buryID(String input) {return "#" + input;}
  
  /**Should error messages be collected up and reported all at once?
   * If set to true, a list of errors will be returned after trying to parse.
   * If set to false, the first error ends the parsing.
   */
  public void poolErrors(boolean pool) {this.poolErrors = pool;}
  public void emitErrorMessage(String msg) {
  	if (poolErrors) {errors.add(msg);}
  	else {super.emitErrorMessage(msg);}
  }
  
  public List getErrors() {return errors;}


  public static enum RuleOpts {
    All,  //Anything is allowed 
    Simple, //Only argument lists (no split or range)
    Empty
  }; //Must be empty
  
  
  public String customArgsCall(String call) {
    return call.substring(SIGIL.length()) + NAME_SEPARATOR + CUSTOM_PARSER_FACET;
  }
}

program : imports* externals order canvasLayer (streamDef | layerDef | operatorDef | pythonDef | operatorTemplate)*
    -> ^(PROGRAM  
          ^(LIST["Imports"] imports*) 
          order 
          externals 
          canvasLayer
          ^(LIST["Layers"] layerDef*) 
          ^(LIST["Operators"] operatorDef*) 
          ^(LIST["Pythons"] pythonDef*) 
          ^(LIST["OperatorTemplates"] operatorTemplate*));



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


//////////////////////////////////////////// CANVAS & VIEW LAYER ///////////////////////////

canvasLayer
  : CANVAS name=ID canvasProperties guideDef+ 
    -> ^(CANVAS_DEF[$name.text] canvasProperties ^(LIST["Guides"] guideDef+))
  | -> ^(CANVAS_DEF["default"] ^(SPECIALIZER DEFAULT) ^(LIST["Guides"]));

guideDef: GUIDE type=ID spec=specializer[RuleOpts.Simple] FROM layer=ID attribute=ID rule["glyph"]* 
			-> ^(GUIDE[$attribute.text] $layer $type $spec ^(LIST["Rules"] rule*));

canvasProperties: specializer[RuleOpts.Simple]; 

//////////////////////////////////////////// STREAM & LAYER ///////////////////////////

streamDef
  : STREAM name=ID tuple[true]  (consumesBlock["return"])+
    -> ^(STREAM[$name.text] tuple ^(LIST["Consumes"] consumesBlock+))*;

layerDef
  : LAYER name=ID implantationDef defaultsBlock consumesBlock["glyph"]+
    -> ^(LAYER[$name.text] implantationDef defaultsBlock ^(LIST["Consumes"] consumesBlock+));
  
implantationDef
  : ARG type=ID CLOSE_ARG -> GLYPH[$type.text]
  | -> GLYPH[DEFAULT_GLYPH_TYPE];
    
defaultsBlock
  : DEFAULT rule["glyph"]+ -> ^(LIST["Defaults"] rule+)
  | -> ^(LIST["Defaults"]);
  
consumesBlock[String def]
  : FROM stream=ID filter* rule[def]+ 
    -> ^(CONSUMES[$stream.text] ^(LIST["Filters"] filter*) ^(LIST["Rules"] rule+));

filter: FILTER! predicate;

//////////////////////////////////////////// OPERATORS ///////////////////////////

operatorTemplate : TEMPLATE OPERATOR name=ID -> ^(OPERATOR_TEMPLATE[$name.text]);
  
operatorDef
  : OPERATOR  name=ID tuple[false] YIELDS tuple[false] operatorRule+
    ->  ^(OPERATOR[$name.text] ^(YIELDS tuple tuple) ^(LIST["Rules"] operatorRule+))
  | OPERATOR name=ID BASE base=ID specializer[RuleOpts.All]
    -> ^(OPERATOR_REFERENCE[$name.text] OPERATOR_BASE[$base.text] specializer);
  	  
operatorRule
  : predicate GATE rule["return"]+
    -> ^(OPERATOR_RULE predicate ^(LIST["Rules"] rule+));

/////////////////////////////////////////  CALLS  ////////////////////////////////////
rule[String def]
  : target[def] (DEFINE | DYNAMIC) callChain
    -> ^(RULE target callChain DEFINE? DYNAMIC?);

callChain: callChainMember -> ^(CALL_CHAIN callChainMember);
callChainMember
  : value -> ^(PACK value)
  | emptySet -> ^(PACK)
  | valueList -> ^(PACK valueList)
  | functionCallTarget;
  
  
functionCallTarget
  : (functionCall passOp)=> f1=functionCall passOp f2=callChainMember 
     -> ^($f1 passOp $f2)
  | f1=functionCall -> ^($f1 DIRECT_YIELD ^(PACK DEFAULT));
   

functionCall
  :(callName[MAIN_FACET] specializer[RuleOpts.All] valueList) =>
   name=callName[MAIN_FACET] specializer[RuleOpts.All] valueList
    -> ^(FUNCTION[((Tree)name.tree).getText()] specializer ^(LIST["args"] valueList))
  | name=callName[MAIN_FACET] specializer[RuleOpts.All] emptySet
    -> ^(FUNCTION[((Tree)name.tree).getText()] specializer ^(LIST["args"]))
  | t=TAGGED_ID ISLAND_BLOCK
    -> ^(FUNCTION[customArgsCall($t.text)] ^(SPECIALIZER DEFAULT) ISLAND_BLOCK);  

//Apply defaultCall to functions that have no explicit call
callName[String defaultCall]
  : pre=ID NAMESPACE post=ID 
    -> {post.getText().indexOf(".") > 0}? ID[$pre.text + NAMESPACE + $post.text]
    ->                    ID[$pre.text + NAMESPACE + $post.text + "." + defaultCall]
  | name=ID
    -> {name.getText().indexOf(".") > 0}? ID[$name.text] 
    ->                    ID[$name.text + "." + defaultCall];

target[String def]
  : PREFILTER^ tuple[false]
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
	: FACET 'init' ISLAND_BLOCK
		-> ^(PYTHON_FACET["init"] ^(YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE) ^(LIST["Annotations"] ^(ANNOTATION["Type"] STRING["NA"])) ISLAND_BLOCK)
	| annotations FACET name=ID tuple[true] YIELDS tuple[false] ISLAND_BLOCK
		-> ^(PYTHON_FACET[name] ^(YIELDS tuple tuple) annotations ISLAND_BLOCK);

annotations
  : annotation+ -> ^(LIST["Annotations"] annotation+)
  | -> ^(LIST["Annotations"] ^(MAP_ENTRY["TYPE"] STRING["CATEGORIZE"]));

//Upper case and remove tag-character
annotation: t=TAGGED_ID GROUP atom CLOSE_GROUP -> ^(MAP_ENTRY[$t.text.toUpperCase().substring(1)] atom); 

//////////////////////////////////////////// GENERAL OBJECTS ///////////////////////////
predicate
  : GROUP? ALL CLOSE_GROUP?
    -> ^(LIST["Predicates"] ^(PREDICATE ALL))
  | GROUP value booleanOp value (SEPARATOR value booleanOp value)* CLOSE_GROUP
    -> ^(LIST["Predicates"] ^(PREDICATE value booleanOp value)+);

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
  | (values SEPARATOR mapList)=> values SEPARATOR! mapList
  | values -> values ^(LIST["Map Arguments"])
  | mapList -> ^(LIST["Value Arguments"]) mapList;

values
  : atom (SEPARATOR atom)* -> ^(LIST["Value Arguments"] atom*);

mapList
  : mapEntry (SEPARATOR mapEntry)* -> ^(LIST["Map Arguments"] mapEntry*);
  
mapEntry 
  : k=ID '=' v=atom -> ^(MAP_ENTRY[$k.text] $v);

tuple[boolean allowEmpty] //TODO: Add optionally permitted types [boolean allowEmpty]
  : emptySet {allowEmpty}?
    -> ^(TUPLE_PROTOTYPE)
  | ID
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT))
  | GROUP ID (SEPARATOR ID)* CLOSE_GROUP
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT)+);

emptySet: GROUP! CLOSE_GROUP!;

valueList:  GROUP! value (SEPARATOR! value)* CLOSE_GROUP!;  //TODO: combine with 'values' above...
		
range
  : number RANGE number
    -> ^(RANGE number number)
  | number RANGE i=ID
    {$i.text.equals(FINAL_VALUE)}?
    -> ^(RANGE number NUMBER[RANGE_END])
  | s=ID RANGE e=ID
    {$s.text.equals(FINAL_VALUE) && $e.text.equals(FINAL_VALUE)}?
    -> ^(RANGE NUMBER[RANGE_END] NUMBER[RANGE_END]);


split[boolean pre]
  : ID  -> {pre}? ^(SPLIT BASIC PRE ID)
        ->        ^(SPLIT BASIC POST ID)
    | ORDER ID
        -> {pre}? ^(SPLIT ORDER PRE ID)
        ->        ^(SPLIT ORDER  POST ID);

value : tupleRef |  atom;
atom  : number | STRING | DEFAULT | ALL;  //TODO: Does this need to be here, now that there is separate filterRule branch?

tupleRef
  : simpleRef
  | simpleRef qualifiedRef+ -> ^(simpleRef qualifiedRef+);

private simpleRef
  : ID  -> ^(TUPLE_REF ID)
  | '_' -> ^(TUPLE_REF NUMBER["0"])
  | ARG number CLOSE_ARG -> ^(TUPLE_REF number)
  | c=CANVAS -> ^(TUPLE_REF ID[$c.text])
  | l=LOCAL -> ^(TUPLE_REF ID[$l.text])
  | v=VIEW -> ^(TUPLE_REF ID[$v.text]);

private qualifiedRef 
  : ARG i=ID CLOSE_ARG -> ^(TUPLE_REF $i)
  | ARG n=number CLOSE_ARG -> ^(TUPLE_REF $n)
  | ARG '_' CLOSE_ARG -> ^(TUPLE_REF NUMBER["0"]);


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
  : directYield
  | GUIDE_YIELD; 
    //TODO: Add feed

directYield
  : '-[' id=ID ']>' -> ^(DIRECT_YIELD[$id.text])
  | YIELDS -> ^(DIRECT_YIELD[(String) null]);


//Numbers may be integers or doubles, signed or unsigned.  These rules turn number parts into a single number.
number  :  doubleNum | intNum;

private intNum
  : (n='-' | p='+') d=DIGITS -> ^(NUMBER[p!=null?"+":"-" + $d.text])
  | d=DIGITS -> ^(NUMBER[$d.text]);

private doubleNum
  : '.' d2=DIGITS -> ^(NUMBER["0." + $d2.text])
  | d=DIGITS '.' d2=DIGITS -> ^(NUMBER[$d.text + "." + $d2.text])
  | (n='-' | p='+') d=DIGITS '.' d2=DIGITS -> ^(NUMBER[p!=null?"+":"-" + $d.text + "." + $d2.text])
  | (n='-' | p='+') '.' d2=DIGITS -> ^(NUMBER[p!=null?"+":"-" + "." + $d2.text]);


TAGGED_ID: TAG ID;

ID    : ('a'..'z' | 'A'..'Z' | '_') ('.'? ('a'..'z' | 'A'..'Z' | '_' | '0'..'9'))*;

DIGITS  : '0'..'9'+;


ISLAND_BLOCK
    : NESTED_BLOCK; //Strip braces

fragment 
NESTED_BLOCK
    : '{' (options {greedy=false;k=2;}: NESTED_BLOCK | .)* '}';

STRING
      :  '"' ( ESCAPE_SEQUENCE | ~('\\'|'"') )* '"'
        {setText(stripQuotes($text));}; //Strip the quotes

fragment
ESCAPE_SEQUENCE
    : '\\b' {setText($text.substring(0, $text.length()-2) + "\b");} 
    | '\\t' {setText($text.substring(0, $text.length()-2) + "\t");}
    | '\\n' {setText($text.substring(0, $text.length()-2) + "\n");}
    | '\\f' {setText($text.substring(0, $text.length()-2) + "\f");}
    | '\\r' {setText($text.substring(0, $text.length()-2) + "\r");}
    | '\\\"'{setText($text.substring(0, $text.length()-2) + "\"");}
    | '\\\''{setText($text.substring(0, $text.length()-2) + "\'");}
    | '\\\\'{setText($text.substring(0, $text.length()-2) + "\\");};


WS  : (' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();};
COMMENT :   '/*' (options {greedy=false;} :.)* '*/' {skip(); };