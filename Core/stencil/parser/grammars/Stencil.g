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
  BASIC;         //Marker for specialization (BASIC vs. ORDER)
  CONSUMES;
  CALL_CHAIN;
  CANVAS_DEF;
  DIRECT_YIELD;
  DYNAMIC_RULE;
  FUNCTION;
  GLYPH_TYPE;        	//Indicate layer type
  GUIDE_GENERATOR;
  GUIDE_DIRECT;
  GUIDE_SUMMARIZATION;
  GUIDE_YIELD;
  LIST;
  MAP_ENTRY;
  NUMBER;
  OPERATOR_INSTANCE; //Operator, fully specified in stencil (either directly or through a template/specializer)
  OPERATOR_PROXY;    //Operator, specfied by base reference to an instance of an imported operator 
  OPERATOR_REFERENCE;//Operator, specified by base reference to a template or imported operator (instantiates to either a proxy or an instance)
  OPERATOR_TEMPLATE; //Template used to create an operator instance
  OPERATOR_RULE;     //Combination of filter, return and function calls in a operator
  OPERATOR_BASE;
  OPERATOR_FACET;
  POST;
  PRE;
  PREDICATE;
  PROGRAM;
  PACK;
  PYTHON_FACET;
  RESULT;		//Consumes blocks value that indicates the contextual result (e.g. glyph value, stream tuple or operator tuple); can only be derived, not specified
  RULE;
  SIGIL_ARGS;
  STATE_QUERY;    //List of entities ot check if state has changed
  SPECIALIZER;
  SELECTOR;		    //Indicate some part of a stencil
  STREAM_DEF;		//Root of a stream definition
  TUPLE_PROTOTYPE;
  TUPLE_FIELD_DEF;
  TUPLE_REF;
  
  //General Keywords
  ALL = 'ALL';        //Pattern that matches anything; range proxy for 1..n
  AS  = 'as';         //used in imports
  BASE  = 'base';     //Refer to the base entity
  CANVAS  = 'canvas';
  CONST = 'const';
  DEFAULT = 'default';
  FACET = 'facet';
  FILTER  = 'filter';
  FROM  = 'from';
  GUIDE = 'guide';
  IMPORT  = 'import';
  LAYER = 'layer';
  LAST ='LAST';     //Proxy value for n..n range
  LOCAL = 'local';  //Target to indicate temporary storage after filtering
  NULL = 'NULL';
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


  //Name manipulation
  NAMESPACE  = '::';
  
  //Boolean operators
   GT  = '>';
   GTE = '>='; 
   LT  = '<';  
   LTE = '<=';
   EQ  = '='; 
   NEQ = '!='; 
   RE  = '=~'; 
   NRE = '!~'; 
  


  //Bindings
  DEFINE  = ':';
  DYNAMIC = ':*';//Rules that should be periodically re-evaluated
  ANIMATED = '<:';
  ANIMATED_DYNAMIC = '<:*';

  //Value references
  DEFAULT_VALUE = '_';
  TUPLE_VALUE = '*';
  
  //Linkages
  YIELDS  = '->';   // 1:1
  MAP     = '>>';   // map
  FOLD    = '>-';   // reduce
  GATE    = '=>';   // test
  
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
  
  public String customArgsCall(String call) {
    return call.substring(SIGIL.length()) + NAME_SEPARATOR + CUSTOM_PARSER_FACET;  }
}

program : imports* globalValue* externalStream* order canvasLayer (streamDef | layerDef | operatorDef | pythonDef | operatorTemplate)*
    -> ^(PROGRAM  
          ^(LIST["Imports"] imports*) 
          ^(LIST["Global Values"] globalValue*)
          ^(LIST["Stream Declarations"] externalStream*)
          order 
          canvasLayer
          ^(LIST["Streams"] streamDef*) 
          ^(LIST["Layers"] layerDef*) 
          ^(LIST["Operators"] operatorDef* operatorTemplate*) 
          ^(LIST["Pythons"] pythonDef*));



