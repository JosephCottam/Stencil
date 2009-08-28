// $ANTLR 3.1.3 Mar 18, 2009 10:09:25 /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g 2009-08-28 15:23:10

	/** Performs the built of automatic guide mark generation.
	 *
	 * Precondition: To operate properly, this pass must be run after ensuring 
	 * guide operators exist and after annotating function calls with their
	 * associated call targets.
	 *  
	 * Uses ANTLR tree filter/rewrite: http://www.antlr.org/wiki/display/~admin/2008/11/29/Woohoo!+Tree+pattern+matching%2C+rewriting+a+reality	  
	 **/
	 
	package stencil.parser.string;
	 
	import java.util.Set;
	import java.util.HashSet;
	import stencil.util.MultiPartName;
	import stencil.legend.StencilLegend;
	import stencil.parser.tree.*;
	import stencil.rules.ModuleCache;
	import stencil.legend.module.*;
	import static stencil.legend.module.LegendData.OpType;
	import static stencil.util.Tuples.stripQuotes;	 
	 //TODO: Extend so we can handle more than the first field in a mapping definition



import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("all")
public class EnsureGuideOp extends TreeRewriter {
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


        public EnsureGuideOp(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public EnsureGuideOp(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return EnsureGuideOp.tokenNames; }
    public String getGrammarFileName() { return "/nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g"; }


    	protected Set<String> requestedGuides = new HashSet<String>();
    	protected ModuleCache modules;
        
    	public EnsureGuideOp(TreeNodeStream input, ModuleCache modules) {
    		super(input, new RecognizerSharedState());
    		assert modules != null : "Module cache must not be null.";
    		this.modules = modules;
    	}

       	public Object transform(Object t) throws Exception {
    		build(t);
    		t = replace(t);
    		t = ensure(t);
    		return t;
    	}	

    	/**Build a list of things that need guides.**/
    	private void build(Object t) {
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return listRequirements(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    downup(t, down, up);
        }
        
        /**Replace the auto-categorize operator.**/
        private Object replace(Object t) throws Exception {
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return replaceCompactForm(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    Object r = downup(t, down, up);
    		return r;
        }
        
        /**Make sure that things which need guides have minimum necessary operators
         *
         *@throws Exception Not all requested guides are found for ensuring
         */
        private Object ensure(Object t) throws Exception {
    		fptr down =	new fptr() {public Object rule() throws RecognitionException { return ensure(); }};
       	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
       	    Object r = downup(t, down, up);
       	    return r;
        }


        private Object downup(Object t, final fptr down, final fptr up) {
            TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
            TreeVisitorAction actions = new TreeVisitorAction() {
                public Object pre(Object t)  { return applyOnce(t, down); }
                public Object post(Object t) { return applyRepeatedly(t, up); }
            };
            t = v.visit(t, actions);
            return t;    
        }
        
        
        private String key(String layer, Tree attribute) {return key(layer, attribute.getText());}
    	private String key(String layer, String attribute) {
        	MultiPartName att = new MultiPartName(attribute);
        	String key= layer + ":" + att.getName();	//Trim to just the attribute name
        	return key;
        } 

        private boolean requiresChanges(CallGroup group) {
        	if (group.getChildCount() >1) {throw new RuntimeException("Cannot support auto-guide ensure for compound call groups.");}
           	CallChain chain = group.getChains().get(0);
            CallTarget call = chain.getStart();

    		//Check if there is a categorize operator
        	boolean hasCategorize = false;
        	while (!(call instanceof Pack) && !hasCategorize) {
        		Function f = (Function) call;
        		MultiPartName name = new MultiPartName(f.getName());
        		Module m = modules.findModuleForLegend(name.prefixedName()).module;
           		try {
            		OpType opType =  m.getOperatorData(name.getName(), f.getSpecializer()).getFacetData(name.getFacet()).getFacetType();
            		hasCategorize = (opType == OpType.CATEGORIZE);
            	} catch (SpecializationException e) {throw new Error("Specialization error after ensuring specialization supposedly performed.");}
    			call = f.getCall();
           	}    	
        	return !hasCategorize;
        }
        
        //Given a call group, what are the values retrieved from the tuple in the first round
        //of calls?
        private String findInitialArgs(CallGroup call) {
        	StringBuilder args= new StringBuilder();
        	for (CallChain chain: call.getChains()) {
        		CallTarget t = chain.getStart();
        		for (Value v: t.getArguments()) {
        			if (v.isTupleRef()) {
        				args.append("\"");
        				args.append(v.getChild(0).toString());//TODO: HACK...won't admit indexed tuple-refs
        				args.append("\"");
        				args.append(",");
        			}
        		}
        	}
        	if (args.length() ==0) {throw new RuntimeException("No tuple-dependent arguments found when creating guide operator.");}
        	args.deleteCharAt(args.length()-1); //Remove trailing comma
        	
        	return args.toString();
        }
                    
    	private Tree newCall(String layer, String field, CommonTree c) {
    	 	CallGroup call = (CallGroup) c; 
        	String key = key(layer, field);
        	if (!requestedGuides.contains(key)) {return call;}
        	if (!requiresChanges(call)) {return call;} 
        	String intialArgs = findInitialArgs(call);
        	
        	String specSource = String.format("[1 .. n, %1$s]", intialArgs);
        	Specializer specializer;
        	StencilLegend op;
        
        	try {
    	    	specializer =ParseStencil.parseSpecializer(specSource); 
        	} catch (Exception e) {
        		throw new Error("Error creating auto-guide required categorize operator.",e);
        	}
        	
        	
        	stencil.parser.tree.List args = (stencil.parser.tree.List) adaptor.create(LIST, "args");
        	try {
        		String[] argNames = intialArgs.split(",");
        		for (String name: argNames) {
        			name= stripQuotes(name);
        			TupleRef ref = (TupleRef) adaptor.create(TUPLE_REF, "TUPLE_REF");
        			adaptor.addChild(ref, adaptor.create(ID, name));
        			adaptor.addChild(args,ref);
        		}
        	} catch (Exception e) {
        		throw new Error("Error creating auto-guide required argument list.",e);
        	}

    		//Construct function node
        	Function functionNode = (Function) adaptor.create(FUNCTION, "EchoCategorize.Map");
        	adaptor.addChild(functionNode, specializer);
        	adaptor.addChild(functionNode, args);
        	adaptor.addChild(functionNode, adaptor.create(YIELDS, "->"));
    		
    		//Construct chain node
    		CallChain chainNode = (CallChain) adaptor.create(CALL_CHAIN, "CALL_CHAIN");
    		adaptor.addChild(chainNode, functionNode);
    		
    		CallGroup groupNode;
    		if (call.getChains().size() == 1) {
    			adaptor.addChild(functionNode, call.getChains().get(0).getStart());
    			groupNode = (CallGroup) adaptor.create(CALL_GROUP, "CALL_GROUP");
    			adaptor.addChild(groupNode, chainNode);
    		} else {
    			throw new Error("Auto guide with joined call chains not supported.");
    		}
    		
    		
        	return groupNode;
        }
        
    	public Specializer autoEchoSpecializer(CommonTree t) {
        	//Switch on the target type
        	//Get the names out of its arguments list
        	//Remember those names in the echo categorize
        
        	String specializerTemplate = "[1 .. n, %1$s]";
        	StringBuilder refs = new StringBuilder();
        	 
        	if (t instanceof Pack || t instanceof Function) {
        		CallTarget target = (CallTarget) t;
        		for (Value v:target.getArguments()) {
        			if (v.isAtom()) {continue;} //Skip all the atoms, we only want tuple-refs
        			refs.append("\"");
        			refs.append(v.getValue());
        			refs.append("\"");
        			refs.append(",");
        		}
        		refs.deleteCharAt(refs.length()-1); //Remove the last comma
        	} else {
        		throw new IllegalArgumentException("Attempt to use target of uknown type: " + t.getClass().getName());
        	}
        	
        	
    		String specSource =String.format(specializerTemplate, refs);
        	try {
        		Specializer spec = ParseStencil.parseSpecializer(specSource);
        		return spec;
        	} catch (Exception e) {
        		throw new RuntimeException("Error creating default catgorical operator with specialzier " + specSource, e);
        	}
        }
        
        public List<Value> autoEchoArgs(CommonTree t) {
        	CallTarget target = (CallTarget) t;
        	List<Value> args = (List<Value>) adaptor.create(LIST, "Arguments");
        	
     		for (Value v: target.getArguments()) {
     			if (v.isAtom()) {continue;}
     			adaptor.addChild(args, adaptor.dupTree(v));
     		}
     		return args;
        }



    public static class listRequirements_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "listRequirements"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:268:1: listRequirements : ^(name= LAYER . ^( LIST ( guide[$name.text] )* ) . ) ;
    public final EnsureGuideOp.listRequirements_return listRequirements() throws RecognitionException {
        EnsureGuideOp.listRequirements_return retval = new EnsureGuideOp.listRequirements_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree wildcard1=null;
        CommonTree LIST2=null;
        CommonTree wildcard4=null;
        EnsureGuideOp.guide_return guide3 = null;


        CommonTree name_tree=null;
        CommonTree wildcard1_tree=null;
        CommonTree LIST2_tree=null;
        CommonTree wildcard4_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:268:17: ( ^(name= LAYER . ^( LIST ( guide[$name.text] )* ) . ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:268:19: ^(name= LAYER . ^( LIST ( guide[$name.text] )* ) . )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            name=(CommonTree)match(input,LAYER,FOLLOW_LAYER_in_listRequirements68); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = name;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            wildcard1=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard1;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_2 = _last;
            CommonTree _first_2 = null;
            _last = (CommonTree)input.LT(1);
            LIST2=(CommonTree)match(input,LIST,FOLLOW_LIST_in_listRequirements73); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = LIST2;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return retval;
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:268:41: ( guide[$name.text] )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0==GUIDE) ) {
                        alt1=1;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:268:41: guide[$name.text]
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_guide_in_listRequirements75);
                	    guide3=guide((name!=null?name.getText():null));

                	    state._fsp--;
                	    if (state.failed) return retval;
                	    if ( state.backtracking==1 ) 
                	     
                	    if ( _first_2==null ) _first_2 = guide3.tree;

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
            }_last = _save_last_2;
            }

            _last = (CommonTree)input.LT(1);
            wildcard4=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard4;

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
    // $ANTLR end "listRequirements"

    public static class guide_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "guide"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:269:1: guide[String layer] : ^( GUIDE . . field= ID ) ;
    public final EnsureGuideOp.guide_return guide(String layer) throws RecognitionException {
        EnsureGuideOp.guide_return retval = new EnsureGuideOp.guide_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree field=null;
        CommonTree GUIDE5=null;
        CommonTree wildcard6=null;
        CommonTree wildcard7=null;

        CommonTree field_tree=null;
        CommonTree GUIDE5_tree=null;
        CommonTree wildcard6_tree=null;
        CommonTree wildcard7_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:269:20: ( ^( GUIDE . . field= ID ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:269:22: ^( GUIDE . . field= ID )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            GUIDE5=(CommonTree)match(input,GUIDE,FOLLOW_GUIDE_in_guide89); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = GUIDE5;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            wildcard6=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard6;
            _last = (CommonTree)input.LT(1);
            wildcard7=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard7;
            _last = (CommonTree)input.LT(1);
            field=(CommonTree)match(input,ID,FOLLOW_ID_in_guide97); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = field;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }

            if ( state.backtracking==1 ) {
              requestedGuides.add(key(layer, field));
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
    // $ANTLR end "guide"

    public static class ensure_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ensure"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:273:1: ensure : ^(name= LAYER . . ^( LIST ^( CONSUMES . ^( LIST ( rule[$name.text] )* ) ) ) ) ;
    public final EnsureGuideOp.ensure_return ensure() throws RecognitionException {
        EnsureGuideOp.ensure_return retval = new EnsureGuideOp.ensure_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree name=null;
        CommonTree wildcard8=null;
        CommonTree wildcard9=null;
        CommonTree LIST10=null;
        CommonTree CONSUMES11=null;
        CommonTree wildcard12=null;
        CommonTree LIST13=null;
        EnsureGuideOp.rule_return rule14 = null;


        CommonTree name_tree=null;
        CommonTree wildcard8_tree=null;
        CommonTree wildcard9_tree=null;
        CommonTree LIST10_tree=null;
        CommonTree CONSUMES11_tree=null;
        CommonTree wildcard12_tree=null;
        CommonTree LIST13_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:273:7: ( ^(name= LAYER . . ^( LIST ^( CONSUMES . ^( LIST ( rule[$name.text] )* ) ) ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:273:9: ^(name= LAYER . . ^( LIST ^( CONSUMES . ^( LIST ( rule[$name.text] )* ) ) ) )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            name=(CommonTree)match(input,LAYER,FOLLOW_LAYER_in_ensure113); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = name;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            wildcard8=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard8;
            _last = (CommonTree)input.LT(1);
            wildcard9=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = wildcard9;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_2 = _last;
            CommonTree _first_2 = null;
            _last = (CommonTree)input.LT(1);
            LIST10=(CommonTree)match(input,LIST,FOLLOW_LIST_in_ensure120); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = LIST10;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_3 = _last;
            CommonTree _first_3 = null;
            _last = (CommonTree)input.LT(1);
            CONSUMES11=(CommonTree)match(input,CONSUMES,FOLLOW_CONSUMES_in_ensure123); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_2==null ) _first_2 = CONSUMES11;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            wildcard12=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_3==null ) _first_3 = wildcard12;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_4 = _last;
            CommonTree _first_4 = null;
            _last = (CommonTree)input.LT(1);
            LIST13=(CommonTree)match(input,LIST,FOLLOW_LIST_in_ensure128); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_3==null ) _first_3 = LIST13;
            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return retval;
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:273:53: ( rule[$name.text] )*
                loop2:
                do {
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==RULE) ) {
                        alt2=1;
                    }


                    switch (alt2) {
                	case 1 :
                	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:273:53: rule[$name.text]
                	    {
                	    _last = (CommonTree)input.LT(1);
                	    pushFollow(FOLLOW_rule_in_ensure130);
                	    rule14=rule((name!=null?name.getText():null));

                	    state._fsp--;
                	    if (state.failed) return retval;
                	    if ( state.backtracking==1 ) 
                	     
                	    if ( _first_4==null ) _first_4 = rule14.tree;

                	    if ( state.backtracking==1 ) {
                	    retval.tree = (CommonTree)_first_0;
                	    if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
                	        retval.tree = (CommonTree)adaptor.getParent(retval.tree);}
                	    }
                	    break;

                	default :
                	    break loop2;
                    }
                } while (true);


                match(input, Token.UP, null); if (state.failed) return retval;
            }_last = _save_last_4;
            }


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_3;
            }


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_2;
            }


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
    // $ANTLR end "ensure"

