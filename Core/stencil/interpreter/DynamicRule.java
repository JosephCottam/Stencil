// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g 2009-08-28 15:23:37

	/** Converts a rule into a rule suitable for dynamic bindings.
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality	  
	 **/

	package stencil.interpreter;
		
	import stencil.util.MultiPartName;
	import stencil.parser.tree.Function;
	import stencil.parser.tree.StencilTreeAdapter;
	import stencil.parser.tree.Rule;
	import static stencil.parser.ParserConstants.QUERY_BLOCK_TAG;

	import java.util.Map;
	import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class DynamicRule extends TreeRewriter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ANNOTATION", "BOOLEAN_OP", "BASIC", "CONSUMES", "CALL_GROUP", "CALL_CHAIN", "FUNCTION", "GUIDE", "LEGEND_RULE", "LIST", "NUMBER", "POST", "PRE", "PREDICATE", "PROGRAM", "PACK", "RULE", "SIGIL", "SPECIALIZER", "TUPLE_PROTOTYPE", "TUPLE_REF", "MAP_ENTRY", "ALL", "BASE", "CANVAS", "COLOR", "DEFAULT", "EXTERNAL", "FILTER", "FROM", "GLYPH", "IMPORT", "LOCAL", "LAYER", "LEGEND", "ORDER", "PYTHON", "RETURN", "STATIC", "STREAM", "VIEW", "AS", "FACET", "GROUP", "CLOSE_GROUP", "ARG", "CLOSE_ARG", "SEPARATOR", "RANGE", "NAMESPACE", "NAMESPLIT", "DEFINE", "DYNAMIC", "YIELDS", "FEED", "GUIDE_FEED", "GUIDE_YIELD", "GATE", "SPLIT", "JOIN", "TAG", "ID", "CODE_BLOCK", "TAGGED_ID", "STRING", "DIGITS", "ESCAPE_SEQUENCE", "WS", "COMMENT", "'>'", "'Init'", "'='", "'n'", "'>='", "'<'", "'<='", "'!='", "'=~'", "'!~'", "'-'", "'+'"
    };
    public static final int PRE=16;
    public static final int CLOSE_GROUP=48;
    public static final int AS=45;
    public static final int MAP_ENTRY=25;
    public static final int EXTERNAL=31;
    public static final int LIST=13;
    public static final int CALL_GROUP=8;
    public static final int GLYPH=34;
    public static final int T__80=80;
    public static final int NUMBER=14;
    public static final int FACET=46;
    public static final int T__73=73;
    public static final int NAMESPACE=53;
    public static final int GATE=61;
    public static final int RULE=20;
    public static final int VIEW=44;
    public static final int PACK=19;
    public static final int TUPLE_PROTOTYPE=23;
    public static final int IMPORT=35;
    public static final int PROGRAM=18;
    public static final int TUPLE_REF=24;
    public static final int T__74=74;
    public static final int ORDER=39;
    public static final int POST=15;
    public static final int PREDICATE=17;
    public static final int BASIC=6;
    public static final int LOCAL=36;
    public static final int FUNCTION=10;
    public static final int GUIDE_FEED=59;
    public static final int GUIDE=11;
    public static final int GROUP=47;
    public static final int LAYER=37;
    public static final int LEGEND=38;
    public static final int YIELDS=57;
    public static final int TAG=64;
    public static final int JOIN=63;
    public static final int BASE=27;
    public static final int CANVAS=28;
    public static final int ID=65;
    public static final int FROM=33;
    public static final int STREAM=43;
    public static final int RANGE=52;
    public static final int SIGIL=21;
    public static final int DIGITS=69;
    public static final int T__78=78;
    public static final int FILTER=32;
    public static final int SPLIT=62;
    public static final int TAGGED_ID=67;
    public static final int CODE_BLOCK=66;
    public static final int WS=71;
    public static final int CALL_CHAIN=9;
    public static final int T__79=79;
    public static final int STRING=68;
    public static final int LEGEND_RULE=12;
    public static final int NAMESPLIT=54;
    public static final int COMMENT=72;
    public static final int T__77=77;
    public static final int SPECIALIZER=22;
    public static final int CLOSE_ARG=50;
    public static final int STATIC=42;
    public static final int GUIDE_YIELD=60;
    public static final int T__84=84;
    public static final int SEPARATOR=51;
    public static final int DEFINE=55;
    public static final int RETURN=41;
    public static final int T__75=75;
    public static final int ESCAPE_SEQUENCE=70;
    public static final int CONSUMES=7;
    public static final int ARG=49;
    public static final int EOF=-1;
    public static final int DYNAMIC=56;
    public static final int PYTHON=40;
    public static final int COLOR=29;
    public static final int T__76=76;
    public static final int DEFAULT=30;
    public static final int T__82=82;
    public static final int ANNOTATION=4;
    public static final int FEED=58;
    public static final int T__81=81;
    public static final int ALL=26;
    public static final int BOOLEAN_OP=5;
    public static final int T__83=83;

    // delegates
    // delegators


        public DynamicRule(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public DynamicRule(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DynamicRule.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g"; }


    	/**Tree adaptor for translation and duplication.**/
    	private static StencilTreeAdapter treeAdaptor = new StencilTreeAdapter(); 
    	
    	/**Cache of rules that the dynamic representation has already been constructed for.**/
    	private static Map<Rule, Rule> mapCache  = new HashMap<Rule, Rule>();
    	
    	/**Convert a rule to its dynamic for; this is the preferred method
    	  *for creating a dynamic rule.
    	  *
    	  *This method is memoized.
    	 **/
    	public static Rule toDynamic(Rule original) {
    		if (mapCache.containsKey(original)) {return mapCache.get(original);}
    	
    		Rule rule = (Rule) treeAdaptor.dupTree(original);
    		
    		CommonTreeNodeStream treeTokens = new CommonTreeNodeStream(rule);
    		DynamicRule dr = new DynamicRule(treeTokens);
    		dr.setTreeAdaptor(treeAdaptor);
    		rule = (Rule) dr.downup(rule);
    		
    		mapCache.put(original, rule);
    		return rule;
    	}
        
        
        private String queryName(String name) {return new MultiPartName(name).modSuffix(QUERY_BLOCK_TAG).toString();}       


    public static class topdown_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "topdown"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g:85:1: topdown : ^(f= FUNCTION spec= . args= . yield= . target= . ) -> ^( FUNCTION[queryName($f.getText())] $spec $args $yield $target) ;
    public final DynamicRule.topdown_return topdown() throws RecognitionException {
        DynamicRule.topdown_return retval = new DynamicRule.topdown_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f=null;
        CommonTree spec=null;
        CommonTree args=null;
        CommonTree yield=null;
        CommonTree target=null;

        CommonTree f_tree=null;
        CommonTree spec_tree=null;
        CommonTree args_tree=null;
        CommonTree yield_tree=null;
        CommonTree target_tree=null;
        RewriteRuleNodeStream stream_FUNCTION=new RewriteRuleNodeStream(adaptor,"token FUNCTION");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g:87:2: ( ^(f= FUNCTION spec= . args= . yield= . target= . ) -> ^( FUNCTION[queryName($f.getText())] $spec $args $yield $target) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g:87:3: ^(f= FUNCTION spec= . args= . yield= . target= . )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            f=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_topdown71); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_FUNCTION.add(f);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = f;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            spec=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = spec;
            _last = (CommonTree)input.LT(1);
            args=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = args;
            _last = (CommonTree)input.LT(1);
            yield=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = yield;
            _last = (CommonTree)input.LT(1);
            target=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = target;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: spec, args, FUNCTION, yield, target
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: yield, target, args, spec
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_yield=new RewriteRuleSubtreeStream(adaptor,"wildcard yield",yield);
            RewriteRuleSubtreeStream stream_target=new RewriteRuleSubtreeStream(adaptor,"wildcard target",target);
            RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"wildcard args",args);
            RewriteRuleSubtreeStream stream_spec=new RewriteRuleSubtreeStream(adaptor,"wildcard spec",spec);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 87:48: -> ^( FUNCTION[queryName($f.getText())] $spec $args $yield $target)
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/DynamicRule.g:87:51: ^( FUNCTION[queryName($f.getText())] $spec $args $yield $target)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, queryName(f.getText())), root_1);

                adaptor.addChild(root_1, stream_spec.nextTree());
                adaptor.addChild(root_1, stream_args.nextTree());
                adaptor.addChild(root_1, stream_yield.nextTree());
                adaptor.addChild(root_1, stream_target.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);}
            }

            if ( state.backtracking==1 ) {
              ((Function) retval.tree).setOperator(((Function) f).getOperator());
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "topdown"

    // Delegated rules


 

    public static final BitSet FOLLOW_FUNCTION_in_topdown71 = new BitSet(new long[]{0x0000000000000004L});

}