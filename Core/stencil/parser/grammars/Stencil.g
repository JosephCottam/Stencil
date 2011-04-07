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
  DYNAMIC_REDUCER;
  FUNCTION;
  GUIDE_GENERATOR;
  GUIDE_DIRECT;
  GUIDE_SUMMARIZATION;
  LIST_ARGS;
  LIST_CONSUMES;
  LIST_FILTERS;
  LIST_GUIDES;
  LIST_GUIDE_GENERATORS;
  LIST_GUIDE_MONITORS;
  LIST_GUIDE_SAMPLERS;
  LIST_GLOBALS;
  LIST_IMPORTS;
  LIST_JAVAS;
  LIST_LAYERS;
  LIST_OPERATORS;
  LIST_PREDICATES;
  LIST_RULES;
  LIST_SELECTORS;
  LIST_STREAMS;
  LIST_STREAM_DECLS;
  LIST_STREAM_DEFS;
  LIST_TEMPLATES;
  MAP_ENTRY;
  NUMBER;
  OPERATOR_INSTANCE; //Operator, fully specified in stencil (either directly or through a template/specializer)
  OPERATOR_PROXY;    //Operator, specfied by base reference to an instance of an imported operator 
  OPERATOR_REFERENCE;//Operator, specified by base reference to a template or imported operator (instantiates to either a proxy or an instance)
  OPERATOR_TEMPLATE; //Template used to create an operator instance
  OPERATOR_RULE;     //Combination of filter, return and function calls in a operator
  OPERATOR_BASE;
  OPERATOR_FACET;
  OP_AS_ARG;         //Identifies when an operator appears in argument position (requires special resolution)
  POST;
  PRE;
  PREDICATE;
  PROGRAM;
  PACK;
  RESULT;		//Consumes blocks value that indicates the contextual result (e.g. glyph value, stream tuple or operator tuple); can only be derived, not specified

  RULE;
  RULES_CANVAS;
  RULES_DEFAULTS;
  RULES_DYNAMIC;
  RULES_FILTER;
  RULES_LOCAL;
  RULES_OPERATOR;   //List of rules in an operator
  RULES_PREDICATES;
  RULES_PREFILTER;  
  RULES_RESULT;
  RULES_VIEW;

  PROTOTYPE_ARG;
  PROTOTYPE_RESULT;

  SAMPLE_OPERATOR;//Guide system sample-operator node
  SAMPLE_TYPE;    //Guide system sample-operator indicator  
  STATE_QUERY;    //List of entities ot check if state has changed
  SPECIALIZER;
  SELECTOR;		    //Indicate some part of a stencil
  MONITOR_OPERATOR;  //Guide system monitor-operator node; monitor operators collect and report the status of memory-of-interest
  STREAM_DEF;		  //Root of a stream definition
  TARGET;
  TUPLE_PROTOTYPE;
  TUPLE_FIELD_DEF;
  TUPLE_REF;
  TYPE;
  
  //General Keywords
  ALL = 'ALL';        //Pattern that matches anything; range proxy for 1..n
  CANVAS  = 'canvas';
  CONST = 'const';
  DEFAULT = 'default';
  ELEMENT = 'element';
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
  JAVA  = 'java';
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
  YIELDS  	 	 = '->';   // 1:1
  GUIDE_YIELD    = '-#>';   // 1:1, but moves the sample operator
  GATE  	  	 = '=>';   // test
  
  TAG = '@';
}

@header{
  package stencil.parser.string;

  import static stencil.parser.ParserConstants.*;
  import java.util.ArrayList;
  import java.util.List;
  import stencil.parser.ParserConstants;
  import static stencil.parser.string.util.Utilities.genSym;
  import static stencil.parser.string.util.Utilities.FRAME_SYM_PREFIX;
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
  
  
  public String ensureFacet(String name, String defaultFacet) {
    if (name.indexOf(".") >0) {return name;}
    else {return name + "." + defaultFacet;}
  }
  
  //TODO: Eliminate CUSTOM_PARSER_FACET, just use Map
  public String customArgsCall(String call) {
    return call.substring(SIGIL.length()) + NAME_SEPARATOR + CUSTOM_PARSER_FACET;  }
}

