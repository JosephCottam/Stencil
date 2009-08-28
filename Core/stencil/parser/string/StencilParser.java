// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g 2009-08-28 15:16:22

	package stencil.parser.string;

	//TODO: Include base types in layers
	//TODO: Remove/delete glyph operation
	//TODO: Replacement of identifiers with numbers in tuples
	//TOOD: Modular color space

	import static stencil.parser.ParserConstants.*;
	import java.util.ArrayList;
	import java.util.List;
	
	


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

@SuppressWarnings("all")
public class StencilParser extends Parser {
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
    public static final int VIEW=44;
    public static final int RULE=20;
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
    public static final int SPLIT=62;
    public static final int FILTER=32;
    public static final int TAGGED_ID=67;
    public static final int CODE_BLOCK=66;
    public static final int WS=71;
    public static final int STRING=68;
    public static final int T__79=79;
    public static final int CALL_CHAIN=9;
    public static final int NAMESPLIT=54;
    public static final int LEGEND_RULE=12;
    public static final int COMMENT=72;
    public static final int T__77=77;
    public static final int SPECIALIZER=22;
    public static final int CLOSE_ARG=50;
    public static final int GUIDE_YIELD=60;
    public static final int STATIC=42;
    public static final int T__84=84;
    public static final int DEFINE=55;
    public static final int SEPARATOR=51;
    public static final int RETURN=41;
    public static final int T__75=75;
    public static final int ESCAPE_SEQUENCE=70;
    public static final int ARG=49;
    public static final int CONSUMES=7;
    public static final int EOF=-1;
    public static final int DYNAMIC=56;
    public static final int PYTHON=40;
    public static final int COLOR=29;
    public static final int T__76=76;
    public static final int FEED=58;
    public static final int ANNOTATION=4;
    public static final int T__82=82;
    public static final int DEFAULT=30;
    public static final int T__81=81;
    public static final int BOOLEAN_OP=5;
    public static final int ALL=26;
    public static final int T__83=83;

    // delegates
    // delegators


        public StencilParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public StencilParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return StencilParser.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g"; }


    	List<String> errors = new ArrayList<String>();

    	/**Buried IDs are strings that cannot be input as identifiers according to the
    	 * Stencil grammar, but are used internally as IDs.
    	 */
    	public static String buryID(String input) {return "#" + input;}
    	
    	public void emitErrorMessage(String msg) {errors.add(msg);}
    	public List getErrors() {return errors;}


    	public static enum RuleOpts {
    		All, 	//Anything is allowed 
    		Simple,	//Only argument lists (no split or range)
    		Empty}; //Must be empty