    public static class rule_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:274:1: rule[String layer] : ^( RULE field= glyphField call= . bind= . ) -> ^( RULE $field $bind) ;
    public final EnsureGuideOp.rule_return rule(String layer) throws RecognitionException {
        EnsureGuideOp.rule_return retval = new EnsureGuideOp.rule_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree RULE15=null;
        CommonTree call=null;
        CommonTree bind=null;
        EnsureGuideOp.glyphField_return field = null;


        CommonTree RULE15_tree=null;
        CommonTree call_tree=null;
        CommonTree bind_tree=null;
        RewriteRuleNodeStream stream_RULE=new RewriteRuleNodeStream(adaptor,"token RULE");
        RewriteRuleSubtreeStream stream_glyphField=new RewriteRuleSubtreeStream(adaptor,"rule glyphField");
        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:274:19: ( ^( RULE field= glyphField call= . bind= . ) -> ^( RULE $field $bind) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:275:2: ^( RULE field= glyphField call= . bind= . )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            RULE15=(CommonTree)match(input,RULE,FOLLOW_RULE_in_rule146); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_RULE.add(RULE15);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = RULE15;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            pushFollow(FOLLOW_glyphField_in_rule150);
            field=glyphField();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==1 ) stream_glyphField.add(field.getTree());
            _last = (CommonTree)input.LT(1);
            call=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = call;
            _last = (CommonTree)input.LT(1);
            bind=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = bind;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: RULE, field, bind
            // token labels: 
            // rule labels: field, retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: bind
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_bind=new RewriteRuleSubtreeStream(adaptor,"wildcard bind",bind);
            RewriteRuleSubtreeStream stream_field=new RewriteRuleSubtreeStream(adaptor,"rule field",field!=null?field.tree:null);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 276:3: -> ^( RULE $field $bind)
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:276:7: ^( RULE $field $bind)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_RULE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_field.nextTree());
                adaptor.addChild(root_1, newCall(layer, (field!=null?field.field:null), call));
                adaptor.addChild(root_1, stream_bind.nextTree());

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
    // $ANTLR end "rule"

