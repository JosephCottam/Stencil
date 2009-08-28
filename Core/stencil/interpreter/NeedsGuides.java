// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g 2009-08-28 15:23:32

	package stencil.interpreter;
	
	import stencil.parser.tree.*;	


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**Operators over a properly formed-AST and ensures that
 * all guides are up-to date.
 */
@SuppressWarnings("all")
public class NeedsGuides extends TreeFilter {
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


        public NeedsGuides(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public NeedsGuides(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return NeedsGuides.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g"; }


    	private boolean needsGuide;
    	
    	public boolean check(Program program) {
    		needsGuide = false;
    		downup(program);
    		return needsGuide;
    	}



    // $ANTLR start "topdown"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:54:1: topdown : ^( GUIDE ID . ID callGroup ) ;
    public final void topdown() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:54:8: ( ^( GUIDE ID . ID callGroup ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:54:10: ^( GUIDE ID . ID callGroup )
            {
            match(input,GUIDE,FOLLOW_GUIDE_in_topdown57); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ID,FOLLOW_ID_in_topdown59); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            match(input,ID,FOLLOW_ID_in_topdown63); if (state.failed) return ;
            pushFollow(FOLLOW_callGroup_in_topdown65);
            callGroup();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;

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


    // $ANTLR start "callGroup"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:55:1: callGroup : ^( CALL_GROUP callChain ) ;
    public final void callGroup() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:55:10: ( ^( CALL_GROUP callChain ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:55:12: ^( CALL_GROUP callChain )
            {
            match(input,CALL_GROUP,FOLLOW_CALL_GROUP_in_callGroup73); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_callChain_in_callGroup75);
            callChain();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;

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
    // $ANTLR end "callGroup"


    // $ANTLR start "callChain"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:56:1: callChain : ^( CALL_CHAIN target ) ;
    public final void callChain() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:56:10: ( ^( CALL_CHAIN target ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:56:12: ^( CALL_CHAIN target )
            {
            match(input,CALL_CHAIN,FOLLOW_CALL_CHAIN_in_callChain83); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_target_in_callChain85);
            target();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;

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
    // $ANTLR end "callChain"


    // $ANTLR start "target"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:57:1: target : ( ^(f= FUNCTION . . . target ) | ^( PACK . ) );
    public final void target() throws RecognitionException {
        StencilTree f=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:58:2: ( ^(f= FUNCTION . . . target ) | ^( PACK . ) )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==FUNCTION) ) {
                alt1=1;
            }
            else if ( (LA1_0==PACK) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:58:4: ^(f= FUNCTION . . . target )
                    {
                    f=(StencilTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_target97); if (state.failed) return ;

                    if ( state.backtracking==1 ) {
                      needsGuide = (needsGuide || ((Function) f).getOperator().refreshGuide());
                    }

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    pushFollow(FOLLOW_target_in_target107);
                    target();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/NeedsGuides.g:60:4: ^( PACK . )
                    {
                    match(input,PACK,FOLLOW_PACK_in_target116); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;

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
    // $ANTLR end "target"

    // Delegated rules


 

    public static final BitSet FOLLOW_GUIDE_in_topdown57 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_topdown59 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_ID_in_topdown63 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_callGroup_in_topdown65 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_GROUP_in_callGroup73 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_callChain_in_callGroup75 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_CHAIN_in_callChain83 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_target_in_callChain85 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_target97 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_target_in_target107 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACK_in_target116 = new BitSet(new long[]{0x0000000000000004L});

}