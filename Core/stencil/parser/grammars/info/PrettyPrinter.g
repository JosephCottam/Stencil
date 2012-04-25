tree grammar PrettyPrinter;
options {
  tokenVocab = Stencil;
  ASTLabelType = StencilTree;  
  output=template;
}

@header {
  /** Creates a string representation of a program tree.
   *  Designed for the tree that results from a basic parse, not full processing.
  **/
  package stencil.parser.string;
  
  import stencil.parser.tree.StencilTree;
  import stencil.interpreter.tree.Freezer;
}

@members {
  public static String format(Tree t) {
	try {
	    CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
	    PrettyPrinter printer = new PrettyPrinter(nodes);
		PrettyPrinter.program_return rv = printer.program();
    	return rv.st.toString().trim();
    }catch (Exception e) {
    	throw new RuntimeException("Error pretty printing.",e);
    }
    
  }
}

program: ^(PROGRAM i=imports g=globals sdec=streamDecs o=order c=canvases v=views sdef=streamDefs l=layers ops=operators) 
	-> template(i={$i.st},g={$g.st},sdec={$sdec.st},o={$o.st},c={$c.st},v={$v.st},sdef={$sdef.st},l={$l.st},ops={$ops.st}) 
		"<i><g><sdec><o><c><v><sdef><l><ops>";

imports: LIST_IMPORTS -> {%{""}}
	   | ^(l=LIST_IMPORTS is+=imp+) -> template(i={$is}) "<i><\n>";  
	   
imp: ^(IMPORT m=ID a=ID) 
		-> {a.getText() != null && !a.getText().equals("")}? template(m={$m.text},a={$a.text}) "import <m> : <a><\n>"
		-> template(m={$m.text}) "import <m><\n>";

globals
  : LIST_GLOBALS -> {%{""}}
  | ^(LIST_GLOBALS gs+=global*) -> template(g={$gs}) "<g><\n>";
global: ^(c=CONST v=.) -> template(g={$c.getText()}, v={$v.getText()}) "const <g> : <v><\n>";


streamDecs
  : LIST_STREAM_DECLS -> {%{""}}
  | ^(LIST_STREAM_DECLS sd+=streamDec*) -> template(s={$sd}) "<s><\n>";
streamDec
  : ^(n=STREAM p=tuplePrototype t=. spec=specializer) 
	-> template(n={$n},p={$p.st},t={$t},s={$spec.st}) "stream <n><p> : <t><s><\n>";


order
	: ORDER -> {%{""}}
	| ^(o=ORDER sg+=subgroup*) -> template(sg={$sg}) "order <sg; separator=\" \> \"><\n><\n>";	
subgroup: ^(LIST_STREAMS ids+=ID+) -> template(ids={$ids}) "(<ids; separator=\"|\">)";
	 
	 
views
  : LIST_VIEW -> {%{""}}
  | ^(LIST_VIEW v+=view*) -> template(v={$v}) "<v; separator=\"\n\"><\n>";
view:
  ^(v=VIEW s=specializer cs=consumes)
  -> template(v={$v},s={$s.st},cs={$cs.st})
   "view <s><\n><cs>";
   
canvases
  : LIST_CANVAS -> {%{""}}
  | ^(LIST_CANVAS c+=canvas*) -> template(c={$c}) "<c; separator=\"\n\"><\n>";
canvas
  : ^(c=CANVAS s=specializer cs=consumes)
     -> template(c={$c},s={$s.st},cs={$cs.st})
        "canvas <c> <s><\n><cs>";   

streamDefs 
  : LIST_STREAM_DEFS -> {%{""}}
  | ^(LIST_STREAM_DEFS sd+=streamDef+) -> template(sd={$sd}) "<sd;separator=\"\n\"><\n>";
streamDef
  : ^(n=STREAM_DEF t=tuplePrototype cs=consumes)
   -> template(n={$n},t={$t.st},cs={$cs.st})
     "stream <n> <t><\n><cs><\n>";

layers 
	: LIST_LAYERS -> {%{""}}
	| ^(LIST_LAYERS ls+=layer*) -> template(ls={$ls}) "<ls; separator=\"\n\"><\n>";
layer: ^(l=LAYER s=specializer g=guides d=defaults  cs=consumes)
	-> template(l={$l},s={$s.st}, g={$g.st},cs={$cs.st},d={$d.st}) 
	   "layer <l><s><\n><g><d><cs><\n>";

