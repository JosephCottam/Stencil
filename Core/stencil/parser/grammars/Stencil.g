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
  COUNTERPART_FACET;	//The facet enclosed should NOT be used, its counterpart should be used instead
  DEFAULT_FACET;
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
  LIST_LAYERS;
  LIST_OPERATORS;
  LIST_PREDICATES;
  LIST_RULES;
  LIST_SELECTORS;
  LIST_STREAMS;
  LIST_STREAM_DECLS;
  LIST_STREAM_DEFS;
  LIST_TEMPLATES;
  LIST_CANVAS;
  LIST_VIEW;
  MAP_ENTRY;
  NUMBER;
  NO_UPDATE;
  OPERATOR_INSTANCE; //Operator, fully specified in stencil (either directly or through a template/specializer)
  OPERATOR_PROXY;    //Operator, specfied by base reference to an instance of an imported operator 
  OPERATOR_REFERENCE;//Operator, specified by base reference to a template or imported operator (instantiates to either a proxy or an instance)
  OPERATOR_TEMPLATE; //Template used to create an operator instance
  OPERATOR_RULE;     //Combination of filter, return and function calls in a operator
  OPERATOR_BASE;
  OPERATOR_FACET;
  OP_AS_ARG;         //Identifies when an operator appears in argument position (requires special resolution)
  OP_NAME;
  POST;
  PRE;
  PREDICATE;
  PROGRAM;
  PACK;
  RESULT;		//Consumes blocks value that indicates the contextual result (e.g. glyph value, stream tuple or operator tuple); can only be derived, not specified

  RULE;
  RULES_DEFAULTS;
  RULES_DYNAMIC;
  RULES_FILTER;
  RULES_LOCAL;
  RULES_OPERATOR;   //List of rules in an operator
  RULES_PREDICATES;
  RULES_PREFILTER;  
  RULES_RESULT;
  
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
  TARGET_TUPLE;
  TUPLE_FIELD;
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
  LOCAL = 'local';  //Target to indicate temporary storage after filtering
  NULL = 'NULL';
  OPERATOR= 'operator';
  ORDER = 'order';
  PREFILTER = 'prefilter'; //Target to indicate actions that occure before filters
  TEMPLATE= 'template';
  STREAM  = 'stream';
  VIEW  = 'view';
  RENDER = '#Render';
  TRUE = 'true';
  FALSE = 'false';

  //Markers
  GROUP   = '(';
  CLOSE_GROUP = ')';
  ARG     = '[';
  CLOSE_ARG = ']';
  SEPARATOR = ',';
  DOT = '.';


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

  import stencil.parser.tree.StencilTree;
  import stencil.interpreter.tree.Freezer;
  import static stencil.parser.ParserConstants.*;
  import static stencil.parser.string.util.Utilities.genSym;
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
}

program : imports* (globalValue | externalStream)* order  (canvasLayer | viewLayer | elementDef | layerDef | operatorDef | operatorTemplate | streamDef)*
    -> ^(PROGRAM  
          ^(LIST_IMPORTS imports*) 
          ^(LIST_GLOBALS globalValue*)
          ^(LIST_STREAM_DECLS externalStream*)
          order 
          ^(LIST_CANVAS canvasLayer*)
          ^(LIST_VIEW viewLayer*)
          ^(LIST_STREAM_DEFS streamDef*) 
          ^(LIST_LAYERS layerDef* elementDef*) 
          ^(LIST_OPERATORS operatorDef* operatorTemplate*));



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
//TODO: Allow calculations using only constant values (maybe process the constants before doing the rest of the program)


externalStream: 
  STREAM name=ID tuple[false] FROM type=ID specializer 
  	-> ^(STREAM[$name.text] 
  	      tuple
  	      $type
  	      specializer);

//////////////////////////////////////////// CANVAS & VIEW LAYER ///////////////////////////
canvasLayer
  : CANVAS name=ID specializer consumesBlock[true]*
    -> ^(CANVAS[$name.text] specializer ^(LIST_CONSUMES consumesBlock*));


viewLayer
    : VIEW specializer consumesBlock[true]*
      -> ^(VIEW specializer ^(LIST_CONSUMES consumesBlock*));
    
//////////////////////////////////////////// STREAM, LAYER, ELEMENT ///////////////////////////

streamDef
  : STREAM name=ID tuple[true]  consumesBlock[false]+
    -> ^(STREAM_DEF[$name.text] tuple ^(LIST_CONSUMES["Consumes"] consumesBlock+));

