// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g 2009-08-05 10:52:03

  package stencil.legend.module.util;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ModuleDataLexer extends Lexer {
    public static final int DEF=20;
    public static final int CDATA=22;
    public static final int FACETS=10;
    public static final int WS=24;
    public static final int TARGET=15;
    public static final int FACET=9;
    public static final int REV=14;
    public static final int COMMENT=25;
    public static final int OPT=11;
    public static final int OPTS=12;
    public static final int TERM_OPEN=19;
    public static final int CLASS=4;
    public static final int VAL=21;
    public static final int OPEN=16;
    public static final int EOF=-1;
    public static final int CLOSE=17;
    public static final int DESC=6;
    public static final int MODULE_DATA=7;
    public static final int OPERATOR=8;
    public static final int NAME=13;
    public static final int DEFAULTS=5;
    public static final int TERM=18;
    public static final int ID=23;

    // delegates
    // delegators

    public ModuleDataLexer() {;} 
    public ModuleDataLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ModuleDataLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g"; }

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:11:7: ( 'Class' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:11:9: 'Class'
            {
            match("Class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "DEFAULTS"
    public final void mDEFAULTS() throws RecognitionException {
        try {
            int _type = DEFAULTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:12:10: ( 'Defaults' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:12:12: 'Defaults'
            {
            match("Defaults"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFAULTS"

    // $ANTLR start "DESC"
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:13:6: ( 'Description' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:13:8: 'Description'
            {
            match("Description"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DESC"

    // $ANTLR start "MODULE_DATA"
    public final void mMODULE_DATA() throws RecognitionException {
        try {
            int _type = MODULE_DATA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:14:13: ( 'ModuleData' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:14:15: 'ModuleData'
            {
            match("ModuleData"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MODULE_DATA"

    // $ANTLR start "OPERATOR"
    public final void mOPERATOR() throws RecognitionException {
        try {
            int _type = OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:15:10: ( 'Operator' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:15:12: 'Operator'
            {
            match("Operator"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPERATOR"

    // $ANTLR start "FACET"
    public final void mFACET() throws RecognitionException {
        try {
            int _type = FACET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:16:7: ( 'Facet' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:16:9: 'Facet'
            {
            match("Facet"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FACET"

    // $ANTLR start "FACETS"
    public final void mFACETS() throws RecognitionException {
        try {
            int _type = FACETS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:17:8: ( 'Facets' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:17:10: 'Facets'
            {
            match("Facets"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FACETS"

    // $ANTLR start "OPT"
    public final void mOPT() throws RecognitionException {
        try {
            int _type = OPT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:18:5: ( 'Opt' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:18:7: 'Opt'
            {
            match("Opt"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPT"

    // $ANTLR start "OPTS"
    public final void mOPTS() throws RecognitionException {
        try {
            int _type = OPTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:19:6: ( 'Opts' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:19:8: 'Opts'
            {
            match("Opts"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPTS"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:20:6: ( 'name' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:20:8: 'name'
            {
            match("name"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "REV"
    public final void mREV() throws RecognitionException {
        try {
            int _type = REV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:21:5: ( 'grammarRev' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:21:7: 'grammarRev'
            {
            match("grammarRev"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REV"

    // $ANTLR start "TARGET"
    public final void mTARGET() throws RecognitionException {
        try {
            int _type = TARGET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:22:8: ( 'target' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:22:10: 'target'
            {
            match("target"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TARGET"

    // $ANTLR start "OPEN"
    public final void mOPEN() throws RecognitionException {
        try {
            int _type = OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:23:6: ( '<' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:23:8: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPEN"

    // $ANTLR start "CLOSE"
    public final void mCLOSE() throws RecognitionException {
        try {
            int _type = CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:24:7: ( '\\>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:24:9: '\\>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE"

    // $ANTLR start "TERM"
    public final void mTERM() throws RecognitionException {
        try {
            int _type = TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:25:6: ( '/>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:25:8: '/>'
            {
            match("/>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TERM"

    // $ANTLR start "TERM_OPEN"
    public final void mTERM_OPEN() throws RecognitionException {
        try {
            int _type = TERM_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:26:11: ( '</' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:26:13: '</'
            {
            match("</"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TERM_OPEN"

    // $ANTLR start "DEF"
    public final void mDEF() throws RecognitionException {
        try {
            int _type = DEF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:27:5: ( '=' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:27:7: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEF"

    // $ANTLR start "VAL"
    public final void mVAL() throws RecognitionException {
        try {
            int _type = VAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:181:7: ( '\"' ( options {greedy=false; } : . )* '\"' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:181:9: '\"' ( options {greedy=false; } : . )* '\"'
            {
            match('\"'); 
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:181:13: ( options {greedy=false; } : . )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\"') ) {
                    alt1=2;
                }
                else if ( ((LA1_0>='\u0000' && LA1_0<='!')||(LA1_0>='#' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:181:39: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAL"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )* )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:37: ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='.'||(LA3_0>='0' && LA3_0<='9')||(LA3_0>='A' && LA3_0<='Z')||LA3_0=='_'||(LA3_0>='a' && LA3_0<='z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:38: ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )
            	    {
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:38: ( '.' )?
            	    int alt2=2;
            	    int LA2_0 = input.LA(1);

            	    if ( (LA2_0=='.') ) {
            	        alt2=1;
            	    }
            	    switch (alt2) {
            	        case 1 :
            	            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:183:38: '.'
            	            {
            	            match('.'); 

            	            }
            	            break;

            	    }

            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "CDATA"
    public final void mCDATA() throws RecognitionException {
        try {
            int _type = CDATA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:185:7: ( '<![CDATA[' ( options {greedy=false; } : . )* ']]>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:185:9: '<![CDATA[' ( options {greedy=false; } : . )* ']]>'
            {
            match("<![CDATA["); 

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:185:21: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==']') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1==']') ) {
                        int LA4_3 = input.LA(3);

                        if ( (LA4_3=='>') ) {
                            alt4=2;
                        }
                        else if ( ((LA4_3>='\u0000' && LA4_3<='=')||(LA4_3>='?' && LA4_3<='\uFFFF')) ) {
                            alt4=1;
                        }


                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<='\\')||(LA4_1>='^' && LA4_1<='\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='\\')||(LA4_0>='^' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:185:47: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match("]]>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CDATA"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:187:4: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+ )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:187:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:187:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
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
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:
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

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:188:9: ( '<!--' ( options {greedy=false; } : . )* '-->' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:188:13: '<!--' ( options {greedy=false; } : . )* '-->'
            {
            match("<!--"); 

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:188:20: ( options {greedy=false; } : . )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0=='-') ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1=='-') ) {
                        int LA6_3 = input.LA(3);

                        if ( (LA6_3=='>') ) {
                            alt6=2;
                        }
                        else if ( ((LA6_3>='\u0000' && LA6_3<='=')||(LA6_3>='?' && LA6_3<='\uFFFF')) ) {
                            alt6=1;
                        }


                    }
                    else if ( ((LA6_1>='\u0000' && LA6_1<=',')||(LA6_1>='.' && LA6_1<='\uFFFF')) ) {
                        alt6=1;
                    }


                }
                else if ( ((LA6_0>='\u0000' && LA6_0<=',')||(LA6_0>='.' && LA6_0<='\uFFFF')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:188:46: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            match("-->"); 

            skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    public void mTokens() throws RecognitionException {
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:8: ( CLASS | DEFAULTS | DESC | MODULE_DATA | OPERATOR | FACET | FACETS | OPT | OPTS | NAME | REV | TARGET | OPEN | CLOSE | TERM | TERM_OPEN | DEF | VAL | ID | CDATA | WS | COMMENT )
        int alt7=22;
        alt7 = dfa7.predict(input);
        switch (alt7) {
            case 1 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:10: CLASS
                {
                mCLASS(); 

                }
                break;
            case 2 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:16: DEFAULTS
                {
                mDEFAULTS(); 

                }
                break;
            case 3 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:25: DESC
                {
                mDESC(); 

                }
                break;
            case 4 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:30: MODULE_DATA
                {
                mMODULE_DATA(); 

                }
                break;
            case 5 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:42: OPERATOR
                {
                mOPERATOR(); 

                }
                break;
            case 6 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:51: FACET
                {
                mFACET(); 

                }
                break;
            case 7 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:57: FACETS
                {
                mFACETS(); 

                }
                break;
            case 8 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:64: OPT
                {
                mOPT(); 

                }
                break;
            case 9 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:68: OPTS
                {
                mOPTS(); 

                }
                break;
            case 10 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:73: NAME
                {
                mNAME(); 

                }
                break;
            case 11 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:78: REV
                {
                mREV(); 

                }
                break;
            case 12 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:82: TARGET
                {
                mTARGET(); 

                }
                break;
            case 13 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:89: OPEN
                {
                mOPEN(); 

                }
                break;
            case 14 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:94: CLOSE
                {
                mCLOSE(); 

                }
                break;
            case 15 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:100: TERM
                {
                mTERM(); 

                }
                break;
            case 16 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:105: TERM_OPEN
                {
                mTERM_OPEN(); 

                }
                break;
            case 17 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:115: DEF
                {
                mDEF(); 

                }
                break;
            case 18 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:119: VAL
                {
                mVAL(); 

                }
                break;
            case 19 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:123: ID
                {
                mID(); 

                }
                break;
            case 20 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:126: CDATA
                {
                mCDATA(); 

                }
                break;
            case 21 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:132: WS
                {
                mWS(); 

                }
                break;
            case 22 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:1:135: COMMENT
                {
                mCOMMENT(); 

                }
                break;

        }

    }


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\1\uffff\10\16\1\32\6\uffff\10\16\3\uffff\5\16\1\55\4\16\2\uffff"+
        "\5\16\1\67\1\uffff\1\16\1\71\2\16\1\74\4\16\1\uffff\1\102\1\uffff"+
        "\2\16\1\uffff\4\16\1\111\1\uffff\1\16\1\113\4\16\1\uffff\1\16\1"+
        "\uffff\1\121\2\16\1\124\1\16\1\uffff\2\16\1\uffff\2\16\1\132\1\133"+
        "\1\134\3\uffff";
    static final String DFA7_eofS =
        "\135\uffff";
    static final String DFA7_minS =
        "\1\11\1\154\1\145\1\157\1\160\2\141\1\162\1\141\1\41\6\uffff\1\141"+
        "\1\146\1\144\1\145\1\143\1\155\1\141\1\162\1\uffff\1\55\1\uffff"+
        "\1\163\1\141\1\143\1\165\1\162\1\56\2\145\1\155\1\147\2\uffff\1"+
        "\163\1\165\1\162\1\154\1\141\1\56\1\uffff\1\164\1\56\1\155\1\145"+
        "\1\56\1\154\1\151\1\145\1\164\1\uffff\1\56\1\uffff\1\141\1\164\1"+
        "\uffff\1\164\1\160\1\104\1\157\1\56\1\uffff\1\162\1\56\1\163\1\164"+
        "\1\141\1\162\1\uffff\1\122\1\uffff\1\56\1\151\1\164\1\56\1\145\1"+
        "\uffff\1\157\1\141\1\uffff\1\166\1\156\3\56\3\uffff";
    static final String DFA7_maxS =
        "\1\172\1\154\1\145\1\157\1\160\2\141\1\162\1\141\1\57\6\uffff\1"+
        "\141\1\163\1\144\1\164\1\143\1\155\1\141\1\162\1\uffff\1\133\1\uffff"+
        "\1\163\1\141\1\143\1\165\1\162\1\172\2\145\1\155\1\147\2\uffff\1"+
        "\163\1\165\1\162\1\154\1\141\1\172\1\uffff\1\164\1\172\1\155\1\145"+
        "\1\172\1\154\1\151\1\145\1\164\1\uffff\1\172\1\uffff\1\141\1\164"+
        "\1\uffff\1\164\1\160\1\104\1\157\1\172\1\uffff\1\162\1\172\1\163"+
        "\1\164\1\141\1\162\1\uffff\1\122\1\uffff\1\172\1\151\1\164\1\172"+
        "\1\145\1\uffff\1\157\1\141\1\uffff\1\166\1\156\3\172\3\uffff";
    static final String DFA7_acceptS =
        "\12\uffff\1\16\1\17\1\21\1\22\1\23\1\25\10\uffff\1\20\1\uffff\1"+
        "\15\12\uffff\1\24\1\26\6\uffff\1\10\11\uffff\1\11\1\uffff\1\12\2"+
        "\uffff\1\1\5\uffff\1\6\6\uffff\1\7\1\uffff\1\14\5\uffff\1\2\2\uffff"+
        "\1\5\5\uffff\1\4\1\13\1\3";
    static final String DFA7_specialS =
        "\135\uffff}>";
    static final String[] DFA7_transitionS = {
            "\2\17\1\uffff\2\17\22\uffff\1\17\1\uffff\1\15\14\uffff\1\13"+
            "\14\uffff\1\11\1\14\1\12\2\uffff\2\16\1\1\1\2\1\16\1\5\6\16"+
            "\1\3\1\16\1\4\13\16\4\uffff\1\16\1\uffff\6\16\1\7\6\16\1\6\5"+
            "\16\1\10\6\16",
            "\1\20",
            "\1\21",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\25",
            "\1\26",
            "\1\27",
            "\1\31\15\uffff\1\30",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\33",
            "\1\34\14\uffff\1\35",
            "\1\36",
            "\1\37\16\uffff\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "",
            "\1\46\55\uffff\1\45",
            "",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\22\16"+
            "\1\54\7\16",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "",
            "",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "",
            "\1\70",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\72",
            "\1\73",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\22\16"+
            "\1\101\7\16",
            "",
            "\1\103",
            "\1\104",
            "",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "",
            "\1\112",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "",
            "\1\120",
            "",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\122",
            "\1\123",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\125",
            "",
            "\1\126",
            "\1\127",
            "",
            "\1\130",
            "\1\131",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "\1\16\1\uffff\12\16\7\uffff\32\16\4\uffff\1\16\1\uffff\32\16",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( CLASS | DEFAULTS | DESC | MODULE_DATA | OPERATOR | FACET | FACETS | OPT | OPTS | NAME | REV | TARGET | OPEN | CLOSE | TERM | TERM_OPEN | DEF | VAL | ID | CDATA | WS | COMMENT );";
        }
    }
 

}