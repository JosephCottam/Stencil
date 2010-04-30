// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g 2010-04-29 20:21:32
package stencil.parser.tree;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class StencilTreeAdapterLexer extends Lexer {
    public static final int WS=8;
    public static final int ID_IGNORE=7;
    public static final int INT=6;
    public static final int EQ=5;
    public static final int ID=4;
    public static final int EOF=-1;

    // delegates
    // delegators

    public StencilTreeAdapterLexer() {;} 
    public StencilTreeAdapterLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public StencilTreeAdapterLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g"; }

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:136:3: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' )+ )
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:136:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' )+
            {
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:136:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:
            	    {
            	    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:137:4: ( ( '0' .. '9' )+ )
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:137:6: ( '0' .. '9' )+
            {
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:137:6: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:137:6: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:138:3: ( '=' )
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:138:5: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "ID_IGNORE"
    public final void mID_IGNORE() throws RecognitionException {
        try {
            int _type = ID_IGNORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:143:2: ( '\\'' ( . )* '\\'' | ID INT )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='\'') ) {
                alt4=1;
            }
            else if ( ((LA4_0>='A' && LA4_0<='Z')||LA4_0=='_'||(LA4_0>='a' && LA4_0<='z')) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:143:4: '\\'' ( . )* '\\''
                    {
                    match('\''); 
                    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:143:9: ( . )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0=='\'') ) {
                            alt3=2;
                        }
                        else if ( ((LA3_0>='\u0000' && LA3_0<='&')||(LA3_0>='(' && LA3_0<='\uFFFF')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:143:9: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;
                case 2 :
                    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:144:4: ID INT
                    {
                    mID(); 
                    mINT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID_IGNORE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:145:4: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+ )
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:145:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            {
            // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:145:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\t' && LA5_0<='\n')||(LA5_0>='\f' && LA5_0<='\r')||LA5_0==' ') ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:8: ( ID | INT | EQ | ID_IGNORE | WS )
        int alt6=5;
        alt6 = dfa6.predict(input);
        switch (alt6) {
            case 1 :
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:10: ID
                {
                mID(); 

                }
                break;
            case 2 :
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:13: INT
                {
                mINT(); 

                }
                break;
            case 3 :
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:17: EQ
                {
                mEQ(); 

                }
                break;
            case 4 :
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:20: ID_IGNORE
                {
                mID_IGNORE(); 

                }
                break;
            case 5 :
                // /Users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:1:30: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    static final String DFA6_eotS =
        "\1\uffff\1\6\5\uffff";
    static final String DFA6_eofS =
        "\7\uffff";
    static final String DFA6_minS =
        "\1\11\1\60\5\uffff";
    static final String DFA6_maxS =
        "\2\172\5\uffff";
    static final String DFA6_acceptS =
        "\2\uffff\1\2\1\3\1\4\1\5\1\1";
    static final String DFA6_specialS =
        "\7\uffff}>";
    static final String[] DFA6_transitionS = {
            "\2\5\1\uffff\2\5\22\uffff\1\5\6\uffff\1\4\10\uffff\12\2\3\uffff"+
            "\1\3\3\uffff\32\1\4\uffff\1\1\1\uffff\32\1",
            "\12\4\7\uffff\32\1\4\uffff\1\1\1\uffff\32\1",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ID | INT | EQ | ID_IGNORE | WS );";
        }
    }
 

}