layerDef
  : LAYER name=ID specializer guidesBlock defaultsBlock consumesBlock[false]+
    -> ^(LAYER[$name.text] specializer guidesBlock defaultsBlock ^(LIST_CONSUMES["Consumes"] consumesBlock+));

elementDef
  : ELEMENT name=ID specializer defaultsBlock consumesBlock[false]+
  	->^(ELEMENT[$name.text] specializer defaultsBlock ^(LIST_CONSUMES["Consumes"] consumesBlock+)); 
    
defaultsBlock
  : DEFAULT rule[RESULT]+ -> ^(RULES_DEFAULTS rule+)
  | -> RULES_DEFAULTS;
  
consumesBlock[boolean allowRender]
  : FROM stream=ID filter* rule[RESULT]+
     ->  ^(CONSUMES[$stream.text] ^(LIST_FILTERS filter*) ^(LIST_RULES rule+))
  | {allowRender}? FROM stream=RENDER filter*  rule[RESULT]+
    -> ^(CONSUMES[$stream.text] ^(LIST_FILTERS filter*) ^(LIST_RULES rule+));

filter: FILTER! predicate;

guidesBlock
   : GUIDE guideDef+ -> ^(LIST_GUIDES guideDef*)
   |                  -> ^(LIST_GUIDES);
   
guideDef: type=ID specializer selectorList rule[RESULT]*
      -> ^(GUIDE $type specializer selectorList ^(LIST_RULES rule*));

selectorList
  options{backtrack=true;}
  : selector (SEPARATOR selector)* -> ^(LIST_SELECTORS selector+);

selector
  : sample=ID FROM field=ID -> ^(SELECTOR[$field.text] SAMPLE_TYPE[$sample.text])
  |           FROM field=ID -> ^(SELECTOR[$field.text] DEFAULT);


//////////////////////////////////////////// OPERATORS ///////////////////////////

operatorTemplate : TEMPLATE od=operatorDef -> ^(OPERATOR_TEMPLATE[((Tree) $od.tree).getText()] operatorDef);
  
operatorDef
  : OPERATOR name=ID tuple[false] YIELDS tuple[false] pf=rule[PREFILTER]* operatorRule+
    ->  ^(OPERATOR[$name.text] ^(YIELDS tuple tuple) ^(RULES_PREFILTER $pf*) ^(RULES_OPERATOR operatorRule+))
  | OPERATOR name=ID DEFINE opName specializer
    -> ^(OPERATOR_REFERENCE[$name.text] opName specializer)
  | OPERATOR name=ID DEFINE opName specializer GROUP opRef CLOSE_GROUP
    -> ^(OPERATOR_REFERENCE[$name.text] opName specializer ^(LIST_ARGS opRef));

opName
  : pre=ID NAMESPACE post=ID -> ^(OPERATOR_BASE ID ID) 
  | post=ID -> ^(OPERATOR_BASE DEFAULT ID);

operatorRule
  : predicate GATE rule[RESULT]+
    -> ^(OPERATOR_RULE predicate ^(LIST_RULES["Rules"] rule+));

/////////////////////////////////////////  CALLS  ////////////////////////////////////
rule[int defaultTarget]  : target[defaultTarget] (DEFINE | DYNAMIC | ANIMATED | ANIMATED_DYNAMIC) callChain
    -> ^(RULE target callChain DEFINE? DYNAMIC? ANIMATED? ANIMATED_DYNAMIC?);

callChain: callChainMember -> ^(CALL_CHAIN callChainMember);

callChainMember
  : value -> ^(PACK value)
  | emptySet -> ^(PACK)
  | valueList -> ^(PACK valueList)
  | functionCallTarget;
  
  
functionCallTarget
  :	l=frameLabel functionCall passOp[$l.label] callChainMember
  		-> ^(FUNCTION functionCall passOp callChainMember)
  | fc=functionCall passOp[$fc.baseName] callChainMember    
  		-> ^(FUNCTION functionCall passOp callChainMember)
  | l=frameLabel functionCall 
      -> ^(FUNCTION functionCall DIRECT_YIELD[$l.label] ^(PACK DEFAULT))
  | fc=functionCall 
      -> ^(FUNCTION functionCall DIRECT_YIELD[$fc.baseName] ^(PACK DEFAULT));
   
frameLabel returns [String label]: ARG ID CLOSE_ARG {$label=$ID.text;} -> ID;

functionCall returns [String baseName]
  @after{retval.baseName = ((Tree) retval.tree).getChild(0).getChild(1).getText();} //Unpack the OP_NAME
  : callName specializer valueList 
     -> callName specializer ^(LIST_ARGS valueList)
  | callName specializer emptySet
     -> callName specializer ^(LIST_ARGS)
  | t=callName specializer ISLAND_BLOCK
     -> callName specializer ISLAND_BLOCK;