program : imports* (globalValue | externalStream)* order canvasLayer (elementDef | layerDef | operatorDef | operatorTemplate | streamDef | javaDef)*
    -> ^(PROGRAM  
          ^(LIST_IMPORTS imports*) 
          ^(LIST_GLOBALS globalValue*)
          ^(LIST_STREAM_DECLS externalStream*)
          order 
          canvasLayer
          ^(LIST_STREAM_DEFS streamDef*) 
          ^(LIST_LAYERS layerDef* elementDef*) 
          ^(LIST_OPERATORS operatorDef* operatorTemplate*) 
          ^(LIST_JAVAS javaDef*));



//////////////////////////////////////////// PREAMBLE ///////////////////////////
imports
  : IMPORT name=ID DEFINE as=ID -> ^(IMPORT $name $as)
  | IMPORT name=ID -> ^(IMPORT $name ID[""]);

order
  : ORDER orderRef ('>' orderRef)*
    -> ^(ORDER orderRef+)
  | -> ^(ORDER);

orderRef
  : ID -> ^(LIST_STREAMS["Streams"] ID)
  | GROUP ID ('|' ID)+ CLOSE_GROUP ->  ^(LIST_STREAMS ID+);

globalValue
//  options{backtrack=true;}
  : CONST name=ID DEFINE atom -> ^(CONST[$name] atom);
//  | CONST name=ID -> ^(CONST[$name]);

externalStream: STREAM name=ID tuple[false] -> ^(STREAM[$name.text] tuple);

//////////////////////////////////////////// CANVAS & VIEW LAYER ///////////////////////////
canvasLayer
  : CANVAS name=ID specializer 
    -> ^(CANVAS_DEF[$name.text] specializer)
  | -> ^(CANVAS_DEF["default"] ^(SPECIALIZER DEFAULT));

  
//////////////////////////////////////////// STREAM, LAYER, ELEMENT ///////////////////////////

streamDef
  : STREAM name=ID tuple[true]  consumesBlock+
    -> ^(STREAM_DEF[$name.text] tuple ^(LIST_CONSUMES["Consumes"] consumesBlock+));

layerDef
  : LAYER name=ID specializer guidesBlock defaultsBlock consumesBlock+
    -> ^(LAYER[$name.text] specializer guidesBlock defaultsBlock ^(LIST_CONSUMES["Consumes"] consumesBlock+));

elementDef
  : ELEMENT name=ID specializer defaultsBlock consumesBlock+
  	->^(ELEMENT[$name.text] specializer defaultsBlock ^(LIST_CONSUMES["Consumes"] consumesBlock+)); 
    
defaultsBlock
  : DEFAULT rule["result"]+ -> ^(RULES_DEFAULTS rule+)
  | -> RULES_DEFAULTS;
  
consumesBlock
  : FROM stream=ID filter* rule["result"]+ 
    -> ^(CONSUMES[$stream.text] ^(LIST_FILTERS filter*) ^(LIST_RULES rule+));

filter: FILTER! predicate;

guidesBlock
   : GUIDE guideDef+ -> ^(LIST_GUIDES guideDef*)
   |                  -> ^(LIST_GUIDES);
   
guideDef: type=ID specializer selectorList rule["result"]*
      -> ^(GUIDE $type specializer selectorList ^(LIST_RULES rule*));

selectorList
  options{backtrack=true;}
  : selector (SEPARATOR selector)* -> ^(LIST_SELECTORS selector+);

