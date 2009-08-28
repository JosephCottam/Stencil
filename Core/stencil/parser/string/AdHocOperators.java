// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g 2009-08-28 15:23:00

	package stencil.parser.string;

	import stencil.adapters.Adapter;
	import stencil.display.DisplayLayer;
    import stencil.rules.ModuleCache;
    import stencil.rules.EncapsulationGenerator;
    import stencil.legend.StencilLegend;
    import stencil.legend.DynamicStencilLegend;
    import stencil.parser.tree.Legend;
    import stencil.parser.tree.Python;
    import stencil.parser.tree.Layer;
    import stencil.parser.tree.StencilTree;
    import stencil.legend.module.*;
    import stencil.legend.module.util.*;
	import stencil.legend.wrappers.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class AdHocOperators extends TreeFilter {
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


        public AdHocOperators(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public AdHocOperators(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return AdHocOperators.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g"; }


    	protected ModuleCache modules;
    	protected Adapter adapter;
    	EncapsulationGenerator encGenerator = new EncapsulationGenerator();
    	
    	public AdHocOperators(TreeNodeStream input, ModuleCache modules, Adapter adapter) {
    		super(input, new RecognizerSharedState());
    		assert modules != null : "Module cache must not be null.";
    		assert adapter != null : "Adapter must not be null.";
    		
    		this.modules = modules;
    		this.adapter = adapter;		
    	}

    	protected void makeOperator(Legend op) {
    		MutableModule adHoc = modules.getAdHoc();
    		DynamicStencilLegend operator = new SyntheticLegend(adHoc.getModuleData().getName(), op);
    		
    		adHoc.addOperator(operator);
    	}	
    	
    	protected void makePython(Python p) {
    		encGenerator.generate(p, modules.getAdHoc());
    	}
    	
    	protected void makeLayer(Layer l) {
    		MutableModule adHoc = modules.getAdHoc();
    		DisplayLayer dl =adapter.makeLayer(l); 
    		l.setDisplayLayer(dl);
    		
    		DisplayLegend legend = new DisplayLegend(dl);
    		adHoc.addOperator(legend, legend.getLegendData(adHoc.getName()));
    	}

    	



    // $ANTLR start "topdown"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:95:1: topdown : ( ^(op= LEGEND ( . )* ) | ^(py= PYTHON ( . )* ) | ^(lay= LAYER ( . )* ) );
    public final void topdown() throws RecognitionException {
        StencilTree op=null;
        StencilTree py=null;
        StencilTree lay=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:96:2: ( ^(op= LEGEND ( . )* ) | ^(py= PYTHON ( . )* ) | ^(lay= LAYER ( . )* ) )
            int alt4=3;
            switch ( input.LA(1) ) {
            case LEGEND:
                {
                alt4=1;
                }
                break;
            case PYTHON:
                {
                alt4=2;
                }
                break;
            case LAYER:
                {
                alt4=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:96:4: ^(op= LEGEND ( . )* )
                    {
                    op=(StencilTree)match(input,LEGEND,FOLLOW_LEGEND_in_topdown64); if (state.failed) return ;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return ;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:96:16: ( . )*
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
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:96:16: .
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
                      makeOperator((Legend) op);
                    }

                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:97:4: ^(py= PYTHON ( . )* )
                    {
                    py=(StencilTree)match(input,PYTHON,FOLLOW_PYTHON_in_topdown78); if (state.failed) return ;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return ;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:97:16: ( . )*
                        loop2:
                        do {
                            int alt2=2;
                            int LA2_0 = input.LA(1);

                            if ( ((LA2_0>=ANNOTATION && LA2_0<=84)) ) {
                                alt2=1;
                            }
                            else if ( (LA2_0==UP) ) {
                                alt2=2;
                            }


                            switch (alt2) {
                        	case 1 :
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:97:16: .
                        	    {
                        	    matchAny(input); if (state.failed) return ;

                        	    }
                        	    break;

                        	default :
                        	    break loop2;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return ;
                    }
                    if ( state.backtracking==1 ) {
                      makePython((Python) py);
                    }

                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:98:4: ^(lay= LAYER ( . )* )
                    {
                    lay=(StencilTree)match(input,LAYER,FOLLOW_LAYER_in_topdown92); if (state.failed) return ;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return ;
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:98:16: ( . )*
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
                        	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/AdHocOperators.g:98:16: .
                        	    {
                        	    matchAny(input); if (state.failed) return ;

                        	    }
                        	    break;

                        	default :
                        	    break loop3;
                            }
                        } while (true);


                        match(input, Token.UP, null); if (state.failed) return ;
                    }
                    if ( state.backtracking==1 ) {
                      makeLayer((Layer) lay);
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
    // $ANTLR end "topdown"

    // Delegated rules


 

    public static final BitSet FOLLOW_LEGEND_in_topdown64 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_PYTHON_in_topdown78 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LAYER_in_topdown92 = new BitSet(new long[]{0x0000000000000004L});

}