defaults
  : RULES_DEFAULTS -> {%{""}}
  | ^(RULES_DEFAULTS r+=rule+) -> template(r={$r}) "default<\n><r; separator=\"\n\">";


consumes
  : LIST_CONSUMES -> {%{""}}
  | ^(LIST_CONSUMES cs+=consume+) -> template(cs={$cs}) "<cs; separator=\"\n\">";
   
consume: ^(c=CONSUMES f=filters r=rules) -> template(sn={$c},f={$f.st},r={$r.st}) "  from <sn><\n><f><r>";

filters
  : LIST_FILTERS -> {%{""}}
  | ^(LIST_FILTERS f+=filter*) -> template(f={$f}) "<f; separator=\"\n\"><\n>";
filter
  : ^(LIST_PREDICATES p+=pred*) -> template(p={$p}) "    filter(<p; separator=\",\">)";

pred
  : ^(PREDICATE ALL) -> template() "default"
  | ^(PREDICATE l=value o=. r=value) 
	-> template(l={$l.st}, o={$o}, r={$r.st}) "<l><o><r>";

operators: ^(LIST_OPERATORS o+=operator*) -> template(o={$o}) "<o; separator=\"\n\">";
operator
   :  ^(n=OPERATOR_REFERENCE b=opBase s=specializer) 
      -> template(n={$n},b={$b.st},s={$s.st}) 
      "operator <n> : <b><s><\n>"
   |  ^(n=OPERATOR_REFERENCE y=yields b=opBase s=specializer a=args) 
      -> template(n={$n},y={$y.st},b={$b.st},s={$s.st},a={$args.st}) 
      "operator <n> <y> : <b><s><a><\n>"      
   | ^(n=OPERATOR y=yields pf=prefilterRules or=operatorRules) 
     -> template(n={$n},y={$y.st},pf={$pf.st},or={$or.st}) 
       "operator <n> <y><\n><pf><or><\n>";


yields
    : ^(YIELDS in=tuplePrototype out=tuplePrototype)
     -> template(in={$in.st}, out={$out.st}) "<in>-><out>";
     
opBase 
    : ^(OPERATOR_BASE DEFAULT b=ID)
      -> template(b={$b}) "<b>"
    | ^(OPERATOR_BASE pre=ID b=ID)
      -> template(pre={$pre}, b={$b}) "<pre>::<b>";
    

prefilterRules
  : RULES_PREFILTER -> {%{""}}
  | ^(RULES_PREFILTER pf+=rule+) -> template(pf={$pf}) "<pf; separator=\"\n\">";
     
operatorRules
   : RULES_OPERATOR -> {%{""}}
   | ^(RULES_OPERATOR or+=opRule+) 
     -> template(or={$or}) "<or;separator=\"\n\">";
opRule
   : ^(OPERATOR_RULE ^(LIST_PREDICATES ps+=pred*) r=rules)
      -> template(ps={$ps},r={$r.st}) 
      "   (<ps>) => <r>";

rules
  : LIST_RULES -> {%{""}}
  | ^(LIST_RULES rs+=rule+) -> template(rs={$rs}) "<rs; separator=\"\n\">";

rule
  : ^(RULE t=target c=chain b=bind) 
    -> template(t={$t.st},c={$c.st},b={$b.st}) "<t> <b> <c>";

target 
	: ^(RESULT ^(TARGET_TUPLE fs+=tupleField*)) -> template(fs={$fs}) "    (<fs; separator=\",\">)"
	| ^(PREFILTER ^(TARGET_TUPLE fs+=tupleField*)) -> template(fs={$fs}) "    prefilter(<fs; separator=\",\">)"
	| ^(LOCAL ^(TARGET_TUPLE fs+=tupleField*)) -> template(fs={$fs}) "    local(<fs; separator=\",\">)";

chain
    : ^(CALL_CHAIN ct=chainTarget) -> template(ct={$ct.st}) "<ct>";

chainTarget
	: ^(PACK p+=value*) -> template(ps={$p}) "(<ps; separator=\",\">)"
	| ^(FUNCTION n=opName s=specializer a=args y=yield t=chainTarget) 
		-> {$y.lab.equals($n.st.toString().split("\\.")[0])}? template(n={$n.st},s={$s.st},a={$a.st},y={$y.st},t={$t.st})
		"<n><s><a> <y> <t>"
		-> template(n={$n.st},s={$s.st},a={$a.st},y={$y.st},t={$t.st},l={$y.lab})
		"[<l>] <n><s><a> <y> <t>"; 