//////////////////////////////////////////// PREAMBLE ///////////////////////////
imports
  : (IMPORT ID specializer AS) => IMPORT name=ID args=specializer AS as=ID
      -> ^(IMPORT[$name.text] $as $args)
  | (IMPORT ID AS) => IMPORT name=ID AS as=ID
      -> ^(IMPORT[$name.text] $as LIST["Arguments"])
  | (IMPORT ID specializer) => IMPORT name=ID args=specializer
      -> ^(IMPORT[$name.text] ID[""] $args)
  | IMPORT ID
      -> ^(IMPORT[$name.text] ID[""] LIST["Arguments"]);

order
  : ORDER orderRef ('>' orderRef)*
    -> ^(ORDER orderRef+)
  | -> ^(ORDER);

orderRef
  : ID -> ^(LIST["Streams"] ID)
  | GROUP ID ('|' ID)+ CLOSE_GROUP ->  ^(LIST["Streams"] ID+);

globalValue
  : (CONST ID atom) => CONST name=ID DEFINE atom
  | CONST name=ID;

externalStream: STREAM name=ID tuple[false] -> ^(STREAM[$name.text] tuple);

//////////////////////////////////////////// CANVAS & VIEW LAYER ///////////////////////////

canvasLayer
  : CANVAS name=ID specializer guideDef+ 
    -> ^(CANVAS_DEF[$name.text] specializer ^(LIST["Guides"] guideDef+))
  | -> ^(CANVAS_DEF["default"] ^(SPECIALIZER LIST) ^(LIST["Guides"]));

guideDef: GUIDE ID specializer FROM selector rule["result"]* 
			-> ^(GUIDE ID specializer selector ^(LIST["Rules"] rule*));

selector
  options{backtrack=true;}
  : att=ID DEFINE path+=ID+ -> ^(SELECTOR[$att.text] $att ^(LIST["path"] $path+))
  |               path+=ID+ -> ^(SELECTOR["DEFAULT"] DEFAULT ^(LIST["path"] $path+));

//////////////////////////////////////////// STREAM & LAYER ///////////////////////////

streamDef
  : STREAM name=ID tuple[true]  consumesBlock+
    -> ^(STREAM_DEF[$name.text] tuple ^(LIST["Consumes"] consumesBlock+));

layerDef
  : LAYER name=ID implantationDef defaultsBlock consumesBlock+
    -> ^(LAYER[$name.text] implantationDef defaultsBlock ^(LIST["Consumes"] consumesBlock+));
  
implantationDef
  : ARG type=ID CLOSE_ARG -> GLYPH_TYPE[$type.text]
  | -> GLYPH_TYPE[DEFAULT_GLYPH_TYPE];
    
defaultsBlock
  : DEFAULT rule["result"]+ -> ^(LIST["Defaults"] rule+)
  | -> ^(LIST["Defaults"]);
  
consumesBlock
  : FROM stream=ID filter* rule["result"]+ 
    -> ^(CONSUMES[$stream.text] ^(LIST["Filters"] filter*) ^(LIST["Rules"] rule+));

filter: FILTER! predicate;

//////////////////////////////////////////// OPERATORS ///////////////////////////

operatorTemplate : TEMPLATE OPERATOR name=ID -> ^(OPERATOR_TEMPLATE[$name.text]);
  
operatorDef
  : OPERATOR name=ID tuple[false] YIELDS tuple[false] pf=rule["prefilter"]* operatorRule+
    ->  ^(OPERATOR[$name.text] ^(YIELDS tuple tuple) ^(LIST["Prefilters"] $pf*) ^(LIST["Rules"] operatorRule+))
  | OPERATOR name=ID BASE base=ID specializer
    -> ^(OPERATOR_REFERENCE[$name.text] OPERATOR_BASE[$base.text] specializer);
  	  