    public static class glyphField_return extends TreeRuleReturnScope {
        public String field;
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "glyphField"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:279:1: glyphField returns [String field] : ^( GLYPH ^( TUPLE_PROTOTYPE f= ID ( . )* ) ) ;
    public final EnsureGuideOp.glyphField_return glyphField() throws RecognitionException {
        EnsureGuideOp.glyphField_return retval = new EnsureGuideOp.glyphField_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree f=null;
        CommonTree GLYPH16=null;
        CommonTree TUPLE_PROTOTYPE17=null;
        CommonTree wildcard18=null;

        CommonTree f_tree=null;
        CommonTree GLYPH16_tree=null;
        CommonTree TUPLE_PROTOTYPE17_tree=null;
        CommonTree wildcard18_tree=null;

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:279:34: ( ^( GLYPH ^( TUPLE_PROTOTYPE f= ID ( . )* ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:279:36: ^( GLYPH ^( TUPLE_PROTOTYPE f= ID ( . )* ) )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            GLYPH16=(CommonTree)match(input,GLYPH,FOLLOW_GLYPH_in_glyphField190); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = GLYPH16;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_2 = _last;
            CommonTree _first_2 = null;
            _last = (CommonTree)input.LT(1);
            TUPLE_PROTOTYPE17=(CommonTree)match(input,TUPLE_PROTOTYPE,FOLLOW_TUPLE_PROTOTYPE_in_glyphField193); if (state.failed) return retval;


            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = TUPLE_PROTOTYPE17;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            f=(CommonTree)match(input,ID,FOLLOW_ID_in_glyphField197); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_2==null ) _first_2 = f;
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:279:67: ( . )*
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
            	    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:279:67: .
            	    {
            	    _last = (CommonTree)input.LT(1);
            	    wildcard18=(CommonTree)input.LT(1);
            	    matchAny(input); if (state.failed) return retval;
            	     
            	    if ( state.backtracking==1 )
            	    if ( _first_2==null ) _first_2 = wildcard18;

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


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_2;
            }


            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }

            if ( state.backtracking==1 ) {
              retval.field =(f!=null?f.getText():null);
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
    // $ANTLR end "glyphField"

    public static class replaceCompactForm_return extends TreeRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "replaceCompactForm"
    // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:282:1: replaceCompactForm : ^( FUNCTION s= . a= . GUIDE_YIELD t= . ) -> ^( FUNCTION $s $a YIELDS ^( FUNCTION[\"EchoCategorize.Map\"] YIELDS ) ) ;
    public final EnsureGuideOp.replaceCompactForm_return replaceCompactForm() throws RecognitionException {
        EnsureGuideOp.replaceCompactForm_return retval = new EnsureGuideOp.replaceCompactForm_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        CommonTree _first_0 = null;
        CommonTree _last = null;

        CommonTree FUNCTION19=null;
        CommonTree GUIDE_YIELD20=null;
        CommonTree s=null;
        CommonTree a=null;
        CommonTree t=null;

        CommonTree FUNCTION19_tree=null;
        CommonTree GUIDE_YIELD20_tree=null;
        CommonTree s_tree=null;
        CommonTree a_tree=null;
        CommonTree t_tree=null;
        RewriteRuleNodeStream stream_GUIDE_YIELD=new RewriteRuleNodeStream(adaptor,"token GUIDE_YIELD");
        RewriteRuleNodeStream stream_FUNCTION=new RewriteRuleNodeStream(adaptor,"token FUNCTION");

        try {
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:282:19: ( ^( FUNCTION s= . a= . GUIDE_YIELD t= . ) -> ^( FUNCTION $s $a YIELDS ^( FUNCTION[\"EchoCategorize.Map\"] YIELDS ) ) )
            // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:283:2: ^( FUNCTION s= . a= . GUIDE_YIELD t= . )
            {
            _last = (CommonTree)input.LT(1);
            {
            CommonTree _save_last_1 = _last;
            CommonTree _first_1 = null;
            _last = (CommonTree)input.LT(1);
            FUNCTION19=(CommonTree)match(input,FUNCTION,FOLLOW_FUNCTION_in_replaceCompactForm214); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_FUNCTION.add(FUNCTION19);


            if ( state.backtracking==1 )
            if ( _first_0==null ) _first_0 = FUNCTION19;
            match(input, Token.DOWN, null); if (state.failed) return retval;
            _last = (CommonTree)input.LT(1);
            s=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = s;
            _last = (CommonTree)input.LT(1);
            a=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = a;
            _last = (CommonTree)input.LT(1);
            GUIDE_YIELD20=(CommonTree)match(input,GUIDE_YIELD,FOLLOW_GUIDE_YIELD_in_replaceCompactForm224); if (state.failed) return retval; 
            if ( state.backtracking==1 ) stream_GUIDE_YIELD.add(GUIDE_YIELD20);

            _last = (CommonTree)input.LT(1);
            t=(CommonTree)input.LT(1);
            matchAny(input); if (state.failed) return retval;
             
            if ( state.backtracking==1 )
            if ( _first_1==null ) _first_1 = t;

            match(input, Token.UP, null); if (state.failed) return retval;_last = _save_last_1;
            }



            // AST REWRITE
            // elements: s, a, FUNCTION, FUNCTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: a, s
            if ( state.backtracking==1 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_a=new RewriteRuleSubtreeStream(adaptor,"wildcard a",a);
            RewriteRuleSubtreeStream stream_s=new RewriteRuleSubtreeStream(adaptor,"wildcard s",s);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 283:38: -> ^( FUNCTION $s $a YIELDS ^( FUNCTION[\"EchoCategorize.Map\"] YIELDS ) )
            {
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:284:3: ^( FUNCTION $s $a YIELDS ^( FUNCTION[\"EchoCategorize.Map\"] YIELDS ) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot(stream_FUNCTION.nextNode(), root_1);

                adaptor.addChild(root_1, stream_s.nextTree());
                adaptor.addChild(root_1, stream_a.nextTree());
                adaptor.addChild(root_1, (CommonTree)adaptor.create(YIELDS, "YIELDS"));
                // /nfs/rontok/xraid/users/jcottam/Documents/workspace/Stencil/Stencil/Core/stencil/parser/EnsureGuideOp.g:284:27: ^( FUNCTION[\"EchoCategorize.Map\"] YIELDS )
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, "EchoCategorize.Map"), root_2);

                adaptor.addChild(root_2, autoEchoSpecializer(t));
                adaptor.addChild(root_2, autoEchoArgs(t));
                adaptor.addChild(root_2, (CommonTree)adaptor.create(YIELDS, "YIELDS"));
                adaptor.addChild(root_2, adaptor.dupTree(t));

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
    // $ANTLR end "replaceCompactForm"

    // Delegated rules


 

    public static final BitSet FOLLOW_LAYER_in_listRequirements68 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LIST_in_listRequirements73 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_guide_in_listRequirements75 = new BitSet(new long[]{0x0000000000000808L});
    public static final BitSet FOLLOW_GUIDE_in_guide89 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_guide97 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LAYER_in_ensure113 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LIST_in_ensure120 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_CONSUMES_in_ensure123 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_LIST_in_ensure128 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rule_in_ensure130 = new BitSet(new long[]{0x0000000000100008L});
    public static final BitSet FOLLOW_RULE_in_rule146 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_glyphField_in_rule150 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_GLYPH_in_glyphField190 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TUPLE_PROTOTYPE_in_glyphField193 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_glyphField197 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_FUNCTION_in_replaceCompactForm214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_GUIDE_YIELD_in_replaceCompactForm224 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});

}