callName
  : ID NAMESPACE ID        -> ^(OP_NAME ID ID DEFAULT_FACET)
  | ID NAMESPACE ID DOT ID -> ^(OP_NAME ID ID ID)
  | ID                     -> ^(OP_NAME DEFAULT ID DEFAULT_FACET)
  | ID DOT ID              -> ^(OP_NAME DEFAULT ID ID);

target[int defaultTarget] 
  : PREFILTER^ targetTuple
  | LOCAL^ targetTuple
  | targetTuple
    -> ^({adaptor.create(defaultTarget, StencilTree.typeName(defaultTarget))} targetTuple);

targetTuple
  : GROUP CLOSE_GROUP -> ^(TARGET_TUPLE)
  | longName -> ^(TARGET_TUPLE longName)
  | GROUP longName (SEPARATOR longName)* CLOSE_GROUP 
      -> ^(TARGET_TUPLE longName*);

longName : ID (DOT ID)* -> ^(TUPLE_FIELD ID*);

//////////////////////////////////////////// GENERAL OBJECTS ///////////////////////////
predicate
  : DEFAULT
    -> ^(LIST_PREDICATES ^(PREDICATE ALL))  //TODO: Change this (and down-stream references) to be DEFAULT as well
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
    : ID -> ^(MAP_ENTRY[genSym(POSITIONAL_ARG)] ID)
    | atom -> ^(MAP_ENTRY[genSym(POSITIONAL_ARG)] atom);

mapList
  : mapEntry (SEPARATOR! mapEntry)*;
  
private mapEntry 
  : k=longName DEFINE v=atom -> ^(MAP_ENTRY[Freezer.tupleField((StencilTree) k.tree).toString()] $v)
  | k=longName DEFINE r=ID   -> ^(MAP_ENTRY[Freezer.tupleField((StencilTree) k.tree).toString()] $r);

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
atom  : number | STRING | DEFAULT | ALL | TRUE | FALSE  | NULL;
opRef : o=TAGGED_ID specializer -> ^(OP_AS_ARG ^(OP_NAME DEFAULT ID[$o.text.substring(1)] DEFAULT)   specializer)
      | ns=TAGGED_ID NAMESPACE o=ID specializer -> ^(OP_AS_ARG ^(OP_NAME ID[$ns.text.substring(1)] $o DEFAULT)  specializer);      
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
  | ARG! number CLOSE_ARG!
  | c=CANVAS -> ID[$c.text]
  | l=LOCAL -> ID[$l.text]
  | v=VIEW -> ID[$v.text]
  | p=PREFILTER -> ID[$p.text]
  ;

private qualifiedRef 
  : DOT i=ID -> $i
  | DOT n=intNum -> $n
  | DOT DEFAULT_VALUE -> NUMBER["0"]
  | DOT TUPLE_VALUE -> ALL;

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
  : DOT d2=DIGITS -> ^(NUMBER["0." + $d2.text])
  | d=DIGITS DOT d2=DIGITS -> ^(NUMBER[$d.text + "." + $d2.text])
  | (n='-' | p='+') d=DIGITS DOT d2=DIGITS -> ^(NUMBER[p!=null?"+":"-" + $d.text + "." + $d2.text])
  | (n='-' | p='+') DOT d2=DIGITS -> ^(NUMBER[p!=null?"+":"-" + "." + $d2.text]);


TAGGED_ID: TAG ID;

ID    : ('a'..'z' | 'A'..'Z' | '_')  ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;

DIGITS  : '0'..'9'+;


ISLAND_BLOCK: NESTED_BLOCK; //Strip braces

fragment 
NESTED_BLOCK
    : '{' (options {greedy=false;k=2;}: NESTED_BLOCK | .)* '}';

STRING          
@init{StringBuilder lBuf = new StringBuilder();}
    :   
           '"' 
           ( escaped= ESCAPE_SEQUENCE {lBuf.append(getText());} | 
             normal=~('"'|'\\'|'\n'|'\r')     {lBuf.appendCodePoint(normal);} )* 		//Single line strings variant
           '"'     
           {setText(lBuf.toString());}
    |
       '@"' 
       (escaped= LONG_ESCAPE {lBuf.append(getText());} | 
         normal=~('@')     {lBuf.appendCodePoint(normal);} )*
       '"@'
       {setText(lBuf.toString());}
    ;    

fragment LONG_ESCAPE : '\\@' {setText("@");};


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
