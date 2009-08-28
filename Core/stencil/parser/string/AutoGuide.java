// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g 2009-08-28 15:23:15

	/** Performs the built of automatic guide mark generation.
	 *
	 * Precondition: To operate properly, this pass must be run after ensuring 
	 * guide operators exist and after annotating function calls with their
	 * associated call targets.
	 *  
	 *
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality	  
	 **/

	package stencil.parser.string;
	
	import java.util.Map;
	import java.util.HashMap;
	import stencil.parser.tree.*;
	import stencil.legend.module.*;
	import stencil.legend.module.util.*;
	import stencil.rules.ModuleCache;
	import org.antlr.runtime.tree.*;
	import stencil.rules.ModuleCache;
	import stencil.util.MultiPartName;
	import static stencil.parser.ParserConstants.GUIDE_BLOCK_TAG;
	import stencil.legend.module.LegendData.OpType;
	


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class AutoGuide extends TreeRewriteSequence {
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


        public AutoGuide(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public AutoGuide(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return AutoGuide.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g"; }


    	protected Map<String, CommonTree> attDefs = new HashMap<String, CommonTree>();
    	protected ModuleCache modules;

    	protected String layerName; 	//Which layer is currently being processed
    	protected String guideName;		//Which guide is currently being processed
        protected boolean inGuide;		//Is the rule part of a guide?
        
        public static class AutoGuideException extends RuntimeException {public AutoGuideException(String message) {super(message);}}
        
    	public AutoGuide(TreeNodeStream input, ModuleCache modules) {
    		super(input, new RecognizerSharedState());
    		this.modules = modules;
    	}
    		
    	public Object transform(Object t) {
    		t = build(t);
    		t = transfer(t);
    		t = trim(t);
    		t = rename(t);
    		return t;
    	}	
    	
    	//Trim each mapping chain to its last categorical operator
    	private Object trim(Object t) {
    		layerName = null;
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return trimGuide(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    return downup(t, down, up);
    	}

    	
    	//Build a mapping from the layer/attribute names to mapping trees
    	public Object build(Object t) {
    		layerName = null;
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return buildMappings(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    return downup(t, down, up);
        }
        
        //Transfer appropriate mapping tree to the guide clause
        public Object transfer(Object t) {
        	layerName = null;
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return transferMappings(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    return downup(t, down, up);
        }
        
        //Rename functions to use the guide channel
        public Object rename(Object t) {
    		layerName = null;
    		inGuide = false;
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return renameMappingsDown(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return renameMappingsUp(); }};
       	    return downup(t, down, up);		
        }
        
        
        private String key(Tree layer, Tree attribute) {return key(layer.getText(), attribute.getText());}
        private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
        private String key(String layer, String attribute) {
        	MultiPartName att = new MultiPartName(attribute);
        	
        	return layer + ":" + att.getName();	//Trim to just the attribute name
        }
        
        private boolean inGuide() {return guideName != null;}
        private String guideName(String name) {return new MultiPartName(name).modSuffix(GUIDE_BLOCK_TAG).toString();}       


    	//EnsureGuideOp guarantees that one categorize exists...we move on from there!
    	private Tree trimCall(CallTarget tree) {
        	if (tree instanceof Pack) {return null;}

        	try {
        		Tree trimmed = trimCall(((Function) tree).getCall());
            	if (trimmed != null) {return trimmed;}
        	} catch (Exception e) {
        		throw new RuntimeException("Error trimming: " + tree.getText(),e);
        	}

        	if (isCategorize((Function) tree)) {return tree;}
        	else {return null;}
    	}
        	
    	
    	private boolean isCategorize(Function f) {
       		MultiPartName name = new MultiPartName(f.getName());
       		Module m = modules.findModuleForLegend(name.prefixedName()).module;
       		try {
       			OpType opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacetData(name.getFacet()).getFacetType();;
       			return (opType == OpType.CATEGORIZE);
       		} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.");}

    	}



    public static class buildMappings_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "buildMappings"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:163:1: buildMappings : ( ^(name= LAYER ( . )* ) | ^( RULE ^( GLYPH ^( TUPLE_PROTOTYPE field= . ) ) group= . . ) );
    public final AutoGuide.buildMappings_return buildMappings() throws RecognitionException {
        AutoGuide.buildMappings_return retval = new AutoGuide.buildMappings_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree wildcard1=null;
        CommonTree RULE2=null;
        CommonTree GLYPH3=null;
        CommonTree TUPLE_PROTOTYPE4=null;
        CommonTree wildcard5=null;
        CommonTree field=null;
        CommonTree group=null;

        CommonTree name_tree=null;
        CommonTree wildcard1_tree=null;
        CommonTree RULE2_tree=null;
        CommonTree GLYPH3_tree=null;
        CommonTree TUPLE_PROTOTYPE4_tree=null;
        CommonTree wildcard5_tree=null;
        CommonTree field_tree=null;
        CommonTree group_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:164:2: ( ^(name= LAYER ( . )* ) | ^( RULE ^( GLYPH ^( TUPLE_PROTOTYPE field= . ) ) group= . . ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==LAYER) ) {
                alt2=1;
            }
            else if ( (LA2_0==RULE) ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:164:4: ^(name= LAYER ( . )* )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    name=(CommonTree)match(input,LAYER,FOLLOW_LAYER_in_buildMappings73); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = name;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return retval;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:164:17: ( . )*
                        loop1:
                        do {
                            int alt1=2;
                            int LA1_0 = input.LA(1);

                            if ( ((LA1_0>=ANNOTATION && LA1_0<=84)) ) {
                                alt1=1;
                            }
                            else if ( (LA1_0==UP) ) {
                                alt1=2;
                            }


                            switch (alt1) {
                        	case 1 :
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:164:17: .
                        	    {
                        	    _last = (CommonTree)input.LT(1);
                        	    wildcard1=(CommonTree)input.LT(1);
                        	    matchAny(input); if (state.failed) return retval;
                        	     
                        	    if ( state.backtracking==1 )
                        	    if ( _first_1==null ) _first_1 = wildcard1;

                        	    if ( state.backtracking==1 ) {
                        	    retval.tree = (CommonTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                        	    }
                        	    break;

                        	default :
                        	    break loop1;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return retval;
                    }_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      layerName = (name!=null?name.getText():null);
                    }

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:165:4: ^( RULE ^( GLYPH ^( TUPLE_PROTOTYPE field= . ) ) group= . . )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    RULE2=(CommonTree)match(input,RULE,FOLLOW_RULE_in_buildMappings85); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = RULE2;
                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_2 = _last;
                    CommonTree _first_2 = null;
                    _last = (CommonTree)input.LT(1);
                    GLYPH3=(CommonTree)match(input,GLYPH,FOLLOW_GLYPH_in_buildMappings88); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = GLYPH3;
                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_3 = _last;
                    CommonTree _first_3 = null;
                    _last = (CommonTree)input.LT(1);
                    TUPLE_PROTOTYPE4=(CommonTree)match(input,TUPLE_PROTOTYPE,FOLLOW_TUPLE_PROTOTYPE_in_buildMappings91); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_2==null ) _first_2 = TUPLE_PROTOTYPE4;
                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    _last = (CommonTree)input.LT(1);
                    field=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_3==null ) _first_3 = field;

                    match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_3;
                    }


                    match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_2;
                    }

                    _last = (CommonTree)input.LT(1);
                    group=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = group;
                    _last = (CommonTree)input.LT(1);
                    wildcard5=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = wildcard5;

                    match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      attDefs.put(key(layerName, field), group);
                    }

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
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
        return retval;
    }
    // $ANTLR end "buildMappings"

    public static class transferMappings_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "transferMappings"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:168:1: transferMappings : ( ^(name= LAYER ( . )* ) | ^( GUIDE type= ID args= . field= ID ) -> ^( GUIDE $type $args $field) );
    public final AutoGuide.transferMappings_return transferMappings() throws RecognitionException {
        AutoGuide.transferMappings_return retval = new AutoGuide.transferMappings_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree type=null;
        CommonTree field=null;
        CommonTree wildcard6=null;
        CommonTree GUIDE7=null;
        CommonTree args=null;

        CommonTree name_tree=null;
        CommonTree type_tree=null;
        CommonTree field_tree=null;
        CommonTree wildcard6_tree=null;
        CommonTree GUIDE7_tree=null;
        CommonTree args_tree=null;
        RewriteRuleNodeStream stream_GUIDE=new RewriteRuleNodeStream(adaptor,"token GUIDE");
        RewriteRuleNodeStream stream_ID=new RewriteRuleNodeStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:169:3: ( ^(name= LAYER ( . )* ) | ^( GUIDE type= ID args= . field= ID ) -> ^( GUIDE $type $args $field) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==LAYER) ) {
                alt4=1;
            }
            else if ( (LA4_0==GUIDE) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:169:5: ^(name= LAYER ( . )* )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    name=(CommonTree)match(input,LAYER,FOLLOW_LAYER_in_transferMappings122); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = name;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return retval;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:169:18: ( . )*
                        loop3:
                        do {
                            int alt3=2;
                            int LA3_0 = input.LA(1);

                            if ( ((LA3_0>=ANNOTATION && LA3_0<=84)) ) {
                                alt3=1;
                            }
                            else if ( (LA3_0==UP) ) {
                                alt3=2;
                            }


                            switch (alt3) {
                        	case 1 :
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:169:18: .
                        	    {
                        	    _last = (CommonTree)input.LT(1);
                        	    wildcard6=(CommonTree)input.LT(1);
                        	    matchAny(input); if (state.failed) return retval;
                        	     
                        	    if ( state.backtracking==1 )
                        	    if ( _first_1==null ) _first_1 = wildcard6;

                        	    if ( state.backtracking==1 ) {
                        	    retval.tree = (CommonTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                        	    }
                        	    break;

                        	default :
                        	    break loop3;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return retval;
                    }_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      layerName = (name!=null?name.getText():null);
                    }

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:170:5: ^( GUIDE type= ID args= . field= ID )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    GUIDE7=(CommonTree)match(input,GUIDE,FOLLOW_GUIDE_in_transferMappings135); if (state.failed) return retval; 
                    if ( state.backtracking==1 ) stream_GUIDE.add(GUIDE7);


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = GUIDE7;
                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    _last = (CommonTree)input.LT(1);
                    type=(CommonTree)match(input,ID,FOLLOW_ID_in_transferMappings139); if (state.failed) return retval; 
                    if ( state.backtracking==1 ) stream_ID.add(type);

                    _last = (CommonTree)input.LT(1);
                    args=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = args;
                    _last = (CommonTree)input.LT(1);
                    field=(CommonTree)match(input,ID,FOLLOW_ID_in_transferMappings147); if (state.failed) return retval; 
                    if ( state.backtracking==1 ) stream_ID.add(field);


                    match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      if (!attDefs.containsKey(key(layerName,field))) {throw new AutoGuideException("Guide requested for unavailable glyph attribute " + key(layerName, field));}
                    }


                    // AST REWRITE
                    // elements: GUIDE, field, args, type
                    // token labels: type, field
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: args
                    if ( state.backtracking==1 ) {
                    retval.tree = root_0;
                    RewriteRuleNodeStream stream_type=new RewriteRuleNodeStream(adaptor,"token type",type);
                    RewriteRuleNodeStream stream_field=new RewriteRuleNodeStream(adaptor,"token field",field);
                    RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"wildcard args",args);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 172:4: -> ^( GUIDE $type $args $field)
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:172:7: ^( GUIDE $type $args $field)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot(stream_GUIDE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_type.nextNode());
                        adaptor.addChild(root_1, stream_args.nextTree());
                        adaptor.addChild(root_1, stream_field.nextNode());
                        adaptor.addChild(root_1, adaptor.dupTree(attDefs.get(key(layerName,field))));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                    input.replaceChildren(adaptor.getParent(retval.start),
                                          adaptor.getChildIndex(retval.start),
                                          adaptor.getChildIndex(_last),
                                          retval.tree);}
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
        return retval;
    }
    // $ANTLR end "transferMappings"

    public static class trimGuide_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "trimGuide"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:176:1: trimGuide : ^( GUIDE . . . trimCallGroup ) ;
    public final AutoGuide.trimGuide_return trimGuide() throws RecognitionException {
        AutoGuide.trimGuide_return retval = new AutoGuide.trimGuide_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree GUIDE8=null;
        CommonTree wildcard9=null;
        CommonTree wildcard10=null;
        CommonTree wildcard11=null;
        AutoGuide.trimCallGroup_return trimCallGroup12 = null;


        CommonTree GUIDE8_tree=null;
        CommonTree wildcard9_tree=null;
        CommonTree wildcard10_tree=null;
        CommonTree wildcard11_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:176:11: ( ^( GUIDE . . . trimCallGroup ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:176:13: ^( GUIDE . . . trimCallGroup )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            GUIDE8=(CommonTree)match(input,GUIDE,FOLLOW_GUIDE_in_trimGuide185); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = GUIDE8;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            wildcard9=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard9;
            _last = (CommonTree)input.LT(1);
            wildcard10=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard10;
            _last = (CommonTree)input.LT(1);
            wildcard11=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard11;
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_trimCallGroup_in_trimGuide193);
            trimCallGroup12=trimCallGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==1 ) 
             
            if ( _first_1==null ) _first_1 = trimCallGroup12.tree;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }


            if ( state.backtracking==1 ) {
            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
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
    // $ANTLR end "trimGuide"

    public static class trimCallGroup_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "trimCallGroup"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:177:1: trimCallGroup : ^( CALL_GROUP ^( CALL_CHAIN call= . ) ) -> ^( CALL_GROUP ^( CALL_CHAIN ) ) ;
    public final AutoGuide.trimCallGroup_return trimCallGroup() throws RecognitionException {
        AutoGuide.trimCallGroup_return retval = new AutoGuide.trimCallGroup_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree CALL_GROUP13=null;
        CommonTree CALL_CHAIN14=null;
        CommonTree call=null;

        CommonTree CALL_GROUP13_tree=null;
        CommonTree CALL_CHAIN14_tree=null;
        CommonTree call_tree=null;
        RewriteRuleNodeStream stream_CALL_GROUP=new RewriteRuleNodeStream(adaptor,"token CALL_GROUP");
        RewriteRuleNodeStream stream_CALL_CHAIN=new RewriteRuleNodeStream(adaptor,"token CALL_CHAIN");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:177:14: ( ^( CALL_GROUP ^( CALL_CHAIN call= . ) ) -> ^( CALL_GROUP ^( CALL_CHAIN ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:177:16: ^( CALL_GROUP ^( CALL_CHAIN call= . ) )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            CALL_GROUP13=(CommonTree)match(input,CALL_GROUP,FOLLOW_CALL_GROUP_in_trimCallGroup201); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_CALL_GROUP.add(CALL_GROUP13);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = CALL_GROUP13;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_2 = _last;
            CommonTree _first_2 = null;
            _last = (CommonTree)input.LT(1);
            CALL_CHAIN14=(CommonTree)match(input,CALL_CHAIN,FOLLOW_CALL_CHAIN_in_trimCallGroup204); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_CALL_CHAIN.add(CALL_CHAIN14);


            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = CALL_CHAIN14;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            call=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_2==null ) _first_2 = call;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_2;
            }


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: CALL_GROUP, CALL_CHAIN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 177:51: -> ^( CALL_GROUP ^( CALL_CHAIN ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:177:54: ^( CALL_GROUP ^( CALL_CHAIN ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_CALL_GROUP.nextNode(), root_1);

                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:177:67: ^( CALL_CHAIN )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot(stream_CALL_CHAIN.nextNode(), root_2);

                adaptor.addChild(root_2, trimCall((CallTarget) call));

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            input.replaceChildren(adaptor.getParent(retval.start),
                                  adaptor.getChildIndex(retval.start),
                                  adaptor.getChildIndex(_last),
                                  retval.tree);}
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
    // $ANTLR end "trimCallGroup"

    public static class renameMappingsDown_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "renameMappingsDown"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:182:1: renameMappingsDown : ( ^(name= LAYER ( . )* ) | ^( GUIDE . . field= ID . ) | renameGuideMapping );
    public final AutoGuide.renameMappingsDown_return renameMappingsDown() throws RecognitionException {
        AutoGuide.renameMappingsDown_return retval = new AutoGuide.renameMappingsDown_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree field=null;
        CommonTree wildcard15=null;
        CommonTree GUIDE16=null;
        CommonTree wildcard17=null;
        CommonTree wildcard18=null;
        CommonTree wildcard19=null;
        AutoGuide.renameGuideMapping_return renameGuideMapping20 = null;


        CommonTree name_tree=null;
        CommonTree field_tree=null;
        CommonTree wildcard15_tree=null;
        CommonTree GUIDE16_tree=null;
        CommonTree wildcard17_tree=null;
        CommonTree wildcard18_tree=null;
        CommonTree wildcard19_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:183:3: ( ^(name= LAYER ( . )* ) | ^( GUIDE . . field= ID . ) | renameGuideMapping )
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LAYER) ) {
                alt6=1;
            }
            else if ( (LA6_0==GUIDE) ) {
                alt6=2;
            }
            else if ( (LA6_0==FUNCTION) && ((inGuide()))) {
                alt6=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:183:5: ^(name= LAYER ( . )* )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    name=(CommonTree)match(input,LAYER,FOLLOW_LAYER_in_renameMappingsDown238); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = name;
                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return retval;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:183:18: ( . )*
                        loop5:
                        do {
                            int alt5=2;
                            int LA5_0 = input.LA(1);

                            if ( ((LA5_0>=ANNOTATION && LA5_0<=84)) ) {
                                alt5=1;
                            }
                            else if ( (LA5_0==UP) ) {
                                alt5=2;
                            }


                            switch (alt5) {
                        	case 1 :
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:183:18: .
                        	    {
                        	    _last = (CommonTree)input.LT(1);
                        	    wildcard15=(CommonTree)input.LT(1);
                        	    matchAny(input); if (state.failed) return retval;
                        	     
                        	    if ( state.backtracking==1 )
                        	    if ( _first_1==null ) _first_1 = wildcard15;

                        	    if ( state.backtracking==1 ) {
                        	    retval.tree = (CommonTree)_first_0;
                        	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                        	    }
                        	    break;

                        	default :
                        	    break loop5;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return retval;
                    }_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      layerName = (name!=null?name.getText():null);
                    }

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:184:5: ^( GUIDE . . field= ID . )
                    {
                    _last = (CommonTree)input.LT(1);
                    {
                    CommonTree _save_last_1 = _last;
                    CommonTree _first_1 = null;
                    _last = (CommonTree)input.LT(1);
                    GUIDE16=(CommonTree)match(input,GUIDE,FOLLOW_GUIDE_in_renameMappingsDown251); if (state.failed) return retval;


                    if ( state.backtracking==1 )
                    if ( _first_0==null ) _first_0 = GUIDE16;
                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    _last = (CommonTree)input.LT(1);
                    wildcard17=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = wildcard17;
                    _last = (CommonTree)input.LT(1);
                    wildcard18=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = wildcard18;
                    _last = (CommonTree)input.LT(1);
                    field=(CommonTree)match(input,ID,FOLLOW_ID_in_renameMappingsDown259); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = field;
                    _last = (CommonTree)input.LT(1);
                    wildcard19=(CommonTree)input.LT(1);
                    matchAny(input); if (state.failed) return retval;
                     
                    if ( state.backtracking==1 )
                    if ( _first_1==null ) _first_1 = wildcard19;

                    match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
                    }

                    if ( state.backtracking==1 ) {
                      guideName = (field!=null?field.getText():null);
                    }

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:185:5: renameGuideMapping
                    {
                    _last = (CommonTree)input.LT(1);
                    pushFollow(FOLLOW_renameGuideMapping_in_renameMappingsDown270);
                    renameGuideMapping20=renameGuideMapping();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==1 ) 
                     
                    if ( _first_0==null ) _first_0 = renameGuideMapping20.tree;

                    if ( state.backtracking==1 ) {
                    retval.tree = (CommonTree)_first_0;
                    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
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
        return retval;
    }
    // $ANTLR end "renameMappingsDown"

    public static class renameGuideMapping_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "renameGuideMapping"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:187:1: renameGuideMapping : {...}? => ^(f= FUNCTION spec= . args= . style= . call= . ) -> ^( FUNCTION[guideName($f.text)] $spec $args $style $call) ;
    public final AutoGuide.renameGuideMapping_return renameGuideMapping() throws RecognitionException {
        AutoGuide.renameGuideMapping_return retval = new AutoGuide.renameGuideMapping_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f=null;
        CommonTree spec=null;
        CommonTree args=null;
        CommonTree style=null;
        CommonTree call=null;

        CommonTree f_tree=null;
        CommonTree spec_tree=null;
        CommonTree args_tree=null;
        CommonTree style_tree=null;
        CommonTree call_tree=null;
        RewriteRuleNodeStream stream_FUNCTION=new RewriteRuleNodeStream(adaptor,"token FUNCTION");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:189:3: ({...}? => ^(f= FUNCTION spec= . args= . style= . call= . ) -> ^( FUNCTION[guideName($f.text)] $spec $args $style $call) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:189:5: {...}? => ^(f= FUNCTION spec= . args= . style= . call= . )
            {
            if ( !((inGuide())) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "renameGuideMapping", "inGuide()");
            }
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            f=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_renameGuideMapping294); if (state.failed) return retval; 
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
            style=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = style;
            _last = (CommonTree)input.LT(1);
            call=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = call;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: FUNCTION, call, style, args, spec
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: call, args, spec, style
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_call=new RewriteRuleSubtreeStream(adaptor,"wildcard call",call);
            RewriteRuleSubtreeStream stream_args=new RewriteRuleSubtreeStream(adaptor,"wildcard args",args);
            RewriteRuleSubtreeStream stream_spec=new RewriteRuleSubtreeStream(adaptor,"wildcard spec",spec);
            RewriteRuleSubtreeStream stream_style=new RewriteRuleSubtreeStream(adaptor,"wildcard style",style);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 189:64: -> ^( FUNCTION[guideName($f.text)] $spec $args $style $call)
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:190:4: ^( FUNCTION[guideName($f.text)] $spec $args $style $call)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, guideName((f!=null?f.getText():null))), root_1);

                adaptor.addChild(root_1, stream_spec.nextTree());
                adaptor.addChild(root_1, stream_args.nextTree());
                adaptor.addChild(root_1, stream_style.nextTree());
                adaptor.addChild(root_1, stream_call.nextTree());

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
    // $ANTLR end "renameGuideMapping"

    public static class renameMappingsUp_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "renameMappingsUp"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:194:1: renameMappingsUp : ^( GUIDE ( . )* ) ;
    public final AutoGuide.renameMappingsUp_return renameMappingsUp() throws RecognitionException {
        AutoGuide.renameMappingsUp_return retval = new AutoGuide.renameMappingsUp_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree GUIDE21=null;
        CommonTree wildcard22=null;

        CommonTree GUIDE21_tree=null;
        CommonTree wildcard22_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:195:3: ( ^( GUIDE ( . )* ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:195:5: ^( GUIDE ( . )* )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            GUIDE21=(CommonTree)match(input,GUIDE,FOLLOW_GUIDE_in_renameMappingsUp347); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = GUIDE21;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return retval;
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:195:13: ( . )*
                loop7:
                do {
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( ((LA7_0>=ANNOTATION && LA7_0<=84)) ) {
                        alt7=1;
                    }
                    else if ( (LA7_0==UP) ) {
                        alt7=2;
                    }


                    switch (alt7) {
                	case 1 :
                	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AutoGuide.g:195:13: .
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    wildcard22=(CommonTree)input.LT(1);
                	    matchAny(input); if (state.failed) return retval;
                	     
                	    if ( state.backtracking==1 )
                	    if ( _first_1==null ) _first_1 = wildcard22;

                	    if ( state.backtracking==1 ) {
                	    retval.tree = (CommonTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                	    }
                	    break;

                	default :
                	    break loop7;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return retval;
            }_last = _save_last_1;
            }

            if ( state.backtracking==1 ) {
              guideName = null;
            }

            if ( state.backtracking==1 ) {
            retval.tree = (CommonTree)_first_0;
            if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
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
    // $ANTLR end "renameMappingsUp"

    // Delegated rules


 

    public static final BitSet FOLLOW_LAYER_in_buildMappings73 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_in_buildMappings85 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GLYPH_in_buildMappings88 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TUPLE_PROTOTYPE_in_buildMappings91 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LAYER_in_transferMappings122 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GUIDE_in_transferMappings135 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_transferMappings139 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_ID_in_transferMappings147 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GUIDE_in_trimGuide185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_trimCallGroup_in_trimGuide193 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_GROUP_in_trimCallGroup201 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CALL_CHAIN_in_trimCallGroup204 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LAYER_in_renameMappingsDown238 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GUIDE_in_renameMappingsDown251 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_renameMappingsDown259 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_renameGuideMapping_in_renameMappingsDown270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_renameGuideMapping294 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GUIDE_in_renameMappingsUp347 = new BitSet(new long[]{0x0000000000000004L});

}