// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g 2009-08-05 10:52:01

	package stencil.legend.module.util;
	
	import java.io.*;
	import stencil.parser.string.ParseStencil;	
	import stencil.legend.module.ModuleData;
	import static stencil.util.Tuples.stripQuotes;	


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

@SuppressWarnings("all")
public class ModuleDataParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CLASS", "DEFAULTS", "DESC", "MODULE_DATA", "OPERATOR", "FACET", "FACETS", "OPT", "OPTS", "NAME", "REV", "TARGET", "OPEN", "CLOSE", "TERM", "TERM_OPEN", "DEF", "VAL", "CDATA", "ID", "WS", "COMMENT"
    };
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


        public ModuleDataParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ModuleDataParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return ModuleDataParser.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g"; }


    	public static final String REVISION = "\"2\"";
    	
    	protected Tree defaultFacets;
    	protected Tree defaultOpts;
    		
    	/**Parse the contents of the passed reader.**/
    	public static ModuleData parse(BufferedReader reader) throws Exception {
        String line = reader.readLine();
        StringBuilder source = new StringBuilder();
        while (line !=null) {source.append(line); line = reader.readLine();}

      
    		ANTLRStringStream input = new ANTLRStringStream(source.toString());
        
    		ModuleDataLexer lexer = new ModuleDataLexer(input);
    		CommonTokenStream tokens = new CommonTokenStream(lexer);

    		ModuleDataParser parser = new ModuleDataParser(tokens);
    		parser.setTreeAdaptor(new MDTreeAdapter());
    		ModuleData t= (ModuleData) parser.moduleData().getTree();
    		return t;
    	}
    	
    	
    	//TODO: Remove when all references to this are removed.  Resolution is by absolute file name only...BAD!!!!
    	public static ModuleData parse(String filename) throws Exception {
    		StringBuilder source = new StringBuilder();
    		BufferedReader file = new BufferedReader(new FileReader(filename));
    		return parse(file);
    	}

    	protected void setDefaultFacets(Tree defaultFacets) {this.defaultFacets=defaultFacets;}
    	protected Tree getDefaultFacets() {return (Tree) adaptor.dupTree(defaultFacets);}
    	
    	protected void setDefaultOperatorOpts(Tree defaultOpts) {this.defaultOpts = defaultOpts;}
    	
    	//Get the default operator options, but splice in the given child node to the options as well.
    	protected Tree getDefaultOperatorOpts(Tree... children) {
    		Tree t = (Tree) adaptor.dupTree(defaultOpts);
            if (children != null) {
            	for (Tree child: children) {adaptor.addChild(t, child);}
            }		
    		return t;
    	}

    	protected void mismatch(IntStream input, int type, BitSet follow) 
    		throws RecognitionException 
    	{
    		throw new MismatchedTokenException(type, input);
    	}
    	
    	public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) 
    		throws RecognitionException
    	{
    		throw e;	
    	}



    public static class moduleData_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "moduleData"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:134:1: moduleData : OPEN MODULE_DATA r= rev n= name CLOSE clss description defaults ( operator )+ TERM_OPEN MODULE_DATA CLOSE -> ^( MODULE_DATA[$n.name] clss defaults description ( operator )+ ) ;
    public final ModuleDataParser.moduleData_return moduleData() throws RecognitionException {
        ModuleDataParser.moduleData_return retval = new ModuleDataParser.moduleData_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN1=null;
        Token MODULE_DATA2=null;
        Token CLOSE3=null;
        Token TERM_OPEN8=null;
        Token MODULE_DATA9=null;
        Token CLOSE10=null;
        ModuleDataParser.rev_return r = null;

        ModuleDataParser.name_return n = null;

        ModuleDataParser.clss_return clss4 = null;

        ModuleDataParser.description_return description5 = null;

        ModuleDataParser.defaults_return defaults6 = null;

        ModuleDataParser.operator_return operator7 = null;


        CommonTree OPEN1_tree=null;
        CommonTree MODULE_DATA2_tree=null;
        CommonTree CLOSE3_tree=null;
        CommonTree TERM_OPEN8_tree=null;
        CommonTree MODULE_DATA9_tree=null;
        CommonTree CLOSE10_tree=null;
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleTokenStream stream_TERM_OPEN=new RewriteRuleTokenStream(adaptor,"token TERM_OPEN");
        RewriteRuleTokenStream stream_MODULE_DATA=new RewriteRuleTokenStream(adaptor,"token MODULE_DATA");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleSubtreeStream stream_description=new RewriteRuleSubtreeStream(adaptor,"rule description");
        RewriteRuleSubtreeStream stream_clss=new RewriteRuleSubtreeStream(adaptor,"rule clss");
        RewriteRuleSubtreeStream stream_defaults=new RewriteRuleSubtreeStream(adaptor,"rule defaults");
        RewriteRuleSubtreeStream stream_operator=new RewriteRuleSubtreeStream(adaptor,"rule operator");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_rev=new RewriteRuleSubtreeStream(adaptor,"rule rev");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:135:2: ( OPEN MODULE_DATA r= rev n= name CLOSE clss description defaults ( operator )+ TERM_OPEN MODULE_DATA CLOSE -> ^( MODULE_DATA[$n.name] clss defaults description ( operator )+ ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:135:4: OPEN MODULE_DATA r= rev n= name CLOSE clss description defaults ( operator )+ TERM_OPEN MODULE_DATA CLOSE
            {
            OPEN1=(Token)match(input,OPEN,FOLLOW_OPEN_in_moduleData209);  
            stream_OPEN.add(OPEN1);

            MODULE_DATA2=(Token)match(input,MODULE_DATA,FOLLOW_MODULE_DATA_in_moduleData211);  
            stream_MODULE_DATA.add(MODULE_DATA2);

            pushFollow(FOLLOW_rev_in_moduleData215);
            r=rev();

            state._fsp--;

            stream_rev.add(r.getTree());
            pushFollow(FOLLOW_name_in_moduleData219);
            n=name();

            state._fsp--;

            stream_name.add(n.getTree());
            CLOSE3=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_moduleData221);  
            stream_CLOSE.add(CLOSE3);

            if (!r.ver.equals(REVISION)) {
            		String message = "Revision number not recognized. Found "+ r.ver + ", expected " + REVISION + ".";
            		throw new RuntimeException(message);}
            	  
            pushFollow(FOLLOW_clss_in_moduleData233);
            clss4=clss();

            state._fsp--;

            stream_clss.add(clss4.getTree());
            pushFollow(FOLLOW_description_in_moduleData235);
            description5=description();

            state._fsp--;

            stream_description.add(description5.getTree());
            pushFollow(FOLLOW_defaults_in_moduleData237);
            defaults6=defaults();

            state._fsp--;

            stream_defaults.add(defaults6.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:140:30: ( operator )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==OPEN) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:140:30: operator
            	    {
            	    pushFollow(FOLLOW_operator_in_moduleData239);
            	    operator7=operator();

            	    state._fsp--;

            	    stream_operator.add(operator7.getTree());

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

            TERM_OPEN8=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_moduleData242);  
            stream_TERM_OPEN.add(TERM_OPEN8);

            MODULE_DATA9=(Token)match(input,MODULE_DATA,FOLLOW_MODULE_DATA_in_moduleData244);  
            stream_MODULE_DATA.add(MODULE_DATA9);

            CLOSE10=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_moduleData246);  
            stream_CLOSE.add(CLOSE10);



            // AST REWRITE
            // elements: defaults, clss, operator, description, MODULE_DATA
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 141:3: -> ^( MODULE_DATA[$n.name] clss defaults description ( operator )+ )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:141:6: ^( MODULE_DATA[$n.name] clss defaults description ( operator )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(MODULE_DATA, (n!=null?n.name:null)), root_1);

                adaptor.addChild(root_1, stream_clss.nextTree());
                adaptor.addChild(root_1, stream_defaults.nextTree());
                adaptor.addChild(root_1, stream_description.nextTree());
                if ( !(stream_operator.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_operator.hasNext() ) {
                    adaptor.addChild(root_1, stream_operator.nextTree());

                }
                stream_operator.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "moduleData"

    public static class rev_return extends ParserRuleReturnScope {
        public String ver;
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rev"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:144:1: rev returns [String ver] : REV DEF v= VAL ;
    public final ModuleDataParser.rev_return rev() throws RecognitionException {
        ModuleDataParser.rev_return retval = new ModuleDataParser.rev_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token v=null;
        Token REV11=null;
        Token DEF12=null;

        CommonTree v_tree=null;
        CommonTree REV11_tree=null;
        CommonTree DEF12_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:144:24: ( REV DEF v= VAL )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:144:26: REV DEF v= VAL
            {
            root_0 = (CommonTree)adaptor.nil();

            REV11=(Token)match(input,REV,FOLLOW_REV_in_rev276); 
            REV11_tree = (CommonTree)adaptor.create(REV11);
            adaptor.addChild(root_0, REV11_tree);

            DEF12=(Token)match(input,DEF,FOLLOW_DEF_in_rev278); 
            DEF12_tree = (CommonTree)adaptor.create(DEF12);
            adaptor.addChild(root_0, DEF12_tree);

            v=(Token)match(input,VAL,FOLLOW_VAL_in_rev282); 
            v_tree = (CommonTree)adaptor.create(v);
            adaptor.addChild(root_0, v_tree);

            retval.ver = (v!=null?v.getText():null);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rev"

    public static class clss_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "clss"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:146:1: clss : OPEN CLASS n= name TERM -> CLASS[$n.name] ;
    public final ModuleDataParser.clss_return clss() throws RecognitionException {
        ModuleDataParser.clss_return retval = new ModuleDataParser.clss_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN13=null;
        Token CLASS14=null;
        Token TERM15=null;
        ModuleDataParser.name_return n = null;


        CommonTree OPEN13_tree=null;
        CommonTree CLASS14_tree=null;
        CommonTree TERM15_tree=null;
        RewriteRuleTokenStream stream_CLASS=new RewriteRuleTokenStream(adaptor,"token CLASS");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_TERM=new RewriteRuleTokenStream(adaptor,"token TERM");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:146:5: ( OPEN CLASS n= name TERM -> CLASS[$n.name] )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:146:7: OPEN CLASS n= name TERM
            {
            OPEN13=(Token)match(input,OPEN,FOLLOW_OPEN_in_clss291);  
            stream_OPEN.add(OPEN13);

            CLASS14=(Token)match(input,CLASS,FOLLOW_CLASS_in_clss293);  
            stream_CLASS.add(CLASS14);

            pushFollow(FOLLOW_name_in_clss297);
            n=name();

            state._fsp--;

            stream_name.add(n.getTree());
            TERM15=(Token)match(input,TERM,FOLLOW_TERM_in_clss299);  
            stream_TERM.add(TERM15);



            // AST REWRITE
            // elements: CLASS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 146:30: -> CLASS[$n.name]
            {
                adaptor.addChild(root_0, (CommonTree)adaptor.create(CLASS, (n!=null?n.name:null)));

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "clss"

    public static class description_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "description"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:148:1: description : ( OPEN DESC CLOSE d= CDATA TERM_OPEN DESC CLOSE -> DESC[$d.text] | -> DESC[\"\"] );
    public final ModuleDataParser.description_return description() throws RecognitionException {
        ModuleDataParser.description_return retval = new ModuleDataParser.description_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token d=null;
        Token OPEN16=null;
        Token DESC17=null;
        Token CLOSE18=null;
        Token TERM_OPEN19=null;
        Token DESC20=null;
        Token CLOSE21=null;

        CommonTree d_tree=null;
        CommonTree OPEN16_tree=null;
        CommonTree DESC17_tree=null;
        CommonTree CLOSE18_tree=null;
        CommonTree TERM_OPEN19_tree=null;
        CommonTree DESC20_tree=null;
        CommonTree CLOSE21_tree=null;
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleTokenStream stream_TERM_OPEN=new RewriteRuleTokenStream(adaptor,"token TERM_OPEN");
        RewriteRuleTokenStream stream_DESC=new RewriteRuleTokenStream(adaptor,"token DESC");
        RewriteRuleTokenStream stream_CDATA=new RewriteRuleTokenStream(adaptor,"token CDATA");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:148:12: ( OPEN DESC CLOSE d= CDATA TERM_OPEN DESC CLOSE -> DESC[$d.text] | -> DESC[\"\"] )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPEN) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==DESC) ) {
                    alt2=1;
                }
                else if ( (LA2_1==DEFAULTS) ) {
                    alt2=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:148:14: OPEN DESC CLOSE d= CDATA TERM_OPEN DESC CLOSE
                    {
                    OPEN16=(Token)match(input,OPEN,FOLLOW_OPEN_in_description311);  
                    stream_OPEN.add(OPEN16);

                    DESC17=(Token)match(input,DESC,FOLLOW_DESC_in_description313);  
                    stream_DESC.add(DESC17);

                    CLOSE18=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_description315);  
                    stream_CLOSE.add(CLOSE18);

                    d=(Token)match(input,CDATA,FOLLOW_CDATA_in_description319);  
                    stream_CDATA.add(d);

                    TERM_OPEN19=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_description321);  
                    stream_TERM_OPEN.add(TERM_OPEN19);

                    DESC20=(Token)match(input,DESC,FOLLOW_DESC_in_description323);  
                    stream_DESC.add(DESC20);

                    CLOSE21=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_description325);  
                    stream_CLOSE.add(CLOSE21);



                    // AST REWRITE
                    // elements: DESC
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 148:59: -> DESC[$d.text]
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(DESC, (d!=null?d.getText():null)));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:149:6: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 149:6: -> DESC[\"\"]
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(DESC, ""));

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "description"

    public static class defaults_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "defaults"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:151:1: defaults : OPEN DEFAULTS CLOSE o= operatorOpts f= facets TERM_OPEN DEFAULTS CLOSE ;
    public final ModuleDataParser.defaults_return defaults() throws RecognitionException {
        ModuleDataParser.defaults_return retval = new ModuleDataParser.defaults_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN22=null;
        Token DEFAULTS23=null;
        Token CLOSE24=null;
        Token TERM_OPEN25=null;
        Token DEFAULTS26=null;
        Token CLOSE27=null;
        ModuleDataParser.operatorOpts_return o = null;

        ModuleDataParser.facets_return f = null;


        CommonTree OPEN22_tree=null;
        CommonTree DEFAULTS23_tree=null;
        CommonTree CLOSE24_tree=null;
        CommonTree TERM_OPEN25_tree=null;
        CommonTree DEFAULTS26_tree=null;
        CommonTree CLOSE27_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:151:9: ( OPEN DEFAULTS CLOSE o= operatorOpts f= facets TERM_OPEN DEFAULTS CLOSE )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:151:11: OPEN DEFAULTS CLOSE o= operatorOpts f= facets TERM_OPEN DEFAULTS CLOSE
            {
            root_0 = (CommonTree)adaptor.nil();

            OPEN22=(Token)match(input,OPEN,FOLLOW_OPEN_in_defaults347); 
            DEFAULTS23=(Token)match(input,DEFAULTS,FOLLOW_DEFAULTS_in_defaults350); 
            DEFAULTS23_tree = (CommonTree)adaptor.create(DEFAULTS23);
            root_0 = (CommonTree)adaptor.becomeRoot(DEFAULTS23_tree, root_0);

            CLOSE24=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_defaults353); 
            pushFollow(FOLLOW_operatorOpts_in_defaults358);
            o=operatorOpts();

            state._fsp--;

            adaptor.addChild(root_0, o.getTree());
            pushFollow(FOLLOW_facets_in_defaults362);
            f=facets();

            state._fsp--;

            adaptor.addChild(root_0, f.getTree());
            TERM_OPEN25=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_defaults364); 
            DEFAULTS26=(Token)match(input,DEFAULTS,FOLLOW_DEFAULTS_in_defaults367); 
            CLOSE27=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_defaults370); 
            setDefaultOperatorOpts(o.tree); setDefaultFacets(f.tree);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "defaults"

    public static class operator_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operator"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:154:1: operator : ( OPEN OPERATOR n= name t= target[$n.name] TERM -> ^( OPERATOR[$n.name] ) | OPEN OPERATOR n= name t= target[$n.name] CLOSE ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( FACETS ( facet )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ^( FACETS ( facet )+ ) ) );
    public final ModuleDataParser.operator_return operator() throws RecognitionException {
        ModuleDataParser.operator_return retval = new ModuleDataParser.operator_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN28=null;
        Token OPERATOR29=null;
        Token TERM30=null;
        Token OPEN31=null;
        Token OPERATOR32=null;
        Token CLOSE33=null;
        Token TERM_OPEN35=null;
        Token OPERATOR36=null;
        Token CLOSE37=null;
        Token OPEN38=null;
        Token OPERATOR39=null;
        Token CLOSE41=null;
        Token TERM_OPEN43=null;
        Token OPERATOR44=null;
        Token CLOSE45=null;
        Token OPEN46=null;
        Token OPERATOR47=null;
        Token CLOSE49=null;
        Token TERM_OPEN52=null;
        Token OPERATOR53=null;
        Token CLOSE54=null;
        ModuleDataParser.name_return n = null;

        ModuleDataParser.target_return t = null;

        ModuleDataParser.facet_return facet34 = null;

        ModuleDataParser.target_return target40 = null;

        ModuleDataParser.operatorOpt_return operatorOpt42 = null;

        ModuleDataParser.target_return target48 = null;

        ModuleDataParser.operatorOpt_return operatorOpt50 = null;

        ModuleDataParser.facet_return facet51 = null;


        CommonTree OPEN28_tree=null;
        CommonTree OPERATOR29_tree=null;
        CommonTree TERM30_tree=null;
        CommonTree OPEN31_tree=null;
        CommonTree OPERATOR32_tree=null;
        CommonTree CLOSE33_tree=null;
        CommonTree TERM_OPEN35_tree=null;
        CommonTree OPERATOR36_tree=null;
        CommonTree CLOSE37_tree=null;
        CommonTree OPEN38_tree=null;
        CommonTree OPERATOR39_tree=null;
        CommonTree CLOSE41_tree=null;
        CommonTree TERM_OPEN43_tree=null;
        CommonTree OPERATOR44_tree=null;
        CommonTree CLOSE45_tree=null;
        CommonTree OPEN46_tree=null;
        CommonTree OPERATOR47_tree=null;
        CommonTree CLOSE49_tree=null;
        CommonTree TERM_OPEN52_tree=null;
        CommonTree OPERATOR53_tree=null;
        CommonTree CLOSE54_tree=null;
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleTokenStream stream_TERM_OPEN=new RewriteRuleTokenStream(adaptor,"token TERM_OPEN");
        RewriteRuleTokenStream stream_OPERATOR=new RewriteRuleTokenStream(adaptor,"token OPERATOR");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_TERM=new RewriteRuleTokenStream(adaptor,"token TERM");
        RewriteRuleSubtreeStream stream_operatorOpt=new RewriteRuleSubtreeStream(adaptor,"rule operatorOpt");
        RewriteRuleSubtreeStream stream_facet=new RewriteRuleSubtreeStream(adaptor,"rule facet");
        RewriteRuleSubtreeStream stream_target=new RewriteRuleSubtreeStream(adaptor,"rule target");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:155:2: ( OPEN OPERATOR n= name t= target[$n.name] TERM -> ^( OPERATOR[$n.name] ) | OPEN OPERATOR n= name t= target[$n.name] CLOSE ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( FACETS ( facet )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ^( FACETS ( facet )+ ) ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:155:4: OPEN OPERATOR n= name t= target[$n.name] TERM
                    {
                    OPEN28=(Token)match(input,OPEN,FOLLOW_OPEN_in_operator384);  
                    stream_OPEN.add(OPEN28);

                    OPERATOR29=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator386);  
                    stream_OPERATOR.add(OPERATOR29);

                    pushFollow(FOLLOW_name_in_operator390);
                    n=name();

                    state._fsp--;

                    stream_name.add(n.getTree());
                    pushFollow(FOLLOW_target_in_operator394);
                    t=target((n!=null?n.name:null));

                    state._fsp--;

                    stream_target.add(t.getTree());
                    TERM30=(Token)match(input,TERM,FOLLOW_TERM_in_operator397);  
                    stream_TERM.add(TERM30);



                    // AST REWRITE
                    // elements: OPERATOR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 156:3: -> ^( OPERATOR[$n.name] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:156:6: ^( OPERATOR[$n.name] )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPERATOR, (n!=null?n.name:null)), root_1);

                        adaptor.addChild(root_1, getDefaultOperatorOpts((Tree) (t!=null?((CommonTree)t.tree):null)));
                        adaptor.addChild(root_1, getDefaultFacets());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:158:4: OPEN OPERATOR n= name t= target[$n.name] CLOSE ( facet )+ TERM_OPEN OPERATOR CLOSE
                    {
                    OPEN31=(Token)match(input,OPEN,FOLLOW_OPEN_in_operator418);  
                    stream_OPEN.add(OPEN31);

                    OPERATOR32=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator420);  
                    stream_OPERATOR.add(OPERATOR32);

                    pushFollow(FOLLOW_name_in_operator424);
                    n=name();

                    state._fsp--;

                    stream_name.add(n.getTree());
                    pushFollow(FOLLOW_target_in_operator428);
                    t=target((n!=null?n.name:null));

                    state._fsp--;

                    stream_target.add(t.getTree());
                    CLOSE33=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator431);  
                    stream_CLOSE.add(CLOSE33);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:158:49: ( facet )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==OPEN) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:158:49: facet
                    	    {
                    	    pushFollow(FOLLOW_facet_in_operator433);
                    	    facet34=facet();

                    	    state._fsp--;

                    	    stream_facet.add(facet34.getTree());

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

                    TERM_OPEN35=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_operator436);  
                    stream_TERM_OPEN.add(TERM_OPEN35);

                    OPERATOR36=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator438);  
                    stream_OPERATOR.add(OPERATOR36);

                    CLOSE37=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator440);  
                    stream_CLOSE.add(CLOSE37);



                    // AST REWRITE
                    // elements: facet, OPERATOR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 159:3: -> ^( OPERATOR[$n.name] ^( FACETS ( facet )+ ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:159:6: ^( OPERATOR[$n.name] ^( FACETS ( facet )+ ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPERATOR, (n!=null?n.name:null)), root_1);

                        adaptor.addChild(root_1, getDefaultOperatorOpts((Tree) (t!=null?((CommonTree)t.tree):null)));
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:159:67: ^( FACETS ( facet )+ )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FACETS, "FACETS"), root_2);

                        if ( !(stream_facet.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_facet.hasNext() ) {
                            adaptor.addChild(root_2, stream_facet.nextTree());

                        }
                        stream_facet.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:161:4: OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ TERM_OPEN OPERATOR CLOSE
                    {
                    OPEN38=(Token)match(input,OPEN,FOLLOW_OPEN_in_operator465);  
                    stream_OPEN.add(OPEN38);

                    OPERATOR39=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator467);  
                    stream_OPERATOR.add(OPERATOR39);

                    pushFollow(FOLLOW_name_in_operator471);
                    n=name();

                    state._fsp--;

                    stream_name.add(n.getTree());
                    pushFollow(FOLLOW_target_in_operator473);
                    target40=target((n!=null?n.name:null));

                    state._fsp--;

                    stream_target.add(target40.getTree());
                    CLOSE41=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator476);  
                    stream_CLOSE.add(CLOSE41);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:161:47: ( operatorOpt )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==OPEN) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:161:47: operatorOpt
                    	    {
                    	    pushFollow(FOLLOW_operatorOpt_in_operator478);
                    	    operatorOpt42=operatorOpt();

                    	    state._fsp--;

                    	    stream_operatorOpt.add(operatorOpt42.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);

                    TERM_OPEN43=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_operator481);  
                    stream_TERM_OPEN.add(TERM_OPEN43);

                    OPERATOR44=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator483);  
                    stream_OPERATOR.add(OPERATOR44);

                    CLOSE45=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator485);  
                    stream_CLOSE.add(CLOSE45);



                    // AST REWRITE
                    // elements: operatorOpt, OPERATOR, target
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 162:3: -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:162:6: ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPERATOR, (n!=null?n.name:null)), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:162:26: ^( OPTS target ( operatorOpt )+ )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPTS, "OPTS"), root_2);

                        adaptor.addChild(root_2, stream_target.nextTree());
                        if ( !(stream_operatorOpt.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_operatorOpt.hasNext() ) {
                            adaptor.addChild(root_2, stream_operatorOpt.nextTree());

                        }
                        stream_operatorOpt.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, getDefaultFacets());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:164:4: OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ ( facet )+ TERM_OPEN OPERATOR CLOSE
                    {
                    OPEN46=(Token)match(input,OPEN,FOLLOW_OPEN_in_operator512);  
                    stream_OPEN.add(OPEN46);

                    OPERATOR47=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator514);  
                    stream_OPERATOR.add(OPERATOR47);

                    pushFollow(FOLLOW_name_in_operator518);
                    n=name();

                    state._fsp--;

                    stream_name.add(n.getTree());
                    pushFollow(FOLLOW_target_in_operator520);
                    target48=target((n!=null?n.name:null));

                    state._fsp--;

                    stream_target.add(target48.getTree());
                    CLOSE49=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator523);  
                    stream_CLOSE.add(CLOSE49);

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:164:47: ( operatorOpt )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==OPEN) ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1==OPT) ) {
                                alt5=1;
                            }


                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:164:47: operatorOpt
                    	    {
                    	    pushFollow(FOLLOW_operatorOpt_in_operator525);
                    	    operatorOpt50=operatorOpt();

                    	    state._fsp--;

                    	    stream_operatorOpt.add(operatorOpt50.getTree());

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

                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:164:60: ( facet )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==OPEN) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:164:60: facet
                    	    {
                    	    pushFollow(FOLLOW_facet_in_operator528);
                    	    facet51=facet();

                    	    state._fsp--;

                    	    stream_facet.add(facet51.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    TERM_OPEN52=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_operator531);  
                    stream_TERM_OPEN.add(TERM_OPEN52);

                    OPERATOR53=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_operator533);  
                    stream_OPERATOR.add(OPERATOR53);

                    CLOSE54=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operator535);  
                    stream_CLOSE.add(CLOSE54);



                    // AST REWRITE
                    // elements: facet, OPERATOR, operatorOpt, target
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 165:3: -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ^( FACETS ( facet )+ ) )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:165:6: ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ^( FACETS ( facet )+ ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPERATOR, (n!=null?n.name:null)), root_1);

                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:165:26: ^( OPTS target ( operatorOpt )+ )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPTS, "OPTS"), root_2);

                        adaptor.addChild(root_2, stream_target.nextTree());
                        if ( !(stream_operatorOpt.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_operatorOpt.hasNext() ) {
                            adaptor.addChild(root_2, stream_operatorOpt.nextTree());

                        }
                        stream_operatorOpt.reset();

                        adaptor.addChild(root_1, root_2);
                        }
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:165:54: ^( FACETS ( facet )+ )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FACETS, "FACETS"), root_2);

                        if ( !(stream_facet.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_facet.hasNext() ) {
                            adaptor.addChild(root_2, stream_facet.nextTree());

                        }
                        stream_facet.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator"

    public static class operatorOpts_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operatorOpts"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:1: operatorOpts : OPEN OPTS CLOSE ( operatorOpt )+ TERM_OPEN OPTS CLOSE -> ^( OPTS ( operatorOpt )+ ) ;
    public final ModuleDataParser.operatorOpts_return operatorOpts() throws RecognitionException {
        ModuleDataParser.operatorOpts_return retval = new ModuleDataParser.operatorOpts_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN55=null;
        Token OPTS56=null;
        Token CLOSE57=null;
        Token TERM_OPEN59=null;
        Token OPTS60=null;
        Token CLOSE61=null;
        ModuleDataParser.operatorOpt_return operatorOpt58 = null;


        CommonTree OPEN55_tree=null;
        CommonTree OPTS56_tree=null;
        CommonTree CLOSE57_tree=null;
        CommonTree TERM_OPEN59_tree=null;
        CommonTree OPTS60_tree=null;
        CommonTree CLOSE61_tree=null;
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleTokenStream stream_TERM_OPEN=new RewriteRuleTokenStream(adaptor,"token TERM_OPEN");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_OPTS=new RewriteRuleTokenStream(adaptor,"token OPTS");
        RewriteRuleSubtreeStream stream_operatorOpt=new RewriteRuleSubtreeStream(adaptor,"rule operatorOpt");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:13: ( OPEN OPTS CLOSE ( operatorOpt )+ TERM_OPEN OPTS CLOSE -> ^( OPTS ( operatorOpt )+ ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:15: OPEN OPTS CLOSE ( operatorOpt )+ TERM_OPEN OPTS CLOSE
            {
            OPEN55=(Token)match(input,OPEN,FOLLOW_OPEN_in_operatorOpts567);  
            stream_OPEN.add(OPEN55);

            OPTS56=(Token)match(input,OPTS,FOLLOW_OPTS_in_operatorOpts569);  
            stream_OPTS.add(OPTS56);

            CLOSE57=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operatorOpts571);  
            stream_CLOSE.add(CLOSE57);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:31: ( operatorOpt )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==OPEN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:31: operatorOpt
            	    {
            	    pushFollow(FOLLOW_operatorOpt_in_operatorOpts573);
            	    operatorOpt58=operatorOpt();

            	    state._fsp--;

            	    stream_operatorOpt.add(operatorOpt58.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            TERM_OPEN59=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_operatorOpts576);  
            stream_TERM_OPEN.add(TERM_OPEN59);

            OPTS60=(Token)match(input,OPTS,FOLLOW_OPTS_in_operatorOpts578);  
            stream_OPTS.add(OPTS60);

            CLOSE61=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_operatorOpts580);  
            stream_CLOSE.add(CLOSE61);



            // AST REWRITE
            // elements: OPTS, operatorOpt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 167:65: -> ^( OPTS ( operatorOpt )+ )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:167:68: ^( OPTS ( operatorOpt )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_OPTS.nextNode(), root_1);

                if ( !(stream_operatorOpt.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_operatorOpt.hasNext() ) {
                    adaptor.addChild(root_1, stream_operatorOpt.nextTree());

                }
                stream_operatorOpt.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operatorOpts"

    public static class operatorOpt_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "operatorOpt"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:168:1: operatorOpt : OPEN OPT vp= valuePair TERM -> ^( OPT[vp.tree.getText()] ) ;
    public final ModuleDataParser.operatorOpt_return operatorOpt() throws RecognitionException {
        ModuleDataParser.operatorOpt_return retval = new ModuleDataParser.operatorOpt_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN62=null;
        Token OPT63=null;
        Token TERM64=null;
        ModuleDataParser.valuePair_return vp = null;


        CommonTree OPEN62_tree=null;
        CommonTree OPT63_tree=null;
        CommonTree TERM64_tree=null;
        RewriteRuleTokenStream stream_OPT=new RewriteRuleTokenStream(adaptor,"token OPT");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_TERM=new RewriteRuleTokenStream(adaptor,"token TERM");
        RewriteRuleSubtreeStream stream_valuePair=new RewriteRuleSubtreeStream(adaptor,"rule valuePair");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:168:12: ( OPEN OPT vp= valuePair TERM -> ^( OPT[vp.tree.getText()] ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:168:14: OPEN OPT vp= valuePair TERM
            {
            OPEN62=(Token)match(input,OPEN,FOLLOW_OPEN_in_operatorOpt595);  
            stream_OPEN.add(OPEN62);

            OPT63=(Token)match(input,OPT,FOLLOW_OPT_in_operatorOpt597);  
            stream_OPT.add(OPT63);

            pushFollow(FOLLOW_valuePair_in_operatorOpt601);
            vp=valuePair();

            state._fsp--;

            stream_valuePair.add(vp.getTree());
            TERM64=(Token)match(input,TERM,FOLLOW_TERM_in_operatorOpt603);  
            stream_TERM.add(TERM64);



            // AST REWRITE
            // elements: OPT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 168:41: -> ^( OPT[vp.tree.getText()] )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:168:44: ^( OPT[vp.tree.getText()] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPT, vp.tree.getText()), root_1);

                adaptor.addChild(root_1, vp.tree.getChild(0));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operatorOpt"

    public static class facets_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "facets"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:1: facets : OPEN FACETS CLOSE ( facet )+ TERM_OPEN FACETS CLOSE -> ^( FACETS ( facet )+ ) ;
    public final ModuleDataParser.facets_return facets() throws RecognitionException {
        ModuleDataParser.facets_return retval = new ModuleDataParser.facets_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN65=null;
        Token FACETS66=null;
        Token CLOSE67=null;
        Token TERM_OPEN69=null;
        Token FACETS70=null;
        Token CLOSE71=null;
        ModuleDataParser.facet_return facet68 = null;


        CommonTree OPEN65_tree=null;
        CommonTree FACETS66_tree=null;
        CommonTree CLOSE67_tree=null;
        CommonTree TERM_OPEN69_tree=null;
        CommonTree FACETS70_tree=null;
        CommonTree CLOSE71_tree=null;
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleTokenStream stream_TERM_OPEN=new RewriteRuleTokenStream(adaptor,"token TERM_OPEN");
        RewriteRuleTokenStream stream_FACETS=new RewriteRuleTokenStream(adaptor,"token FACETS");
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleSubtreeStream stream_facet=new RewriteRuleSubtreeStream(adaptor,"rule facet");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:7: ( OPEN FACETS CLOSE ( facet )+ TERM_OPEN FACETS CLOSE -> ^( FACETS ( facet )+ ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:9: OPEN FACETS CLOSE ( facet )+ TERM_OPEN FACETS CLOSE
            {
            OPEN65=(Token)match(input,OPEN,FOLLOW_OPEN_in_facets619);  
            stream_OPEN.add(OPEN65);

            FACETS66=(Token)match(input,FACETS,FOLLOW_FACETS_in_facets621);  
            stream_FACETS.add(FACETS66);

            CLOSE67=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_facets623);  
            stream_CLOSE.add(CLOSE67);

            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:27: ( facet )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==OPEN) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:27: facet
            	    {
            	    pushFollow(FOLLOW_facet_in_facets625);
            	    facet68=facet();

            	    state._fsp--;

            	    stream_facet.add(facet68.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            TERM_OPEN69=(Token)match(input,TERM_OPEN,FOLLOW_TERM_OPEN_in_facets628);  
            stream_TERM_OPEN.add(TERM_OPEN69);

            FACETS70=(Token)match(input,FACETS,FOLLOW_FACETS_in_facets630);  
            stream_FACETS.add(FACETS70);

            CLOSE71=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_facets632);  
            stream_CLOSE.add(CLOSE71);



            // AST REWRITE
            // elements: FACETS, facet
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 170:57: -> ^( FACETS ( facet )+ )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:170:60: ^( FACETS ( facet )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FACETS.nextNode(), root_1);

                if ( !(stream_facet.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_facet.hasNext() ) {
                    adaptor.addChild(root_1, stream_facet.nextTree());

                }
                stream_facet.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "facets"

    public static class facet_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "facet"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:1: facet : OPEN FACET n= name ( valuePair )+ TERM -> ^( FACET[$n.name] ( valuePair )+ ) ;
    public final ModuleDataParser.facet_return facet() throws RecognitionException {
        ModuleDataParser.facet_return retval = new ModuleDataParser.facet_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token OPEN72=null;
        Token FACET73=null;
        Token TERM75=null;
        ModuleDataParser.name_return n = null;

        ModuleDataParser.valuePair_return valuePair74 = null;


        CommonTree OPEN72_tree=null;
        CommonTree FACET73_tree=null;
        CommonTree TERM75_tree=null;
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_FACET=new RewriteRuleTokenStream(adaptor,"token FACET");
        RewriteRuleTokenStream stream_TERM=new RewriteRuleTokenStream(adaptor,"token TERM");
        RewriteRuleSubtreeStream stream_valuePair=new RewriteRuleSubtreeStream(adaptor,"rule valuePair");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:7: ( OPEN FACET n= name ( valuePair )+ TERM -> ^( FACET[$n.name] ( valuePair )+ ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:9: OPEN FACET n= name ( valuePair )+ TERM
            {
            OPEN72=(Token)match(input,OPEN,FOLLOW_OPEN_in_facet648);  
            stream_OPEN.add(OPEN72);

            FACET73=(Token)match(input,FACET,FOLLOW_FACET_in_facet650);  
            stream_FACET.add(FACET73);

            pushFollow(FOLLOW_name_in_facet654);
            n=name();

            state._fsp--;

            stream_name.add(n.getTree());
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:27: ( valuePair )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==ID) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:27: valuePair
            	    {
            	    pushFollow(FOLLOW_valuePair_in_facet656);
            	    valuePair74=valuePair();

            	    state._fsp--;

            	    stream_valuePair.add(valuePair74.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            TERM75=(Token)match(input,TERM,FOLLOW_TERM_in_facet659);  
            stream_TERM.add(TERM75);



            // AST REWRITE
            // elements: FACET, valuePair
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 171:43: -> ^( FACET[$n.name] ( valuePair )+ )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:171:46: ^( FACET[$n.name] ( valuePair )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FACET, (n!=null?n.name:null)), root_1);

                if ( !(stream_valuePair.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_valuePair.hasNext() ) {
                    adaptor.addChild(root_1, stream_valuePair.nextTree());

                }
                stream_valuePair.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "facet"

    public static class target_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "target"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:173:1: target[String name] : ( TARGET '=' id= VAL -> ^( OPT[\"Target\"] VAL[stripQuotes($id.text.toUpperCase())] ) | -> ^( OPT[\"Target\"] VAL[name.toUpperCase()] ) );
    public final ModuleDataParser.target_return target(String name) throws RecognitionException {
        ModuleDataParser.target_return retval = new ModuleDataParser.target_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token id=null;
        Token TARGET76=null;
        Token char_literal77=null;

        CommonTree id_tree=null;
        CommonTree TARGET76_tree=null;
        CommonTree char_literal77_tree=null;
        RewriteRuleTokenStream stream_VAL=new RewriteRuleTokenStream(adaptor,"token VAL");
        RewriteRuleTokenStream stream_DEF=new RewriteRuleTokenStream(adaptor,"token DEF");
        RewriteRuleTokenStream stream_TARGET=new RewriteRuleTokenStream(adaptor,"token TARGET");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:174:2: ( TARGET '=' id= VAL -> ^( OPT[\"Target\"] VAL[stripQuotes($id.text.toUpperCase())] ) | -> ^( OPT[\"Target\"] VAL[name.toUpperCase()] ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==TARGET) ) {
                alt11=1;
            }
            else if ( ((LA11_0>=CLOSE && LA11_0<=TERM)) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:174:4: TARGET '=' id= VAL
                    {
                    TARGET76=(Token)match(input,TARGET,FOLLOW_TARGET_in_target679);  
                    stream_TARGET.add(TARGET76);

                    char_literal77=(Token)match(input,DEF,FOLLOW_DEF_in_target681);  
                    stream_DEF.add(char_literal77);

                    id=(Token)match(input,VAL,FOLLOW_VAL_in_target685);  
                    stream_VAL.add(id);



                    // AST REWRITE
                    // elements: VAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 174:22: -> ^( OPT[\"Target\"] VAL[stripQuotes($id.text.toUpperCase())] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:174:25: ^( OPT[\"Target\"] VAL[stripQuotes($id.text.toUpperCase())] )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPT, "Target"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(VAL, stripQuotes((id!=null?id.getText():null).toUpperCase())));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:175:4: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 175:4: -> ^( OPT[\"Target\"] VAL[name.toUpperCase()] )
                    {
                        // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:175:7: ^( OPT[\"Target\"] VAL[name.toUpperCase()] )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OPT, "Target"), root_1);

                        adaptor.addChild(root_1, (CommonTree)adaptor.create(VAL, name.toUpperCase()));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "target"

    public static class name_return extends ParserRuleReturnScope {
        public String name;
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "name"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:177:1: name returns [String name] : NAME DEF v= VAL ;
    public final ModuleDataParser.name_return name() throws RecognitionException {
        ModuleDataParser.name_return retval = new ModuleDataParser.name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token v=null;
        Token NAME78=null;
        Token DEF79=null;

        CommonTree v_tree=null;
        CommonTree NAME78_tree=null;
        CommonTree DEF79_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:177:27: ( NAME DEF v= VAL )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:177:29: NAME DEF v= VAL
            {
            root_0 = (CommonTree)adaptor.nil();

            NAME78=(Token)match(input,NAME,FOLLOW_NAME_in_name720); 
            NAME78_tree = (CommonTree)adaptor.create(NAME78);
            adaptor.addChild(root_0, NAME78_tree);

            DEF79=(Token)match(input,DEF,FOLLOW_DEF_in_name722); 
            DEF79_tree = (CommonTree)adaptor.create(DEF79);
            root_0 = (CommonTree)adaptor.becomeRoot(DEF79_tree, root_0);

            v=(Token)match(input,VAL,FOLLOW_VAL_in_name727); 
            v_tree = (CommonTree)adaptor.create(v);
            adaptor.addChild(root_0, v_tree);

            retval.name =stripQuotes((v!=null?v.getText():null));

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "name"

    public static class valuePair_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "valuePair"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:179:1: valuePair : id= ID DEF v= VAL -> ^( DEF[stripQuotes($id.text)] VAL[stripQuotes($v.text)] ) ;
    public final ModuleDataParser.valuePair_return valuePair() throws RecognitionException {
        ModuleDataParser.valuePair_return retval = new ModuleDataParser.valuePair_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token id=null;
        Token v=null;
        Token DEF80=null;

        CommonTree id_tree=null;
        CommonTree v_tree=null;
        CommonTree DEF80_tree=null;
        RewriteRuleTokenStream stream_VAL=new RewriteRuleTokenStream(adaptor,"token VAL");
        RewriteRuleTokenStream stream_DEF=new RewriteRuleTokenStream(adaptor,"token DEF");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:179:11: (id= ID DEF v= VAL -> ^( DEF[stripQuotes($id.text)] VAL[stripQuotes($v.text)] ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:179:13: id= ID DEF v= VAL
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_valuePair739);  
            stream_ID.add(id);

            DEF80=(Token)match(input,DEF,FOLLOW_DEF_in_valuePair741);  
            stream_DEF.add(DEF80);

            v=(Token)match(input,VAL,FOLLOW_VAL_in_valuePair745);  
            stream_VAL.add(v);



            // AST REWRITE
            // elements: DEF, VAL
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 179:29: -> ^( DEF[stripQuotes($id.text)] VAL[stripQuotes($v.text)] )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/TSM/src/stencil/legend/module/util/ModuleData.g:179:32: ^( DEF[stripQuotes($id.text)] VAL[stripQuotes($v.text)] )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(DEF, stripQuotes((id!=null?id.getText():null))), root_1);

                adaptor.addChild(root_1, (CommonTree)adaptor.create(VAL, stripQuotes((v!=null?v.getText():null))));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "valuePair"

    // Delegated rules


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\25\uffff";
    static final String DFA7_eofS =
        "\25\uffff";
    static final String DFA7_minS =
        "\1\20\1\10\1\15\1\24\1\25\1\17\1\24\1\20\1\uffff\1\25\1\11\1\21"+
        "\1\27\1\uffff\1\24\1\25\1\22\1\20\1\uffff\1\11\1\uffff";
    static final String DFA7_maxS =
        "\1\20\1\10\1\15\1\24\1\25\1\22\1\24\1\20\1\uffff\1\25\1\13\1\22"+
        "\1\27\1\uffff\1\24\1\25\1\22\1\23\1\uffff\1\13\1\uffff";
    static final String DFA7_acceptS =
        "\10\uffff\1\1\4\uffff\1\2\4\uffff\1\3\1\uffff\1\4";
    static final String DFA7_specialS =
        "\25\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6\1\uffff\1\7\1\10",
            "\1\11",
            "\1\12",
            "",
            "\1\13",
            "\1\15\1\uffff\1\14",
            "\1\7\1\10",
            "\1\16",
            "",
            "\1\17",
            "\1\20",
            "\1\21",
            "\1\23\2\uffff\1\22",
            "",
            "\1\24\1\uffff\1\14",
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
            return "154:1: operator : ( OPEN OPERATOR n= name t= target[$n.name] TERM -> ^( OPERATOR[$n.name] ) | OPEN OPERATOR n= name t= target[$n.name] CLOSE ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( FACETS ( facet )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ) | OPEN OPERATOR n= name target[$n.name] CLOSE ( operatorOpt )+ ( facet )+ TERM_OPEN OPERATOR CLOSE -> ^( OPERATOR[$n.name] ^( OPTS target ( operatorOpt )+ ) ^( FACETS ( facet )+ ) ) );";
        }
    }
 

    public static final BitSet FOLLOW_OPEN_in_moduleData209 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_MODULE_DATA_in_moduleData211 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_rev_in_moduleData215 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_moduleData219 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_moduleData221 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_clss_in_moduleData233 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_description_in_moduleData235 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_defaults_in_moduleData237 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_operator_in_moduleData239 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_moduleData242 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_MODULE_DATA_in_moduleData244 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_moduleData246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REV_in_rev276 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DEF_in_rev278 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_VAL_in_rev282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_clss291 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_CLASS_in_clss293 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_clss297 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_TERM_in_clss299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_description311 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_DESC_in_description313 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_description315 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_CDATA_in_description319 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_description321 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_DESC_in_description323 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_description325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_defaults347 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFAULTS_in_defaults350 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_defaults353 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_operatorOpts_in_defaults358 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_facets_in_defaults362 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_defaults364 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFAULTS_in_defaults367 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_defaults370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operator384 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator386 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_operator390 = new BitSet(new long[]{0x0000000000048000L});
    public static final BitSet FOLLOW_target_in_operator394 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_TERM_in_operator397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operator418 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator420 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_operator424 = new BitSet(new long[]{0x0000000000028000L});
    public static final BitSet FOLLOW_target_in_operator428 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator431 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_facet_in_operator433 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_operator436 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator438 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operator465 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator467 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_operator471 = new BitSet(new long[]{0x0000000000028000L});
    public static final BitSet FOLLOW_target_in_operator473 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator476 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_operatorOpt_in_operator478 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_operator481 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator483 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operator512 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator514 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_operator518 = new BitSet(new long[]{0x0000000000028000L});
    public static final BitSet FOLLOW_target_in_operator520 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator523 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_operatorOpt_in_operator525 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_facet_in_operator528 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_operator531 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OPERATOR_in_operator533 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operator535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operatorOpts567 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OPTS_in_operatorOpts569 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operatorOpts571 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_operatorOpt_in_operatorOpts573 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_operatorOpts576 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OPTS_in_operatorOpts578 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_operatorOpts580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_operatorOpt595 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_OPT_in_operatorOpt597 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_valuePair_in_operatorOpt601 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_TERM_in_operatorOpt603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_facets619 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_FACETS_in_facets621 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_facets623 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_facet_in_facets625 = new BitSet(new long[]{0x0000000000090000L});
    public static final BitSet FOLLOW_TERM_OPEN_in_facets628 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_FACETS_in_facets630 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_in_facets632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_facet648 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_FACET_in_facet650 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_name_in_facet654 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_valuePair_in_facet656 = new BitSet(new long[]{0x0000000000840000L});
    public static final BitSet FOLLOW_TERM_in_facet659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TARGET_in_target679 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DEF_in_target681 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_VAL_in_target685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_name720 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DEF_in_name722 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_VAL_in_name727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_valuePair739 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_DEF_in_valuePair741 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_VAL_in_valuePair745 = new BitSet(new long[]{0x0000000000000002L});

}