yield returns [String lab]
  : dy=DIRECT_YIELD {$lab=$dy.text;} -> template() "->"
  | gy=GUIDE_YIELD {$lab=$gy.text;} -> template() "-#>";

opName
  : ^(OP_NAME DEFAULT o=ID f=ID) -> template(o={$o},f={$f}) "<o>.<f>"
  | ^(OP_NAME DEFAULT o=ID DEFAULT_FACET) -> template(o={$o}) "<o>"
  | ^(OP_NAME p=ID o=ID DEFAULT_FACET) -> template(p={$p},o={$o}) "<p>::<o>"
  | ^(OP_NAME p=ID o=ID f=ID) -> template(p={$p},o={$o},f={$f}) "<p>::<o>.<f>";
  
args
  : LIST_ARGS -> template() "()"
  | i=ISLAND_BLOCK -> template(i={$i}) "<i>"
  | ^(LIST_ARGS a+=value*) -> template(a={$a}) "(<a; separator=\",\">)";
  
    
bind
  : DEFINE -> template() ":"
  | DYNAMIC -> template() ":*";
    	
guides 
	: LIST_GUIDES
	| ^(l=LIST_GUIDES gs+=guide*)-> template(gs={$gs}) "  guide<\n><gs; separator=\"\n\"><\n>";
guide: ^(GUIDE t=ID s=specializer f=selectors r=rules) 
         -> template(t={$t},s={$s.st},f={$f.st},r={$r.st}) 
            "    <t><s> <f><\n><r>";

selectors: ^(LIST_SELECTORS s+=selector+) -> template(s={$s}) "<s; separator=\",\">";
selector
  : ^(a=SELECTOR DEFAULT) -> template(a={$a}) "from <a>"
  | ^(a=SELECTOR i=SAMPLE_TYPE) -> template(a={$a},i={$i}) "<i> from <a>";

	 	 
specializer
  : SPECIALIZER -> {%{""}}
  | ^(SPECIALIZER DEFAULT) ->{%{""}}
  | ^(SPECIALIZER se+=specEntry*) -> template(se={$se}) "[<se; separator=\", \">]";
	
specEntry: ^(me=MAP_ENTRY v=value) 
		-> {me.getText().startsWith("#")}? template(v={$v.st}) "<v>"
		-> template(n={$me},v={$v.st}) "<n>:<v>";
	 

tuplePrototype
  : TUPLE_PROTOTYPE -> template() "()"
  | ^(TUPLE_PROTOTYPE dfs+=tupleFieldDef*) 
  		-> template(dfs={$dfs}) "(<dfs; separator=\", \">)";
  		
tupleFieldDef 
  : ^(TUPLE_FIELD_DEF n=ID DEFAULT) -> {%{$n.text}}
  | ^(TUPLE_FIELD_DEF n=ID)         -> {%{$n.text}}
  | ^(TUPLE_FIELD_DEF n=ID t=TYPE)    -> template(n={$n}, t={$t}) " <t> <n>";
  
tupleField : ^(TUPLE_FIELD n+=value*) -> template(n={$n}) "<n; separator=\".\">";
  
value 
  : s=STRING ->{$s.getText().contains("\n")}? template(s={$s.getText().replace("@","\\@")}) "@\"<s>\"@"
             -> template(s={$s.getText().replace("\\","\\\\")}) "\"<s>\""
  | n=NUMBER -> {%{$n.text}}
  | i=ID     -> {%{$i.text}}
  | NULL     -> {%{"NULL"}}
  | ALL      -> {%{"ALL"}}
  | FALSE    -> {%{"false"}}
  | TRUE     -> {%{"true"}}
  | ^(OP_AS_ARG on=opName sp=specializer) -> template(n={$on.st},s={$sp.st}) "@<n><s>"
  | (TUPLE_REF NUMBER) => ^(TUPLE_REF n=NUMBER) -> {%{"_"}}
  | ^(TUPLE_REF r+=refPart+) -> template(r={$r}) "<r; separator=\".\">";
  
  
refPart
  : n=NUMBER -> {%{$n.text}}
  | i=ID     -> {%{$i.text}}
  | ALL      -> {%{"*"}};