operatorRule
  : predicate GATE rule["result"]+
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
  :(callName[MAP_FACET] specializer valueList) =>
   name=callName[MAP_FACET] specializer valueList
    -> ^(FUNCTION[((Tree)name.tree).getText()] specializer ^(LIST["args"] valueList))
  | name=callName[MAP_FACET] specializer emptySet
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
    -> {def.equals("prefilter")}? ^(PREFILTER tuple)
    -> {def.equals("result")}? ^(RESULT tuple)
    -> ^(DEFAULT tuple); //TODO: Can this case be removed???

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

specializer
  : ARG mapList CLOSE_ARG -> ^(SPECIALIZER mapList)
  | ARG CLOSE_ARG -> ^(SPECIALIZER LIST["Map Arguments"])
  | -> ^(SPECIALIZER DEFAULT);


mapList
  : mapEntry (SEPARATOR mapEntry)* -> ^(LIST["Map Arguments"] mapEntry*);
  
mapEntry 
  : k=ID DEFINE v=atom -> ^(MAP_ENTRY[$k.text] $v);

tuple[boolean allowEmpty] //TODO: Add optionally permitted types
  : emptySet {allowEmpty}?
    -> ^(TUPLE_PROTOTYPE)
  | ID
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT))
  | GROUP ID (SEPARATOR ID)* CLOSE_GROUP
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT)+);

emptySet: GROUP! CLOSE_GROUP!;

atomList: atom (SEPARATOR atom)* -> ^(LIST["Value Arguments"] atom*);
valueList:  GROUP! value (SEPARATOR! value)* CLOSE_GROUP!; 
		
value : tupleRef | atom;
atom  : number | STRING | DEFAULT | ALL | LAST | NULL;

tupleRef
  options{backtrack=true;}
  : simpleRef
  | simpleRef qualifiedRef+ -> ^(simpleRef qualifiedRef+);

private simpleRef
  : ID  -> ^(TUPLE_REF ID)
  | DEFAULT_VALUE -> ^(TUPLE_REF NUMBER["0"])
  | TUPLE_VALUE -> ^(TUPLE_REF ALL)
  | LAST -> ^(TUPLE_REF LAST)
  | ARG number CLOSE_ARG -> ^(TUPLE_REF number)
  | c=CANVAS -> ^(TUPLE_REF ID[$c.text])
  | l=LOCAL -> ^(TUPLE_REF ID[$l.text])
  | v=VIEW -> ^(TUPLE_REF ID[$v.text]);

private qualifiedRef 
  : ARG i=ID CLOSE_ARG -> ^(TUPLE_REF $i)
  | ARG n=number CLOSE_ARG -> ^(TUPLE_REF $n)
  | ARG DEFAULT_VALUE CLOSE_ARG -> ^(TUPLE_REF NUMBER["0"]);

booleanOp : GT |  GTE | LT | LTE | EQ | NEQ | RE | NRE;

passOp  
  : directYield
  | guideYield
  | MAP
  | FOLD;

directYield
  : '-[' id=ID ']>' -> ^(DIRECT_YIELD[$id.text])
  | YIELDS -> ^(DIRECT_YIELD[(String) null]);

guideYield
  : '-[' id=ID ']#>' -> ^(GUIDE_YIELD[$id.text])
  | '-#>' -> ^(GUIDE_YIELD[(String) null]);

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
@init{StringBuilder lBuf = new StringBuilder();}
    :   
           '"' 
           ( escaped= ESCAPE_SEQUENCE {lBuf.append(getText());} | 
             normal=~('"'|'\\'|'\n'|'\r')     {lBuf.appendCodePoint(normal);} )* 
           '"'     
           {setText(lBuf.toString());}
    ;

fragment
ESCAPE_SEQUENCE
    :   '\\'
        (       'n'    {setText("\n");}
        |       'r'    {setText("\r");}
        |       't'    {setText("\t");}
        |       'b'    {setText("\b");}
        |       'f'    {setText("\f");}
        |       '"'    {setText("\"");}
        |       '\''   {setText("\'");}
        |       '\\'   {setText("\\");}
        )
    ;

WS  : (' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();};
COMMENT :   '/*' (options {greedy=false;} :.)* '*/' {skip(); };