    public static class program_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "program"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:1: program : ( imports )* externals order ( streamDef | layerDef | legendDef | pythonDef )* -> ^( PROGRAM ^( LIST[\"Imports\"] ( imports )* ) order externals ^( LIST[\"Layers\"] ( layerDef )* ) ^( LIST[\"Legends\"] ( legendDef )* ) ^( LIST[\"Pythons\"] ( pythonDef )* ) ) ;
    public final StencilParser.program_return program() throws RecognitionException {
        StencilParser.program_return retval = new StencilParser.program_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.imports_return imports1 = null;

        StencilParser.externals_return externals2 = null;

        StencilParser.order_return order3 = null;

        StencilParser.streamDef_return streamDef4 = null;

        StencilParser.layerDef_return layerDef5 = null;

        StencilParser.legendDef_return legendDef6 = null;

        StencilParser.pythonDef_return pythonDef7 = null;


        RewriteRuleSubtreeStream stream_imports=new RewriteRuleSubtreeStream(adaptor,"rule imports");
        RewriteRuleSubtreeStream stream_externals=new RewriteRuleSubtreeStream(adaptor,"rule externals");
        RewriteRuleSubtreeStream stream_legendDef=new RewriteRuleSubtreeStream(adaptor,"rule legendDef");
        RewriteRuleSubtreeStream stream_streamDef=new RewriteRuleSubtreeStream(adaptor,"rule streamDef");
        RewriteRuleSubtreeStream stream_order=new RewriteRuleSubtreeStream(adaptor,"rule order");
        RewriteRuleSubtreeStream stream_layerDef=new RewriteRuleSubtreeStream(adaptor,"rule layerDef");
        RewriteRuleSubtreeStream stream_pythonDef=new RewriteRuleSubtreeStream(adaptor,"rule pythonDef");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:9: ( ( imports )* externals order ( streamDef | layerDef | legendDef | pythonDef )* -> ^( PROGRAM ^( LIST[\"Imports\"] ( imports )* ) order externals ^( LIST[\"Layers\"] ( layerDef )* ) ^( LIST[\"Legends\"] ( legendDef )* ) ^( LIST[\"Pythons\"] ( pythonDef )* ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:11: ( imports )* externals order ( streamDef | layerDef | legendDef | pythonDef )*
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:11: ( imports )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:11: imports
            	    {
            	    pushFollow(FOLLOW_imports_in_program518);
            	    imports1=imports();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_imports.add(imports1.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            pushFollow(FOLLOW_externals_in_program521);
            externals2=externals();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_externals.add(externals2.getTree());
            pushFollow(FOLLOW_order_in_program523);
            order3=order();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_order.add(order3.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:36: ( streamDef | layerDef | legendDef | pythonDef )*
            loop2:
            do {
                int alt2=5;
                switch ( input.LA(1) ) {
                case STREAM:
                    {
                    alt2=1;
                    }
                    break;
                case LAYER:
                    {
                    alt2=2;
                    }
                    break;
                case LEGEND:
                    {
                    alt2=3;
                    }
                    break;
                case PYTHON:
                    {
                    alt2=4;
                    }
                    break;

                }

                switch (alt2) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:37: streamDef
            	    {
            	    pushFollow(FOLLOW_streamDef_in_program526);
            	    streamDef4=streamDef();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_streamDef.add(streamDef4.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:49: layerDef
            	    {
            	    pushFollow(FOLLOW_layerDef_in_program530);
            	    layerDef5=layerDef();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_layerDef.add(layerDef5.getTree());

            	    }
            	    break;
            	case 3 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:60: legendDef
            	    {
            	    pushFollow(FOLLOW_legendDef_in_program534);
            	    legendDef6=legendDef();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_legendDef.add(legendDef6.getTree());

            	    }
            	    break;
            	case 4 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:151:72: pythonDef
            	    {
            	    pushFollow(FOLLOW_pythonDef_in_program538);
            	    pythonDef7=pythonDef();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_pythonDef.add(pythonDef7.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);



            // AST REWRITE
            // elements: legendDef, order, imports, pythonDef, layerDef, externals
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 152:3: -> ^( PROGRAM ^( LIST[\"Imports\"] ( imports )* ) order externals ^( LIST[\"Layers\"] ( layerDef )* ) ^( LIST[\"Legends\"] ( legendDef )* ) ^( LIST[\"Pythons\"] ( pythonDef )* ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:6: ^( PROGRAM ^( LIST[\"Imports\"] ( imports )* ) order externals ^( LIST[\"Layers\"] ( layerDef )* ) ^( LIST[\"Legends\"] ( legendDef )* ) ^( LIST[\"Pythons\"] ( pythonDef )* ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROGRAM, "PROGRAM"), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:17: ^( LIST[\"Imports\"] ( imports )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Imports"), root_2);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:35: ( imports )*
                while ( stream_imports.hasNext() ) {
                    adaptor.addChild(root_2, stream_imports.nextTree());

                }
                stream_imports.reset();

                adaptor.addChild(root_1, root_2);
                }
                adaptor.addChild(root_1, stream_order.nextTree());
                adaptor.addChild(root_1, stream_externals.nextTree());
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:61: ^( LIST[\"Layers\"] ( layerDef )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Layers"), root_2);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:78: ( layerDef )*
                while ( stream_layerDef.hasNext() ) {
                    adaptor.addChild(root_2, stream_layerDef.nextTree());

                }
                stream_layerDef.reset();

                adaptor.addChild(root_1, root_2);
                }
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:89: ^( LIST[\"Legends\"] ( legendDef )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Legends"), root_2);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:107: ( legendDef )*
                while ( stream_legendDef.hasNext() ) {
                    adaptor.addChild(root_2, stream_legendDef.nextTree());

                }
                stream_legendDef.reset();

                adaptor.addChild(root_1, root_2);
                }
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:119: ^( LIST[\"Pythons\"] ( pythonDef )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Pythons"), root_2);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:152:137: ( pythonDef )*
                while ( stream_pythonDef.hasNext() ) {
                    adaptor.addChild(root_2, stream_pythonDef.nextTree());

                }
                stream_pythonDef.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "program"

    public static class imports_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "imports"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:157:1: imports : IMPORT name= ID ( ARG args= argList CLOSE_ARG )? ( AS as= ID )? -> {as==null && args==null}? ^( IMPORT[$name.text] ID[\"\"] LIST[\"Arguments\"] ) -> {as==null && args!=null}? ^( IMPORT[$name.text] ID[\"\"] $args) -> {as!=null && args==null}? ^( IMPORT[$name.text] $as LIST[\"Arguments\"] ) -> ^( IMPORT[$name.text] $as $args) ;
    public final StencilParser.imports_return imports() throws RecognitionException {
        StencilParser.imports_return retval = new StencilParser.imports_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token as=null;
        Token IMPORT8=null;
        Token ARG9=null;
        Token CLOSE_ARG10=null;
        Token AS11=null;
        StencilParser.argList_return args = null;


        Object name_tree=null;
        Object as_tree=null;
        Object IMPORT8_tree=null;
        Object ARG9_tree=null;
        Object CLOSE_ARG10_tree=null;
        Object AS11_tree=null;
        RewriteRuleTokenStream stream_AS=new RewriteRuleTokenStream(adaptor,"token AS");
        RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
        RewriteRuleTokenStream stream_ARG=new RewriteRuleTokenStream(adaptor,"token ARG");
        RewriteRuleTokenStream stream_CLOSE_ARG=new RewriteRuleTokenStream(adaptor,"token CLOSE_ARG");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_argList=new RewriteRuleSubtreeStream(adaptor,"rule argList");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:2: ( IMPORT name= ID ( ARG args= argList CLOSE_ARG )? ( AS as= ID )? -> {as==null && args==null}? ^( IMPORT[$name.text] ID[\"\"] LIST[\"Arguments\"] ) -> {as==null && args!=null}? ^( IMPORT[$name.text] ID[\"\"] $args) -> {as!=null && args==null}? ^( IMPORT[$name.text] $as LIST[\"Arguments\"] ) -> ^( IMPORT[$name.text] $as $args) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:4: IMPORT name= ID ( ARG args= argList CLOSE_ARG )? ( AS as= ID )?
            {
            IMPORT8=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_imports597); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPORT.add(IMPORT8);

            name=(Token)match(input,ID,FOLLOW_ID_in_imports601); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:19: ( ARG args= argList CLOSE_ARG )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==ARG) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:20: ARG args= argList CLOSE_ARG
                    {
                    ARG9=(Token)match(input,ARG,FOLLOW_ARG_in_imports604); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG9);

                    pushFollow(FOLLOW_argList_in_imports608);
                    args=argList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_argList.add(args.getTree());
                    CLOSE_ARG10=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_imports610); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG10);


                    }
                    break;

            }

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:49: ( AS as= ID )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==AS) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:158:50: AS as= ID
                    {
                    AS11=(Token)match(input,AS,FOLLOW_AS_in_imports615); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_AS.add(AS11);

                    as=(Token)match(input,ID,FOLLOW_ID_in_imports619); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(as);


                    }
                    break;

            }



            // AST REWRITE
            // elements: IMPORT, as, as, IMPORT, args, IMPORT, IMPORT, ID, args, ID
            // token labels: as
            // rule labels: args, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_as=new RewriteRuleTokenStream(adaptor,"token as",as);
            RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"rule args",args!=null?args.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 159:4: -> {as==null && args==null}? ^( IMPORT[$name.text] ID[\"\"] LIST[\"Arguments\"] )
            if (as==null && args==null) {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:159:33: ^( IMPORT[$name.text] ID[\"\"] LIST[\"Arguments\"] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IMPORT, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, (Object)adaptor.create(ID, ""));
                adaptor.addChild(root_1, (Object)adaptor.create(LIST, "Arguments"));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 160:4: -> {as==null && args!=null}? ^( IMPORT[$name.text] ID[\"\"] $args)
            if (as==null && args!=null) {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:160:33: ^( IMPORT[$name.text] ID[\"\"] $args)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IMPORT, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, (Object)adaptor.create(ID, ""));
                adaptor.addChild(root_1, stream_args.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 161:4: -> {as!=null && args==null}? ^( IMPORT[$name.text] $as LIST[\"Arguments\"] )
            if (as!=null && args==null) {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:161:33: ^( IMPORT[$name.text] $as LIST[\"Arguments\"] )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IMPORT, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, stream_as.nextNode());
                adaptor.addChild(root_1, (Object)adaptor.create(LIST, "Arguments"));

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 162:4: -> ^( IMPORT[$name.text] $as $args)
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:162:7: ^( IMPORT[$name.text] $as $args)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(IMPORT, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, stream_as.nextNode());
                adaptor.addChild(root_1, stream_args.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "imports"

    public static class order_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "order"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:164:1: order : ( ORDER orderRef ( '>' orderRef )* -> ^( ORDER ( orderRef )+ ) | -> ^( ORDER ) );
    public final StencilParser.order_return order() throws RecognitionException {
        StencilParser.order_return retval = new StencilParser.order_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ORDER12=null;
        Token char_literal14=null;
        StencilParser.orderRef_return orderRef13 = null;

        StencilParser.orderRef_return orderRef15 = null;


        Object ORDER12_tree=null;
        Object char_literal14_tree=null;
        RewriteRuleTokenStream stream_ORDER=new RewriteRuleTokenStream(adaptor,"token ORDER");
        RewriteRuleTokenStream stream_73=new RewriteRuleTokenStream(adaptor,"token 73");
        RewriteRuleSubtreeStream stream_orderRef=new RewriteRuleSubtreeStream(adaptor,"rule orderRef");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:165:2: ( ORDER orderRef ( '>' orderRef )* -> ^( ORDER ( orderRef )+ ) | -> ^( ORDER ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ORDER) ) {
                alt6=1;
            }
            else if ( (LA6_0==EOF||(LA6_0>=LAYER && LA6_0<=LEGEND)||LA6_0==PYTHON||LA6_0==STREAM) ) {
                alt6=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:165:4: ORDER orderRef ( '>' orderRef )*
                    {
                    ORDER12=(Token)match(input,ORDER,FOLLOW_ORDER_in_order702); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ORDER.add(ORDER12);

                    pushFollow(FOLLOW_orderRef_in_order704);
                    orderRef13=orderRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_orderRef.add(orderRef13.getTree());
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:165:19: ( '>' orderRef )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==73) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:165:20: '>' orderRef
                    	    {
                    	    char_literal14=(Token)match(input,73,FOLLOW_73_in_order707); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_73.add(char_literal14);

                    	    pushFollow(FOLLOW_orderRef_in_order709);
                    	    orderRef15=orderRef();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_orderRef.add(orderRef15.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: orderRef, ORDER
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 166:3: -> ^( ORDER ( orderRef )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:166:6: ^( ORDER ( orderRef )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ORDER.nextNode(), root_1);

                        if ( !(stream_orderRef.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_orderRef.hasNext() ) {
                            adaptor.addChild(root_1, stream_orderRef.nextTree());

                        }
                        stream_orderRef.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:167:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 167:4: -> ^( ORDER )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:167:7: ^( ORDER )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ORDER, "ORDER"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "order"

    public static class orderRef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "orderRef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:169:1: orderRef : ( ID -> ^( LIST[\"Streams\"] ID ) | GROUP ID ( SPLIT ID )+ CLOSE_GROUP -> ^( LIST[\"Streams\"] ( ID )+ ) );
    public final StencilParser.orderRef_return orderRef() throws RecognitionException {
        StencilParser.orderRef_return retval = new StencilParser.orderRef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID16=null;
        Token GROUP17=null;
        Token ID18=null;
        Token SPLIT19=null;
        Token ID20=null;
        Token CLOSE_GROUP21=null;

        Object ID16_tree=null;
        Object GROUP17_tree=null;
        Object ID18_tree=null;
        Object SPLIT19_tree=null;
        Object ID20_tree=null;
        Object CLOSE_GROUP21_tree=null;
        RewriteRuleTokenStream stream_CLOSE_GROUP=new RewriteRuleTokenStream(adaptor,"token CLOSE_GROUP");
        RewriteRuleTokenStream stream_SPLIT=new RewriteRuleTokenStream(adaptor,"token SPLIT");
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:170:2: ( ID -> ^( LIST[\"Streams\"] ID ) | GROUP ID ( SPLIT ID )+ CLOSE_GROUP -> ^( LIST[\"Streams\"] ( ID )+ ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                alt8=1;
            }
            else if ( (LA8_0==GROUP) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:170:4: ID
                    {
                    ID16=(Token)match(input,ID,FOLLOW_ID_in_orderRef740); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID16);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 170:7: -> ^( LIST[\"Streams\"] ID )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:170:10: ^( LIST[\"Streams\"] ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Streams"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:171:4: GROUP ID ( SPLIT ID )+ CLOSE_GROUP
                    {
                    GROUP17=(Token)match(input,GROUP,FOLLOW_GROUP_in_orderRef754); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GROUP.add(GROUP17);

                    ID18=(Token)match(input,ID,FOLLOW_ID_in_orderRef756); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID18);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:171:13: ( SPLIT ID )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==SPLIT) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:171:14: SPLIT ID
                    	    {
                    	    SPLIT19=(Token)match(input,SPLIT,FOLLOW_SPLIT_in_orderRef759); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_SPLIT.add(SPLIT19);

                    	    ID20=(Token)match(input,ID,FOLLOW_ID_in_orderRef761); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_ID.add(ID20);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                    CLOSE_GROUP21=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_orderRef765); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP21);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 171:37: -> ^( LIST[\"Streams\"] ( ID )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:171:41: ^( LIST[\"Streams\"] ( ID )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Streams"), root_1);

                        if ( !(stream_ID.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_ID.hasNext() ) {
                            adaptor.addChild(root_1, stream_ID.nextNode());

                        }
                        stream_ID.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "orderRef"

    public static class externals_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "externals"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:1: externals : ( externalStream )* -> ^( LIST[\"Externals\"] ( externalStream )* ) ;
    public final StencilParser.externals_return externals() throws RecognitionException {
        StencilParser.externals_return retval = new StencilParser.externals_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.externalStream_return externalStream22 = null;


        RewriteRuleSubtreeStream stream_externalStream=new RewriteRuleSubtreeStream(adaptor,"rule externalStream");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:10: ( ( externalStream )* -> ^( LIST[\"Externals\"] ( externalStream )* ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:12: ( externalStream )*
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:12: ( externalStream )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==EXTERNAL) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:12: externalStream
            	    {
            	    pushFollow(FOLLOW_externalStream_in_externals783);
            	    externalStream22=externalStream();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_externalStream.add(externalStream22.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);



            // AST REWRITE
            // elements: externalStream
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 173:28: -> ^( LIST[\"Externals\"] ( externalStream )* )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:31: ^( LIST[\"Externals\"] ( externalStream )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Externals"), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:173:51: ( externalStream )*
                while ( stream_externalStream.hasNext() ) {
                    adaptor.addChild(root_1, stream_externalStream.nextTree());

                }
                stream_externalStream.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "externals"

    public static class externalStream_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "externalStream"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:174:1: externalStream : EXTERNAL STREAM name= ID tuple[false] -> ^( EXTERNAL[$name.text] tuple ) ;
    public final StencilParser.externalStream_return externalStream() throws RecognitionException {
        StencilParser.externalStream_return retval = new StencilParser.externalStream_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token EXTERNAL23=null;
        Token STREAM24=null;
        StencilParser.tuple_return tuple25 = null;


        Object name_tree=null;
        Object EXTERNAL23_tree=null;
        Object STREAM24_tree=null;
        RewriteRuleTokenStream stream_STREAM=new RewriteRuleTokenStream(adaptor,"token STREAM");
        RewriteRuleTokenStream stream_EXTERNAL=new RewriteRuleTokenStream(adaptor,"token EXTERNAL");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_tuple=new RewriteRuleSubtreeStream(adaptor,"rule tuple");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:174:15: ( EXTERNAL STREAM name= ID tuple[false] -> ^( EXTERNAL[$name.text] tuple ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:174:17: EXTERNAL STREAM name= ID tuple[false]
            {
            EXTERNAL23=(Token)match(input,EXTERNAL,FOLLOW_EXTERNAL_in_externalStream800); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EXTERNAL.add(EXTERNAL23);

            STREAM24=(Token)match(input,STREAM,FOLLOW_STREAM_in_externalStream802); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STREAM.add(STREAM24);

            name=(Token)match(input,ID,FOLLOW_ID_in_externalStream806); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            pushFollow(FOLLOW_tuple_in_externalStream808);
            tuple25=tuple(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tuple.add(tuple25.getTree());


            // AST REWRITE
            // elements: tuple, EXTERNAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 174:54: -> ^( EXTERNAL[$name.text] tuple )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:174:57: ^( EXTERNAL[$name.text] tuple )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXTERNAL, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, stream_tuple.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "externalStream"

    public static class streamDef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "streamDef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:180:1: streamDef : STREAM name= ID tuple[true] ( consumesBlock[\"return\"] )+ -> ( ^( STREAM[$name.text] tuple ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) )* ;
    public final StencilParser.streamDef_return streamDef() throws RecognitionException {
        StencilParser.streamDef_return retval = new StencilParser.streamDef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token STREAM26=null;
        StencilParser.tuple_return tuple27 = null;

        StencilParser.consumesBlock_return consumesBlock28 = null;


        Object name_tree=null;
        Object STREAM26_tree=null;
        RewriteRuleTokenStream stream_STREAM=new RewriteRuleTokenStream(adaptor,"token STREAM");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_consumesBlock=new RewriteRuleSubtreeStream(adaptor,"rule consumesBlock");
        RewriteRuleSubtreeStream stream_tuple=new RewriteRuleSubtreeStream(adaptor,"rule tuple");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:181:2: ( STREAM name= ID tuple[true] ( consumesBlock[\"return\"] )+ -> ( ^( STREAM[$name.text] tuple ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) )* )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:181:4: STREAM name= ID tuple[true] ( consumesBlock[\"return\"] )+
            {
            STREAM26=(Token)match(input,STREAM,FOLLOW_STREAM_in_streamDef831); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_STREAM.add(STREAM26);

            name=(Token)match(input,ID,FOLLOW_ID_in_streamDef835); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            pushFollow(FOLLOW_tuple_in_streamDef837);
            tuple27=tuple(true);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tuple.add(tuple27.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:181:32: ( consumesBlock[\"return\"] )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==FROM) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:181:33: consumesBlock[\"return\"]
            	    {
            	    pushFollow(FOLLOW_consumesBlock_in_streamDef842);
            	    consumesBlock28=consumesBlock("return");

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_consumesBlock.add(consumesBlock28.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);



            // AST REWRITE
            // elements: consumesBlock, STREAM, tuple
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 182:3: -> ( ^( STREAM[$name.text] tuple ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) )*
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:182:6: ( ^( STREAM[$name.text] tuple ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) )*
                while ( stream_STREAM.hasNext()||stream_tuple.hasNext() ) {
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:182:6: ^( STREAM[$name.text] tuple ^( LIST[\"Consumes\"] ( consumesBlock )+ ) )
                    {
                    Object root_1 = (Object)adaptor.nil();
                    root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STREAM, (name!=null?name.getText():null)), root_1);

                    adaptor.addChild(root_1, stream_tuple.nextTree());
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:182:33: ^( LIST[\"Consumes\"] ( consumesBlock )+ )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Consumes"), root_2);

                    if ( !(stream_consumesBlock.hasNext()) ) {
                        throw new RewriteEarlyExitException();
                    }
                    while ( stream_consumesBlock.hasNext() ) {
                        adaptor.addChild(root_2, stream_consumesBlock.nextTree());

                    }
                    stream_consumesBlock.reset();

                    adaptor.addChild(root_1, root_2);
                    }

                    adaptor.addChild(root_0, root_1);
                    }

                }
                stream_STREAM.reset();
                stream_tuple.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "streamDef"

    public static class layerDef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "layerDef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:184:1: layerDef : LAYER name= ID implantationDef guidesBlock ( consumesBlock[\"glyph\"] )+ -> ^( LAYER[$name.text] implantationDef guidesBlock ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) ;
    public final StencilParser.layerDef_return layerDef() throws RecognitionException {
        StencilParser.layerDef_return retval = new StencilParser.layerDef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LAYER29=null;
        StencilParser.implantationDef_return implantationDef30 = null;

        StencilParser.guidesBlock_return guidesBlock31 = null;

        StencilParser.consumesBlock_return consumesBlock32 = null;


        Object name_tree=null;
        Object LAYER29_tree=null;
        RewriteRuleTokenStream stream_LAYER=new RewriteRuleTokenStream(adaptor,"token LAYER");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_consumesBlock=new RewriteRuleSubtreeStream(adaptor,"rule consumesBlock");
        RewriteRuleSubtreeStream stream_implantationDef=new RewriteRuleSubtreeStream(adaptor,"rule implantationDef");
        RewriteRuleSubtreeStream stream_guidesBlock=new RewriteRuleSubtreeStream(adaptor,"rule guidesBlock");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:185:2: ( LAYER name= ID implantationDef guidesBlock ( consumesBlock[\"glyph\"] )+ -> ^( LAYER[$name.text] implantationDef guidesBlock ^( LIST[\"Consumes\"] ( consumesBlock )+ ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:185:4: LAYER name= ID implantationDef guidesBlock ( consumesBlock[\"glyph\"] )+
            {
            LAYER29=(Token)match(input,LAYER,FOLLOW_LAYER_in_layerDef874); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LAYER.add(LAYER29);

            name=(Token)match(input,ID,FOLLOW_ID_in_layerDef878); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            pushFollow(FOLLOW_implantationDef_in_layerDef880);
            implantationDef30=implantationDef();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_implantationDef.add(implantationDef30.getTree());
            pushFollow(FOLLOW_guidesBlock_in_layerDef882);
            guidesBlock31=guidesBlock();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_guidesBlock.add(guidesBlock31.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:185:46: ( consumesBlock[\"glyph\"] )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==FROM) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:185:46: consumesBlock[\"glyph\"]
            	    {
            	    pushFollow(FOLLOW_consumesBlock_in_layerDef884);
            	    consumesBlock32=consumesBlock("glyph");

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_consumesBlock.add(consumesBlock32.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);



            // AST REWRITE
            // elements: guidesBlock, implantationDef, LAYER, consumesBlock
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 186:3: -> ^( LAYER[$name.text] implantationDef guidesBlock ^( LIST[\"Consumes\"] ( consumesBlock )+ ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:186:6: ^( LAYER[$name.text] implantationDef guidesBlock ^( LIST[\"Consumes\"] ( consumesBlock )+ ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LAYER, (name!=null?name.getText():null)), root_1);

                adaptor.addChild(root_1, stream_implantationDef.nextTree());
                adaptor.addChild(root_1, stream_guidesBlock.nextTree());
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:186:54: ^( LIST[\"Consumes\"] ( consumesBlock )+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Consumes"), root_2);

                if ( !(stream_consumesBlock.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_consumesBlock.hasNext() ) {
                    adaptor.addChild(root_2, stream_consumesBlock.nextTree());

                }
                stream_consumesBlock.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "layerDef"

    public static class implantationDef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "implantationDef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:188:1: implantationDef : ( ARG type= ID CLOSE_ARG -> GLYPH[$type.text] | -> GLYPH[DEFAULT_GLYPH_TYPE] );
    public final StencilParser.implantationDef_return implantationDef() throws RecognitionException {
        StencilParser.implantationDef_return retval = new StencilParser.implantationDef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token type=null;
        Token ARG33=null;
        Token CLOSE_ARG34=null;

        Object type_tree=null;
        Object ARG33_tree=null;
        Object CLOSE_ARG34_tree=null;
        RewriteRuleTokenStream stream_ARG=new RewriteRuleTokenStream(adaptor,"token ARG");
        RewriteRuleTokenStream stream_CLOSE_ARG=new RewriteRuleTokenStream(adaptor,"token CLOSE_ARG");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:189:2: ( ARG type= ID CLOSE_ARG -> GLYPH[$type.text] | -> GLYPH[DEFAULT_GLYPH_TYPE] )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ARG) ) {
                alt12=1;
            }
            else if ( (LA12_0==FROM||LA12_0==ID) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:189:4: ARG type= ID CLOSE_ARG
                    {
                    ARG33=(Token)match(input,ARG,FOLLOW_ARG_in_implantationDef917); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG33);

                    type=(Token)match(input,ID,FOLLOW_ID_in_implantationDef921); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(type);

                    CLOSE_ARG34=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_implantationDef923); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG34);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 189:26: -> GLYPH[$type.text]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(GLYPH, (type!=null?type.getText():null)));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:190:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 190:4: -> GLYPH[DEFAULT_GLYPH_TYPE]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(GLYPH, DEFAULT_GLYPH_TYPE));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "implantationDef"

    public static class guidesBlock_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "guidesBlock"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:192:1: guidesBlock : ( ID specializer[RuleOpts.Simple] DEFINE ID )* -> ^( LIST[\"Guides\"] ( ^( GUIDE ID specializer ID ) )* ) ;
    public final StencilParser.guidesBlock_return guidesBlock() throws RecognitionException {
        StencilParser.guidesBlock_return retval = new StencilParser.guidesBlock_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID35=null;
        Token DEFINE37=null;
        Token ID38=null;
        StencilParser.specializer_return specializer36 = null;


        Object ID35_tree=null;
        Object DEFINE37_tree=null;
        Object ID38_tree=null;
        RewriteRuleTokenStream stream_DEFINE=new RewriteRuleTokenStream(adaptor,"token DEFINE");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_specializer=new RewriteRuleSubtreeStream(adaptor,"rule specializer");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:193:2: ( ( ID specializer[RuleOpts.Simple] DEFINE ID )* -> ^( LIST[\"Guides\"] ( ^( GUIDE ID specializer ID ) )* ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:193:4: ( ID specializer[RuleOpts.Simple] DEFINE ID )*
            {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:193:4: ( ID specializer[RuleOpts.Simple] DEFINE ID )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==ID) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:193:5: ID specializer[RuleOpts.Simple] DEFINE ID
            	    {
            	    ID35=(Token)match(input,ID,FOLLOW_ID_in_guidesBlock947); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID35);

            	    pushFollow(FOLLOW_specializer_in_guidesBlock949);
            	    specializer36=specializer(RuleOpts.Simple);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_specializer.add(specializer36.getTree());
            	    DEFINE37=(Token)match(input,DEFINE,FOLLOW_DEFINE_in_guidesBlock952); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DEFINE.add(DEFINE37);

            	    ID38=(Token)match(input,ID,FOLLOW_ID_in_guidesBlock954); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_ID.add(ID38);


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);



            // AST REWRITE
            // elements: ID, specializer, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 194:3: -> ^( LIST[\"Guides\"] ( ^( GUIDE ID specializer ID ) )* )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:194:6: ^( LIST[\"Guides\"] ( ^( GUIDE ID specializer ID ) )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Guides"), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:194:23: ( ^( GUIDE ID specializer ID ) )*
                while ( stream_ID.hasNext()||stream_specializer.hasNext()||stream_ID.hasNext() ) {
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:194:23: ^( GUIDE ID specializer ID )
                    {
                    Object root_2 = (Object)adaptor.nil();
                    root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(GUIDE, "GUIDE"), root_2);

                    adaptor.addChild(root_2, stream_ID.nextNode());
                    adaptor.addChild(root_2, stream_specializer.nextTree());
                    adaptor.addChild(root_2, stream_ID.nextNode());

                    adaptor.addChild(root_1, root_2);
                    }

                }
                stream_ID.reset();
                stream_specializer.reset();
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "guidesBlock"

    public static class consumesBlock_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "consumesBlock"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:196:1: consumesBlock[String def] : FROM stream= ID ( filterRule )* ( rule[def] )+ -> ^( CONSUMES[$stream.text] ^( LIST[\"Filters\"] ( filterRule )* ) ^( LIST[\"Rules\"] ( rule )+ ) ) ;
    public final StencilParser.consumesBlock_return consumesBlock(String def) throws RecognitionException {
        StencilParser.consumesBlock_return retval = new StencilParser.consumesBlock_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token stream=null;
        Token FROM39=null;
        StencilParser.filterRule_return filterRule40 = null;

        StencilParser.rule_return rule41 = null;


        Object stream_tree=null;
        Object FROM39_tree=null;
        RewriteRuleTokenStream stream_FROM=new RewriteRuleTokenStream(adaptor,"token FROM");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        RewriteRuleSubtreeStream stream_filterRule=new RewriteRuleSubtreeStream(adaptor,"rule filterRule");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:2: ( FROM stream= ID ( filterRule )* ( rule[def] )+ -> ^( CONSUMES[$stream.text] ^( LIST[\"Filters\"] ( filterRule )* ) ^( LIST[\"Rules\"] ( rule )+ ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:4: FROM stream= ID ( filterRule )* ( rule[def] )+
            {
            FROM39=(Token)match(input,FROM,FOLLOW_FROM_in_consumesBlock988); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FROM.add(FROM39);

            stream=(Token)match(input,ID,FOLLOW_ID_in_consumesBlock992); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(stream);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:19: ( filterRule )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==FILTER) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:19: filterRule
            	    {
            	    pushFollow(FOLLOW_filterRule_in_consumesBlock994);
            	    filterRule40=filterRule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_filterRule.add(filterRule40.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:31: ( rule[def] )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==CANVAS||LA15_0==GLYPH||LA15_0==LOCAL||LA15_0==RETURN||LA15_0==VIEW||LA15_0==GROUP||LA15_0==ID) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:197:31: rule[def]
            	    {
            	    pushFollow(FOLLOW_rule_in_consumesBlock997);
            	    rule41=rule(def);

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule41.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);



            // AST REWRITE
            // elements: rule, filterRule
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 198:3: -> ^( CONSUMES[$stream.text] ^( LIST[\"Filters\"] ( filterRule )* ) ^( LIST[\"Rules\"] ( rule )+ ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:198:6: ^( CONSUMES[$stream.text] ^( LIST[\"Filters\"] ( filterRule )* ) ^( LIST[\"Rules\"] ( rule )+ ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONSUMES, (stream!=null?stream.getText():null)), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:198:31: ^( LIST[\"Filters\"] ( filterRule )* )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Filters"), root_2);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:198:49: ( filterRule )*
                while ( stream_filterRule.hasNext() ) {
                    adaptor.addChild(root_2, stream_filterRule.nextTree());

                }
                stream_filterRule.reset();

                adaptor.addChild(root_1, root_2);
                }
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:198:62: ^( LIST[\"Rules\"] ( rule )+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Rules"), root_2);

                if ( !(stream_rule.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rule.hasNext() ) {
                    adaptor.addChild(root_2, stream_rule.nextTree());

                }
                stream_rule.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "consumesBlock"

    public static class filterRule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "filterRule"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:200:1: filterRule : FILTER rulePredicate DEFINE callGroup -> ^( FILTER rulePredicate callGroup ) ;
    public final StencilParser.filterRule_return filterRule() throws RecognitionException {
        StencilParser.filterRule_return retval = new StencilParser.filterRule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FILTER42=null;
        Token DEFINE44=null;
        StencilParser.rulePredicate_return rulePredicate43 = null;

        StencilParser.callGroup_return callGroup45 = null;


        Object FILTER42_tree=null;
        Object DEFINE44_tree=null;
        RewriteRuleTokenStream stream_DEFINE=new RewriteRuleTokenStream(adaptor,"token DEFINE");
        RewriteRuleTokenStream stream_FILTER=new RewriteRuleTokenStream(adaptor,"token FILTER");
        RewriteRuleSubtreeStream stream_rulePredicate=new RewriteRuleSubtreeStream(adaptor,"rule rulePredicate");
        RewriteRuleSubtreeStream stream_callGroup=new RewriteRuleSubtreeStream(adaptor,"rule callGroup");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:201:2: ( FILTER rulePredicate DEFINE callGroup -> ^( FILTER rulePredicate callGroup ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:201:4: FILTER rulePredicate DEFINE callGroup
            {
            FILTER42=(Token)match(input,FILTER,FOLLOW_FILTER_in_filterRule1034); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FILTER.add(FILTER42);

            pushFollow(FOLLOW_rulePredicate_in_filterRule1036);
            rulePredicate43=rulePredicate();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rulePredicate.add(rulePredicate43.getTree());
            DEFINE44=(Token)match(input,DEFINE,FOLLOW_DEFINE_in_filterRule1038); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_DEFINE.add(DEFINE44);

            pushFollow(FOLLOW_callGroup_in_filterRule1040);
            callGroup45=callGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_callGroup.add(callGroup45.getTree());


            // AST REWRITE
            // elements: rulePredicate, FILTER, callGroup
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 202:3: -> ^( FILTER rulePredicate callGroup )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:202:6: ^( FILTER rulePredicate callGroup )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_FILTER.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rulePredicate.nextTree());
                adaptor.addChild(root_1, stream_callGroup.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "filterRule"

    public static class rulePredicate_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rulePredicate"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:204:1: rulePredicate : ( GROUP ALL CLOSE_GROUP -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) ) | GROUP value booleanOp value ( SEPARATOR value booleanOp value )* CLOSE_GROUP -> ^( LIST[\"Predicates\"] ( ^( PREDICATE value booleanOp value ) )+ ) );
    public final StencilParser.rulePredicate_return rulePredicate() throws RecognitionException {
        StencilParser.rulePredicate_return retval = new StencilParser.rulePredicate_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GROUP46=null;
        Token ALL47=null;
        Token CLOSE_GROUP48=null;
        Token GROUP49=null;
        Token SEPARATOR53=null;
        Token CLOSE_GROUP57=null;
        StencilParser.value_return value50 = null;

        StencilParser.booleanOp_return booleanOp51 = null;

        StencilParser.value_return value52 = null;

        StencilParser.value_return value54 = null;

        StencilParser.booleanOp_return booleanOp55 = null;

        StencilParser.value_return value56 = null;


        Object GROUP46_tree=null;
        Object ALL47_tree=null;
        Object CLOSE_GROUP48_tree=null;
        Object GROUP49_tree=null;
        Object SEPARATOR53_tree=null;
        Object CLOSE_GROUP57_tree=null;
        RewriteRuleTokenStream stream_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token SEPARATOR");
        RewriteRuleTokenStream stream_CLOSE_GROUP=new RewriteRuleTokenStream(adaptor,"token CLOSE_GROUP");
        RewriteRuleTokenStream stream_ALL=new RewriteRuleTokenStream(adaptor,"token ALL");
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        RewriteRuleSubtreeStream stream_booleanOp=new RewriteRuleSubtreeStream(adaptor,"rule booleanOp");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:205:2: ( GROUP ALL CLOSE_GROUP -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) ) | GROUP value booleanOp value ( SEPARATOR value booleanOp value )* CLOSE_GROUP -> ^( LIST[\"Predicates\"] ( ^( PREDICATE value booleanOp value ) )+ ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==GROUP) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==ALL) ) {
                    int LA17_2 = input.LA(3);

                    if ( (LA17_2==CLOSE_GROUP) ) {
                        alt17=1;
                    }
                    else if ( (LA17_2==73||LA17_2==75||(LA17_2>=77 && LA17_2<=82)) ) {
                        alt17=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 17, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA17_1==DEFAULT||LA17_1==ARG||LA17_1==NAMESPLIT||LA17_1==ID||(LA17_1>=TAGGED_ID && LA17_1<=DIGITS)||(LA17_1>=83 && LA17_1<=84)) ) {
                    alt17=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:205:4: GROUP ALL CLOSE_GROUP
                    {
                    GROUP46=(Token)match(input,GROUP,FOLLOW_GROUP_in_rulePredicate1061); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GROUP.add(GROUP46);

                    ALL47=(Token)match(input,ALL,FOLLOW_ALL_in_rulePredicate1063); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ALL.add(ALL47);

                    CLOSE_GROUP48=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_rulePredicate1065); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP48);



                    // AST REWRITE
                    // elements: ALL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 206:3: -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:206:6: ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Predicates"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:206:27: ^( PREDICATE ALL )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREDICATE, "PREDICATE"), root_2);

                        adaptor.addChild(root_2, stream_ALL.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:207:4: GROUP value booleanOp value ( SEPARATOR value booleanOp value )* CLOSE_GROUP
                    {
                    GROUP49=(Token)match(input,GROUP,FOLLOW_GROUP_in_rulePredicate1085); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GROUP.add(GROUP49);

                    pushFollow(FOLLOW_value_in_rulePredicate1087);
                    value50=value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value.add(value50.getTree());
                    pushFollow(FOLLOW_booleanOp_in_rulePredicate1089);
                    booleanOp51=booleanOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_booleanOp.add(booleanOp51.getTree());
                    pushFollow(FOLLOW_value_in_rulePredicate1091);
                    value52=value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value.add(value52.getTree());
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:207:32: ( SEPARATOR value booleanOp value )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==SEPARATOR) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:207:33: SEPARATOR value booleanOp value
                    	    {
                    	    SEPARATOR53=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_rulePredicate1094); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_SEPARATOR.add(SEPARATOR53);

                    	    pushFollow(FOLLOW_value_in_rulePredicate1096);
                    	    value54=value();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_value.add(value54.getTree());
                    	    pushFollow(FOLLOW_booleanOp_in_rulePredicate1098);
                    	    booleanOp55=booleanOp();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_booleanOp.add(booleanOp55.getTree());
                    	    pushFollow(FOLLOW_value_in_rulePredicate1100);
                    	    value56=value();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_value.add(value56.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);

                    CLOSE_GROUP57=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_rulePredicate1104); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP57);



                    // AST REWRITE
                    // elements: value, value, booleanOp
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 208:3: -> ^( LIST[\"Predicates\"] ( ^( PREDICATE value booleanOp value ) )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:208:6: ^( LIST[\"Predicates\"] ( ^( PREDICATE value booleanOp value ) )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Predicates"), root_1);

                        if ( !(stream_value.hasNext()||stream_value.hasNext()||stream_booleanOp.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_value.hasNext()||stream_value.hasNext()||stream_booleanOp.hasNext() ) {
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:208:27: ^( PREDICATE value booleanOp value )
                            {
                            Object root_2 = (Object)adaptor.nil();
                            root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREDICATE, "PREDICATE"), root_2);

                            adaptor.addChild(root_2, stream_value.nextTree());
                            adaptor.addChild(root_2, stream_booleanOp.nextTree());
                            adaptor.addChild(root_2, stream_value.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_value.reset();
                        stream_value.reset();
                        stream_booleanOp.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rulePredicate"

    public static class legendDef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "legendDef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:214:1: legendDef : LEGEND name= ID tuple[false] YIELDS tuple[false] ( legendRule )+ -> ^( LEGEND[$name.text] ^( YIELDS tuple tuple ) ^( LIST[\"Rules\"] ( legendRule )+ ) ) ;
    public final StencilParser.legendDef_return legendDef() throws RecognitionException {
        StencilParser.legendDef_return retval = new StencilParser.legendDef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token LEGEND58=null;
        Token YIELDS60=null;
        StencilParser.tuple_return tuple59 = null;

        StencilParser.tuple_return tuple61 = null;

        StencilParser.legendRule_return legendRule62 = null;


        Object name_tree=null;
        Object LEGEND58_tree=null;
        Object YIELDS60_tree=null;
        RewriteRuleTokenStream stream_LEGEND=new RewriteRuleTokenStream(adaptor,"token LEGEND");
        RewriteRuleTokenStream stream_YIELDS=new RewriteRuleTokenStream(adaptor,"token YIELDS");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_tuple=new RewriteRuleSubtreeStream(adaptor,"rule tuple");
        RewriteRuleSubtreeStream stream_legendRule=new RewriteRuleSubtreeStream(adaptor,"rule legendRule");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:215:2: ( LEGEND name= ID tuple[false] YIELDS tuple[false] ( legendRule )+ -> ^( LEGEND[$name.text] ^( YIELDS tuple tuple ) ^( LIST[\"Rules\"] ( legendRule )+ ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:215:4: LEGEND name= ID tuple[false] YIELDS tuple[false] ( legendRule )+
            {
            LEGEND58=(Token)match(input,LEGEND,FOLLOW_LEGEND_in_legendDef1137); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LEGEND.add(LEGEND58);

            name=(Token)match(input,ID,FOLLOW_ID_in_legendDef1141); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            pushFollow(FOLLOW_tuple_in_legendDef1143);
            tuple59=tuple(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tuple.add(tuple59.getTree());
            YIELDS60=(Token)match(input,YIELDS,FOLLOW_YIELDS_in_legendDef1146); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_YIELDS.add(YIELDS60);

            pushFollow(FOLLOW_tuple_in_legendDef1148);
            tuple61=tuple(false);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_tuple.add(tuple61.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:215:52: ( legendRule )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==ALL||LA18_0==GROUP) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:215:52: legendRule
            	    {
            	    pushFollow(FOLLOW_legendRule_in_legendDef1151);
            	    legendRule62=legendRule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_legendRule.add(legendRule62.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);



            // AST REWRITE
            // elements: legendRule, tuple, tuple, YIELDS, LEGEND
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 216:3: -> ^( LEGEND[$name.text] ^( YIELDS tuple tuple ) ^( LIST[\"Rules\"] ( legendRule )+ ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:216:7: ^( LEGEND[$name.text] ^( YIELDS tuple tuple ) ^( LIST[\"Rules\"] ( legendRule )+ ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LEGEND, (name!=null?name.getText():null)), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:216:28: ^( YIELDS tuple tuple )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot(stream_YIELDS.nextNode(), root_2);

                adaptor.addChild(root_2, stream_tuple.nextTree());
                adaptor.addChild(root_2, stream_tuple.nextTree());

                adaptor.addChild(root_1, root_2);
                }
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:216:50: ^( LIST[\"Rules\"] ( legendRule )+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Rules"), root_2);

                if ( !(stream_legendRule.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_legendRule.hasNext() ) {
                    adaptor.addChild(root_2, stream_legendRule.nextTree());

                }
                stream_legendRule.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "legendDef"

    public static class legendRule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "legendRule"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:218:1: legendRule : predicate GATE ( rule[\"return\"] )+ -> ^( LEGEND_RULE predicate ^( LIST[\"Rules\"] ( rule )+ ) ) ;
    public final StencilParser.legendRule_return legendRule() throws RecognitionException {
        StencilParser.legendRule_return retval = new StencilParser.legendRule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GATE64=null;
        StencilParser.predicate_return predicate63 = null;

        StencilParser.rule_return rule65 = null;


        Object GATE64_tree=null;
        RewriteRuleTokenStream stream_GATE=new RewriteRuleTokenStream(adaptor,"token GATE");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        RewriteRuleSubtreeStream stream_predicate=new RewriteRuleSubtreeStream(adaptor,"rule predicate");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:219:2: ( predicate GATE ( rule[\"return\"] )+ -> ^( LEGEND_RULE predicate ^( LIST[\"Rules\"] ( rule )+ ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:219:4: predicate GATE ( rule[\"return\"] )+
            {
            pushFollow(FOLLOW_predicate_in_legendRule1189);
            predicate63=predicate();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_predicate.add(predicate63.getTree());
            GATE64=(Token)match(input,GATE,FOLLOW_GATE_in_legendRule1191); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GATE.add(GATE64);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:219:19: ( rule[\"return\"] )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==GROUP) ) {
                    int LA19_2 = input.LA(2);

                    if ( (LA19_2==CLOSE_GROUP) ) {
                        alt19=1;
                    }
                    else if ( (LA19_2==ID) ) {
                        int LA19_4 = input.LA(3);

                        if ( (LA19_4==CLOSE_GROUP||LA19_4==SEPARATOR) ) {
                            alt19=1;
                        }


                    }


                }
                else if ( (LA19_0==CANVAS||LA19_0==GLYPH||LA19_0==LOCAL||LA19_0==RETURN||LA19_0==VIEW||LA19_0==ID) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:219:19: rule[\"return\"]
            	    {
            	    pushFollow(FOLLOW_rule_in_legendRule1193);
            	    rule65=rule("return");

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule65.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);



            // AST REWRITE
            // elements: predicate, rule
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 220:3: -> ^( LEGEND_RULE predicate ^( LIST[\"Rules\"] ( rule )+ ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:220:6: ^( LEGEND_RULE predicate ^( LIST[\"Rules\"] ( rule )+ ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LEGEND_RULE, "LEGEND_RULE"), root_1);

                adaptor.addChild(root_1, stream_predicate.nextTree());
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:220:30: ^( LIST[\"Rules\"] ( rule )+ )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Rules"), root_2);

                if ( !(stream_rule.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rule.hasNext() ) {
                    adaptor.addChild(root_2, stream_rule.nextTree());

                }
                stream_rule.reset();

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "legendRule"

    public static class predicate_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "predicate"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:222:1: predicate : ( ( GROUP )? ALL ( CLOSE_GROUP )? -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) ) | GROUP callGroup booleanOp callGroup ( SEPARATOR callGroup booleanOp callGroup )* CLOSE_GROUP -> ^( LIST[\"Predicates\"] ( ^( PREDICATE callGroup booleanOp callGroup ) )+ ) );
    public final StencilParser.predicate_return predicate() throws RecognitionException {
        StencilParser.predicate_return retval = new StencilParser.predicate_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GROUP66=null;
        Token ALL67=null;
        Token CLOSE_GROUP68=null;
        Token GROUP69=null;
        Token SEPARATOR73=null;
        Token CLOSE_GROUP77=null;
        StencilParser.callGroup_return callGroup70 = null;

        StencilParser.booleanOp_return booleanOp71 = null;

        StencilParser.callGroup_return callGroup72 = null;

        StencilParser.callGroup_return callGroup74 = null;

        StencilParser.booleanOp_return booleanOp75 = null;

        StencilParser.callGroup_return callGroup76 = null;


        Object GROUP66_tree=null;
        Object ALL67_tree=null;
        Object CLOSE_GROUP68_tree=null;
        Object GROUP69_tree=null;
        Object SEPARATOR73_tree=null;
        Object CLOSE_GROUP77_tree=null;
        RewriteRuleTokenStream stream_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token SEPARATOR");
        RewriteRuleTokenStream stream_CLOSE_GROUP=new RewriteRuleTokenStream(adaptor,"token CLOSE_GROUP");
        RewriteRuleTokenStream stream_ALL=new RewriteRuleTokenStream(adaptor,"token ALL");
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleSubtreeStream stream_booleanOp=new RewriteRuleSubtreeStream(adaptor,"rule booleanOp");
        RewriteRuleSubtreeStream stream_callGroup=new RewriteRuleSubtreeStream(adaptor,"rule callGroup");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:2: ( ( GROUP )? ALL ( CLOSE_GROUP )? -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) ) | GROUP callGroup booleanOp callGroup ( SEPARATOR callGroup booleanOp callGroup )* CLOSE_GROUP -> ^( LIST[\"Predicates\"] ( ^( PREDICATE callGroup booleanOp callGroup ) )+ ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==GROUP) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==DEFAULT||LA23_1==GROUP||LA23_1==ARG||LA23_1==NAMESPLIT||LA23_1==ID||(LA23_1>=TAGGED_ID && LA23_1<=DIGITS)||(LA23_1>=83 && LA23_1<=84)) ) {
                    alt23=2;
                }
                else if ( (LA23_1==ALL) ) {
                    int LA23_4 = input.LA(3);

                    if ( (LA23_4==SPLIT||LA23_4==73||LA23_4==75||(LA23_4>=77 && LA23_4<=82)) ) {
                        alt23=2;
                    }
                    else if ( (LA23_4==CLOSE_GROUP||LA23_4==GATE) ) {
                        alt23=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 23, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA23_0==ALL) ) {
                alt23=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:4: ( GROUP )? ALL ( CLOSE_GROUP )?
                    {
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:4: ( GROUP )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==GROUP) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:4: GROUP
                            {
                            GROUP66=(Token)match(input,GROUP,FOLLOW_GROUP_in_predicate1222); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_GROUP.add(GROUP66);


                            }
                            break;

                    }

                    ALL67=(Token)match(input,ALL,FOLLOW_ALL_in_predicate1225); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ALL.add(ALL67);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:15: ( CLOSE_GROUP )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==CLOSE_GROUP) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:223:15: CLOSE_GROUP
                            {
                            CLOSE_GROUP68=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_predicate1227); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP68);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ALL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 224:3: -> ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:224:6: ^( LIST[\"Predicates\"] ^( PREDICATE ALL ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Predicates"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:224:27: ^( PREDICATE ALL )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREDICATE, "PREDICATE"), root_2);

                        adaptor.addChild(root_2, stream_ALL.nextNode());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:225:4: GROUP callGroup booleanOp callGroup ( SEPARATOR callGroup booleanOp callGroup )* CLOSE_GROUP
                    {
                    GROUP69=(Token)match(input,GROUP,FOLLOW_GROUP_in_predicate1248); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GROUP.add(GROUP69);

                    pushFollow(FOLLOW_callGroup_in_predicate1250);
                    callGroup70=callGroup();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callGroup.add(callGroup70.getTree());
                    pushFollow(FOLLOW_booleanOp_in_predicate1252);
                    booleanOp71=booleanOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_booleanOp.add(booleanOp71.getTree());
                    pushFollow(FOLLOW_callGroup_in_predicate1254);
                    callGroup72=callGroup();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callGroup.add(callGroup72.getTree());
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:225:40: ( SEPARATOR callGroup booleanOp callGroup )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==SEPARATOR) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:225:41: SEPARATOR callGroup booleanOp callGroup
                    	    {
                    	    SEPARATOR73=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_predicate1257); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_SEPARATOR.add(SEPARATOR73);

                    	    pushFollow(FOLLOW_callGroup_in_predicate1259);
                    	    callGroup74=callGroup();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_callGroup.add(callGroup74.getTree());
                    	    pushFollow(FOLLOW_booleanOp_in_predicate1261);
                    	    booleanOp75=booleanOp();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_booleanOp.add(booleanOp75.getTree());
                    	    pushFollow(FOLLOW_callGroup_in_predicate1263);
                    	    callGroup76=callGroup();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_callGroup.add(callGroup76.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);

                    CLOSE_GROUP77=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_predicate1267); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP77);



                    // AST REWRITE
                    // elements: callGroup, booleanOp, callGroup
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 226:3: -> ^( LIST[\"Predicates\"] ( ^( PREDICATE callGroup booleanOp callGroup ) )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:226:6: ^( LIST[\"Predicates\"] ( ^( PREDICATE callGroup booleanOp callGroup ) )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Predicates"), root_1);

                        if ( !(stream_callGroup.hasNext()||stream_booleanOp.hasNext()||stream_callGroup.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_callGroup.hasNext()||stream_booleanOp.hasNext()||stream_callGroup.hasNext() ) {
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:226:27: ^( PREDICATE callGroup booleanOp callGroup )
                            {
                            Object root_2 = (Object)adaptor.nil();
                            root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREDICATE, "PREDICATE"), root_2);

                            adaptor.addChild(root_2, stream_callGroup.nextTree());
                            adaptor.addChild(root_2, stream_booleanOp.nextTree());
                            adaptor.addChild(root_2, stream_callGroup.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_callGroup.reset();
                        stream_booleanOp.reset();
                        stream_callGroup.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "predicate"

    public static class rule_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:230:1: rule[String def] : target[def] ( DEFINE | DYNAMIC ) callGroup -> ^( RULE target callGroup ( DEFINE )? ( DYNAMIC )? ) ;
    public final StencilParser.rule_return rule(String def) throws RecognitionException {
        StencilParser.rule_return retval = new StencilParser.rule_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token DEFINE79=null;
        Token DYNAMIC80=null;
        StencilParser.target_return target78 = null;

        StencilParser.callGroup_return callGroup81 = null;


        Object DEFINE79_tree=null;
        Object DYNAMIC80_tree=null;
        RewriteRuleTokenStream stream_DEFINE=new RewriteRuleTokenStream(adaptor,"token DEFINE");
        RewriteRuleTokenStream stream_DYNAMIC=new RewriteRuleTokenStream(adaptor,"token DYNAMIC");
        RewriteRuleSubtreeStream stream_target=new RewriteRuleSubtreeStream(adaptor,"rule target");
        RewriteRuleSubtreeStream stream_callGroup=new RewriteRuleSubtreeStream(adaptor,"rule callGroup");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:231:2: ( target[def] ( DEFINE | DYNAMIC ) callGroup -> ^( RULE target callGroup ( DEFINE )? ( DYNAMIC )? ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:231:4: target[def] ( DEFINE | DYNAMIC ) callGroup
            {
            pushFollow(FOLLOW_target_in_rule1299);
            target78=target(def);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_target.add(target78.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:231:16: ( DEFINE | DYNAMIC )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==DEFINE) ) {
                alt24=1;
            }
            else if ( (LA24_0==DYNAMIC) ) {
                alt24=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:231:17: DEFINE
                    {
                    DEFINE79=(Token)match(input,DEFINE,FOLLOW_DEFINE_in_rule1303); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DEFINE.add(DEFINE79);


                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:231:26: DYNAMIC
                    {
                    DYNAMIC80=(Token)match(input,DYNAMIC,FOLLOW_DYNAMIC_in_rule1307); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DYNAMIC.add(DYNAMIC80);


                    }
                    break;

            }

            pushFollow(FOLLOW_callGroup_in_rule1310);
            callGroup81=callGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_callGroup.add(callGroup81.getTree());


            // AST REWRITE
            // elements: DEFINE, DYNAMIC, target, callGroup
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 232:3: -> ^( RULE target callGroup ( DEFINE )? ( DYNAMIC )? )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:232:6: ^( RULE target callGroup ( DEFINE )? ( DYNAMIC )? )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RULE, "RULE"), root_1);

                adaptor.addChild(root_1, stream_target.nextTree());
                adaptor.addChild(root_1, stream_callGroup.nextTree());
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:232:30: ( DEFINE )?
                if ( stream_DEFINE.hasNext() ) {
                    adaptor.addChild(root_1, stream_DEFINE.nextNode());

                }
                stream_DEFINE.reset();
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:232:38: ( DYNAMIC )?
                if ( stream_DYNAMIC.hasNext() ) {
                    adaptor.addChild(root_1, stream_DYNAMIC.nextNode());

                }
                stream_DYNAMIC.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class callGroup_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "callGroup"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:234:1: callGroup : ( ( callChain SPLIT )=> callChain ( SPLIT callChain )+ JOIN callChain -> ^( CALL_GROUP ( callChain )+ ) | callChain -> ^( CALL_GROUP callChain ) );
    public final StencilParser.callGroup_return callGroup() throws RecognitionException {
        StencilParser.callGroup_return retval = new StencilParser.callGroup_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SPLIT83=null;
        Token JOIN85=null;
        StencilParser.callChain_return callChain82 = null;

        StencilParser.callChain_return callChain84 = null;

        StencilParser.callChain_return callChain86 = null;

        StencilParser.callChain_return callChain87 = null;


        Object SPLIT83_tree=null;
        Object JOIN85_tree=null;
        RewriteRuleTokenStream stream_SPLIT=new RewriteRuleTokenStream(adaptor,"token SPLIT");
        RewriteRuleTokenStream stream_JOIN=new RewriteRuleTokenStream(adaptor,"token JOIN");
        RewriteRuleSubtreeStream stream_callChain=new RewriteRuleSubtreeStream(adaptor,"rule callChain");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:2: ( ( callChain SPLIT )=> callChain ( SPLIT callChain )+ JOIN callChain -> ^( CALL_GROUP ( callChain )+ ) | callChain -> ^( CALL_GROUP callChain ) )
            int alt26=2;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:4: ( callChain SPLIT )=> callChain ( SPLIT callChain )+ JOIN callChain
                    {
                    pushFollow(FOLLOW_callChain_in_callGroup1344);
                    callChain82=callChain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callChain.add(callChain82.getTree());
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:34: ( SPLIT callChain )+
                    int cnt25=0;
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==SPLIT) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:35: SPLIT callChain
                    	    {
                    	    SPLIT83=(Token)match(input,SPLIT,FOLLOW_SPLIT_in_callGroup1347); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_SPLIT.add(SPLIT83);

                    	    pushFollow(FOLLOW_callChain_in_callGroup1349);
                    	    callChain84=callChain();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_callChain.add(callChain84.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt25 >= 1 ) break loop25;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(25, input);
                                throw eee;
                        }
                        cnt25++;
                    } while (true);

                    JOIN85=(Token)match(input,JOIN,FOLLOW_JOIN_in_callGroup1353); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_JOIN.add(JOIN85);

                    pushFollow(FOLLOW_callChain_in_callGroup1355);
                    callChain86=callChain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callChain.add(callChain86.getTree());


                    // AST REWRITE
                    // elements: callChain
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 236:3: -> ^( CALL_GROUP ( callChain )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:236:6: ^( CALL_GROUP ( callChain )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL_GROUP, "CALL_GROUP"), root_1);

                        if ( !(stream_callChain.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_callChain.hasNext() ) {
                            adaptor.addChild(root_1, stream_callChain.nextTree());

                        }
                        stream_callChain.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:237:4: callChain
                    {
                    pushFollow(FOLLOW_callChain_in_callGroup1371);
                    callChain87=callChain();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callChain.add(callChain87.getTree());


                    // AST REWRITE
                    // elements: callChain
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 238:3: -> ^( CALL_GROUP callChain )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:238:6: ^( CALL_GROUP callChain )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL_GROUP, "CALL_GROUP"), root_1);

                        adaptor.addChild(root_1, stream_callChain.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "callGroup"

    public static class callChain_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "callChain"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:240:1: callChain : callTarget -> ^( CALL_CHAIN callTarget ) ;
    public final StencilParser.callChain_return callChain() throws RecognitionException {
        StencilParser.callChain_return retval = new StencilParser.callChain_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.callTarget_return callTarget88 = null;


        RewriteRuleSubtreeStream stream_callTarget=new RewriteRuleSubtreeStream(adaptor,"rule callTarget");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:240:10: ( callTarget -> ^( CALL_CHAIN callTarget ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:240:12: callTarget
            {
            pushFollow(FOLLOW_callTarget_in_callChain1388);
            callTarget88=callTarget();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_callTarget.add(callTarget88.getTree());


            // AST REWRITE
            // elements: callTarget
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 240:23: -> ^( CALL_CHAIN callTarget )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:240:26: ^( CALL_CHAIN callTarget )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CALL_CHAIN, "CALL_CHAIN"), root_1);

                adaptor.addChild(root_1, stream_callTarget.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "callChain"

    public static class callTarget_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "callTarget"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:242:1: callTarget : ( value -> ^( PACK value ) | valueList -> ^( PACK valueList ) | emptySet -> ^( PACK ) | f1= functionCall passOp f2= callTarget -> ^( $f1 passOp $f2) );
    public final StencilParser.callTarget_return callTarget() throws RecognitionException {
        StencilParser.callTarget_return retval = new StencilParser.callTarget_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.functionCall_return f1 = null;

        StencilParser.callTarget_return f2 = null;

        StencilParser.value_return value89 = null;

        StencilParser.valueList_return valueList90 = null;

        StencilParser.emptySet_return emptySet91 = null;

        StencilParser.passOp_return passOp92 = null;


        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        RewriteRuleSubtreeStream stream_emptySet=new RewriteRuleSubtreeStream(adaptor,"rule emptySet");
        RewriteRuleSubtreeStream stream_callTarget=new RewriteRuleSubtreeStream(adaptor,"rule callTarget");
        RewriteRuleSubtreeStream stream_valueList=new RewriteRuleSubtreeStream(adaptor,"rule valueList");
        RewriteRuleSubtreeStream stream_functionCall=new RewriteRuleSubtreeStream(adaptor,"rule functionCall");
        RewriteRuleSubtreeStream stream_passOp=new RewriteRuleSubtreeStream(adaptor,"rule passOp");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:243:2: ( value -> ^( PACK value ) | valueList -> ^( PACK valueList ) | emptySet -> ^( PACK ) | f1= functionCall passOp f2= callTarget -> ^( $f1 passOp $f2) )
            int alt27=4;
            alt27 = dfa27.predict(input);
            switch (alt27) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:243:4: value
                    {
                    pushFollow(FOLLOW_value_in_callTarget1405);
                    value89=value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value.add(value89.getTree());


                    // AST REWRITE
                    // elements: value
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 243:10: -> ^( PACK value )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:243:13: ^( PACK value )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PACK, "PACK"), root_1);

                        adaptor.addChild(root_1, stream_value.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:244:4: valueList
                    {
                    pushFollow(FOLLOW_valueList_in_callTarget1418);
                    valueList90=valueList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_valueList.add(valueList90.getTree());


                    // AST REWRITE
                    // elements: valueList
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 244:14: -> ^( PACK valueList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:244:17: ^( PACK valueList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PACK, "PACK"), root_1);

                        adaptor.addChild(root_1, stream_valueList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:245:4: emptySet
                    {
                    pushFollow(FOLLOW_emptySet_in_callTarget1431);
                    emptySet91=emptySet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_emptySet.add(emptySet91.getTree());


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 245:13: -> ^( PACK )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:245:16: ^( PACK )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PACK, "PACK"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:246:4: f1= functionCall passOp f2= callTarget
                    {
                    pushFollow(FOLLOW_functionCall_in_callTarget1444);
                    f1=functionCall();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_functionCall.add(f1.getTree());
                    pushFollow(FOLLOW_passOp_in_callTarget1446);
                    passOp92=passOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_passOp.add(passOp92.getTree());
                    pushFollow(FOLLOW_callTarget_in_callTarget1450);
                    f2=callTarget();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_callTarget.add(f2.getTree());


                    // AST REWRITE
                    // elements: f1, f2, passOp
                    // token labels: 
                    // rule labels: f2, f1, retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_f2=new RewriteRuleSubtreeStream(adaptor,"rule f2",f2!=null?f2.tree:null);
                    RewriteRuleSubtreeStream stream_f1=new RewriteRuleSubtreeStream(adaptor,"rule f1",f1!=null?f1.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 247:3: -> ^( $f1 passOp $f2)
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:247:6: ^( $f1 passOp $f2)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_f1.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_passOp.nextTree());
                        adaptor.addChild(root_1, stream_f2.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "callTarget"

    public static class functionCall_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "functionCall"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:250:1: functionCall : name= callName[MAIN_BLOCK_TAG] specializer[RuleOpts.All] valueList -> ^( FUNCTION[((Tree)name.tree).getText()] specializer ^( LIST[\"args\"] valueList ) ) ;
    public final StencilParser.functionCall_return functionCall() throws RecognitionException {
        StencilParser.functionCall_return retval = new StencilParser.functionCall_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.callName_return name = null;

        StencilParser.specializer_return specializer93 = null;

        StencilParser.valueList_return valueList94 = null;


        RewriteRuleSubtreeStream stream_callName=new RewriteRuleSubtreeStream(adaptor,"rule callName");
        RewriteRuleSubtreeStream stream_specializer=new RewriteRuleSubtreeStream(adaptor,"rule specializer");
        RewriteRuleSubtreeStream stream_valueList=new RewriteRuleSubtreeStream(adaptor,"rule valueList");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:251:2: (name= callName[MAIN_BLOCK_TAG] specializer[RuleOpts.All] valueList -> ^( FUNCTION[((Tree)name.tree).getText()] specializer ^( LIST[\"args\"] valueList ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:251:4: name= callName[MAIN_BLOCK_TAG] specializer[RuleOpts.All] valueList
            {
            pushFollow(FOLLOW_callName_in_functionCall1477);
            name=callName(MAIN_BLOCK_TAG);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_callName.add(name.getTree());
            pushFollow(FOLLOW_specializer_in_functionCall1480);
            specializer93=specializer(RuleOpts.All);

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_specializer.add(specializer93.getTree());
            pushFollow(FOLLOW_valueList_in_functionCall1483);
            valueList94=valueList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_valueList.add(valueList94.getTree());


            // AST REWRITE
            // elements: specializer, valueList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 252:3: -> ^( FUNCTION[((Tree)name.tree).getText()] specializer ^( LIST[\"args\"] valueList ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:252:6: ^( FUNCTION[((Tree)name.tree).getText()] specializer ^( LIST[\"args\"] valueList ) )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUNCTION, ((Tree)name.tree).getText()), root_1);

                adaptor.addChild(root_1, stream_specializer.nextTree());
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:252:58: ^( LIST[\"args\"] valueList )
                {
                Object root_2 = (Object)adaptor.nil();
                root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "args"), root_2);

                adaptor.addChild(root_2, stream_valueList.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "functionCall"

    public static class callName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "callName"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:255:1: callName[String defaultCall] : (pre= ID NAMESPACE post= ID -> {post.getText().indexOf(\".\") > 0}? ID[$pre.text + NAMESPACE + $post.text] -> ID[$pre.text + NAMESPACE + $post.text + \".\" + defaultCall] | name= ID -> {name.getText().indexOf(\".\") > 0}? ID[$name.text] -> ID[$name.text + \".\" + defaultCall] );
    public final StencilParser.callName_return callName(String defaultCall) throws RecognitionException {
        StencilParser.callName_return retval = new StencilParser.callName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token pre=null;
        Token post=null;
        Token name=null;
        Token NAMESPACE95=null;

        Object pre_tree=null;
        Object post_tree=null;
        Object name_tree=null;
        Object NAMESPACE95_tree=null;
        RewriteRuleTokenStream stream_NAMESPACE=new RewriteRuleTokenStream(adaptor,"token NAMESPACE");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:256:2: (pre= ID NAMESPACE post= ID -> {post.getText().indexOf(\".\") > 0}? ID[$pre.text + NAMESPACE + $post.text] -> ID[$pre.text + NAMESPACE + $post.text + \".\" + defaultCall] | name= ID -> {name.getText().indexOf(\".\") > 0}? ID[$name.text] -> ID[$name.text + \".\" + defaultCall] )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==ID) ) {
                int LA28_1 = input.LA(2);

                if ( (LA28_1==NAMESPACE) ) {
                    alt28=1;
                }
                else if ( (LA28_1==GROUP||LA28_1==ARG) ) {
                    alt28=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:256:4: pre= ID NAMESPACE post= ID
                    {
                    pre=(Token)match(input,ID,FOLLOW_ID_in_callName1514); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(pre);

                    NAMESPACE95=(Token)match(input,NAMESPACE,FOLLOW_NAMESPACE_in_callName1516); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NAMESPACE.add(NAMESPACE95);

                    post=(Token)match(input,ID,FOLLOW_ID_in_callName1520); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(post);



                    // AST REWRITE
                    // elements: ID, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 257:3: -> {post.getText().indexOf(\".\") > 0}? ID[$pre.text + NAMESPACE + $post.text]
                    if (post.getText().indexOf(".") > 0) {
                        adaptor.addChild(root_0, (Object)adaptor.create(ID, (pre!=null?pre.getText():null) + NAMESPACE + (post!=null?post.getText():null)));

                    }
                    else // 258:3: -> ID[$pre.text + NAMESPACE + $post.text + \".\" + defaultCall]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(ID, (pre!=null?pre.getText():null) + NAMESPACE + (post!=null?post.getText():null) + "." + defaultCall));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:259:4: name= ID
                    {
                    name=(Token)match(input,ID,FOLLOW_ID_in_callName1555); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);



                    // AST REWRITE
                    // elements: ID, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 260:3: -> {name.getText().indexOf(\".\") > 0}? ID[$name.text]
                    if (name.getText().indexOf(".") > 0) {
                        adaptor.addChild(root_0, (Object)adaptor.create(ID, (name!=null?name.getText():null)));

                    }
                    else // 261:3: -> ID[$name.text + \".\" + defaultCall]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(ID, (name!=null?name.getText():null) + "." + defaultCall));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "callName"

    public static class target_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "target"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:263:1: target[String def] : ( GLYPH tuple[false] | RETURN tuple[false] | CANVAS tuple[false] | LOCAL tuple[false] | VIEW tuple[false] | tuple[true] -> {def.equals(\"glyph\")}? ^( GLYPH tuple ) -> {def.equals(\"return\")}? ^( RETURN tuple ) -> ^( DEFAULT tuple ) );
    public final StencilParser.target_return target(String def) throws RecognitionException {
        StencilParser.target_return retval = new StencilParser.target_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GLYPH96=null;
        Token RETURN98=null;
        Token CANVAS100=null;
        Token LOCAL102=null;
        Token VIEW104=null;
        StencilParser.tuple_return tuple97 = null;

        StencilParser.tuple_return tuple99 = null;

        StencilParser.tuple_return tuple101 = null;

        StencilParser.tuple_return tuple103 = null;

        StencilParser.tuple_return tuple105 = null;

        StencilParser.tuple_return tuple106 = null;


        Object GLYPH96_tree=null;
        Object RETURN98_tree=null;
        Object CANVAS100_tree=null;
        Object LOCAL102_tree=null;
        Object VIEW104_tree=null;
        RewriteRuleSubtreeStream stream_tuple=new RewriteRuleSubtreeStream(adaptor,"rule tuple");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:264:2: ( GLYPH tuple[false] | RETURN tuple[false] | CANVAS tuple[false] | LOCAL tuple[false] | VIEW tuple[false] | tuple[true] -> {def.equals(\"glyph\")}? ^( GLYPH tuple ) -> {def.equals(\"return\")}? ^( RETURN tuple ) -> ^( DEFAULT tuple ) )
            int alt29=6;
            switch ( input.LA(1) ) {
            case GLYPH:
                {
                alt29=1;
                }
                break;
            case RETURN:
                {
                alt29=2;
                }
                break;
            case CANVAS:
                {
                alt29=3;
                }
                break;
            case LOCAL:
                {
                alt29=4;
                }
                break;
            case VIEW:
                {
                alt29=5;
                }
                break;
            case GROUP:
            case ID:
                {
                alt29=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:264:4: GLYPH tuple[false]
                    {
                    root_0 = (Object)adaptor.nil();

                    GLYPH96=(Token)match(input,GLYPH,FOLLOW_GLYPH_in_target1593); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    GLYPH96_tree = (Object)adaptor.create(GLYPH96);
                    root_0 = (Object)adaptor.becomeRoot(GLYPH96_tree, root_0);
                    }
                    pushFollow(FOLLOW_tuple_in_target1596);
                    tuple97=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tuple97.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:265:4: RETURN tuple[false]
                    {
                    root_0 = (Object)adaptor.nil();

                    RETURN98=(Token)match(input,RETURN,FOLLOW_RETURN_in_target1602); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RETURN98_tree = (Object)adaptor.create(RETURN98);
                    root_0 = (Object)adaptor.becomeRoot(RETURN98_tree, root_0);
                    }
                    pushFollow(FOLLOW_tuple_in_target1605);
                    tuple99=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tuple99.getTree());

                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:266:4: CANVAS tuple[false]
                    {
                    root_0 = (Object)adaptor.nil();

                    CANVAS100=(Token)match(input,CANVAS,FOLLOW_CANVAS_in_target1611); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    CANVAS100_tree = (Object)adaptor.create(CANVAS100);
                    root_0 = (Object)adaptor.becomeRoot(CANVAS100_tree, root_0);
                    }
                    pushFollow(FOLLOW_tuple_in_target1614);
                    tuple101=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tuple101.getTree());

                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:267:4: LOCAL tuple[false]
                    {
                    root_0 = (Object)adaptor.nil();

                    LOCAL102=(Token)match(input,LOCAL,FOLLOW_LOCAL_in_target1620); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    LOCAL102_tree = (Object)adaptor.create(LOCAL102);
                    root_0 = (Object)adaptor.becomeRoot(LOCAL102_tree, root_0);
                    }
                    pushFollow(FOLLOW_tuple_in_target1623);
                    tuple103=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tuple103.getTree());

                    }
                    break;
                case 5 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:268:4: VIEW tuple[false]
                    {
                    root_0 = (Object)adaptor.nil();

                    VIEW104=(Token)match(input,VIEW,FOLLOW_VIEW_in_target1629); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    VIEW104_tree = (Object)adaptor.create(VIEW104);
                    root_0 = (Object)adaptor.becomeRoot(VIEW104_tree, root_0);
                    }
                    pushFollow(FOLLOW_tuple_in_target1632);
                    tuple105=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tuple105.getTree());

                    }
                    break;
                case 6 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:269:4: tuple[true]
                    {
                    pushFollow(FOLLOW_tuple_in_target1638);
                    tuple106=tuple(true);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tuple.add(tuple106.getTree());


                    // AST REWRITE
                    // elements: tuple, tuple, tuple
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 270:3: -> {def.equals(\"glyph\")}? ^( GLYPH tuple )
                    if (def.equals("glyph")) {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:270:29: ^( GLYPH tuple )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(GLYPH, "GLYPH"), root_1);

                        adaptor.addChild(root_1, stream_tuple.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }
                    else // 271:3: -> {def.equals(\"return\")}? ^( RETURN tuple )
                    if (def.equals("return")) {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:271:30: ^( RETURN tuple )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RETURN, "RETURN"), root_1);

                        adaptor.addChild(root_1, stream_tuple.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }
                    else // 272:3: -> ^( DEFAULT tuple )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:272:6: ^( DEFAULT tuple )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DEFAULT, "DEFAULT"), root_1);

                        adaptor.addChild(root_1, stream_tuple.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "target"

    public static class pythonDef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pythonDef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:276:1: pythonDef : ( ( PYTHON ARG )=> PYTHON ARG env= ID CLOSE_ARG name= ID ( pythonBlock )+ -> ^( PYTHON[$name.text] ID ( pythonBlock )+ ) | PYTHON name= ID ( pythonBlock )+ -> ^( PYTHON[$name.text] ID[buryID($name.text)] ( pythonBlock )+ ) );
    public final StencilParser.pythonDef_return pythonDef() throws RecognitionException {
        StencilParser.pythonDef_return retval = new StencilParser.pythonDef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token env=null;
        Token name=null;
        Token PYTHON107=null;
        Token ARG108=null;
        Token CLOSE_ARG109=null;
        Token PYTHON111=null;
        StencilParser.pythonBlock_return pythonBlock110 = null;

        StencilParser.pythonBlock_return pythonBlock112 = null;


        Object env_tree=null;
        Object name_tree=null;
        Object PYTHON107_tree=null;
        Object ARG108_tree=null;
        Object CLOSE_ARG109_tree=null;
        Object PYTHON111_tree=null;
        RewriteRuleTokenStream stream_ARG=new RewriteRuleTokenStream(adaptor,"token ARG");
        RewriteRuleTokenStream stream_CLOSE_ARG=new RewriteRuleTokenStream(adaptor,"token CLOSE_ARG");
        RewriteRuleTokenStream stream_PYTHON=new RewriteRuleTokenStream(adaptor,"token PYTHON");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_pythonBlock=new RewriteRuleSubtreeStream(adaptor,"rule pythonBlock");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:2: ( ( PYTHON ARG )=> PYTHON ARG env= ID CLOSE_ARG name= ID ( pythonBlock )+ -> ^( PYTHON[$name.text] ID ( pythonBlock )+ ) | PYTHON name= ID ( pythonBlock )+ -> ^( PYTHON[$name.text] ID[buryID($name.text)] ( pythonBlock )+ ) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==PYTHON) ) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==ARG) && (synpred2_Stencil())) {
                    alt32=1;
                }
                else if ( (LA32_1==ID) ) {
                    alt32=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:4: ( PYTHON ARG )=> PYTHON ARG env= ID CLOSE_ARG name= ID ( pythonBlock )+
                    {
                    PYTHON107=(Token)match(input,PYTHON,FOLLOW_PYTHON_in_pythonDef1692); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PYTHON.add(PYTHON107);

                    ARG108=(Token)match(input,ARG,FOLLOW_ARG_in_pythonDef1694); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG108);

                    env=(Token)match(input,ID,FOLLOW_ID_in_pythonDef1698); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(env);

                    CLOSE_ARG109=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_pythonDef1700); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG109);

                    name=(Token)match(input,ID,FOLLOW_ID_in_pythonDef1704); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:56: ( pythonBlock )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0==FACET||LA30_0==TAGGED_ID) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:56: pythonBlock
                    	    {
                    	    pushFollow(FOLLOW_pythonBlock_in_pythonDef1706);
                    	    pythonBlock110=pythonBlock();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_pythonBlock.add(pythonBlock110.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);



                    // AST REWRITE
                    // elements: PYTHON, pythonBlock, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 278:3: -> ^( PYTHON[$name.text] ID ( pythonBlock )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:278:6: ^( PYTHON[$name.text] ID ( pythonBlock )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PYTHON, (name!=null?name.getText():null)), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());
                        if ( !(stream_pythonBlock.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_pythonBlock.hasNext() ) {
                            adaptor.addChild(root_1, stream_pythonBlock.nextTree());

                        }
                        stream_pythonBlock.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:279:4: PYTHON name= ID ( pythonBlock )+
                    {
                    PYTHON111=(Token)match(input,PYTHON,FOLLOW_PYTHON_in_pythonDef1726); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PYTHON.add(PYTHON111);

                    name=(Token)match(input,ID,FOLLOW_ID_in_pythonDef1730); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:279:19: ( pythonBlock )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==FACET||LA31_0==TAGGED_ID) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:279:19: pythonBlock
                    	    {
                    	    pushFollow(FOLLOW_pythonBlock_in_pythonDef1732);
                    	    pythonBlock112=pythonBlock();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_pythonBlock.add(pythonBlock112.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);



                    // AST REWRITE
                    // elements: PYTHON, ID, pythonBlock
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 280:3: -> ^( PYTHON[$name.text] ID[buryID($name.text)] ( pythonBlock )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:280:6: ^( PYTHON[$name.text] ID[buryID($name.text)] ( pythonBlock )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PYTHON, (name!=null?name.getText():null)), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(ID, buryID((name!=null?name.getText():null))));
                        if ( !(stream_pythonBlock.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_pythonBlock.hasNext() ) {
                            adaptor.addChild(root_1, stream_pythonBlock.nextTree());

                        }
                        stream_pythonBlock.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pythonDef"

    public static class pythonBlock_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pythonBlock"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:282:1: pythonBlock : ( FACET 'Init' CODE_BLOCK -> ^( FACET[\"Init\"] ^( YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE ) ^( LIST[\"Annotations\"] ^( ANNOTATION[\"Type\"] STRING[\"NA\"] ) ) CODE_BLOCK ) | annotations FACET name= ID tuple[true] YIELDS tuple[false] CODE_BLOCK -> ^( FACET[name] ^( YIELDS tuple tuple ) annotations CODE_BLOCK ) );
    public final StencilParser.pythonBlock_return pythonBlock() throws RecognitionException {
        StencilParser.pythonBlock_return retval = new StencilParser.pythonBlock_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token name=null;
        Token FACET113=null;
        Token string_literal114=null;
        Token CODE_BLOCK115=null;
        Token FACET117=null;
        Token YIELDS119=null;
        Token CODE_BLOCK121=null;
        StencilParser.annotations_return annotations116 = null;

        StencilParser.tuple_return tuple118 = null;

        StencilParser.tuple_return tuple120 = null;


        Object name_tree=null;
        Object FACET113_tree=null;
        Object string_literal114_tree=null;
        Object CODE_BLOCK115_tree=null;
        Object FACET117_tree=null;
        Object YIELDS119_tree=null;
        Object CODE_BLOCK121_tree=null;
        RewriteRuleTokenStream stream_74=new RewriteRuleTokenStream(adaptor,"token 74");
        RewriteRuleTokenStream stream_YIELDS=new RewriteRuleTokenStream(adaptor,"token YIELDS");
        RewriteRuleTokenStream stream_CODE_BLOCK=new RewriteRuleTokenStream(adaptor,"token CODE_BLOCK");
        RewriteRuleTokenStream stream_FACET=new RewriteRuleTokenStream(adaptor,"token FACET");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_tuple=new RewriteRuleSubtreeStream(adaptor,"rule tuple");
        RewriteRuleSubtreeStream stream_annotations=new RewriteRuleSubtreeStream(adaptor,"rule annotations");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:283:2: ( FACET 'Init' CODE_BLOCK -> ^( FACET[\"Init\"] ^( YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE ) ^( LIST[\"Annotations\"] ^( ANNOTATION[\"Type\"] STRING[\"NA\"] ) ) CODE_BLOCK ) | annotations FACET name= ID tuple[true] YIELDS tuple[false] CODE_BLOCK -> ^( FACET[name] ^( YIELDS tuple tuple ) annotations CODE_BLOCK ) )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==FACET) ) {
                int LA33_1 = input.LA(2);

                if ( (LA33_1==74) ) {
                    alt33=1;
                }
                else if ( (LA33_1==ID) ) {
                    alt33=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA33_0==TAGGED_ID) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:283:4: FACET 'Init' CODE_BLOCK
                    {
                    FACET113=(Token)match(input,FACET,FOLLOW_FACET_in_pythonBlock1758); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FACET.add(FACET113);

                    string_literal114=(Token)match(input,74,FOLLOW_74_in_pythonBlock1760); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_74.add(string_literal114);

                    CODE_BLOCK115=(Token)match(input,CODE_BLOCK,FOLLOW_CODE_BLOCK_in_pythonBlock1762); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CODE_BLOCK.add(CODE_BLOCK115);



                    // AST REWRITE
                    // elements: CODE_BLOCK, FACET
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 284:3: -> ^( FACET[\"Init\"] ^( YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE ) ^( LIST[\"Annotations\"] ^( ANNOTATION[\"Type\"] STRING[\"NA\"] ) ) CODE_BLOCK )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:284:6: ^( FACET[\"Init\"] ^( YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE ) ^( LIST[\"Annotations\"] ^( ANNOTATION[\"Type\"] STRING[\"NA\"] ) ) CODE_BLOCK )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FACET, "Init"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:284:22: ^( YIELDS TUPLE_PROTOTYPE TUPLE_PROTOTYPE )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(YIELDS, "YIELDS"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE"));
                        adaptor.addChild(root_2, (Object)adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:284:64: ^( LIST[\"Annotations\"] ^( ANNOTATION[\"Type\"] STRING[\"NA\"] ) )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Annotations"), root_2);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:284:86: ^( ANNOTATION[\"Type\"] STRING[\"NA\"] )
                        {
                        Object root_3 = (Object)adaptor.nil();
                        root_3 = (Object)adaptor.becomeRoot((Object)adaptor.create(ANNOTATION, "Type"), root_3);

                        adaptor.addChild(root_3, (Object)adaptor.create(STRING, "NA"));

                        adaptor.addChild(root_2, root_3);
                        }

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_CODE_BLOCK.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:285:4: annotations FACET name= ID tuple[true] YIELDS tuple[false] CODE_BLOCK
                    {
                    pushFollow(FOLLOW_annotations_in_pythonBlock1799);
                    annotations116=annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_annotations.add(annotations116.getTree());
                    FACET117=(Token)match(input,FACET,FOLLOW_FACET_in_pythonBlock1801); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_FACET.add(FACET117);

                    name=(Token)match(input,ID,FOLLOW_ID_in_pythonBlock1805); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);

                    pushFollow(FOLLOW_tuple_in_pythonBlock1807);
                    tuple118=tuple(true);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tuple.add(tuple118.getTree());
                    YIELDS119=(Token)match(input,YIELDS,FOLLOW_YIELDS_in_pythonBlock1810); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_YIELDS.add(YIELDS119);

                    pushFollow(FOLLOW_tuple_in_pythonBlock1812);
                    tuple120=tuple(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_tuple.add(tuple120.getTree());
                    CODE_BLOCK121=(Token)match(input,CODE_BLOCK,FOLLOW_CODE_BLOCK_in_pythonBlock1815); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CODE_BLOCK.add(CODE_BLOCK121);



                    // AST REWRITE
                    // elements: annotations, CODE_BLOCK, tuple, tuple, FACET, YIELDS
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 286:3: -> ^( FACET[name] ^( YIELDS tuple tuple ) annotations CODE_BLOCK )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:286:6: ^( FACET[name] ^( YIELDS tuple tuple ) annotations CODE_BLOCK )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FACET, name), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:286:20: ^( YIELDS tuple tuple )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(stream_YIELDS.nextNode(), root_2);

                        adaptor.addChild(root_2, stream_tuple.nextTree());
                        adaptor.addChild(root_2, stream_tuple.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_annotations.nextTree());
                        adaptor.addChild(root_1, stream_CODE_BLOCK.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "pythonBlock"

    public static class annotations_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotations"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:288:1: annotations : (a= annotation -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[$a.text.toUpperCase().substring(1)] ) ) | -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[\"CATEGORIZE\"] ) ) );
    public final StencilParser.annotations_return annotations() throws RecognitionException {
        StencilParser.annotations_return retval = new StencilParser.annotations_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.annotation_return a = null;


        RewriteRuleSubtreeStream stream_annotation=new RewriteRuleSubtreeStream(adaptor,"rule annotation");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:289:2: (a= annotation -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[$a.text.toUpperCase().substring(1)] ) ) | -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[\"CATEGORIZE\"] ) ) )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==TAGGED_ID) ) {
                alt34=1;
            }
            else if ( (LA34_0==FACET) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:289:4: a= annotation
                    {
                    pushFollow(FOLLOW_annotation_in_annotations1848);
                    a=annotation();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_annotation.add(a.getTree());


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 289:17: -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[$a.text.toUpperCase().substring(1)] ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:289:20: ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[$a.text.toUpperCase().substring(1)] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Annotations"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:289:42: ^( ANNOTATION[\"TYPE\"] STRING[$a.text.toUpperCase().substring(1)] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ANNOTATION, "TYPE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(STRING, (a!=null?input.toString(a.start,a.stop):null).toUpperCase().substring(1)));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:290:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 290:4: -> ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[\"CATEGORIZE\"] ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:290:7: ^( LIST[\"Annotations\"] ^( ANNOTATION[\"TYPE\"] STRING[\"CATEGORIZE\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Annotations"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:290:29: ^( ANNOTATION[\"TYPE\"] STRING[\"CATEGORIZE\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(ANNOTATION, "TYPE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(STRING, "CATEGORIZE"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "annotations"

    public static class annotation_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "annotation"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:292:1: annotation : t= TAGGED_ID -> ANNOTATION[\"JUNK\"] ;
    public final StencilParser.annotation_return annotation() throws RecognitionException {
        StencilParser.annotation_return retval = new StencilParser.annotation_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token t=null;

        Object t_tree=null;
        RewriteRuleTokenStream stream_TAGGED_ID=new RewriteRuleTokenStream(adaptor,"token TAGGED_ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:292:11: (t= TAGGED_ID -> ANNOTATION[\"JUNK\"] )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:292:13: t= TAGGED_ID
            {
            t=(Token)match(input,TAGGED_ID,FOLLOW_TAGGED_ID_in_annotation1890); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TAGGED_ID.add(t);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 292:25: -> ANNOTATION[\"JUNK\"]
            {
                adaptor.addChild(root_0, (Object)adaptor.create(ANNOTATION, "JUNK"));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "annotation"

    public static class specializer_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "specializer"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:296:1: specializer[RuleOpts opts] : ( ARG range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range ^( SPLIT BASIC PRE ID[(String) null] ) sepArgList ) | ARG split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) split sepArgList ) | ARG range SPLIT split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG split[true] SPLIT range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG argList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) ^( SPLIT BASIC PRE ID[(String) null] ) argList ) | -> ^( SPECIALIZER DEFAULT ) );
    public final StencilParser.specializer_return specializer(RuleOpts opts) throws RecognitionException {
        StencilParser.specializer_return retval = new StencilParser.specializer_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ARG122=null;
        Token CLOSE_ARG125=null;
        Token ARG126=null;
        Token CLOSE_ARG129=null;
        Token ARG130=null;
        Token SPLIT132=null;
        Token CLOSE_ARG135=null;
        Token ARG136=null;
        Token SPLIT138=null;
        Token CLOSE_ARG141=null;
        Token ARG142=null;
        Token CLOSE_ARG144=null;
        StencilParser.range_return range123 = null;

        StencilParser.sepArgList_return sepArgList124 = null;

        StencilParser.split_return split127 = null;

        StencilParser.sepArgList_return sepArgList128 = null;

        StencilParser.range_return range131 = null;

        StencilParser.split_return split133 = null;

        StencilParser.sepArgList_return sepArgList134 = null;

        StencilParser.split_return split137 = null;

        StencilParser.range_return range139 = null;

        StencilParser.sepArgList_return sepArgList140 = null;

        StencilParser.argList_return argList143 = null;


        Object ARG122_tree=null;
        Object CLOSE_ARG125_tree=null;
        Object ARG126_tree=null;
        Object CLOSE_ARG129_tree=null;
        Object ARG130_tree=null;
        Object SPLIT132_tree=null;
        Object CLOSE_ARG135_tree=null;
        Object ARG136_tree=null;
        Object SPLIT138_tree=null;
        Object CLOSE_ARG141_tree=null;
        Object ARG142_tree=null;
        Object CLOSE_ARG144_tree=null;
        RewriteRuleTokenStream stream_SPLIT=new RewriteRuleTokenStream(adaptor,"token SPLIT");
        RewriteRuleTokenStream stream_ARG=new RewriteRuleTokenStream(adaptor,"token ARG");
        RewriteRuleTokenStream stream_CLOSE_ARG=new RewriteRuleTokenStream(adaptor,"token CLOSE_ARG");
        RewriteRuleSubtreeStream stream_sepArgList=new RewriteRuleSubtreeStream(adaptor,"rule sepArgList");
        RewriteRuleSubtreeStream stream_argList=new RewriteRuleSubtreeStream(adaptor,"rule argList");
        RewriteRuleSubtreeStream stream_split=new RewriteRuleSubtreeStream(adaptor,"rule split");
        RewriteRuleSubtreeStream stream_range=new RewriteRuleSubtreeStream(adaptor,"rule range");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:297:2: ( ARG range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range ^( SPLIT BASIC PRE ID[(String) null] ) sepArgList ) | ARG split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) split sepArgList ) | ARG range SPLIT split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG split[true] SPLIT range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG argList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) ^( SPLIT BASIC PRE ID[(String) null] ) argList ) | -> ^( SPECIALIZER DEFAULT ) )
            int alt35=6;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:297:4: ARG range sepArgList CLOSE_ARG {...}?
                    {
                    ARG122=(Token)match(input,ARG,FOLLOW_ARG_in_specializer1908); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG122);

                    pushFollow(FOLLOW_range_in_specializer1910);
                    range123=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_range.add(range123.getTree());
                    pushFollow(FOLLOW_sepArgList_in_specializer1912);
                    sepArgList124=sepArgList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sepArgList.add(sepArgList124.getTree());
                    CLOSE_ARG125=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_specializer1914); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG125);

                    if ( !((opts == RuleOpts.All)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "specializer", "opts == RuleOpts.All");
                    }


                    // AST REWRITE
                    // elements: range, sepArgList
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 298:27: -> ^( SPECIALIZER range ^( SPLIT BASIC PRE ID[(String) null] ) sepArgList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:298:30: ^( SPECIALIZER range ^( SPLIT BASIC PRE ID[(String) null] ) sepArgList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        adaptor.addChild(root_1, stream_range.nextTree());
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:298:50: ^( SPLIT BASIC PRE ID[(String) null] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BASIC, "BASIC"));
                        adaptor.addChild(root_2, (Object)adaptor.create(PRE, "PRE"));
                        adaptor.addChild(root_2, (Object)adaptor.create(ID, (String) null));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_sepArgList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:299:4: ARG split[false] sepArgList CLOSE_ARG {...}?
                    {
                    ARG126=(Token)match(input,ARG,FOLLOW_ARG_in_specializer1944); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG126);

                    pushFollow(FOLLOW_split_in_specializer1946);
                    split127=split(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_split.add(split127.getTree());
                    pushFollow(FOLLOW_sepArgList_in_specializer1949);
                    sepArgList128=sepArgList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sepArgList.add(sepArgList128.getTree());
                    CLOSE_ARG129=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_specializer1951); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG129);

                    if ( !((opts == RuleOpts.All)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "specializer", "opts == RuleOpts.All");
                    }


                    // AST REWRITE
                    // elements: sepArgList, split
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 300:27: -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) split sepArgList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:300:30: ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) split sepArgList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:300:44: ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(NUMBER, RANGE_END));
                        adaptor.addChild(root_2, (Object)adaptor.create(NUMBER, RANGE_END));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_split.nextTree());
                        adaptor.addChild(root_1, stream_sepArgList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:301:4: ARG range SPLIT split[false] sepArgList CLOSE_ARG {...}?
                    {
                    ARG130=(Token)match(input,ARG,FOLLOW_ARG_in_specializer1980); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG130);

                    pushFollow(FOLLOW_range_in_specializer1982);
                    range131=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_range.add(range131.getTree());
                    SPLIT132=(Token)match(input,SPLIT,FOLLOW_SPLIT_in_specializer1984); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SPLIT.add(SPLIT132);

                    pushFollow(FOLLOW_split_in_specializer1986);
                    split133=split(false);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_split.add(split133.getTree());
                    pushFollow(FOLLOW_sepArgList_in_specializer1989);
                    sepArgList134=sepArgList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sepArgList.add(sepArgList134.getTree());
                    CLOSE_ARG135=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_specializer1991); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG135);

                    if ( !((opts == RuleOpts.All)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "specializer", "opts == RuleOpts.All");
                    }


                    // AST REWRITE
                    // elements: sepArgList, split, range
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 302:27: -> ^( SPECIALIZER range split sepArgList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:302:31: ^( SPECIALIZER range split sepArgList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        adaptor.addChild(root_1, stream_range.nextTree());
                        adaptor.addChild(root_1, stream_split.nextTree());
                        adaptor.addChild(root_1, stream_sepArgList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:303:4: ARG split[true] SPLIT range sepArgList CLOSE_ARG {...}?
                    {
                    ARG136=(Token)match(input,ARG,FOLLOW_ARG_in_specializer2013); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG136);

                    pushFollow(FOLLOW_split_in_specializer2015);
                    split137=split(true);

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_split.add(split137.getTree());
                    SPLIT138=(Token)match(input,SPLIT,FOLLOW_SPLIT_in_specializer2018); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SPLIT.add(SPLIT138);

                    pushFollow(FOLLOW_range_in_specializer2020);
                    range139=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_range.add(range139.getTree());
                    pushFollow(FOLLOW_sepArgList_in_specializer2022);
                    sepArgList140=sepArgList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sepArgList.add(sepArgList140.getTree());
                    CLOSE_ARG141=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_specializer2024); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG141);

                    if ( !((opts == RuleOpts.All)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "specializer", "opts == RuleOpts.All");
                    }


                    // AST REWRITE
                    // elements: sepArgList, range, split
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 304:27: -> ^( SPECIALIZER range split sepArgList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:304:30: ^( SPECIALIZER range split sepArgList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        adaptor.addChild(root_1, stream_range.nextTree());
                        adaptor.addChild(root_1, stream_split.nextTree());
                        adaptor.addChild(root_1, stream_sepArgList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:305:4: ARG argList CLOSE_ARG {...}?
                    {
                    ARG142=(Token)match(input,ARG,FOLLOW_ARG_in_specializer2045); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG142);

                    pushFollow(FOLLOW_argList_in_specializer2047);
                    argList143=argList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_argList.add(argList143.getTree());
                    CLOSE_ARG144=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_specializer2049); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG144);

                    if ( !((opts != RuleOpts.Empty)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "specializer", "opts != RuleOpts.Empty");
                    }


                    // AST REWRITE
                    // elements: argList
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 306:29: -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) ^( SPLIT BASIC PRE ID[(String) null] ) argList )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:306:32: ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) ^( SPLIT BASIC PRE ID[(String) null] ) argList )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:306:46: ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(NUMBER, RANGE_END));
                        adaptor.addChild(root_2, (Object)adaptor.create(NUMBER, RANGE_END));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:306:91: ^( SPLIT BASIC PRE ID[(String) null] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BASIC, "BASIC"));
                        adaptor.addChild(root_2, (Object)adaptor.create(PRE, "PRE"));
                        adaptor.addChild(root_2, (Object)adaptor.create(ID, (String) null));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_argList.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:307:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 307:4: -> ^( SPECIALIZER DEFAULT )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:307:7: ^( SPECIALIZER DEFAULT )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPECIALIZER, "SPECIALIZER"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(DEFAULT, "DEFAULT"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "specializer"

    public static class sepArgList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sepArgList"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:309:1: sepArgList : ( SEPARATOR argList | -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] ) );
    public final StencilParser.sepArgList_return sepArgList() throws RecognitionException {
        StencilParser.sepArgList_return retval = new StencilParser.sepArgList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPARATOR145=null;
        StencilParser.argList_return argList146 = null;


        Object SEPARATOR145_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:310:2: ( SEPARATOR argList | -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] ) )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==SEPARATOR) ) {
                alt36=1;
            }
            else if ( (LA36_0==CLOSE_ARG) ) {
                alt36=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:310:4: SEPARATOR argList
                    {
                    root_0 = (Object)adaptor.nil();

                    SEPARATOR145=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_sepArgList2102); if (state.failed) return retval;
                    pushFollow(FOLLOW_argList_in_sepArgList2105);
                    argList146=argList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, argList146.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:311:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 311:4: -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:311:7: ^( LIST[\"Values Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Values Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:311:35: ^( LIST[\"Map Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Map Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sepArgList"

    public static class argList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "argList"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:313:1: argList : ( -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] ) | values -> values ^( LIST[\"Map Arguments\"] ) | mapList -> ^( LIST[\"Value Arguments\"] ) mapList | values SEPARATOR mapList );
    public final StencilParser.argList_return argList() throws RecognitionException {
        StencilParser.argList_return retval = new StencilParser.argList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPARATOR150=null;
        StencilParser.values_return values147 = null;

        StencilParser.mapList_return mapList148 = null;

        StencilParser.values_return values149 = null;

        StencilParser.mapList_return mapList151 = null;


        Object SEPARATOR150_tree=null;
        RewriteRuleSubtreeStream stream_mapList=new RewriteRuleSubtreeStream(adaptor,"rule mapList");
        RewriteRuleSubtreeStream stream_values=new RewriteRuleSubtreeStream(adaptor,"rule values");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:314:2: ( -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] ) | values -> values ^( LIST[\"Map Arguments\"] ) | mapList -> ^( LIST[\"Value Arguments\"] ) mapList | values SEPARATOR mapList )
            int alt37=4;
            alt37 = dfa37.predict(input);
            switch (alt37) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:314:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 314:4: -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:314:7: ^( LIST[\"Values Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Values Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:314:35: ^( LIST[\"Map Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Map Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:315:4: values
                    {
                    pushFollow(FOLLOW_values_in_argList2146);
                    values147=values();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_values.add(values147.getTree());


                    // AST REWRITE
                    // elements: values
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 315:11: -> values ^( LIST[\"Map Arguments\"] )
                    {
                        adaptor.addChild(root_0, stream_values.nextTree());
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:315:21: ^( LIST[\"Map Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Map Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:316:4: mapList
                    {
                    pushFollow(FOLLOW_mapList_in_argList2160);
                    mapList148=mapList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_mapList.add(mapList148.getTree());


                    // AST REWRITE
                    // elements: mapList
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 316:12: -> ^( LIST[\"Value Arguments\"] ) mapList
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:316:15: ^( LIST[\"Value Arguments\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Value Arguments"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }
                        adaptor.addChild(root_0, stream_mapList.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:317:4: values SEPARATOR mapList
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_values_in_argList2174);
                    values149=values();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, values149.getTree());
                    SEPARATOR150=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_argList2176); if (state.failed) return retval;
                    pushFollow(FOLLOW_mapList_in_argList2179);
                    mapList151=mapList();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, mapList151.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "argList"

    public static class values_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "values"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:319:1: values : atom ( SEPARATOR atom )* -> ^( LIST[\"Value Arguments\"] ( atom )* ) ;
    public final StencilParser.values_return values() throws RecognitionException {
        StencilParser.values_return retval = new StencilParser.values_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPARATOR153=null;
        StencilParser.atom_return atom152 = null;

        StencilParser.atom_return atom154 = null;


        Object SEPARATOR153_tree=null;
        RewriteRuleTokenStream stream_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token SEPARATOR");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:2: ( atom ( SEPARATOR atom )* -> ^( LIST[\"Value Arguments\"] ( atom )* ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:4: atom ( SEPARATOR atom )*
            {
            pushFollow(FOLLOW_atom_in_values2188);
            atom152=atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_atom.add(atom152.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:9: ( SEPARATOR atom )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==SEPARATOR) ) {
                    int LA38_2 = input.LA(2);

                    if ( (LA38_2==ALL||LA38_2==DEFAULT||LA38_2==NAMESPLIT||(LA38_2>=TAGGED_ID && LA38_2<=DIGITS)||(LA38_2>=83 && LA38_2<=84)) ) {
                        alt38=1;
                    }


                }


                switch (alt38) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:10: SEPARATOR atom
            	    {
            	    SEPARATOR153=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_values2191); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEPARATOR.add(SEPARATOR153);

            	    pushFollow(FOLLOW_atom_in_values2193);
            	    atom154=atom();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_atom.add(atom154.getTree());

            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);



            // AST REWRITE
            // elements: atom
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 320:27: -> ^( LIST[\"Value Arguments\"] ( atom )* )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:30: ^( LIST[\"Value Arguments\"] ( atom )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Value Arguments"), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:320:56: ( atom )*
                while ( stream_atom.hasNext() ) {
                    adaptor.addChild(root_1, stream_atom.nextTree());

                }
                stream_atom.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "values"

    public static class mapList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapList"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:322:1: mapList : mapEntry ( SEPARATOR mapEntry )* -> ^( LIST[\"Map Arguments\"] ( mapEntry )* ) ;
    public final StencilParser.mapList_return mapList() throws RecognitionException {
        StencilParser.mapList_return retval = new StencilParser.mapList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token SEPARATOR156=null;
        StencilParser.mapEntry_return mapEntry155 = null;

        StencilParser.mapEntry_return mapEntry157 = null;


        Object SEPARATOR156_tree=null;
        RewriteRuleTokenStream stream_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token SEPARATOR");
        RewriteRuleSubtreeStream stream_mapEntry=new RewriteRuleSubtreeStream(adaptor,"rule mapEntry");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:2: ( mapEntry ( SEPARATOR mapEntry )* -> ^( LIST[\"Map Arguments\"] ( mapEntry )* ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:4: mapEntry ( SEPARATOR mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapList2214);
            mapEntry155=mapEntry();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry155.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:13: ( SEPARATOR mapEntry )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==SEPARATOR) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:14: SEPARATOR mapEntry
            	    {
            	    SEPARATOR156=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_mapList2217); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEPARATOR.add(SEPARATOR156);

            	    pushFollow(FOLLOW_mapEntry_in_mapList2219);
            	    mapEntry157=mapEntry();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_mapEntry.add(mapEntry157.getTree());

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);



            // AST REWRITE
            // elements: mapEntry
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 323:35: -> ^( LIST[\"Map Arguments\"] ( mapEntry )* )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:38: ^( LIST[\"Map Arguments\"] ( mapEntry )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(LIST, "Map Arguments"), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:323:62: ( mapEntry )*
                while ( stream_mapEntry.hasNext() ) {
                    adaptor.addChild(root_1, stream_mapEntry.nextTree());

                }
                stream_mapEntry.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapList"

    public static class mapEntry_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mapEntry"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:325:1: mapEntry : k= ID '=' v= atom -> ^( MAP_ENTRY[$k.text] $v) ;
    public final StencilParser.mapEntry_return mapEntry() throws RecognitionException {
        StencilParser.mapEntry_return retval = new StencilParser.mapEntry_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token k=null;
        Token char_literal158=null;
        StencilParser.atom_return v = null;


        Object k_tree=null;
        Object char_literal158_tree=null;
        RewriteRuleTokenStream stream_75=new RewriteRuleTokenStream(adaptor,"token 75");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:326:2: (k= ID '=' v= atom -> ^( MAP_ENTRY[$k.text] $v) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:326:4: k= ID '=' v= atom
            {
            k=(Token)match(input,ID,FOLLOW_ID_in_mapEntry2244); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(k);

            char_literal158=(Token)match(input,75,FOLLOW_75_in_mapEntry2246); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_75.add(char_literal158);

            pushFollow(FOLLOW_atom_in_mapEntry2250);
            v=atom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_atom.add(v.getTree());


            // AST REWRITE
            // elements: v
            // token labels: 
            // rule labels: retval, v
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_v=new RewriteRuleSubtreeStream(adaptor,"rule v",v!=null?v.tree:null);

            root_0 = (Object)adaptor.nil();
            // 326:20: -> ^( MAP_ENTRY[$k.text] $v)
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:326:23: ^( MAP_ENTRY[$k.text] $v)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(MAP_ENTRY, (k!=null?k.getText():null)), root_1);

                adaptor.addChild(root_1, stream_v.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mapEntry"

    public static class tuple_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tuple"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:328:1: tuple[boolean allowEmpty] : ( emptySet {...}? -> ^( TUPLE_PROTOTYPE ) | ID -> ^( TUPLE_PROTOTYPE ID ) | GROUP ID ( SEPARATOR ID )* CLOSE_GROUP -> ^( TUPLE_PROTOTYPE ( ID )+ ) );
    public final StencilParser.tuple_return tuple(boolean allowEmpty) throws RecognitionException {
        StencilParser.tuple_return retval = new StencilParser.tuple_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID160=null;
        Token GROUP161=null;
        Token ID162=null;
        Token SEPARATOR163=null;
        Token ID164=null;
        Token CLOSE_GROUP165=null;
        StencilParser.emptySet_return emptySet159 = null;


        Object ID160_tree=null;
        Object GROUP161_tree=null;
        Object ID162_tree=null;
        Object SEPARATOR163_tree=null;
        Object ID164_tree=null;
        Object CLOSE_GROUP165_tree=null;
        RewriteRuleTokenStream stream_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token SEPARATOR");
        RewriteRuleTokenStream stream_CLOSE_GROUP=new RewriteRuleTokenStream(adaptor,"token CLOSE_GROUP");
        RewriteRuleTokenStream stream_GROUP=new RewriteRuleTokenStream(adaptor,"token GROUP");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_emptySet=new RewriteRuleSubtreeStream(adaptor,"rule emptySet");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:329:2: ( emptySet {...}? -> ^( TUPLE_PROTOTYPE ) | ID -> ^( TUPLE_PROTOTYPE ID ) | GROUP ID ( SEPARATOR ID )* CLOSE_GROUP -> ^( TUPLE_PROTOTYPE ( ID )+ ) )
            int alt41=3;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==GROUP) ) {
                int LA41_1 = input.LA(2);

                if ( (LA41_1==CLOSE_GROUP) ) {
                    alt41=1;
                }
                else if ( (LA41_1==ID) ) {
                    alt41=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA41_0==ID) ) {
                alt41=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:329:4: emptySet {...}?
                    {
                    pushFollow(FOLLOW_emptySet_in_tuple2270);
                    emptySet159=emptySet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_emptySet.add(emptySet159.getTree());
                    if ( !((allowEmpty)) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "tuple", "allowEmpty");
                    }


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 330:3: -> ^( TUPLE_PROTOTYPE )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:330:6: ^( TUPLE_PROTOTYPE )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:331:4: ID
                    {
                    ID160=(Token)match(input,ID,FOLLOW_ID_in_tuple2285); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID160);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 332:3: -> ^( TUPLE_PROTOTYPE ID )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:332:6: ^( TUPLE_PROTOTYPE ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:333:4: GROUP ID ( SEPARATOR ID )* CLOSE_GROUP
                    {
                    GROUP161=(Token)match(input,GROUP,FOLLOW_GROUP_in_tuple2300); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GROUP.add(GROUP161);

                    ID162=(Token)match(input,ID,FOLLOW_ID_in_tuple2302); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID162);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:333:13: ( SEPARATOR ID )*
                    loop40:
                    do {
                        int alt40=2;
                        int LA40_0 = input.LA(1);

                        if ( (LA40_0==SEPARATOR) ) {
                            alt40=1;
                        }


                        switch (alt40) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:333:14: SEPARATOR ID
                    	    {
                    	    SEPARATOR163=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_tuple2305); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_SEPARATOR.add(SEPARATOR163);

                    	    ID164=(Token)match(input,ID,FOLLOW_ID_in_tuple2307); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_ID.add(ID164);


                    	    }
                    	    break;

                    	default :
                    	    break loop40;
                        }
                    } while (true);

                    CLOSE_GROUP165=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_tuple2311); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_GROUP.add(CLOSE_GROUP165);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 334:3: -> ^( TUPLE_PROTOTYPE ( ID )+ )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:334:6: ^( TUPLE_PROTOTYPE ( ID )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TUPLE_PROTOTYPE, "TUPLE_PROTOTYPE"), root_1);

                        if ( !(stream_ID.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_ID.hasNext() ) {
                            adaptor.addChild(root_1, stream_ID.nextNode());

                        }
                        stream_ID.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "tuple"

    public static class emptySet_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "emptySet"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:337:1: emptySet : GROUP CLOSE_GROUP ;
    public final StencilParser.emptySet_return emptySet() throws RecognitionException {
        StencilParser.emptySet_return retval = new StencilParser.emptySet_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GROUP166=null;
        Token CLOSE_GROUP167=null;

        Object GROUP166_tree=null;
        Object CLOSE_GROUP167_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:337:9: ( GROUP CLOSE_GROUP )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:337:11: GROUP CLOSE_GROUP
            {
            root_0 = (Object)adaptor.nil();

            GROUP166=(Token)match(input,GROUP,FOLLOW_GROUP_in_emptySet2330); if (state.failed) return retval;
            CLOSE_GROUP167=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_emptySet2333); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "emptySet"

    public static class valueList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valueList"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:339:1: valueList : GROUP value ( SEPARATOR value )* CLOSE_GROUP ;
    public final StencilParser.valueList_return valueList() throws RecognitionException {
        StencilParser.valueList_return retval = new StencilParser.valueList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GROUP168=null;
        Token SEPARATOR170=null;
        Token CLOSE_GROUP172=null;
        StencilParser.value_return value169 = null;

        StencilParser.value_return value171 = null;


        Object GROUP168_tree=null;
        Object SEPARATOR170_tree=null;
        Object CLOSE_GROUP172_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:339:10: ( GROUP value ( SEPARATOR value )* CLOSE_GROUP )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:339:12: GROUP value ( SEPARATOR value )* CLOSE_GROUP
            {
            root_0 = (Object)adaptor.nil();

            GROUP168=(Token)match(input,GROUP,FOLLOW_GROUP_in_valueList2341); if (state.failed) return retval;
            pushFollow(FOLLOW_value_in_valueList2344);
            value169=value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, value169.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:339:25: ( SEPARATOR value )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==SEPARATOR) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:339:26: SEPARATOR value
            	    {
            	    SEPARATOR170=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_valueList2347); if (state.failed) return retval;
            	    pushFollow(FOLLOW_value_in_valueList2350);
            	    value171=value();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, value171.getTree());

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);

            CLOSE_GROUP172=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_valueList2354); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "valueList"

    public static class range_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:341:1: range : ( number RANGE number -> ^( RANGE number number ) | number RANGE 'n' -> ^( RANGE number NUMBER[RANGE_END] ) | 'n' RANGE 'n' -> ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) );
    public final StencilParser.range_return range() throws RecognitionException {
        StencilParser.range_return retval = new StencilParser.range_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token RANGE174=null;
        Token RANGE177=null;
        Token char_literal178=null;
        Token char_literal179=null;
        Token RANGE180=null;
        Token char_literal181=null;
        StencilParser.number_return number173 = null;

        StencilParser.number_return number175 = null;

        StencilParser.number_return number176 = null;


        Object RANGE174_tree=null;
        Object RANGE177_tree=null;
        Object char_literal178_tree=null;
        Object char_literal179_tree=null;
        Object RANGE180_tree=null;
        Object char_literal181_tree=null;
        RewriteRuleTokenStream stream_76=new RewriteRuleTokenStream(adaptor,"token 76");
        RewriteRuleTokenStream stream_RANGE=new RewriteRuleTokenStream(adaptor,"token RANGE");
        RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:342:2: ( number RANGE number -> ^( RANGE number number ) | number RANGE 'n' -> ^( RANGE number NUMBER[RANGE_END] ) | 'n' RANGE 'n' -> ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) )
            int alt43=3;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:342:4: number RANGE number
                    {
                    pushFollow(FOLLOW_number_in_range2366);
                    number173=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_number.add(number173.getTree());
                    RANGE174=(Token)match(input,RANGE,FOLLOW_RANGE_in_range2368); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RANGE.add(RANGE174);

                    pushFollow(FOLLOW_number_in_range2370);
                    number175=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_number.add(number175.getTree());


                    // AST REWRITE
                    // elements: RANGE, number, number
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 343:3: -> ^( RANGE number number )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:343:6: ^( RANGE number number )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_RANGE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_number.nextTree());
                        adaptor.addChild(root_1, stream_number.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:344:4: number RANGE 'n'
                    {
                    pushFollow(FOLLOW_number_in_range2387);
                    number176=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_number.add(number176.getTree());
                    RANGE177=(Token)match(input,RANGE,FOLLOW_RANGE_in_range2389); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RANGE.add(RANGE177);

                    char_literal178=(Token)match(input,76,FOLLOW_76_in_range2391); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_76.add(char_literal178);



                    // AST REWRITE
                    // elements: RANGE, number
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 345:3: -> ^( RANGE number NUMBER[RANGE_END] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:345:6: ^( RANGE number NUMBER[RANGE_END] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_RANGE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_number.nextTree());
                        adaptor.addChild(root_1, (Object)adaptor.create(NUMBER, RANGE_END));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:346:4: 'n' RANGE 'n'
                    {
                    char_literal179=(Token)match(input,76,FOLLOW_76_in_range2409); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_76.add(char_literal179);

                    RANGE180=(Token)match(input,RANGE,FOLLOW_RANGE_in_range2411); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RANGE.add(RANGE180);

                    char_literal181=(Token)match(input,76,FOLLOW_76_in_range2413); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_76.add(char_literal181);



                    // AST REWRITE
                    // elements: RANGE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 347:3: -> ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:347:6: ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_RANGE.nextNode(), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(NUMBER, RANGE_END));
                        adaptor.addChild(root_1, (Object)adaptor.create(NUMBER, RANGE_END));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range"

    public static class split_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "split"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:350:1: split[boolean pre] : ( ID -> {pre}? ^( SPLIT BASIC PRE ID ) -> ^( SPLIT BASIC POST ID ) | ORDER ID -> {pre}? ^( SPLIT ORDER PRE ID ) -> ^( SPLIT ORDER POST ID ) );
    public final StencilParser.split_return split(boolean pre) throws RecognitionException {
        StencilParser.split_return retval = new StencilParser.split_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID182=null;
        Token ORDER183=null;
        Token ID184=null;

        Object ID182_tree=null;
        Object ORDER183_tree=null;
        Object ID184_tree=null;
        RewriteRuleTokenStream stream_ORDER=new RewriteRuleTokenStream(adaptor,"token ORDER");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:351:2: ( ID -> {pre}? ^( SPLIT BASIC PRE ID ) -> ^( SPLIT BASIC POST ID ) | ORDER ID -> {pre}? ^( SPLIT ORDER PRE ID ) -> ^( SPLIT ORDER POST ID ) )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==ID) ) {
                alt44=1;
            }
            else if ( (LA44_0==ORDER) ) {
                alt44=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:351:4: ID
                    {
                    ID182=(Token)match(input,ID,FOLLOW_ID_in_split2438); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID182);



                    // AST REWRITE
                    // elements: ID, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 351:8: -> {pre}? ^( SPLIT BASIC PRE ID )
                    if (pre) {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:351:18: ^( SPLIT BASIC PRE ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BASIC, "BASIC"));
                        adaptor.addChild(root_1, (Object)adaptor.create(PRE, "PRE"));
                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }
                    else // 352:5: -> ^( SPLIT BASIC POST ID )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:352:14: ^( SPLIT BASIC POST ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BASIC, "BASIC"));
                        adaptor.addChild(root_1, (Object)adaptor.create(POST, "POST"));
                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:353:7: ORDER ID
                    {
                    ORDER183=(Token)match(input,ORDER,FOLLOW_ORDER_in_split2483); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ORDER.add(ORDER183);

                    ID184=(Token)match(input,ID,FOLLOW_ID_in_split2485); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID184);



                    // AST REWRITE
                    // elements: ORDER, ORDER, ID, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 354:6: -> {pre}? ^( SPLIT ORDER PRE ID )
                    if (pre) {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:354:16: ^( SPLIT ORDER PRE ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_1);

                        adaptor.addChild(root_1, stream_ORDER.nextNode());
                        adaptor.addChild(root_1, (Object)adaptor.create(PRE, "PRE"));
                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }
                    else // 355:3: -> ^( SPLIT ORDER POST ID )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:355:12: ^( SPLIT ORDER POST ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SPLIT, "SPLIT"), root_1);

                        adaptor.addChild(root_1, stream_ORDER.nextNode());
                        adaptor.addChild(root_1, (Object)adaptor.create(POST, "POST"));
                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "split"

    public static class value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:357:1: value : ( tupleRef | atom );
    public final StencilParser.value_return value() throws RecognitionException {
        StencilParser.value_return retval = new StencilParser.value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.tupleRef_return tupleRef185 = null;

        StencilParser.atom_return atom186 = null;



        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:357:7: ( tupleRef | atom )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ARG||LA45_0==ID) ) {
                alt45=1;
            }
            else if ( (LA45_0==ALL||LA45_0==DEFAULT||LA45_0==NAMESPLIT||(LA45_0>=TAGGED_ID && LA45_0<=DIGITS)||(LA45_0>=83 && LA45_0<=84)) ) {
                alt45=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:357:9: tupleRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_tupleRef_in_value2533);
                    tupleRef185=tupleRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tupleRef185.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:357:21: atom
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_atom_in_value2538);
                    atom186=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom186.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value"

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:1: atom : ( sigil | number | STRING | DEFAULT | ALL );
    public final StencilParser.atom_return atom() throws RecognitionException {
        StencilParser.atom_return retval = new StencilParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING189=null;
        Token DEFAULT190=null;
        Token ALL191=null;
        StencilParser.sigil_return sigil187 = null;

        StencilParser.number_return number188 = null;


        Object STRING189_tree=null;
        Object DEFAULT190_tree=null;
        Object ALL191_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:7: ( sigil | number | STRING | DEFAULT | ALL )
            int alt46=5;
            switch ( input.LA(1) ) {
            case TAGGED_ID:
                {
                alt46=1;
                }
                break;
            case NAMESPLIT:
            case DIGITS:
            case 83:
            case 84:
                {
                alt46=2;
                }
                break;
            case STRING:
                {
                alt46=3;
                }
                break;
            case DEFAULT:
                {
                alt46=4;
                }
                break;
            case ALL:
                {
                alt46=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:9: sigil
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_sigil_in_atom2546);
                    sigil187=sigil();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sigil187.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:17: number
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_number_in_atom2550);
                    number188=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, number188.getTree());

                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:26: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING189=(Token)match(input,STRING,FOLLOW_STRING_in_atom2554); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING189_tree = (Object)adaptor.create(STRING189);
                    adaptor.addChild(root_0, STRING189_tree);
                    }

                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:35: DEFAULT
                    {
                    root_0 = (Object)adaptor.nil();

                    DEFAULT190=(Token)match(input,DEFAULT,FOLLOW_DEFAULT_in_atom2558); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    DEFAULT190_tree = (Object)adaptor.create(DEFAULT190);
                    adaptor.addChild(root_0, DEFAULT190_tree);
                    }

                    }
                    break;
                case 5 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:358:45: ALL
                    {
                    root_0 = (Object)adaptor.nil();

                    ALL191=(Token)match(input,ALL,FOLLOW_ALL_in_atom2562); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ALL191_tree = (Object)adaptor.create(ALL191);
                    adaptor.addChild(root_0, ALL191_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom"

    public static class tupleRef_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tupleRef"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:360:1: tupleRef : ( ID -> ^( TUPLE_REF ID ) | ARG number CLOSE_ARG -> ^( TUPLE_REF number ) );
    public final StencilParser.tupleRef_return tupleRef() throws RecognitionException {
        StencilParser.tupleRef_return retval = new StencilParser.tupleRef_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID192=null;
        Token ARG193=null;
        Token CLOSE_ARG195=null;
        StencilParser.number_return number194 = null;


        Object ID192_tree=null;
        Object ARG193_tree=null;
        Object CLOSE_ARG195_tree=null;
        RewriteRuleTokenStream stream_ARG=new RewriteRuleTokenStream(adaptor,"token ARG");
        RewriteRuleTokenStream stream_CLOSE_ARG=new RewriteRuleTokenStream(adaptor,"token CLOSE_ARG");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:361:2: ( ID -> ^( TUPLE_REF ID ) | ARG number CLOSE_ARG -> ^( TUPLE_REF number ) )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==ID) ) {
                alt47=1;
            }
            else if ( (LA47_0==ARG) ) {
                alt47=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:361:4: ID
                    {
                    ID192=(Token)match(input,ID,FOLLOW_ID_in_tupleRef2573); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID192);



                    // AST REWRITE
                    // elements: ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 361:7: -> ^( TUPLE_REF ID )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:361:10: ^( TUPLE_REF ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TUPLE_REF, "TUPLE_REF"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:363:4: ARG number CLOSE_ARG
                    {
                    ARG193=(Token)match(input,ARG,FOLLOW_ARG_in_tupleRef2587); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG.add(ARG193);

                    pushFollow(FOLLOW_number_in_tupleRef2589);
                    number194=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_number.add(number194.getTree());
                    CLOSE_ARG195=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_tupleRef2591); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_CLOSE_ARG.add(CLOSE_ARG195);



                    // AST REWRITE
                    // elements: number
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 363:25: -> ^( TUPLE_REF number )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:363:28: ^( TUPLE_REF number )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TUPLE_REF, "TUPLE_REF"), root_1);

                        adaptor.addChild(root_1, stream_number.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "tupleRef"

    public static class qualifiedID_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qualifiedID"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:365:1: qualifiedID : ID ARG number CLOSE_ARG ;
    public final StencilParser.qualifiedID_return qualifiedID() throws RecognitionException {
        StencilParser.qualifiedID_return retval = new StencilParser.qualifiedID_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID196=null;
        Token ARG197=null;
        Token CLOSE_ARG199=null;
        StencilParser.number_return number198 = null;


        Object ID196_tree=null;
        Object ARG197_tree=null;
        Object CLOSE_ARG199_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:365:13: ( ID ARG number CLOSE_ARG )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:365:15: ID ARG number CLOSE_ARG
            {
            root_0 = (Object)adaptor.nil();

            ID196=(Token)match(input,ID,FOLLOW_ID_in_qualifiedID2607); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID196_tree = (Object)adaptor.create(ID196);
            root_0 = (Object)adaptor.becomeRoot(ID196_tree, root_0);
            }
            ARG197=(Token)match(input,ARG,FOLLOW_ARG_in_qualifiedID2610); if (state.failed) return retval;
            pushFollow(FOLLOW_number_in_qualifiedID2613);
            number198=number();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, number198.getTree());
            CLOSE_ARG199=(Token)match(input,CLOSE_ARG,FOLLOW_CLOSE_ARG_in_qualifiedID2615); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "qualifiedID"

    public static class sigil_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sigil"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:367:1: sigil : t= TAGGED_ID sValueList -> ^( SIGIL[$t.text] sValueList ) ;
    public final StencilParser.sigil_return sigil() throws RecognitionException {
        StencilParser.sigil_return retval = new StencilParser.sigil_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token t=null;
        StencilParser.sValueList_return sValueList200 = null;


        Object t_tree=null;
        RewriteRuleTokenStream stream_TAGGED_ID=new RewriteRuleTokenStream(adaptor,"token TAGGED_ID");
        RewriteRuleSubtreeStream stream_sValueList=new RewriteRuleSubtreeStream(adaptor,"rule sValueList");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:367:6: (t= TAGGED_ID sValueList -> ^( SIGIL[$t.text] sValueList ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:367:8: t= TAGGED_ID sValueList
            {
            t=(Token)match(input,TAGGED_ID,FOLLOW_TAGGED_ID_in_sigil2625); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TAGGED_ID.add(t);

            pushFollow(FOLLOW_sValueList_in_sigil2627);
            sValueList200=sValueList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sValueList.add(sValueList200.getTree());


            // AST REWRITE
            // elements: sValueList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 367:31: -> ^( SIGIL[$t.text] sValueList )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:367:34: ^( SIGIL[$t.text] sValueList )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SIGIL, (t!=null?t.getText():null)), root_1);

                adaptor.addChild(root_1, stream_sValueList.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sigil"

    public static class sValueList_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sValueList"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:368:9: private sValueList : GROUP sValue ( SEPARATOR sValue )* CLOSE_GROUP ;
    public final StencilParser.sValueList_return sValueList() throws RecognitionException {
        StencilParser.sValueList_return retval = new StencilParser.sValueList_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token GROUP201=null;
        Token SEPARATOR203=null;
        Token CLOSE_GROUP205=null;
        StencilParser.sValue_return sValue202 = null;

        StencilParser.sValue_return sValue204 = null;


        Object GROUP201_tree=null;
        Object SEPARATOR203_tree=null;
        Object CLOSE_GROUP205_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:368:19: ( GROUP sValue ( SEPARATOR sValue )* CLOSE_GROUP )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:368:22: GROUP sValue ( SEPARATOR sValue )* CLOSE_GROUP
            {
            root_0 = (Object)adaptor.nil();

            GROUP201=(Token)match(input,GROUP,FOLLOW_GROUP_in_sValueList2645); if (state.failed) return retval;
            pushFollow(FOLLOW_sValue_in_sValueList2648);
            sValue202=sValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, sValue202.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:368:36: ( SEPARATOR sValue )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==SEPARATOR) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:368:37: SEPARATOR sValue
            	    {
            	    SEPARATOR203=(Token)match(input,SEPARATOR,FOLLOW_SEPARATOR_in_sValueList2651); if (state.failed) return retval;
            	    pushFollow(FOLLOW_sValue_in_sValueList2654);
            	    sValue204=sValue();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, sValue204.getTree());

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            CLOSE_GROUP205=(Token)match(input,CLOSE_GROUP,FOLLOW_CLOSE_GROUP_in_sValueList2658); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sValueList"

    public static class sValue_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sValue"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:369:9: private sValue : ( tupleRef | number | STRING );
    public final StencilParser.sValue_return sValue() throws RecognitionException {
        StencilParser.sValue_return retval = new StencilParser.sValue_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token STRING208=null;
        StencilParser.tupleRef_return tupleRef206 = null;

        StencilParser.number_return number207 = null;


        Object STRING208_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:369:16: ( tupleRef | number | STRING )
            int alt49=3;
            switch ( input.LA(1) ) {
            case ARG:
            case ID:
                {
                alt49=1;
                }
                break;
            case NAMESPLIT:
            case DIGITS:
            case 83:
            case 84:
                {
                alt49=2;
                }
                break;
            case STRING:
                {
                alt49=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:369:18: tupleRef
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_tupleRef_in_sValue2668);
                    tupleRef206=tupleRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tupleRef206.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:369:29: number
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_number_in_sValue2672);
                    number207=number();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, number207.getTree());

                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:369:38: STRING
                    {
                    root_0 = (Object)adaptor.nil();

                    STRING208=(Token)match(input,STRING,FOLLOW_STRING_in_sValue2676); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING208_tree = (Object)adaptor.create(STRING208);
                    adaptor.addChild(root_0, STRING208_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sValue"

    public static class booleanOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "booleanOp"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:372:1: booleanOp : (t= '>' -> BOOLEAN_OP[t] | t= '>=' -> BOOLEAN_OP[t] | t= '<' -> BOOLEAN_OP[t] | t= '<=' -> BOOLEAN_OP[t] | t= '=' -> BOOLEAN_OP[t] | t= '!=' -> BOOLEAN_OP[t] | t= '=~' -> BOOLEAN_OP[t] | t= '!~' -> BOOLEAN_OP[t] );
    public final StencilParser.booleanOp_return booleanOp() throws RecognitionException {
        StencilParser.booleanOp_return retval = new StencilParser.booleanOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token t=null;

        Object t_tree=null;
        RewriteRuleTokenStream stream_79=new RewriteRuleTokenStream(adaptor,"token 79");
        RewriteRuleTokenStream stream_81=new RewriteRuleTokenStream(adaptor,"token 81");
        RewriteRuleTokenStream stream_75=new RewriteRuleTokenStream(adaptor,"token 75");
        RewriteRuleTokenStream stream_77=new RewriteRuleTokenStream(adaptor,"token 77");
        RewriteRuleTokenStream stream_78=new RewriteRuleTokenStream(adaptor,"token 78");
        RewriteRuleTokenStream stream_73=new RewriteRuleTokenStream(adaptor,"token 73");
        RewriteRuleTokenStream stream_82=new RewriteRuleTokenStream(adaptor,"token 82");
        RewriteRuleTokenStream stream_80=new RewriteRuleTokenStream(adaptor,"token 80");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:373:2: (t= '>' -> BOOLEAN_OP[t] | t= '>=' -> BOOLEAN_OP[t] | t= '<' -> BOOLEAN_OP[t] | t= '<=' -> BOOLEAN_OP[t] | t= '=' -> BOOLEAN_OP[t] | t= '!=' -> BOOLEAN_OP[t] | t= '=~' -> BOOLEAN_OP[t] | t= '!~' -> BOOLEAN_OP[t] )
            int alt50=8;
            switch ( input.LA(1) ) {
            case 73:
                {
                alt50=1;
                }
                break;
            case 77:
                {
                alt50=2;
                }
                break;
            case 78:
                {
                alt50=3;
                }
                break;
            case 79:
                {
                alt50=4;
                }
                break;
            case 75:
                {
                alt50=5;
                }
                break;
            case 80:
                {
                alt50=6;
                }
                break;
            case 81:
                {
                alt50=7;
                }
                break;
            case 82:
                {
                alt50=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:373:4: t= '>'
                    {
                    t=(Token)match(input,73,FOLLOW_73_in_booleanOp2689); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_73.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 373:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:374:4: t= '>='
                    {
                    t=(Token)match(input,77,FOLLOW_77_in_booleanOp2703); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_77.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 374:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:375:4: t= '<'
                    {
                    t=(Token)match(input,78,FOLLOW_78_in_booleanOp2716); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_78.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 375:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:376:4: t= '<='
                    {
                    t=(Token)match(input,79,FOLLOW_79_in_booleanOp2730); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_79.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 376:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:377:4: t= '='
                    {
                    t=(Token)match(input,75,FOLLOW_75_in_booleanOp2743); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_75.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 377:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:378:4: t= '!='
                    {
                    t=(Token)match(input,80,FOLLOW_80_in_booleanOp2757); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_80.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 378:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 7 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:379:4: t= '=~'
                    {
                    t=(Token)match(input,81,FOLLOW_81_in_booleanOp2770); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_81.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 379:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 8 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:380:4: t= '!~'
                    {
                    t=(Token)match(input,82,FOLLOW_82_in_booleanOp2783); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_82.add(t);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 380:12: -> BOOLEAN_OP[t]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BOOLEAN_OP, t));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "booleanOp"

    public static class passOp_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "passOp"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:383:1: passOp : ( YIELDS | GUIDE_YIELD );
    public final StencilParser.passOp_return passOp() throws RecognitionException {
        StencilParser.passOp_return retval = new StencilParser.passOp_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set209=null;

        Object set209_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:384:3: ( YIELDS | GUIDE_YIELD )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:
            {
            root_0 = (Object)adaptor.nil();

            set209=(Token)input.LT(1);
            if ( input.LA(1)==YIELDS||input.LA(1)==GUIDE_YIELD ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set209));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "passOp"

    public static class number_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "number"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:390:1: number : ( doubleNum | intNum );
    public final StencilParser.number_return number() throws RecognitionException {
        StencilParser.number_return retval = new StencilParser.number_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        StencilParser.doubleNum_return doubleNum210 = null;

        StencilParser.intNum_return intNum211 = null;



        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:390:8: ( doubleNum | intNum )
            int alt51=2;
            switch ( input.LA(1) ) {
            case NAMESPLIT:
                {
                alt51=1;
                }
                break;
            case DIGITS:
                {
                int LA51_2 = input.LA(2);

                if ( (LA51_2==NAMESPLIT) ) {
                    alt51=1;
                }
                else if ( (LA51_2==EOF||LA51_2==ALL||LA51_2==CANVAS||(LA51_2>=FILTER && LA51_2<=GLYPH)||(LA51_2>=LOCAL && LA51_2<=LEGEND)||(LA51_2>=PYTHON && LA51_2<=RETURN)||(LA51_2>=STREAM && LA51_2<=VIEW)||(LA51_2>=GROUP && LA51_2<=CLOSE_GROUP)||(LA51_2>=CLOSE_ARG && LA51_2<=RANGE)||(LA51_2>=SPLIT && LA51_2<=JOIN)||LA51_2==ID||LA51_2==73||LA51_2==75||(LA51_2>=77 && LA51_2<=82)) ) {
                    alt51=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 2, input);

                    throw nvae;
                }
                }
                break;
            case 83:
                {
                int LA51_3 = input.LA(2);

                if ( (LA51_3==DIGITS) ) {
                    int LA51_6 = input.LA(3);

                    if ( (LA51_6==NAMESPLIT) ) {
                        alt51=1;
                    }
                    else if ( (LA51_6==EOF||LA51_6==ALL||LA51_6==CANVAS||(LA51_6>=FILTER && LA51_6<=GLYPH)||(LA51_6>=LOCAL && LA51_6<=LEGEND)||(LA51_6>=PYTHON && LA51_6<=RETURN)||(LA51_6>=STREAM && LA51_6<=VIEW)||(LA51_6>=GROUP && LA51_6<=CLOSE_GROUP)||(LA51_6>=CLOSE_ARG && LA51_6<=RANGE)||(LA51_6>=SPLIT && LA51_6<=JOIN)||LA51_6==ID||LA51_6==73||LA51_6==75||(LA51_6>=77 && LA51_6<=82)) ) {
                        alt51=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 51, 6, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 3, input);

                    throw nvae;
                }
                }
                break;
            case 84:
                {
                int LA51_4 = input.LA(2);

                if ( (LA51_4==DIGITS) ) {
                    int LA51_6 = input.LA(3);

                    if ( (LA51_6==NAMESPLIT) ) {
                        alt51=1;
                    }
                    else if ( (LA51_6==EOF||LA51_6==ALL||LA51_6==CANVAS||(LA51_6>=FILTER && LA51_6<=GLYPH)||(LA51_6>=LOCAL && LA51_6<=LEGEND)||(LA51_6>=PYTHON && LA51_6<=RETURN)||(LA51_6>=STREAM && LA51_6<=VIEW)||(LA51_6>=GROUP && LA51_6<=CLOSE_GROUP)||(LA51_6>=CLOSE_ARG && LA51_6<=RANGE)||(LA51_6>=SPLIT && LA51_6<=JOIN)||LA51_6==ID||LA51_6==73||LA51_6==75||(LA51_6>=77 && LA51_6<=82)) ) {
                        alt51=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 51, 6, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 4, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:390:11: doubleNum
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_doubleNum_in_number2820);
                    doubleNum210=doubleNum();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, doubleNum210.getTree());

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:390:23: intNum
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_intNum_in_number2824);
                    intNum211=intNum();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, intNum211.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "number"

    public static class intNum_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "intNum"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:392:1: intNum : ( (n= '-' | p= '+' ) d= DIGITS -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text] ) | d= DIGITS -> ^( NUMBER[$d.text] ) );
    public final StencilParser.intNum_return intNum() throws RecognitionException {
        StencilParser.intNum_return retval = new StencilParser.intNum_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token n=null;
        Token p=null;
        Token d=null;

        Object n_tree=null;
        Object p_tree=null;
        Object d_tree=null;
        RewriteRuleTokenStream stream_83=new RewriteRuleTokenStream(adaptor,"token 83");
        RewriteRuleTokenStream stream_DIGITS=new RewriteRuleTokenStream(adaptor,"token DIGITS");
        RewriteRuleTokenStream stream_84=new RewriteRuleTokenStream(adaptor,"token 84");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:2: ( (n= '-' | p= '+' ) d= DIGITS -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text] ) | d= DIGITS -> ^( NUMBER[$d.text] ) )
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( ((LA53_0>=83 && LA53_0<=84)) ) {
                alt53=1;
            }
            else if ( (LA53_0==DIGITS) ) {
                alt53=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:4: (n= '-' | p= '+' ) d= DIGITS
                    {
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:4: (n= '-' | p= '+' )
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==83) ) {
                        alt52=1;
                    }
                    else if ( (LA52_0==84) ) {
                        alt52=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 52, 0, input);

                        throw nvae;
                    }
                    switch (alt52) {
                        case 1 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:5: n= '-'
                            {
                            n=(Token)match(input,83,FOLLOW_83_in_intNum2836); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_83.add(n);


                            }
                            break;
                        case 2 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:13: p= '+'
                            {
                            p=(Token)match(input,84,FOLLOW_84_in_intNum2842); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_84.add(p);


                            }
                            break;

                    }

                    d=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_intNum2847); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 393:29: -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:393:32: ^( NUMBER[p!=null?\"+\":\"-\" + $d.text] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, p!=null?"+":"-" + (d!=null?d.getText():null)), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:394:4: d= DIGITS
                    {
                    d=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_intNum2861); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 394:13: -> ^( NUMBER[$d.text] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:394:16: ^( NUMBER[$d.text] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, (d!=null?d.getText():null)), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "intNum"

    public static class doubleNum_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "doubleNum"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:396:1: doubleNum : ( '.' d2= DIGITS -> ^( NUMBER[\"0.\" + $d2.text] ) | d= DIGITS '.' d2= DIGITS -> ^( NUMBER[$d.text + \".\" + $d2.text] ) | (n= '-' | p= '+' ) d= DIGITS '.' d2= DIGITS -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text + \".\" + $d2.text] ) );
    public final StencilParser.doubleNum_return doubleNum() throws RecognitionException {
        StencilParser.doubleNum_return retval = new StencilParser.doubleNum_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token d2=null;
        Token d=null;
        Token n=null;
        Token p=null;
        Token char_literal212=null;
        Token char_literal213=null;
        Token char_literal214=null;

        Object d2_tree=null;
        Object d_tree=null;
        Object n_tree=null;
        Object p_tree=null;
        Object char_literal212_tree=null;
        Object char_literal213_tree=null;
        Object char_literal214_tree=null;
        RewriteRuleTokenStream stream_NAMESPLIT=new RewriteRuleTokenStream(adaptor,"token NAMESPLIT");
        RewriteRuleTokenStream stream_83=new RewriteRuleTokenStream(adaptor,"token 83");
        RewriteRuleTokenStream stream_DIGITS=new RewriteRuleTokenStream(adaptor,"token DIGITS");
        RewriteRuleTokenStream stream_84=new RewriteRuleTokenStream(adaptor,"token 84");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:397:2: ( '.' d2= DIGITS -> ^( NUMBER[\"0.\" + $d2.text] ) | d= DIGITS '.' d2= DIGITS -> ^( NUMBER[$d.text + \".\" + $d2.text] ) | (n= '-' | p= '+' ) d= DIGITS '.' d2= DIGITS -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text + \".\" + $d2.text] ) )
            int alt55=3;
            switch ( input.LA(1) ) {
            case NAMESPLIT:
                {
                alt55=1;
                }
                break;
            case DIGITS:
                {
                alt55=2;
                }
                break;
            case 83:
            case 84:
                {
                alt55=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:397:4: '.' d2= DIGITS
                    {
                    char_literal212=(Token)match(input,NAMESPLIT,FOLLOW_NAMESPLIT_in_doubleNum2877); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NAMESPLIT.add(char_literal212);

                    d2=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_doubleNum2881); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d2);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 397:18: -> ^( NUMBER[\"0.\" + $d2.text] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:397:21: ^( NUMBER[\"0.\" + $d2.text] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, "0." + (d2!=null?d2.getText():null)), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:398:4: d= DIGITS '.' d2= DIGITS
                    {
                    d=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_doubleNum2895); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d);

                    char_literal213=(Token)match(input,NAMESPLIT,FOLLOW_NAMESPLIT_in_doubleNum2897); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NAMESPLIT.add(char_literal213);

                    d2=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_doubleNum2901); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d2);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 398:27: -> ^( NUMBER[$d.text + \".\" + $d2.text] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:398:30: ^( NUMBER[$d.text + \".\" + $d2.text] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, (d!=null?d.getText():null) + "." + (d2!=null?d2.getText():null)), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:399:4: (n= '-' | p= '+' ) d= DIGITS '.' d2= DIGITS
                    {
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:399:4: (n= '-' | p= '+' )
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==83) ) {
                        alt54=1;
                    }
                    else if ( (LA54_0==84) ) {
                        alt54=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 0, input);

                        throw nvae;
                    }
                    switch (alt54) {
                        case 1 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:399:5: n= '-'
                            {
                            n=(Token)match(input,83,FOLLOW_83_in_doubleNum2916); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_83.add(n);


                            }
                            break;
                        case 2 :
                            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:399:13: p= '+'
                            {
                            p=(Token)match(input,84,FOLLOW_84_in_doubleNum2922); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_84.add(p);


                            }
                            break;

                    }

                    d=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_doubleNum2927); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d);

                    char_literal214=(Token)match(input,NAMESPLIT,FOLLOW_NAMESPLIT_in_doubleNum2929); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NAMESPLIT.add(char_literal214);

                    d2=(Token)match(input,DIGITS,FOLLOW_DIGITS_in_doubleNum2933); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DIGITS.add(d2);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 399:43: -> ^( NUMBER[p!=null?\"+\":\"-\" + $d.text + \".\" + $d2.text] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:399:46: ^( NUMBER[p!=null?\"+\":\"-\" + $d.text + \".\" + $d2.text] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, p!=null?"+":"-" + (d!=null?d.getText():null) + "." + (d2!=null?d2.getText():null)), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "doubleNum"

    // $ANTLR start synpred1_Stencil
    public final void synpred1_Stencil_fragment() throws RecognitionException {   
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:4: ( callChain SPLIT )
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:235:5: callChain SPLIT
        {
        pushFollow(FOLLOW_callChain_in_synpred1_Stencil1338);
        callChain();

        state._fsp--;
        if (state.failed) return ;
        match(input,SPLIT,FOLLOW_SPLIT_in_synpred1_Stencil1340); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_Stencil

    // $ANTLR start synpred2_Stencil
    public final void synpred2_Stencil_fragment() throws RecognitionException {   
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:4: ( PYTHON ARG )
        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/Stencil.g:277:5: PYTHON ARG
        {
        match(input,PYTHON,FOLLOW_PYTHON_in_synpred2_Stencil1685); if (state.failed) return ;
        match(input,ARG,FOLLOW_ARG_in_synpred2_Stencil1687); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_Stencil

    // Delegated rules

    public final boolean synpred2_Stencil() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_Stencil_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_Stencil() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Stencil_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA26 dfa26 = new DFA26(this);
    protected DFA27 dfa27 = new DFA27(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA43 dfa43 = new DFA43(this);
    static final String DFA26_eotS =
        "\16\uffff";
    static final String DFA26_eofS =
        "\16\uffff";
    static final String DFA26_minS =
        "\1\32\13\0\2\uffff";
    static final String DFA26_maxS =
        "\1\124\13\0\2\uffff";
    static final String DFA26_acceptS =
        "\14\uffff\1\1\1\2";
    static final String DFA26_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\12\3\uffff\1\11\20\uffff\1\13\1\uffff\1\2\4\uffff\1\4\12"+
            "\uffff\1\1\1\uffff\1\3\1\10\1\5\15\uffff\1\6\1\7",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "234:1: callGroup : ( ( callChain SPLIT )=> callChain ( SPLIT callChain )+ JOIN callChain -> ^( CALL_GROUP ( callChain )+ ) | callChain -> ^( CALL_GROUP callChain ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA26_1 = input.LA(1);

                         
                        int index26_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA26_2 = input.LA(1);

                         
                        int index26_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA26_3 = input.LA(1);

                         
                        int index26_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA26_4 = input.LA(1);

                         
                        int index26_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA26_5 = input.LA(1);

                         
                        int index26_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA26_6 = input.LA(1);

                         
                        int index26_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA26_7 = input.LA(1);

                         
                        int index26_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA26_8 = input.LA(1);

                         
                        int index26_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA26_9 = input.LA(1);

                         
                        int index26_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA26_10 = input.LA(1);

                         
                        int index26_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA26_11 = input.LA(1);

                         
                        int index26_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_Stencil()) ) {s = 12;}

                        else if ( (true) ) {s = 13;}

                         
                        input.seek(index26_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 26, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA27_eotS =
        "\132\uffff";
    static final String DFA27_eofS =
        "\1\uffff\1\2\130\uffff";
    static final String DFA27_minS =
        "\2\32\1\uffff\1\32\1\uffff\1\32\2\uffff\1\57\1\60\1\66\1\57\1\105"+
        "\1\60\2\105\2\60\1\32\1\67\1\71\1\105\1\62\2\105\1\61\1\60\1\105"+
        "\2\60\1\62\1\105\1\60\1\62\1\60\1\66\1\105\1\60\2\105\2\60\1\105"+
        "\1\62\1\105\1\61\1\60\1\105\1\62\2\105\1\60\1\105\2\60\1\62\1\60"+
        "\1\66\1\105\1\60\2\105\1\60\1\62\1\105\1\60\1\62\1\60\2\105\1\62"+
        "\2\105\1\60\1\105\1\60\1\62\1\105\1\60\1\62\1\105\1\60\1\62\1\60"+
        "\1\105\2\62\1\105\1\60\1\62";
    static final String DFA27_maxS =
        "\1\124\1\122\1\uffff\1\124\1\uffff\1\124\2\uffff\2\122\1\124\1\57"+
        "\1\105\1\122\2\105\2\122\1\124\1\74\1\75\1\105\1\66\2\105\1\124"+
        "\1\122\1\105\1\122\1\63\1\62\1\105\1\122\1\66\1\63\1\124\1\105\1"+
        "\66\2\105\1\63\1\122\1\105\1\62\1\105\1\124\1\122\1\105\1\66\2\105"+
        "\1\63\1\105\1\66\1\122\1\62\1\63\1\124\1\105\1\66\2\105\1\63\1\62"+
        "\1\105\1\63\1\66\1\63\2\105\1\66\2\105\1\63\1\105\1\66\1\62\1\105"+
        "\1\63\1\62\1\105\1\63\1\66\1\63\1\105\2\62\1\105\1\63\1\62";
    static final String DFA27_acceptS =
        "\2\uffff\1\1\1\uffff\1\4\1\uffff\1\3\1\2\122\uffff";
    static final String DFA27_specialS =
        "\132\uffff}>";
    static final String[] DFA27_transitionS = {
            "\1\2\3\uffff\1\2\20\uffff\1\3\1\uffff\1\2\4\uffff\1\2\12\uffff"+
            "\1\1\1\uffff\3\2\15\uffff\2\2",
            "\1\2\1\uffff\1\2\3\uffff\3\2\1\uffff\3\2\1\uffff\2\2\1\uffff"+
            "\2\2\2\uffff\1\5\1\2\1\4\1\uffff\1\2\1\uffff\1\4\10\uffff\2"+
            "\2\1\uffff\1\2\7\uffff\1\2\1\uffff\1\2\1\uffff\6\2",
            "",
            "\1\7\3\uffff\1\7\21\uffff\1\6\1\7\4\uffff\1\7\12\uffff\1\7"+
            "\1\uffff\3\7\15\uffff\2\7",
            "",
            "\1\11\3\uffff\1\21\20\uffff\2\2\1\12\4\uffff\1\14\12\uffff"+
            "\1\10\1\uffff\1\13\1\20\1\15\15\uffff\1\16\1\17",
            "",
            "",
            "\1\2\1\23\1\2\1\uffff\1\22\1\uffff\1\2\10\uffff\1\2\12\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\6\2",
            "\1\24\2\uffff\1\4\11\uffff\2\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\25\16\uffff\1\26\15\uffff\1\27\1\30",
            "\1\31",
            "\1\32",
            "\1\4\2\uffff\1\4\2\uffff\1\33\7\uffff\1\2\12\uffff\1\2\1\uffff"+
            "\1\2\1\uffff\6\2",
            "\1\34",
            "\1\34",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\4\3\uffff\1\4\22\uffff\1\4\4\uffff\1\4\12\uffff\1\35\1\uffff"+
            "\3\4\15\uffff\2\4",
            "\2\2\1\4\2\uffff\1\4",
            "\1\4\2\uffff\1\4\1\2",
            "\1\36",
            "\1\40\3\uffff\1\37",
            "\1\41",
            "\1\41",
            "\1\43\4\uffff\1\44\12\uffff\1\42\2\uffff\1\50\1\45\15\uffff"+
            "\1\46\1\47",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\51",
            "\1\4\2\uffff\1\4\2\uffff\1\52\7\uffff\1\2\12\uffff\1\2\1\uffff"+
            "\1\2\1\uffff\6\2",
            "\1\23\2\uffff\1\22",
            "\1\40",
            "\1\53",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\40\3\uffff\1\54",
            "\1\56\2\uffff\1\55",
            "\1\57\16\uffff\1\60\15\uffff\1\61\1\62",
            "\1\63",
            "\1\56\2\uffff\1\55\2\uffff\1\64",
            "\1\65",
            "\1\65",
            "\1\56\2\uffff\1\55",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\66",
            "\1\40",
            "\1\67",
            "\1\71\4\uffff\1\72\12\uffff\1\70\2\uffff\1\76\1\73\15\uffff"+
            "\1\74\1\75",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\77",
            "\1\101\3\uffff\1\100",
            "\1\102",
            "\1\102",
            "\1\56\2\uffff\1\55",
            "\1\103",
            "\1\56\2\uffff\1\55\2\uffff\1\104",
            "\1\4\2\uffff\1\4\12\uffff\1\2\12\uffff\1\2\1\uffff\1\2\1\uffff"+
            "\6\2",
            "\1\40",
            "\1\56\2\uffff\1\55",
            "\1\105\16\uffff\1\106\15\uffff\1\107\1\110",
            "\1\111",
            "\1\56\2\uffff\1\55\2\uffff\1\112",
            "\1\113",
            "\1\113",
            "\1\56\2\uffff\1\55",
            "\1\101",
            "\1\114",
            "\1\56\2\uffff\1\55",
            "\1\101\3\uffff\1\115",
            "\1\56\2\uffff\1\55",
            "\1\116",
            "\1\117",
            "\1\121\3\uffff\1\120",
            "\1\122",
            "\1\122",
            "\1\56\2\uffff\1\55",
            "\1\123",
            "\1\56\2\uffff\1\55\2\uffff\1\124",
            "\1\101",
            "\1\125",
            "\1\56\2\uffff\1\55",
            "\1\121",
            "\1\126",
            "\1\56\2\uffff\1\55",
            "\1\121\3\uffff\1\127",
            "\1\56\2\uffff\1\55",
            "\1\130",
            "\1\101",
            "\1\121",
            "\1\131",
            "\1\56\2\uffff\1\55",
            "\1\121"
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "242:1: callTarget : ( value -> ^( PACK value ) | valueList -> ^( PACK valueList ) | emptySet -> ^( PACK ) | f1= functionCall passOp f2= callTarget -> ^( $f1 passOp $f2) );";
        }
    }
    static final String DFA35_eotS =
        "\44\uffff";
    static final String DFA35_eofS =
        "\44\uffff";
    static final String DFA35_minS =
        "\1\57\1\32\1\uffff\1\105\1\62\2\105\1\64\1\uffff\1\62\1\101\1\62"+
        "\1\105\1\66\1\62\1\114\2\uffff\3\62\1\105\1\62\3\105\1\62\2\uffff"+
        "\1\62\1\105\3\62\1\105\1\62";
    static final String DFA35_maxS =
        "\1\67\1\124\1\uffff\1\105\1\66\2\105\1\64\1\uffff\1\113\1\101\1"+
        "\64\1\105\1\124\1\66\1\114\2\uffff\1\76\1\64\1\76\1\105\1\76\3\105"+
        "\1\76\2\uffff\1\76\1\105\1\76\1\64\1\76\1\105\1\76";
    static final String DFA35_acceptS =
        "\2\uffff\1\6\5\uffff\1\5\7\uffff\1\2\1\4\11\uffff\1\1\1\3\7\uffff";
    static final String DFA35_specialS =
        "\44\uffff}>";
    static final String[] DFA35_transitionS = {
            "\1\2\1\uffff\1\1\5\uffff\1\2",
            "\1\10\3\uffff\1\10\10\uffff\1\12\12\uffff\1\10\3\uffff\1\3"+
            "\12\uffff\1\11\1\uffff\2\10\1\4\6\uffff\1\7\6\uffff\1\5\1\6",
            "",
            "\1\13",
            "\2\10\1\15\1\uffff\1\14",
            "\1\16",
            "\1\16",
            "\1\17",
            "",
            "\2\20\12\uffff\1\21\14\uffff\1\10",
            "\1\22",
            "\2\10\1\15",
            "\1\23",
            "\1\25\16\uffff\1\26\6\uffff\1\24\6\uffff\1\27\1\30",
            "\2\10\1\15\1\uffff\1\31",
            "\1\32",
            "",
            "",
            "\2\20\12\uffff\1\21",
            "\2\10\1\15",
            "\2\33\12\uffff\1\34",
            "\1\35",
            "\2\33\2\uffff\1\36\7\uffff\1\34",
            "\1\37",
            "\1\37",
            "\1\40",
            "\2\33\12\uffff\1\34",
            "",
            "",
            "\2\33\12\uffff\1\34",
            "\1\41",
            "\2\33\2\uffff\1\42\7\uffff\1\34",
            "\2\10\1\15",
            "\2\33\12\uffff\1\34",
            "\1\43",
            "\2\33\12\uffff\1\34"
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "296:1: specializer[RuleOpts opts] : ( ARG range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range ^( SPLIT BASIC PRE ID[(String) null] ) sepArgList ) | ARG split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) split sepArgList ) | ARG range SPLIT split[false] sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG split[true] SPLIT range sepArgList CLOSE_ARG {...}? -> ^( SPECIALIZER range split sepArgList ) | ARG argList CLOSE_ARG {...}? -> ^( SPECIALIZER ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) ^( SPLIT BASIC PRE ID[(String) null] ) argList ) | -> ^( SPECIALIZER DEFAULT ) );";
        }
    }
    static final String DFA37_eotS =
        "\u0088\uffff";
    static final String DFA37_eofS =
        "\u0088\uffff";
    static final String DFA37_minS =
        "\1\32\1\uffff\1\57\1\105\1\62\2\105\3\62\1\uffff\1\61\1\62\1\105"+
        "\1\32\1\uffff\1\62\1\60\1\66\1\105\1\60\2\105\1\60\1\62\1\57\1\105"+
        "\1\62\2\105\3\62\1\uffff\1\105\1\61\1\62\1\105\1\62\2\105\1\60\1"+
        "\105\1\60\1\61\1\62\1\105\2\62\1\60\1\66\1\105\1\60\2\105\1\60\1"+
        "\62\1\105\1\60\1\62\1\60\1\105\1\60\1\66\1\105\1\60\2\105\1\60\1"+
        "\62\2\105\1\62\2\105\1\60\1\105\1\60\1\62\1\105\1\60\1\61\1\62\1"+
        "\105\1\62\2\105\1\60\1\105\1\60\2\62\1\105\1\60\1\62\1\60\1\105"+
        "\1\62\1\60\1\66\1\105\1\60\2\105\1\60\1\62\1\105\1\60\1\62\1\60"+
        "\1\105\1\62\1\105\1\60\1\105\1\62\2\105\1\60\1\105\1\60\1\62\1\105"+
        "\1\60\2\62\1\105\1\60\1\62\1\60\1\105\2\62\1\105\1\60\1\62";
    static final String DFA37_maxS =
        "\1\124\1\uffff\1\57\1\105\1\66\2\105\3\63\1\uffff\1\124\1\63\1\105"+
        "\1\124\1\uffff\1\66\1\63\1\124\1\105\1\66\2\105\2\63\1\57\1\105"+
        "\1\66\2\105\3\63\1\uffff\1\105\1\124\1\63\1\105\1\66\2\105\1\63"+
        "\1\105\1\66\1\124\1\63\1\105\1\66\2\63\1\124\1\105\1\66\2\105\1"+
        "\63\1\62\1\105\1\63\1\66\1\63\1\105\1\63\1\124\1\105\1\66\2\105"+
        "\2\63\2\105\1\66\2\105\1\63\1\105\1\66\1\62\1\105\1\63\1\124\1\63"+
        "\1\105\1\66\2\105\1\63\1\105\1\66\1\63\1\62\1\105\1\63\1\66\1\63"+
        "\1\105\1\62\1\63\1\124\1\105\1\66\2\105\1\63\1\62\1\105\1\63\1\66"+
        "\1\63\1\105\1\62\1\105\1\63\1\105\1\66\2\105\1\63\1\105\1\66\1\62"+
        "\1\105\1\63\2\62\1\105\1\63\1\66\1\63\1\105\2\62\1\105\1\63\1\62";
    static final String DFA37_acceptS =
        "\1\uffff\1\1\10\uffff\1\3\4\uffff\1\2\21\uffff\1\4\146\uffff";
    static final String DFA37_specialS =
        "\u0088\uffff}>";
    static final String[] DFA37_transitionS = {
            "\1\11\3\uffff\1\10\23\uffff\1\1\3\uffff\1\3\12\uffff\1\12\1"+
            "\uffff\1\2\1\7\1\4\15\uffff\1\5\1\6",
            "",
            "\1\13",
            "\1\14",
            "\1\17\1\16\2\uffff\1\15",
            "\1\20",
            "\1\20",
            "\1\17\1\16",
            "\1\17\1\16",
            "\1\17\1\16",
            "",
            "\1\22\4\uffff\1\23\12\uffff\1\21\2\uffff\1\27\1\24\15\uffff"+
            "\1\25\1\26",
            "\1\17\1\16",
            "\1\30",
            "\1\40\3\uffff\1\37\27\uffff\1\32\12\uffff\1\41\1\uffff\1\31"+
            "\1\36\1\33\15\uffff\1\34\1\35",
            "",
            "\1\17\1\16\2\uffff\1\42",
            "\1\44\2\uffff\1\43",
            "\1\45\16\uffff\1\46\15\uffff\1\47\1\50",
            "\1\51",
            "\1\44\2\uffff\1\43\2\uffff\1\52",
            "\1\53",
            "\1\53",
            "\1\44\2\uffff\1\43",
            "\1\17\1\16",
            "\1\54",
            "\1\55",
            "\1\17\1\16\2\uffff\1\56",
            "\1\57",
            "\1\57",
            "\1\17\1\16",
            "\1\17\1\16",
            "\1\17\1\16",
            "",
            "\1\60",
            "\1\62\4\uffff\1\63\12\uffff\1\61\2\uffff\1\67\1\64\15\uffff"+
            "\1\65\1\66",
            "\1\17\1\16",
            "\1\70",
            "\1\72\3\uffff\1\71",
            "\1\73",
            "\1\73",
            "\1\44\2\uffff\1\43",
            "\1\74",
            "\1\44\2\uffff\1\43\2\uffff\1\75",
            "\1\77\4\uffff\1\100\12\uffff\1\76\2\uffff\1\104\1\101\15\uffff"+
            "\1\102\1\103",
            "\1\17\1\16",
            "\1\105",
            "\1\17\1\16\2\uffff\1\106",
            "\1\17\1\16",
            "\1\44\2\uffff\1\43",
            "\1\107\16\uffff\1\110\15\uffff\1\111\1\112",
            "\1\113",
            "\1\44\2\uffff\1\43\2\uffff\1\114",
            "\1\115",
            "\1\115",
            "\1\44\2\uffff\1\43",
            "\1\72",
            "\1\116",
            "\1\44\2\uffff\1\43",
            "\1\72\3\uffff\1\117",
            "\1\44\2\uffff\1\43",
            "\1\120",
            "\1\122\2\uffff\1\121",
            "\1\123\16\uffff\1\124\15\uffff\1\125\1\126",
            "\1\127",
            "\1\122\2\uffff\1\121\2\uffff\1\130",
            "\1\131",
            "\1\131",
            "\1\122\2\uffff\1\121",
            "\1\17\1\16",
            "\1\132",
            "\1\133",
            "\1\135\3\uffff\1\134",
            "\1\136",
            "\1\136",
            "\1\44\2\uffff\1\43",
            "\1\137",
            "\1\44\2\uffff\1\43\2\uffff\1\140",
            "\1\72",
            "\1\141",
            "\1\44\2\uffff\1\43",
            "\1\143\4\uffff\1\144\12\uffff\1\142\2\uffff\1\150\1\145\15"+
            "\uffff\1\146\1\147",
            "\1\17\1\16",
            "\1\151",
            "\1\153\3\uffff\1\152",
            "\1\154",
            "\1\154",
            "\1\122\2\uffff\1\121",
            "\1\155",
            "\1\122\2\uffff\1\121\2\uffff\1\156",
            "\1\17\1\16",
            "\1\135",
            "\1\157",
            "\1\44\2\uffff\1\43",
            "\1\135\3\uffff\1\160",
            "\1\44\2\uffff\1\43",
            "\1\161",
            "\1\72",
            "\1\122\2\uffff\1\121",
            "\1\162\16\uffff\1\163\15\uffff\1\164\1\165",
            "\1\166",
            "\1\122\2\uffff\1\121\2\uffff\1\167",
            "\1\170",
            "\1\170",
            "\1\122\2\uffff\1\121",
            "\1\153",
            "\1\171",
            "\1\122\2\uffff\1\121",
            "\1\153\3\uffff\1\172",
            "\1\122\2\uffff\1\121",
            "\1\173",
            "\1\135",
            "\1\174",
            "\1\44\2\uffff\1\43",
            "\1\175",
            "\1\177\3\uffff\1\176",
            "\1\u0080",
            "\1\u0080",
            "\1\122\2\uffff\1\121",
            "\1\u0081",
            "\1\122\2\uffff\1\121\2\uffff\1\u0082",
            "\1\153",
            "\1\u0083",
            "\1\122\2\uffff\1\121",
            "\1\135",
            "\1\177",
            "\1\u0084",
            "\1\122\2\uffff\1\121",
            "\1\177\3\uffff\1\u0085",
            "\1\122\2\uffff\1\121",
            "\1\u0086",
            "\1\153",
            "\1\177",
            "\1\u0087",
            "\1\122\2\uffff\1\121",
            "\1\177"
    };

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "313:1: argList : ( -> ^( LIST[\"Values Arguments\"] ) ^( LIST[\"Map Arguments\"] ) | values -> values ^( LIST[\"Map Arguments\"] ) | mapList -> ^( LIST[\"Value Arguments\"] ) mapList | values SEPARATOR mapList );";
        }
    }
    static final String DFA43_eotS =
        "\17\uffff";
    static final String DFA43_eofS =
        "\17\uffff";
    static final String DFA43_minS =
        "\1\66\1\105\1\64\2\105\1\uffff\1\64\1\105\1\66\2\64\2\uffff\1\105"+
        "\1\64";
    static final String DFA43_maxS =
        "\1\124\1\105\1\66\2\105\1\uffff\1\64\1\105\1\124\1\66\1\64\2\uffff"+
        "\1\105\1\64";
    static final String DFA43_acceptS =
        "\5\uffff\1\3\5\uffff\1\2\1\1\2\uffff";
    static final String DFA43_specialS =
        "\17\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\1\16\uffff\1\2\6\uffff\1\5\6\uffff\1\3\1\4",
            "\1\6",
            "\1\10\1\uffff\1\7",
            "\1\11",
            "\1\11",
            "",
            "\1\10",
            "\1\12",
            "\1\14\16\uffff\1\14\6\uffff\1\13\6\uffff\2\14",
            "\1\10\1\uffff\1\15",
            "\1\10",
            "",
            "",
            "\1\16",
            "\1\10"
    };

    static final short[] DFA43_eot = DFA.unpackEncodedString(DFA43_eotS);
    static final short[] DFA43_eof = DFA.unpackEncodedString(DFA43_eofS);
    static final char[] DFA43_min = DFA.unpackEncodedStringToUnsignedChars(DFA43_minS);
    static final char[] DFA43_max = DFA.unpackEncodedStringToUnsignedChars(DFA43_maxS);
    static final short[] DFA43_accept = DFA.unpackEncodedString(DFA43_acceptS);
    static final short[] DFA43_special = DFA.unpackEncodedString(DFA43_specialS);
    static final short[][] DFA43_transition;

    static {
        int numStates = DFA43_transitionS.length;
        DFA43_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA43_transition[i] = DFA.unpackEncodedString(DFA43_transitionS[i]);
        }
    }

    class DFA43 extends DFA {

        public DFA43(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 43;
            this.eot = DFA43_eot;
            this.eof = DFA43_eof;
            this.min = DFA43_min;
            this.max = DFA43_max;
            this.accept = DFA43_accept;
            this.special = DFA43_special;
            this.transition = DFA43_transition;
        }
        public String getDescription() {
            return "341:1: range : ( number RANGE number -> ^( RANGE number number ) | number RANGE 'n' -> ^( RANGE number NUMBER[RANGE_END] ) | 'n' RANGE 'n' -> ^( RANGE NUMBER[RANGE_END] NUMBER[RANGE_END] ) );";
        }
    }
 

    public static final BitSet FOLLOW_imports_in_program518 = new BitSet(new long[]{0x000009E880000000L});
    public static final BitSet FOLLOW_externals_in_program521 = new BitSet(new long[]{0x000009E000000000L});
    public static final BitSet FOLLOW_order_in_program523 = new BitSet(new long[]{0x0000096000000002L});
    public static final BitSet FOLLOW_streamDef_in_program526 = new BitSet(new long[]{0x0000096000000002L});
    public static final BitSet FOLLOW_layerDef_in_program530 = new BitSet(new long[]{0x0000096000000002L});
    public static final BitSet FOLLOW_legendDef_in_program534 = new BitSet(new long[]{0x0000096000000002L});
    public static final BitSet FOLLOW_pythonDef_in_program538 = new BitSet(new long[]{0x0000096000000002L});
    public static final BitSet FOLLOW_IMPORT_in_imports597 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_imports601 = new BitSet(new long[]{0x0002200000000002L});
    public static final BitSet FOLLOW_ARG_in_imports604 = new BitSet(new long[]{0x0044000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_argList_in_imports608 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_imports610 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_AS_in_imports615 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_imports619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_order702 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_orderRef_in_order704 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_73_in_order707 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_orderRef_in_order709 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000200L});
    public static final BitSet FOLLOW_ID_in_orderRef740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_orderRef754 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_orderRef756 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SPLIT_in_orderRef759 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_orderRef761 = new BitSet(new long[]{0x4001000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_orderRef765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_externalStream_in_externals783 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_EXTERNAL_in_externalStream800 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STREAM_in_externalStream802 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_externalStream806 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_externalStream808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STREAM_in_streamDef831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_streamDef835 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_streamDef837 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_consumesBlock_in_streamDef842 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_LAYER_in_layerDef874 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_layerDef878 = new BitSet(new long[]{0x0002000200000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_implantationDef_in_layerDef880 = new BitSet(new long[]{0x0000000200000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_guidesBlock_in_layerDef882 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_consumesBlock_in_layerDef884 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_ARG_in_implantationDef917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_implantationDef921 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_implantationDef923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_guidesBlock947 = new BitSet(new long[]{0x0082000000000000L});
    public static final BitSet FOLLOW_specializer_in_guidesBlock949 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DEFINE_in_guidesBlock952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_guidesBlock954 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_consumesBlock988 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_consumesBlock992 = new BitSet(new long[]{0x0000921510000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_filterRule_in_consumesBlock994 = new BitSet(new long[]{0x0000921510000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_consumesBlock997 = new BitSet(new long[]{0x0000921510000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_FILTER_in_filterRule1034 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_rulePredicate_in_filterRule1036 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DEFINE_in_filterRule1038 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_filterRule1040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_rulePredicate1061 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_ALL_in_rulePredicate1063 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_rulePredicate1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_rulePredicate1085 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_rulePredicate1087 = new BitSet(new long[]{0x0000000000000000L,0x000000000007EA00L});
    public static final BitSet FOLLOW_booleanOp_in_rulePredicate1089 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_rulePredicate1091 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_rulePredicate1094 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_rulePredicate1096 = new BitSet(new long[]{0x0000000000000000L,0x000000000007EA00L});
    public static final BitSet FOLLOW_booleanOp_in_rulePredicate1098 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_rulePredicate1100 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_rulePredicate1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEGEND_in_legendDef1137 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_legendDef1141 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_legendDef1143 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_YIELDS_in_legendDef1146 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_legendDef1148 = new BitSet(new long[]{0x0000800004000000L});
    public static final BitSet FOLLOW_legendRule_in_legendDef1151 = new BitSet(new long[]{0x0000800004000002L});
    public static final BitSet FOLLOW_predicate_in_legendRule1189 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_GATE_in_legendRule1191 = new BitSet(new long[]{0x0000921510000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_legendRule1193 = new BitSet(new long[]{0x0000921510000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_predicate1222 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_ALL_in_predicate1225 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_predicate1227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_predicate1248 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_predicate1250 = new BitSet(new long[]{0x0000000000000000L,0x000000000007EA00L});
    public static final BitSet FOLLOW_booleanOp_in_predicate1252 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_predicate1254 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_predicate1257 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_predicate1259 = new BitSet(new long[]{0x0000000000000000L,0x000000000007EA00L});
    public static final BitSet FOLLOW_booleanOp_in_predicate1261 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_predicate1263 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_predicate1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_target_in_rule1299 = new BitSet(new long[]{0x0180000000000000L});
    public static final BitSet FOLLOW_DEFINE_in_rule1303 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_DYNAMIC_in_rule1307 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callGroup_in_rule1310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callChain_in_callGroup1344 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SPLIT_in_callGroup1347 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callChain_in_callGroup1349 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_JOIN_in_callGroup1353 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callChain_in_callGroup1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callChain_in_callGroup1371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callTarget_in_callChain1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_in_callTarget1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueList_in_callTarget1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptySet_in_callTarget1431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionCall_in_callTarget1444 = new BitSet(new long[]{0x1200000000000000L});
    public static final BitSet FOLLOW_passOp_in_callTarget1446 = new BitSet(new long[]{0x0042800044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_callTarget_in_callTarget1450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callName_in_functionCall1477 = new BitSet(new long[]{0x0002800000000000L});
    public static final BitSet FOLLOW_specializer_in_functionCall1480 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_valueList_in_functionCall1483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_callName1514 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_NAMESPACE_in_callName1516 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_callName1520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_callName1555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLYPH_in_target1593 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_target1602 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CANVAS_in_target1611 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCAL_in_target1620 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VIEW_in_target1629 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_target1638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PYTHON_in_pythonDef1692 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_ARG_in_pythonDef1694 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pythonDef1698 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_pythonDef1700 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pythonDef1704 = new BitSet(new long[]{0x0000400000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_pythonBlock_in_pythonDef1706 = new BitSet(new long[]{0x0000400000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_PYTHON_in_pythonDef1726 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pythonDef1730 = new BitSet(new long[]{0x0000400000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_pythonBlock_in_pythonDef1732 = new BitSet(new long[]{0x0000400000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_FACET_in_pythonBlock1758 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_pythonBlock1760 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_CODE_BLOCK_in_pythonBlock1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_pythonBlock1799 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_FACET_in_pythonBlock1801 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_pythonBlock1805 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_pythonBlock1807 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_YIELDS_in_pythonBlock1810 = new BitSet(new long[]{0x0000800000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_tuple_in_pythonBlock1812 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_CODE_BLOCK_in_pythonBlock1815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TAGGED_ID_in_annotation1890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specializer1908 = new BitSet(new long[]{0x0040000000000000L,0x0000000000181020L});
    public static final BitSet FOLLOW_range_in_specializer1910 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_sepArgList_in_specializer1912 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_specializer1914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specializer1944 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_split_in_specializer1946 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_sepArgList_in_specializer1949 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_specializer1951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specializer1980 = new BitSet(new long[]{0x0040000000000000L,0x0000000000181020L});
    public static final BitSet FOLLOW_range_in_specializer1982 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SPLIT_in_specializer1984 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_split_in_specializer1986 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_sepArgList_in_specializer1989 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_specializer1991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specializer2013 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_split_in_specializer2015 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SPLIT_in_specializer2018 = new BitSet(new long[]{0x0040000000000000L,0x0000000000181020L});
    public static final BitSet FOLLOW_range_in_specializer2020 = new BitSet(new long[]{0x000C000000000000L});
    public static final BitSet FOLLOW_sepArgList_in_specializer2022 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_specializer2024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_specializer2045 = new BitSet(new long[]{0x0044000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_argList_in_specializer2047 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_specializer2049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEPARATOR_in_sepArgList2102 = new BitSet(new long[]{0x0040000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_argList_in_sepArgList2105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_values_in_argList2146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapList_in_argList2160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_values_in_argList2174 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_argList2176 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_mapList_in_argList2179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_values2188 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_SEPARATOR_in_values2191 = new BitSet(new long[]{0x0040000044000000L,0x0000000000180038L});
    public static final BitSet FOLLOW_atom_in_values2193 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapList2214 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_SEPARATOR_in_mapList2217 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapList2219 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_ID_in_mapEntry2244 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_mapEntry2246 = new BitSet(new long[]{0x0040000044000000L,0x0000000000180038L});
    public static final BitSet FOLLOW_atom_in_mapEntry2250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptySet_in_tuple2270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_tuple2285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_tuple2300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_tuple2302 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_tuple2305 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_tuple2307 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_tuple2311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_emptySet2330 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_emptySet2333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_valueList2341 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_valueList2344 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_valueList2347 = new BitSet(new long[]{0x0042000044000000L,0x000000000018003AL});
    public static final BitSet FOLLOW_value_in_valueList2350 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_valueList2354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_range2366 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range2368 = new BitSet(new long[]{0x0040000000000000L,0x0000000000180020L});
    public static final BitSet FOLLOW_number_in_range2370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_range2387 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range2389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_range2391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_range2409 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range2411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_range2413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_split2438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_split2483 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_split2485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tupleRef_in_value2533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_value2538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sigil_in_atom2546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_atom2550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_atom2554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_atom2558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_atom2562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_tupleRef2573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_in_tupleRef2587 = new BitSet(new long[]{0x0040000000000000L,0x0000000000180020L});
    public static final BitSet FOLLOW_number_in_tupleRef2589 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_tupleRef2591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualifiedID2607 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_ARG_in_qualifiedID2610 = new BitSet(new long[]{0x0040000000000000L,0x0000000000180020L});
    public static final BitSet FOLLOW_number_in_qualifiedID2613 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_CLOSE_ARG_in_qualifiedID2615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TAGGED_ID_in_sigil2625 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_sValueList_in_sigil2627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_sValueList2645 = new BitSet(new long[]{0x0042000000000000L,0x0000000000180032L});
    public static final BitSet FOLLOW_sValue_in_sValueList2648 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_SEPARATOR_in_sValueList2651 = new BitSet(new long[]{0x0042000000000000L,0x0000000000180032L});
    public static final BitSet FOLLOW_sValue_in_sValueList2654 = new BitSet(new long[]{0x0009000000000000L});
    public static final BitSet FOLLOW_CLOSE_GROUP_in_sValueList2658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tupleRef_in_sValue2668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_sValue2672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_sValue2676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_booleanOp2689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_booleanOp2703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_booleanOp2716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_booleanOp2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_booleanOp2743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_booleanOp2757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_81_in_booleanOp2770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_82_in_booleanOp2783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_passOp0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_doubleNum_in_number2820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_intNum_in_number2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_intNum2836 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_84_in_intNum2842 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DIGITS_in_intNum2847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIGITS_in_intNum2861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMESPLIT_in_doubleNum2877 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DIGITS_in_doubleNum2881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIGITS_in_doubleNum2895 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_NAMESPLIT_in_doubleNum2897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DIGITS_in_doubleNum2901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_doubleNum2916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_84_in_doubleNum2922 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DIGITS_in_doubleNum2927 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_NAMESPLIT_in_doubleNum2929 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_DIGITS_in_doubleNum2933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_callChain_in_synpred1_Stencil1338 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_SPLIT_in_synpred1_Stencil1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PYTHON_in_synpred2_Stencil1685 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_ARG_in_synpred2_Stencil1687 = new BitSet(new long[]{0x0000000000000002L});

}