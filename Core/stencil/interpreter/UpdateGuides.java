// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g 2009-08-28 15:23:28

	package stencil.interpreter;
	
	import java.util.Arrays;
	
	import stencil.util.AutoguidePair;
	import stencil.parser.tree.*;	
	import stencil.util.MultiPartName;
	import stencil.display.*;
	import stencil.rules.ModuleCache;
	import stencil.legend.module.*;


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
public class UpdateGuides extends TreeFilter {
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


        public UpdateGuides(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public UpdateGuides(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return UpdateGuides.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g"; }


    	private String layerName; //What layer is currently being worked on
    	private String attribute; //What attribute is currently being considered
    	private StencilPanel panel; //Panel to take elements from
    	
    	private ModuleCache cache;
    		
    	public void updateGuides(StencilPanel panel) {
    		this.panel = panel;
    		layerName = null;
    		attribute = null;
    		
    		downup(panel.getProgram());
    	}
    	
    	//TODO: Remove when all tuple references are positional
    	public void setModuleCache(ModuleCache c) {this.cache = c;}

    	/**Update an actual guide on the current layer using the passed panel.*/
        private void update(List<Object[]> categories, List<Object[]> results) {
        	try {
           		List<AutoguidePair> pairs = zip(categories, results);
           		DisplayLayer l = panel.getLayer(layerName);
           		DisplayGuide g = l.getGuide(attribute);
           		g.setElements(pairs);
        	} catch (Exception e) {
        		throw new RuntimeException(String.format("Error creating guide for attribute %1$s", attribute), e);
        	}
       	}
    	
    	/**Turn a pair of lists into a list of AutoguidePairs.*/
        private List<AutoguidePair> zip(List<? extends Object[]> categories, List<? extends Object[]> results) {
    		assert categories.size() == results.size() : "Category and result lists must be of the same length";
    		AutoguidePair[] pairs = new AutoguidePair[categories.size()]; 
    		
    		for (int i=0; i < categories.size(); i++) {
       			pairs[i] = new AutoguidePair<Object, Object>(categories.get(i), results.get(i));
    		}
    		return Arrays.asList(pairs);	
    	}	
    	
    	private final void setAttribute(String att) {attribute = att;}
    	
    	private final void setLayer(String layer) {layerName = layer;}
    	
    	private final List<String> getPrototype(Function f) {
    		MultiPartName name = new MultiPartName(f.getName());
    		Specializer spec = f.getSpecializer();
    		try {
       			Module m = cache.findModuleForLegend(name.prefixedName()).module;
       			LegendData ld = m.getOperatorData(name.getName(), spec);
       			FacetData fd = ld.getFacetData("Query");//TODO: This is not always query...we need to add guide facet data
       			assert fd.tupleFields() != null : "Unexpected null prototype tuple.";
       			return fd.tupleFields();
       		} catch (Exception e) {throw new RuntimeException("Error Specailizing", e);}
    	}
    	
    	private final List<Object[]> invokeGuide(Function f, List<Object[]> vals, List<String> prototype) {
    		return f.getOperator().guide(f.getArguments(), vals, prototype);
    	}
    	
      	private final List<Object[]> packGuide(Pack p, List<Object[]> vals, List<String> prototype) {
    		Object[][] results = new Object[vals.size()][];
    		
    		int i=0;
    		for (Object[] val: vals) {
    			results[i] = new Object[p.getArguments().size()];
    			int j=0;
    			Value arg = p.getArguments().get(j); //TODO: Really need to handle the case where chain is setting more than one value
       			if (arg instanceof TupleRef) {results[i][j] = val[prototype.indexOf(((TupleRef) arg).getChild(0).getText())];} //HACK: This is horrible!  Assumes the tuple ref is a name...
    			else {results[i][j] = arg.getValue();}
       			i++;
    		}
    		return Arrays.asList(results);
    	}



    // $ANTLR start "topdown"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:130:1: topdown : ^(l= LAYER . ^( LIST ( guide )* ) . ) ;
    public final void topdown() throws RecognitionException {
        StencilTree l=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:130:8: ( ^(l= LAYER . ^( LIST ( guide )* ) . ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:130:10: ^(l= LAYER . ^( LIST ( guide )* ) . )
            {
            l=(StencilTree)match(input,LAYER,FOLLOW_LAYER_in_topdown60); if (state.failed) return ;

            if ( state.backtracking==1 ) {
              setLayer((l!=null?l.getText():null));
            }

            match(input, Token.DOWN, null); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            match(input,LIST,FOLLOW_LIST_in_topdown67); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:130:50: ( guide )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==GUIDE) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:130:50: guide
                	    {
                	    pushFollow(FOLLOW_guide_in_topdown69);
                	    guide();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop1;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return ;
            }
            matchAny(input); if (state.failed) return ;

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


    // $ANTLR start "guide"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:131:1: guide : ^( GUIDE ID . att= ID callGroup ) ;
    public final void guide() throws RecognitionException {
        StencilTree att=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:131:6: ( ^( GUIDE ID . att= ID callGroup ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:131:8: ^( GUIDE ID . att= ID callGroup )
            {
            match(input,GUIDE,FOLLOW_GUIDE_in_guide81); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ID,FOLLOW_ID_in_guide83); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            att=(StencilTree)match(input,ID,FOLLOW_ID_in_guide89); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              setAttribute((att!=null?att.getText():null));
            }
            pushFollow(FOLLOW_callGroup_in_guide93);
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
    // $ANTLR end "guide"


    // $ANTLR start "callGroup"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:133:1: callGroup : ^( CALL_GROUP callChain ) ;
    public final void callGroup() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:133:10: ( ^( CALL_GROUP callChain ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:133:12: ^( CALL_GROUP callChain )
            {
            match(input,CALL_GROUP,FOLLOW_CALL_GROUP_in_callGroup103); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_callChain_in_callGroup105);
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
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:134:1: callChain : ^( CALL_CHAIN categorize ) ;
    public final void callChain() throws RecognitionException {
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:134:10: ( ^( CALL_CHAIN categorize ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:134:12: ^( CALL_CHAIN categorize )
            {
            match(input,CALL_CHAIN,FOLLOW_CALL_CHAIN_in_callChain113); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            pushFollow(FOLLOW_categorize_in_callChain115);
            categorize();

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


    // $ANTLR start "categorize"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:135:1: categorize : ^(f= FUNCTION . . . target[cats, cats, getPrototype((Function) f)] ) ;
    public final void categorize() throws RecognitionException {
        StencilTree f=null;

        List<Object[]> cats = null;
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:137:2: ( ^(f= FUNCTION . . . target[cats, cats, getPrototype((Function) f)] ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:137:4: ^(f= FUNCTION . . . target[cats, cats, getPrototype((Function) f)] )
            {
            f=(StencilTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_categorize134); if (state.failed) return ;

            if ( state.backtracking==1 ) {
              cats = invokeGuide((Function) f, null, null);
            }

            match(input, Token.DOWN, null); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            matchAny(input); if (state.failed) return ;
            pushFollow(FOLLOW_target_in_categorize144);
            target(cats, cats, getPrototype((Function) f));

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
    // $ANTLR end "categorize"


    // $ANTLR start "target"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:139:1: target[List<Object[]> cats, List<Object[]> vals, List<String> prototype] : ( ^(f= FUNCTION . . . target[cats, invokeGuide((Function) f, vals, prototype), getPrototype((Function) f)] ) | ^(p= PACK . ) );
    public final void target(List<Object[]> cats, List<Object[]> vals, List<String> prototype) throws RecognitionException {
        StencilTree f=null;
        StencilTree p=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:140:2: ( ^(f= FUNCTION . . . target[cats, invokeGuide((Function) f, vals, prototype), getPrototype((Function) f)] ) | ^(p= PACK . ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==FUNCTION) ) {
                alt2=1;
            }
            else if ( (LA2_0==PACK) ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:140:4: ^(f= FUNCTION . . . target[cats, invokeGuide((Function) f, vals, prototype), getPrototype((Function) f)] )
                    {
                    f=(StencilTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_target162); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;
                    pushFollow(FOLLOW_target_in_target170);
                    target(cats, invokeGuide((Function) f, vals, prototype), getPrototype((Function) f));

                    state._fsp--;
                    if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/interpreter/UpdateGuides.g:141:4: ^(p= PACK . )
                    {
                    p=(StencilTree)match(input,PACK,FOLLOW_PACK_in_target180); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    matchAny(input); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      update(cats, packGuide((Pack) p, vals, prototype));
                    }

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


 

    public static final BitSet FOLLOW_LAYER_in_topdown60 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LIST_in_topdown67 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_guide_in_topdown69 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_GUIDE_in_guide81 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_guide83 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_ID_in_guide89 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_callGroup_in_guide93 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_GROUP_in_callGroup103 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_callChain_in_callGroup105 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CALL_CHAIN_in_callChain113 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_categorize_in_callChain115 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_categorize134 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_target_in_categorize144 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FUNCTION_in_target162 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_target_in_target170 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PACK_in_target180 = new BitSet(new long[]{0x0000000000000004L});

}