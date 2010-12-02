// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g 2010-11-25 00:37:36

  /**Gets the usage context of a given operator instance.**/
   

  package stencil.parser.string.info;
  
  import stencil.parser.string.TreeFilterSequence;
  import static stencil.parser.string.StencilParser.LIST;
  import stencil.parser.string.util.Context;
  import stencil.parser.tree.util.MultiPartName;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class UseContext extends TreeFilterSequence {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AST_INVOKEABLE", "ANNOTATION", "BASIC", "CONSUMES", "CALL_CHAIN", "CANVAS_DEF", "DIRECT_YIELD", "DYNAMIC_RULE", "FUNCTION", "GLYPH_TYPE", "GUIDE_GENERATOR", "GUIDE_DIRECT", "GUIDE_SUMMARIZATION", "LIST", "MAP_ENTRY", "NUMBER", "OPERATOR_INSTANCE", "OPERATOR_PROXY", "OPERATOR_REFERENCE", "OPERATOR_TEMPLATE", "OPERATOR_RULE", "OPERATOR_BASE", "OPERATOR_FACET", "POST", "PRE", "PREDICATE", "PROGRAM", "PACK", "PYTHON_FACET", "RESULT", "RULE", "SIGIL_ARGS", "STATE_QUERY", "SPECIALIZER", "SELECTOR", "STREAM_DEF", "TARGET", "TUPLE_PROTOTYPE", "TUPLE_FIELD_DEF", "TUPLE_REF", "TYPE", "ALL", "AS", "CANVAS", "CONST", "DEFAULT", "ELEMENT", "FACET", "FILTER", "FROM", "GUIDE", "IMPORT", "LAYER", "LAST", "LOCAL", "NULL", "OPERATOR", "ORDER", "PREFILTER", "PYTHON", "TEMPLATE", "STREAM", "VIEW", "GROUP", "CLOSE_GROUP", "ARG", "CLOSE_ARG", "SEPARATOR", "NAMESPACE", "GT", "GTE", "LT", "LTE", "EQ", "NEQ", "RE", "NRE", "DEFINE", "DYNAMIC", "ANIMATED", "ANIMATED_DYNAMIC", "DEFAULT_VALUE", "TUPLE_VALUE", "YIELDS", "GUIDE_YIELD", "MAP", "FOLD", "GATE", "TAG", "ID", "TAGGED_ID", "ISLAND_BLOCK", "STRING", "DIGITS", "NESTED_BLOCK", "ESCAPE_SEQUENCE", "WS", "COMMENT", "'|'", "'init'", "'-'", "'+'", "'.'"
    };
    public static final int FUNCTION=12;
    public static final int LAYER=56;
    public static final int LT=75;
    public static final int CONST=48;
    public static final int GUIDE_SUMMARIZATION=16;
    public static final int GUIDE_DIRECT=15;
    public static final int SEPARATOR=71;
    public static final int TUPLE_PROTOTYPE=41;
    public static final int EOF=-1;
    public static final int OPERATOR_TEMPLATE=23;
    public static final int TUPLE_REF=43;
    public static final int CANVAS=47;
    public static final int TYPE=44;
    public static final int IMPORT=55;
    public static final int CALL_CHAIN=8;
    public static final int ARG=69;
    public static final int CLOSE_ARG=70;
    public static final int ELEMENT=50;
    public static final int GUIDE_YIELD=88;
    public static final int EQ=77;
    public static final int LAST=57;
    public static final int STREAM=65;
    public static final int COMMENT=101;
    public static final int OPERATOR_PROXY=21;
    public static final int GATE=91;
    public static final int MAP_ENTRY=18;
    public static final int VIEW=66;
    public static final int RULE=34;
    public static final int CLOSE_GROUP=68;
    public static final int PREFILTER=62;
    public static final int NULL=59;
    public static final int NUMBER=19;
    public static final int OPERATOR_INSTANCE=20;
    public static final int LOCAL=58;
    public static final int LIST=17;
    public static final int NAMESPACE=72;
    public static final int CONSUMES=7;
    public static final int TUPLE_VALUE=86;
    public static final int YIELDS=87;
    public static final int GROUP=67;
    public static final int STATE_QUERY=36;
    public static final int ANIMATED_DYNAMIC=84;
    public static final int WS=100;
    public static final int OPERATOR_RULE=24;
    public static final int FILTER=52;
    public static final int TAGGED_ID=94;
    public static final int GT=73;
    public static final int FROM=53;
    public static final int DEFAULT_VALUE=85;
    public static final int PACK=31;
    public static final int DYNAMIC=82;
    public static final int FOLD=90;
    public static final int FACET=51;
    public static final int RE=79;
    public static final int CANVAS_DEF=9;
    public static final int ORDER=61;
    public static final int ANNOTATION=5;
    public static final int DIGITS=97;
    public static final int GTE=74;
    public static final int PYTHON_FACET=32;
    public static final int PRE=28;
    public static final int OPERATOR_REFERENCE=22;
    public static final int ID=93;
    public static final int GUIDE_GENERATOR=14;
    public static final int DEFINE=81;
    public static final int DIRECT_YIELD=10;
    public static final int LTE=76;
    public static final int PREDICATE=29;
    public static final int ESCAPE_SEQUENCE=99;
    public static final int AS=46;
    public static final int SELECTOR=38;
    public static final int ISLAND_BLOCK=95;
    public static final int GUIDE=54;
    public static final int TUPLE_FIELD_DEF=42;
    public static final int T__103=103;
    public static final int PYTHON=63;
    public static final int T__104=104;
    public static final int ALL=45;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int ANIMATED=83;
    public static final int NRE=80;
    public static final int NESTED_BLOCK=98;
    public static final int AST_INVOKEABLE=4;
    public static final int OPERATOR=60;
    public static final int DEFAULT=49;
    public static final int RESULT=33;
    public static final int T__102=102;
    public static final int TARGET=40;
    public static final int TAG=92;
    public static final int NEQ=78;
    public static final int OPERATOR_BASE=25;
    public static final int TEMPLATE=64;
    public static final int DYNAMIC_RULE=11;
    public static final int MAP=89;
    public static final int SPECIALIZER=37;
    public static final int POST=27;
    public static final int PROGRAM=30;
    public static final int GLYPH_TYPE=13;
    public static final int SIGIL_ARGS=35;
    public static final int BASIC=6;
    public static final int OPERATOR_FACET=26;
    public static final int STRING=96;
    public static final int STREAM_DEF=39;

    // delegates
    // delegators


        public UseContext(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public UseContext(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return UseContext.tokenNames; }
    public String getGrammarFileName() { return "/Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g"; }


      private static Context inProgress;
      private static String targetName;
      
      public static Context apply (Tree t, String target) {
         inProgress = new Context(target);
         TreeFilterSequence.apply(t);
         return inProgress;
      }

      



    // $ANTLR start "topdown"
    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g:35:1: topdown : ^(f= FUNCTION ( . )* ) ;
    public final void topdown() throws RecognitionException {
        CommonTree f=null;

        try {
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g:36:2: ( ^(f= FUNCTION ( . )* ) )
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g:36:4: ^(f= FUNCTION ( . )* )
            {
            f=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_topdown70); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g:36:17: ( . )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=AST_INVOKEABLE && LA1_0<=106)) ) {
                        alt1=1;
                    }
                    else if ( (LA1_0==UP) ) {
                        alt1=2;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/info/UseContext.g:36:17: .
                	    {
                	    matchAny(input); if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
            }
            if ( state.backtracking==1 ) {
               MultiPartName n = new MultiPartName(f.getText());
                    if (n.getName().equals(inProgress.target())) {
                      inProgress.update(f.getFirstChildWithType(LIST).getChildCount());
                      }
                  
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "topdown"

    // Delegated rules


 

    public static final BitSet FOLLOW_FUNCTION_in_topdown70 = new BitSet(new long[]{0x0000000000000004L});

}