selector
  : sample=ID FROM field=ID -> ^(SELECTOR[$field.text] SAMPLE_TYPE[$sample.text])
  |           FROM field=ID -> ^(SELECTOR[$field.text] DEFAULT);


//////////////////////////////////////////// OPERATORS ///////////////////////////

operatorTemplate : TEMPLATE OPERATOR name=ID -> ^(OPERATOR_TEMPLATE[$name.text]);
  
operatorDef
  : OPERATOR name=ID tuple[false] YIELDS tuple[false] pf=rule["prefilter"]* operatorRule+
    ->  ^(OPERATOR[$name.text] ^(YIELDS tuple tuple) ^(RULES_PREFILTER["Prefilters"] $pf*) ^(RULES_OPERATOR["Rules"] operatorRule+))
  | OPERATOR name=ID DEFINE base=opName specializer
    -> ^(OPERATOR_REFERENCE[$name.text] OPERATOR_BASE[$base.text] specializer);
  	  
opName
  : pre=ID NAMESPACE post=ID -> ID[$pre + "::" + $post] 
  | ID;

operatorRule
  : predicate GATE rule["result"]+
    -> ^(OPERATOR_RULE predicate ^(LIST_RULES["Rules"] rule+));

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
  :	l=frameLabel f1=functionCall passOp[$l.label] f2=callChainMember
  		-> ^(FUNCTION[$f1.funcName] $f1 passOp $f2)
  | f1=functionCall passOp[genSym(FRAME_SYM_PREFIX)] f2=callChainMember    
  		-> ^(FUNCTION[$f1.funcName] $f1 passOp $f2)
  | l=frameLabel f1=functionCall 
      -> ^(FUNCTION[$f1.funcName] $f1 DIRECT_YIELD[$l.label] ^(PACK DEFAULT))
  | f1=functionCall 
      -> ^(FUNCTION[$f1.funcName] $f1 DIRECT_YIELD[genSym(FRAME_SYM_PREFIX)] ^(PACK DEFAULT));
   
frameLabel returns [String label]: ARG ID CLOSE_ARG {$label=$ID.text;} -> ID;

functionCall returns[String funcName]
  : name=callName s=specializer valueList 
     {$funcName = ((Tree) name.tree).getText();} 
     -> specializer ^(LIST_ARGS valueList)
  | name=callName specializer emptySet
     {$funcName = ((Tree) name.tree).getText();} 
     -> specializer ^(LIST_ARGS)
  | t=TAGGED_ID ISLAND_BLOCK
     {$funcName = customArgsCall($t.text);} 
     -> ^(SPECIALIZER DEFAULT) ISLAND_BLOCK;  

callName
  : pre=ID NAMESPACE post=ID -> ID[$pre.text + "::" + ensureFacet($post.text, MAP_FACET)]
  | name=ID -> ID[ensureFacet($name.text, MAP_FACET)];

target[String def]
  : PREFILTER^ tuple[false]
  | CANVAS^ tuple[false]
  | LOCAL^ tuple[false]
  | VIEW^ tuple[false]
  | tuple[true]
    -> {def.equals("prefilter")}? ^(PREFILTER tuple)
    -> {def.equals("result")}? ^(RESULT tuple)
    -> ^(DEFAULT tuple); //TODO: Can this case be removed???

//////////////////////////////////////////// JAVA ///////////////////////////

javaDef 
  : JAVA n=ID i=ISLAND_BLOCK b=ISLAND_BLOCK-> ^(JAVA[$ID.text] ID[DEFAULT_JAVA_SUPER] $i $b)
  | JAVA n=ID b=ISLAND_BLOCK -> ^(JAVA[$ID.text] ID[DEFAULT_JAVA_SUPER] ISLAND_BLOCK["{}"] $b)
  | JAVA n=ID DEFINE s=ID b=ISLAND_BLOCK -> ^(JAVA[$n.text] $s ISLAND_BLOCK["{}"] $b)
  | JAVA n=ID DEFINE s=ID i=ISLAND_BLOCK b=ISLAND_BLOCK-> ^(JAVA[$n.text] $s $i $b);

