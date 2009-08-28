// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g 2009-08-28 15:16:25

	package stencil.parser.string;
	import static stencil.util.Tuples.stripQuotes;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class StencilLexer extends Lexer {
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

    public StencilLexer() {;} 
    public StencilLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public StencilLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g"; }

    // $ANTLR start "ALL"
    public final void mALL() throws RecognitionException {
        try {
            int _type = ALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:12:5: ( 'all' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:12:7: 'all'
            {
            match("all"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALL"

    // $ANTLR start "BASE"
    public final void mBASE() throws RecognitionException {
        try {
            int _type = BASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:13:6: ( 'base' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:13:8: 'base'
            {
            match("base"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BASE"

    // $ANTLR start "CANVAS"
    public final void mCANVAS() throws RecognitionException {
        try {
            int _type = CANVAS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:14:8: ( 'canvas' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:14:10: 'canvas'
            {
            match("canvas"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CANVAS"

    // $ANTLR start "COLOR"
    public final void mCOLOR() throws RecognitionException {
        try {
            int _type = COLOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:15:7: ( 'color' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:15:9: 'color'
            {
            match("color"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLOR"

    // $ANTLR start "DEFAULT"
    public final void mDEFAULT() throws RecognitionException {
        try {
            int _type = DEFAULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:16:9: ( 'default' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:16:11: 'default'
            {
            match("default"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFAULT"

    // $ANTLR start "EXTERNAL"
    public final void mEXTERNAL() throws RecognitionException {
        try {
            int _type = EXTERNAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:17:10: ( 'external' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:17:12: 'external'
            {
            match("external"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXTERNAL"

    // $ANTLR start "FILTER"
    public final void mFILTER() throws RecognitionException {
        try {
            int _type = FILTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:18:8: ( 'filter' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:18:10: 'filter'
            {
            match("filter"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FILTER"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:19:6: ( 'from' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:19:8: 'from'
            {
            match("from"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "GLYPH"
    public final void mGLYPH() throws RecognitionException {
        try {
            int _type = GLYPH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:20:7: ( 'glyph' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:20:9: 'glyph'
            {
            match("glyph"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GLYPH"

    // $ANTLR start "IMPORT"
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:21:8: ( 'import' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:21:10: 'import'
            {
            match("import"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPORT"

    // $ANTLR start "LOCAL"
    public final void mLOCAL() throws RecognitionException {
        try {
            int _type = LOCAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:22:7: ( 'local' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:22:9: 'local'
            {
            match("local"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LOCAL"

    // $ANTLR start "LAYER"
    public final void mLAYER() throws RecognitionException {
        try {
            int _type = LAYER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:23:7: ( 'layer' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:23:9: 'layer'
            {
            match("layer"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LAYER"

    // $ANTLR start "LEGEND"
    public final void mLEGEND() throws RecognitionException {
        try {
            int _type = LEGEND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:24:8: ( 'legend' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:24:10: 'legend'
            {
            match("legend"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEGEND"

    // $ANTLR start "ORDER"
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:25:7: ( 'order' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:25:9: 'order'
            {
            match("order"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORDER"

    // $ANTLR start "PYTHON"
    public final void mPYTHON() throws RecognitionException {
        try {
            int _type = PYTHON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:26:8: ( 'python' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:26:10: 'python'
            {
            match("python"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PYTHON"

    // $ANTLR start "RETURN"
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:27:8: ( 'return' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:27:10: 'return'
            {
            match("return"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RETURN"

    // $ANTLR start "STATIC"
    public final void mSTATIC() throws RecognitionException {
        try {
            int _type = STATIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:28:8: ( 'static' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:28:10: 'static'
            {
            match("static"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STATIC"

    // $ANTLR start "STREAM"
    public final void mSTREAM() throws RecognitionException {
        try {
            int _type = STREAM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:29:8: ( 'stream' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:29:10: 'stream'
            {
            match("stream"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STREAM"

    // $ANTLR start "VIEW"
    public final void mVIEW() throws RecognitionException {
        try {
            int _type = VIEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:30:6: ( 'view' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:30:8: 'view'
            {
            match("view"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VIEW"

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:31:4: ( 'as' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:31:6: 'as'
            {
            match("as"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "FACET"
    public final void mFACET() throws RecognitionException {
        try {
            int _type = FACET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:32:7: ( 'facet' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:32:9: 'facet'
            {
            match("facet"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FACET"

    // $ANTLR start "GROUP"
    public final void mGROUP() throws RecognitionException {
        try {
            int _type = GROUP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:33:7: ( '(' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:33:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GROUP"

    // $ANTLR start "CLOSE_GROUP"
    public final void mCLOSE_GROUP() throws RecognitionException {
        try {
            int _type = CLOSE_GROUP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:34:13: ( ')' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:34:15: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE_GROUP"

    // $ANTLR start "ARG"
    public final void mARG() throws RecognitionException {
        try {
            int _type = ARG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:35:5: ( '[' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:35:7: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARG"

    // $ANTLR start "CLOSE_ARG"
    public final void mCLOSE_ARG() throws RecognitionException {
        try {
            int _type = CLOSE_ARG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:36:11: ( ']' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:36:13: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLOSE_ARG"

    // $ANTLR start "SEPARATOR"
    public final void mSEPARATOR() throws RecognitionException {
        try {
            int _type = SEPARATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:37:11: ( ',' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:37:13: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEPARATOR"

    // $ANTLR start "RANGE"
    public final void mRANGE() throws RecognitionException {
        try {
            int _type = RANGE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:38:7: ( '..' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:38:9: '..'
            {
            match(".."); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RANGE"

    // $ANTLR start "NAMESPACE"
    public final void mNAMESPACE() throws RecognitionException {
        try {
            int _type = NAMESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:39:11: ( '::' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:39:13: '::'
            {
            match("::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAMESPACE"

    // $ANTLR start "NAMESPLIT"
    public final void mNAMESPLIT() throws RecognitionException {
        try {
            int _type = NAMESPLIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:40:11: ( '.' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:40:13: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAMESPLIT"

    // $ANTLR start "DEFINE"
    public final void mDEFINE() throws RecognitionException {
        try {
            int _type = DEFINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:41:8: ( ':' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:41:10: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFINE"

    // $ANTLR start "DYNAMIC"
    public final void mDYNAMIC() throws RecognitionException {
        try {
            int _type = DYNAMIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:42:9: ( '<<' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:42:11: '<<'
            {
            match("<<"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DYNAMIC"

    // $ANTLR start "YIELDS"
    public final void mYIELDS() throws RecognitionException {
        try {
            int _type = YIELDS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:43:8: ( '->' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:43:10: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "YIELDS"

    // $ANTLR start "FEED"
    public final void mFEED() throws RecognitionException {
        try {
            int _type = FEED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:44:6: ( '>>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:44:8: '>>'
            {
            match(">>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FEED"

    // $ANTLR start "GUIDE_FEED"
    public final void mGUIDE_FEED() throws RecognitionException {
        try {
            int _type = GUIDE_FEED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:45:12: ( '#>>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:45:14: '#>>'
            {
            match("#>>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GUIDE_FEED"

    // $ANTLR start "GUIDE_YIELD"
    public final void mGUIDE_YIELD() throws RecognitionException {
        try {
            int _type = GUIDE_YIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:46:13: ( '#->' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:46:15: '#->'
            {
            match("#->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GUIDE_YIELD"

    // $ANTLR start "GATE"
    public final void mGATE() throws RecognitionException {
        try {
            int _type = GATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:47:6: ( '=>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:47:8: '=>'
            {
            match("=>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GATE"

    // $ANTLR start "SPLIT"
    public final void mSPLIT() throws RecognitionException {
        try {
            int _type = SPLIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:48:7: ( '|' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:48:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SPLIT"

    // $ANTLR start "JOIN"
    public final void mJOIN() throws RecognitionException {
        try {
            int _type = JOIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:49:6: ( '-->' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:49:8: '-->'
            {
            match("-->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "JOIN"

    // $ANTLR start "TAG"
    public final void mTAG() throws RecognitionException {
        try {
            int _type = TAG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:50:5: ( '@' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:50:7: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TAG"

    // $ANTLR start "T__73"
    public final void mT__73() throws RecognitionException {
        try {
            int _type = T__73;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:51:7: ( '>' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:51:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__73"

    // $ANTLR start "T__74"
    public final void mT__74() throws RecognitionException {
        try {
            int _type = T__74;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:52:7: ( 'Init' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:52:9: 'Init'
            {
            match("Init"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__74"

    // $ANTLR start "T__75"
    public final void mT__75() throws RecognitionException {
        try {
            int _type = T__75;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:53:7: ( '=' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:53:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__75"

    // $ANTLR start "T__76"
    public final void mT__76() throws RecognitionException {
        try {
            int _type = T__76;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:54:7: ( 'n' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:54:9: 'n'
            {
            match('n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__76"

    // $ANTLR start "T__77"
    public final void mT__77() throws RecognitionException {
        try {
            int _type = T__77;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:55:7: ( '>=' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:55:9: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__77"

    // $ANTLR start "T__78"
    public final void mT__78() throws RecognitionException {
        try {
            int _type = T__78;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:56:7: ( '<' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:56:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__78"

    // $ANTLR start "T__79"
    public final void mT__79() throws RecognitionException {
        try {
            int _type = T__79;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:57:7: ( '<=' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:57:9: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__79"

    // $ANTLR start "T__80"
    public final void mT__80() throws RecognitionException {
        try {
            int _type = T__80;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:58:7: ( '!=' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:58:9: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__80"

    // $ANTLR start "T__81"
    public final void mT__81() throws RecognitionException {
        try {
            int _type = T__81;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:59:7: ( '=~' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:59:9: '=~'
            {
            match("=~"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__81"

    // $ANTLR start "T__82"
    public final void mT__82() throws RecognitionException {
        try {
            int _type = T__82;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:60:7: ( '!~' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:60:9: '!~'
            {
            match("!~"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__82"

    // $ANTLR start "T__83"
    public final void mT__83() throws RecognitionException {
        try {
            int _type = T__83;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:61:7: ( '-' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:61:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__83"

    // $ANTLR start "T__84"
    public final void mT__84() throws RecognitionException {
        try {
            int _type = T__84;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:62:7: ( '+' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:62:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__84"

    // $ANTLR start "TAGGED_ID"
    public final void mTAGGED_ID() throws RecognitionException {
        try {
            int _type = TAGGED_ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:402:10: ( TAG ID )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:402:12: TAG ID
            {
            mTAG(); 
            mID(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TAGGED_ID"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:6: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )* )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:8: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:36: ( ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='.'||(LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_'||(LA2_0>='a' && LA2_0<='z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:37: ( '.' )? ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )
            	    {
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:37: ( '.' )?
            	    int alt1=2;
            	    int LA1_0 = input.LA(1);

            	    if ( (LA1_0=='.') ) {
            	        alt1=1;
            	    }
            	    switch (alt1) {
            	        case 1 :
            	            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:404:37: '.'
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
            	    break loop2;
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

    // $ANTLR start "DIGITS"
    public final void mDIGITS() throws RecognitionException {
        try {
            int _type = DIGITS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:406:9: ( ( '0' .. '9' )+ )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:406:11: ( '0' .. '9' )+
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:406:11: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:406:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGITS"

    // $ANTLR start "CODE_BLOCK"
    public final void mCODE_BLOCK() throws RecognitionException {
        try {
            int _type = CODE_BLOCK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:410:3: ( '{{' ( . )* '}}' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:410:5: '{{' ( . )* '}}'
            {
            match("{{"); 

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:410:10: ( . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='}') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1=='}') ) {
                        alt4=2;
                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<='|')||(LA4_1>='~' && LA4_1<='\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='|')||(LA4_0>='~' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:410:10: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match("}}"); 

            setText(getText().substring(2, getText().length()-2));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CODE_BLOCK"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:414:6: ( '\"' ( ESCAPE_SEQUENCE | ~ ( '\\\\' | '\"' ) )* '\"' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:414:9: '\"' ( ESCAPE_SEQUENCE | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:414:13: ( ESCAPE_SEQUENCE | ~ ( '\\\\' | '\"' ) )*
            loop5:
            do {
                int alt5=3;
                int LA5_0 = input.LA(1);

                if ( (LA5_0=='\\') ) {
                    alt5=1;
                }
                else if ( ((LA5_0>='\u0000' && LA5_0<='!')||(LA5_0>='#' && LA5_0<='[')||(LA5_0>=']' && LA5_0<='\uFFFF')) ) {
                    alt5=2;
                }


                switch (alt5) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:414:15: ESCAPE_SEQUENCE
            	    {
            	    mESCAPE_SEQUENCE(); 

            	    }
            	    break;
            	case 2 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:414:33: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match('\"'); 
            setText(stripQuotes(getText()));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "ESCAPE_SEQUENCE"
    public final void mESCAPE_SEQUENCE() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:419:3: ( '\\\\b' | '\\\\t' | '\\\\n' | '\\\\f' | '\\\\r' | '\\\\\\\"' | '\\\\\\'' | '\\\\\\\\' )
            int alt6=8;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:419:5: '\\\\b'
                    {
                    match("\\b"); 

                    setText(getText().substring(0, getText().length()-2) + "\bp");

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:420:5: '\\\\t'
                    {
                    match("\\t"); 

                    setText(getText().substring(0, getText().length()-2) + "\tp");

                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:421:5: '\\\\n'
                    {
                    match("\\n"); 

                    setText(getText().substring(0, getText().length()-2) + "\np");

                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:422:5: '\\\\f'
                    {
                    match("\\f"); 

                    setText(getText().substring(0, getText().length()-2) + "\fp");

                    }
                    break;
                case 5 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:423:5: '\\\\r'
                    {
                    match("\\r"); 

                    setText(getText().substring(0, getText().length()-2) + "\rp");

                    }
                    break;
                case 6 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:424:5: '\\\\\\\"'
                    {
                    match("\\\""); 

                    setText(getText().substring(0, getText().length()-2) + "\"p");

                    }
                    break;
                case 7 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:425:5: '\\\\\\''
                    {
                    match("\\'"); 

                    setText(getText().substring(0, getText().length()-2) + "\'p");

                    }
                    break;
                case 8 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:426:5: '\\\\\\\\'
                    {
                    match("\\\\"); 

                    setText(getText().substring(0, getText().length()-2) + "\\p");

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESCAPE_SEQUENCE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:429:4: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+ )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:429:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:429:6: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='\t' && LA7_0<='\n')||(LA7_0>='\f' && LA7_0<='\r')||LA7_0==' ') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:
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
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
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
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:430:9: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:430:13: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:430:18: ( options {greedy=false; } : . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='*') ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1=='/') ) {
                        alt8=2;
                    }
                    else if ( ((LA8_1>='\u0000' && LA8_1<='.')||(LA8_1>='0' && LA8_1<='\uFFFF')) ) {
                        alt8=1;
                    }


                }
                else if ( ((LA8_0>='\u0000' && LA8_0<=')')||(LA8_0>='+' && LA8_0<='\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:430:44: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match("*/"); 

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
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:8: ( ALL | BASE | CANVAS | COLOR | DEFAULT | EXTERNAL | FILTER | FROM | GLYPH | IMPORT | LOCAL | LAYER | LEGEND | ORDER | PYTHON | RETURN | STATIC | STREAM | VIEW | AS | FACET | GROUP | CLOSE_GROUP | ARG | CLOSE_ARG | SEPARATOR | RANGE | NAMESPACE | NAMESPLIT | DEFINE | DYNAMIC | YIELDS | FEED | GUIDE_FEED | GUIDE_YIELD | GATE | SPLIT | JOIN | TAG | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | TAGGED_ID | ID | DIGITS | CODE_BLOCK | STRING | WS | COMMENT )
        int alt9=58;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:10: ALL
                {
                mALL(); 

                }
                break;
            case 2 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:14: BASE
                {
                mBASE(); 

                }
                break;
            case 3 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:19: CANVAS
                {
                mCANVAS(); 

                }
                break;
            case 4 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:26: COLOR
                {
                mCOLOR(); 

                }
                break;
            case 5 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:32: DEFAULT
                {
                mDEFAULT(); 

                }
                break;
            case 6 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:40: EXTERNAL
                {
                mEXTERNAL(); 

                }
                break;
            case 7 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:49: FILTER
                {
                mFILTER(); 

                }
                break;
            case 8 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:56: FROM
                {
                mFROM(); 

                }
                break;
            case 9 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:61: GLYPH
                {
                mGLYPH(); 

                }
                break;
            case 10 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:67: IMPORT
                {
                mIMPORT(); 

                }
                break;
            case 11 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:74: LOCAL
                {
                mLOCAL(); 

                }
                break;
            case 12 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:80: LAYER
                {
                mLAYER(); 

                }
                break;
            case 13 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:86: LEGEND
                {
                mLEGEND(); 

                }
                break;
            case 14 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:93: ORDER
                {
                mORDER(); 

                }
                break;
            case 15 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:99: PYTHON
                {
                mPYTHON(); 

                }
                break;
            case 16 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:106: RETURN
                {
                mRETURN(); 

                }
                break;
            case 17 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:113: STATIC
                {
                mSTATIC(); 

                }
                break;
            case 18 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:120: STREAM
                {
                mSTREAM(); 

                }
                break;
            case 19 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:127: VIEW
                {
                mVIEW(); 

                }
                break;
            case 20 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:132: AS
                {
                mAS(); 

                }
                break;
            case 21 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:135: FACET
                {
                mFACET(); 

                }
                break;
            case 22 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:141: GROUP
                {
                mGROUP(); 

                }
                break;
            case 23 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:147: CLOSE_GROUP
                {
                mCLOSE_GROUP(); 

                }
                break;
            case 24 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:159: ARG
                {
                mARG(); 

                }
                break;
            case 25 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:163: CLOSE_ARG
                {
                mCLOSE_ARG(); 

                }
                break;
            case 26 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:173: SEPARATOR
                {
                mSEPARATOR(); 

                }
                break;
            case 27 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:183: RANGE
                {
                mRANGE(); 

                }
                break;
            case 28 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:189: NAMESPACE
                {
                mNAMESPACE(); 

                }
                break;
            case 29 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:199: NAMESPLIT
                {
                mNAMESPLIT(); 

                }
                break;
            case 30 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:209: DEFINE
                {
                mDEFINE(); 

                }
                break;
            case 31 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:216: DYNAMIC
                {
                mDYNAMIC(); 

                }
                break;
            case 32 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:224: YIELDS
                {
                mYIELDS(); 

                }
                break;
            case 33 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:231: FEED
                {
                mFEED(); 

                }
                break;
            case 34 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:236: GUIDE_FEED
                {
                mGUIDE_FEED(); 

                }
                break;
            case 35 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:247: GUIDE_YIELD
                {
                mGUIDE_YIELD(); 

                }
                break;
            case 36 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:259: GATE
                {
                mGATE(); 

                }
                break;
            case 37 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:264: SPLIT
                {
                mSPLIT(); 

                }
                break;
            case 38 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:270: JOIN
                {
                mJOIN(); 

                }
                break;
            case 39 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:275: TAG
                {
                mTAG(); 

                }
                break;
            case 40 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:279: T__73
                {
                mT__73(); 

                }
                break;
            case 41 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:285: T__74
                {
                mT__74(); 

                }
                break;
            case 42 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:291: T__75
                {
                mT__75(); 

                }
                break;
            case 43 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:297: T__76
                {
                mT__76(); 

                }
                break;
            case 44 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:303: T__77
                {
                mT__77(); 

                }
                break;
            case 45 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:309: T__78
                {
                mT__78(); 

                }
                break;
            case 46 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:315: T__79
                {
                mT__79(); 

                }
                break;
            case 47 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:321: T__80
                {
                mT__80(); 

                }
                break;
            case 48 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:327: T__81
                {
                mT__81(); 

                }
                break;
            case 49 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:333: T__82
                {
                mT__82(); 

                }
                break;
            case 50 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:339: T__83
                {
                mT__83(); 

                }
                break;
            case 51 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:345: T__84
                {
                mT__84(); 

                }
                break;
            case 52 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:351: TAGGED_ID
                {
                mTAGGED_ID(); 

                }
                break;
            case 53 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:361: ID
                {
                mID(); 

                }
                break;
            case 54 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:364: DIGITS
                {
                mDIGITS(); 

                }
                break;
            case 55 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:371: CODE_BLOCK
                {
                mCODE_BLOCK(); 

                }
                break;
            case 56 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:382: STRING
                {
                mSTRING(); 

                }
                break;
            case 57 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:389: WS
                {
                mWS(); 

                }
                break;
            case 58 :
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:1:392: COMMENT
                {
                mCOMMENT(); 

                }
                break;

        }

    }


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA6_eotS =
        "\12\uffff";
    static final String DFA6_eofS =
        "\12\uffff";
    static final String DFA6_minS =
        "\1\134\1\42\10\uffff";
    static final String DFA6_maxS =
        "\1\134\1\164\10\uffff";
    static final String DFA6_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA6_specialS =
        "\12\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1",
            "\1\7\4\uffff\1\10\64\uffff\1\11\5\uffff\1\2\3\uffff\1\5\7\uffff"+
            "\1\4\3\uffff\1\6\1\uffff\1\3",
            "",
            "",
            "",
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
            return "417:1: fragment ESCAPE_SEQUENCE : ( '\\\\b' | '\\\\t' | '\\\\n' | '\\\\f' | '\\\\r' | '\\\\\\\"' | '\\\\\\'' | '\\\\\\\\' );";
        }
    }
    static final String DFA9_eotS =
        "\1\uffff\16\41\5\uffff\1\74\1\76\1\101\1\104\1\107\1\uffff\1\114"+
        "\1\uffff\1\115\1\41\1\120\10\uffff\1\41\1\124\22\41\24\uffff\1\41"+
        "\3\uffff\1\151\1\uffff\24\41\1\uffff\1\176\5\41\1\u0084\13\41\1"+
        "\u0090\1\u0091\1\uffff\1\41\1\u0093\3\41\1\uffff\1\u0097\1\u0098"+
        "\1\41\1\u009a\1\u009b\1\41\1\u009d\4\41\2\uffff\1\u00a2\1\uffff"+
        "\2\41\1\u00a5\2\uffff\1\u00a6\2\uffff\1\u00a7\1\uffff\1\u00a8\1"+
        "\u00a9\1\u00aa\1\u00ab\1\uffff\1\u00ac\1\41\10\uffff\1\u00ae\1\uffff";
    static final String DFA9_eofS =
        "\u00af\uffff";
    static final String DFA9_minS =
        "\1\11\1\154\2\141\1\145\1\170\1\141\1\154\1\155\1\141\1\162\1\171"+
        "\1\145\1\164\1\151\5\uffff\1\56\1\72\1\74\1\55\1\75\1\55\1\76\1"+
        "\uffff\1\101\1\156\1\56\1\75\7\uffff\1\154\1\56\1\163\1\156\1\154"+
        "\1\146\1\164\1\154\1\157\1\143\1\171\1\160\1\143\1\171\1\147\1\144"+
        "\2\164\1\141\1\145\24\uffff\1\151\3\uffff\1\56\1\uffff\1\145\1\166"+
        "\1\157\1\141\1\145\1\164\1\155\1\145\1\160\1\157\1\141\3\145\1\150"+
        "\1\165\1\164\1\145\1\167\1\164\1\uffff\1\56\1\141\1\162\1\165\1"+
        "\162\1\145\1\56\1\164\1\150\1\162\1\154\1\162\1\156\1\162\1\157"+
        "\1\162\1\151\1\141\2\56\1\uffff\1\163\1\56\1\154\1\156\1\162\1\uffff"+
        "\2\56\1\164\2\56\1\144\1\56\2\156\1\143\1\155\2\uffff\1\56\1\uffff"+
        "\1\164\1\141\1\56\2\uffff\1\56\2\uffff\1\56\1\uffff\4\56\1\uffff"+
        "\1\56\1\154\10\uffff\1\56\1\uffff";
    static final String DFA9_maxS =
        "\1\174\1\163\1\141\1\157\1\145\1\170\1\162\1\154\1\155\1\157\1\162"+
        "\1\171\1\145\1\164\1\151\5\uffff\1\56\1\72\1\75\3\76\1\176\1\uffff"+
        "\1\172\1\156\1\172\1\176\7\uffff\1\154\1\172\1\163\1\156\1\154\1"+
        "\146\1\164\1\154\1\157\1\143\1\171\1\160\1\143\1\171\1\147\1\144"+
        "\2\164\1\162\1\145\24\uffff\1\151\3\uffff\1\172\1\uffff\1\145\1"+
        "\166\1\157\1\141\1\145\1\164\1\155\1\145\1\160\1\157\1\141\3\145"+
        "\1\150\1\165\1\164\1\145\1\167\1\164\1\uffff\1\172\1\141\1\162\1"+
        "\165\1\162\1\145\1\172\1\164\1\150\1\162\1\154\1\162\1\156\1\162"+
        "\1\157\1\162\1\151\1\141\2\172\1\uffff\1\163\1\172\1\154\1\156\1"+
        "\162\1\uffff\2\172\1\164\2\172\1\144\1\172\2\156\1\143\1\155\2\uffff"+
        "\1\172\1\uffff\1\164\1\141\1\172\2\uffff\1\172\2\uffff\1\172\1\uffff"+
        "\4\172\1\uffff\1\172\1\154\10\uffff\1\172\1\uffff";
    static final String DFA9_acceptS =
        "\17\uffff\1\26\1\27\1\30\1\31\1\32\7\uffff\1\45\4\uffff\1\63\1\65"+
        "\1\66\1\67\1\70\1\71\1\72\24\uffff\1\33\1\35\1\34\1\36\1\37\1\56"+
        "\1\55\1\40\1\46\1\62\1\41\1\54\1\50\1\42\1\43\1\44\1\60\1\52\1\47"+
        "\1\64\1\uffff\1\53\1\57\1\61\1\uffff\1\24\24\uffff\1\1\24\uffff"+
        "\1\2\5\uffff\1\10\13\uffff\1\23\1\51\1\uffff\1\4\3\uffff\1\25\1"+
        "\11\1\uffff\1\13\1\14\1\uffff\1\16\4\uffff\1\3\2\uffff\1\7\1\12"+
        "\1\15\1\17\1\20\1\21\1\22\1\5\1\uffff\1\6";
    static final String DFA9_specialS =
        "\u00af\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\45\1\uffff\2\45\22\uffff\1\45\1\37\1\44\1\31\4\uffff\1\17"+
            "\1\20\1\uffff\1\40\1\23\1\27\1\24\1\46\12\42\1\25\1\uffff\1"+
            "\26\1\32\1\30\1\uffff\1\34\10\41\1\35\21\41\1\21\1\uffff\1\22"+
            "\1\uffff\1\41\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\41\1\10"+
            "\2\41\1\11\1\41\1\36\1\12\1\13\1\41\1\14\1\15\2\41\1\16\4\41"+
            "\1\43\1\33",
            "\1\47\6\uffff\1\50",
            "\1\51",
            "\1\52\15\uffff\1\53",
            "\1\54",
            "\1\55",
            "\1\60\7\uffff\1\56\10\uffff\1\57",
            "\1\61",
            "\1\62",
            "\1\64\3\uffff\1\65\11\uffff\1\63",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "",
            "",
            "",
            "",
            "",
            "\1\73",
            "\1\75",
            "\1\77\1\100",
            "\1\103\20\uffff\1\102",
            "\1\106\1\105",
            "\1\111\20\uffff\1\110",
            "\1\112\77\uffff\1\113",
            "",
            "\32\116\4\uffff\1\116\1\uffff\32\116",
            "\1\117",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\121\100\uffff\1\122",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\123",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145\20\uffff\1\146",
            "\1\147",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\150",
            "",
            "",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\u0083",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "\1\u0092",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u0099",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u009c",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "\1\u00a3",
            "\1\u00a4",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            "\1\u00ad",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\41\1\uffff\12\41\7\uffff\32\41\4\uffff\1\41\1\uffff\32\41",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ALL | BASE | CANVAS | COLOR | DEFAULT | EXTERNAL | FILTER | FROM | GLYPH | IMPORT | LOCAL | LAYER | LEGEND | ORDER | PYTHON | RETURN | STATIC | STREAM | VIEW | AS | FACET | GROUP | CLOSE_GROUP | ARG | CLOSE_ARG | SEPARATOR | RANGE | NAMESPACE | NAMESPLIT | DEFINE | DYNAMIC | YIELDS | FEED | GUIDE_FEED | GUIDE_YIELD | GATE | SPLIT | JOIN | TAG | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | T__80 | T__81 | T__82 | T__83 | T__84 | TAGGED_ID | ID | DIGITS | CODE_BLOCK | STRING | WS | COMMENT );";
        }
    }
 

}