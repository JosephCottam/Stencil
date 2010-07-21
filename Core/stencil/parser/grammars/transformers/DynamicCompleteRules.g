tree grammar DynamicCompleteRules;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
  /** Modifies dynamic binding query and update rules to use the correct operator facets. **/
  package stencil.parser.string;
	
  import stencil.parser.tree.util.*;
  import stencil.parser.tree.*;
  import stencil.module.operator.StencilOperator;
  import stencil.module.operator.util.Invokeable;
  import stencil.module.util.FacetData;
  import stencil.module.util.OperatorData;
  import static stencil.parser.string.Utilities.stateQueryList;
}

topdown: ^(CONSUMES f=. pf=. l=. r=. v=. c=. ^(LIST toDynamic*));
toDynamic:  ^(r=RULE rest+=.*) -> ^(DYNAMIC_RULE {adaptor.dupTree($r)} {adaptor.dupTree($r)});
            
bottomup: ^(DYNAMIC_RULE r=rule sq=rule) -> ^(DYNAMIC_RULE $r {stateQueryList(adaptor, $sq.tree)});
rule: ^(RULE .*);