//////////////////////////////////////////// GENERAL OBJECTS ///////////////////////////
predicate
  : GROUP? ALL CLOSE_GROUP?
    -> ^(LIST_PREDICATES ^(PREDICATE ALL))
  | GROUP value booleanOp value (SEPARATOR value booleanOp value)* CLOSE_GROUP
    -> ^(LIST_PREDICATES ^(PREDICATE value booleanOp value)+);

specializer
  : ARG argList CLOSE_ARG -> ^(SPECIALIZER argList)
  | ARG mapList CLOSE_ARG -> ^(SPECIALIZER mapList)
  | ARG argList SEPARATOR mapList CLOSE_ARG -> ^(SPECIALIZER argList mapList)
  | ARG CLOSE_ARG -> ^(SPECIALIZER)
  | -> ^(SPECIALIZER DEFAULT);

argList : argEntry  (SEPARATOR! argEntry)*;
private argEntry
    : ID -> ^(MAP_ENTRY[POSITIONAL_ARG] ID)
    | atom -> ^(MAP_ENTRY[POSITIONAL_ARG] atom);

mapList
  : mapEntry (SEPARATOR! mapEntry)*;
  
private mapEntry 
  : k=ID DEFINE v=atom -> ^(MAP_ENTRY[$k.text] $v)
  | k=ID DEFINE r=ID   -> ^(MAP_ENTRY[$k.text] $r);

tuple[boolean allowEmpty]
  : emptySet {allowEmpty}?
    -> ^(TUPLE_PROTOTYPE)
  | ID
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT))
  | GROUP ID (SEPARATOR ID)* CLOSE_GROUP
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF ID DEFAULT)+)
  | GROUP t+=ID n+=ID (SEPARATOR t+=ID n+=ID)* CLOSE_GROUP
    -> ^(TUPLE_PROTOTYPE ^(TUPLE_FIELD_DEF $n $t)+);


emptySet: GROUP! CLOSE_GROUP!;

valueList:  GROUP! value (SEPARATOR! value)* CLOSE_GROUP!; 
		
value : tupleRef | atom | opRef;
atom  : number | STRING | DEFAULT | ALL | LAST | NULL;  //TODO: Can ALL, LAST be removed from this list?
opRef : t=TAGGED_ID specializer -> ^(OP_AS_ARG[$t.text.substring(1)]  specializer);
            //Operator as an argument is prefixed by the tag, strips off tag 
                      //TODO: Restrict where opRef can be used
                      //TODO: Extend to chains, not just single names
                      //TODO: Ensure no facet in op name
                      //TODO: Does this really belong in the specializer (it gets moved there eventually anyway...)
                      
  
tupleRef
  options{backtrack=true;}
  : simpleRef -> ^(TUPLE_REF simpleRef)
  | simpleRef qualifiedRef+ -> ^(TUPLE_REF simpleRef qualifiedRef+);

private simpleRef
  : ID 
  | DEFAULT_VALUE -> NUMBER["0"]
  | TUPLE_VALUE -> ALL
  | LAST 
  | ALL 
  | ARG! number CLOSE_ARG!
  | c=CANVAS -> ID[$c.text]
  | l=LOCAL -> ID[$l.text]
  | v=VIEW -> ID[$v.text]
  ;

private qualifiedRef 
  : ARG i=ID CLOSE_ARG -> $i
  | ARG n=number CLOSE_ARG -> $n
  | ARG DEFAULT_VALUE CLOSE_ARG -> NUMBER["0"];

booleanOp : GT |  GTE | LT | LTE | EQ | NEQ | RE | NRE;

passOp[String label]  
  : YIELDS -> DIRECT_YIELD[label]
  | GUIDE_YIELD -> GUIDE_YIELD[label];
  
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
