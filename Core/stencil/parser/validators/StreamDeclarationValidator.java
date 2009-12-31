// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g 2009-12-31 15:41:44

  /** Validates that all stream declarations include the 
    * standard source-indicator field in the prototype,
    * that all field names in the declaration are unique
    * and that no field name is the same as the stream name.
   **/
   

  package stencil.parser.validators;
  
  import stencil.parser.tree.External;
  import stencil.parser.string.StencilParser;
  import stencil.parser.tree.TuplePrototype;
  import stencil.parser.tree.TupleFieldDef;

  import java.util.HashSet;
  import java.util.Set;


  import static java.lang.String.format;
  import static stencil.parser.ParserConstants.SOURCE_FIELD;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class StreamDeclarationValidator extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ANNOTATION", "BOOLEAN_OP", "BASIC", "CONSUMES", "CALL_CHAIN", "DIRECT_YIELD", "FUNCTION", "GLYPH", "LIST", "CANVAS_DEF", "NUMBER", "OPERATOR_INSTANCE", "OPERATOR_PROXY", "OPERATOR_REFERENCE", "OPERATOR_TEMPLATE", "OPERATOR_RULE", "OPERATOR_BASE", "POST", "PRE", "PREDICATE", "PROGRAM", "PACK", "PYTHON_FACET", "RETURN", "RULE", "SIGIL", "SPECIALIZER", "TUPLE_PROTOTYPE", "TUPLE_FIELD_DEF", "TUPLE_REF", "MAP_ENTRY", "ALL", "AS", "BASE", "CANVAS", "DEFAULT", "EXTERNAL", "FACET", "FILTER", "FROM", "GUIDE", "IMPORT", "LOCAL", "LAYER", "OPERATOR", "ORDER", "PREFILTER", "PYTHON", "TEMPLATE", "STREAM", "VIEW", "GROUP", "CLOSE_GROUP", "ARG", "CLOSE_ARG", "SEPARATOR", "RANGE", "NAMESPACE", "DEFINE", "DYNAMIC", "ANIMATED", "ANIMATED_DYNAMIC", "YIELDS", "FEED", "GUIDE_FEED", "GUIDE_YIELD", "GATE", "SPLIT", "JOIN", "TAG", "ID", "CODE_BLOCK", "TAGGED_ID", "STRING", "DIGITS", "NESTED_BLOCK", "ESCAPE_SEQUENCE", "WS", "COMMENT", "'>'", "'Init'", "'='", "'_'", "'>='", "'<'", "'<='", "'!='", "'=~'", "'!~'", "'-['", "']>'", "'-'", "'+'", "'.'"
    };
    public static final int DIRECT_YIELD=9;
    public static final int PRE=22;
    public static final int CLOSE_GROUP=56;
    public static final int AS=36;
    public static final int NUMBER=14;
    public static final int FACET=41;
    public static final int TEMPLATE=52;
    public static final int NAMESPACE=61;
    public static final int VIEW=54;
    public static final int IMPORT=45;
    public static final int TUPLE_REF=33;
    public static final int PREDICATE=23;
    public static final int POST=21;
    public static final int ORDER=49;
    public static final int BASIC=6;
    public static final int OPERATOR_BASE=20;
    public static final int LOCAL=46;
    public static final int FUNCTION=10;
    public static final int PREFILTER=50;
    public static final int T__96=96;
    public static final int ANIMATED_DYNAMIC=65;
    public static final int YIELDS=66;
    public static final int TAG=73;
    public static final int CANVAS=38;
    public static final int RANGE=60;
    public static final int SIGIL=29;
    public static final int SPLIT=71;
    public static final int FILTER=42;
    public static final int T__89=89;
    public static final int WS=81;
    public static final int STRING=77;
    public static final int ANIMATED=64;
    public static final int T__92=92;
    public static final int T__88=88;
    public static final int T__90=90;
    public static final int OPERATOR_REFERENCE=17;
    public static final int CANVAS_DEF=13;
    public static final int T__91=91;
    public static final int OPERATOR_TEMPLATE=18;
    public static final int CONSUMES=7;
    public static final int DYNAMIC=63;
    public static final int T__85=85;
    public static final int PYTHON=51;
    public static final int ANNOTATION=4;
    public static final int FEED=67;
    public static final int BOOLEAN_OP=5;
    public static final int ALL=35;
    public static final int T__93=93;
    public static final int T__86=86;
    public static final int MAP_ENTRY=34;
    public static final int T__94=94;
    public static final int EXTERNAL=40;
    public static final int LIST=12;
    public static final int GLYPH=11;
    public static final int T__95=95;
    public static final int OPERATOR_PROXY=16;
    public static final int GATE=70;
    public static final int RULE=28;
    public static final int PACK=25;
    public static final int TUPLE_PROTOTYPE=31;
    public static final int PROGRAM=24;
    public static final int T__87=87;
    public static final int GUIDE_FEED=68;
    public static final int OPERATOR_INSTANCE=15;
    public static final int GUIDE=44;
    public static final int LAYER=47;
    public static final int GROUP=55;
    public static final int NESTED_BLOCK=79;
    public static final int OPERATOR=48;
    public static final int JOIN=72;
    public static final int PYTHON_FACET=26;
    public static final int BASE=37;
    public static final int FROM=43;
    public static final int ID=74;
    public static final int STREAM=53;
    public static final int DIGITS=78;
    public static final int OPERATOR_RULE=19;
    public static final int TAGGED_ID=76;
    public static final int CODE_BLOCK=75;
    public static final int CALL_CHAIN=8;
    public static final int COMMENT=82;
    public static final int SPECIALIZER=30;
    public static final int CLOSE_ARG=58;
    public static final int GUIDE_YIELD=69;
    public static final int T__84=84;
    public static final int SEPARATOR=59;
    public static final int DEFINE=62;
    public static final int T__97=97;
    public static final int RETURN=27;
    public static final int ESCAPE_SEQUENCE=80;
    public static final int ARG=57;
    public static final int EOF=-1;
    public static final int TUPLE_FIELD_DEF=32;
    public static final int DEFAULT=39;
    public static final int T__83=83;

    // delegates
    // delegators


        public StreamDeclarationValidator(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public StreamDeclarationValidator(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return StreamDeclarationValidator.tokenNames; }
    public String getGrammarFileName() { return "/Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g"; }


      
      public void hasSource(External e, TuplePrototype prototype) {
        for (TupleFieldDef def: prototype) {
          if (SOURCE_FIELD.equals(def.getFieldName())) {return;}  
        }
         
        throw new ValidationException(format("Stream declaration %1$s does not include required field '%2$s' in prototype (common position is as the first field).", e.getName(), SOURCE_FIELD));
      }
      
      public void uniqueFieldNames(External e, TuplePrototype prototype) {
        String field = null;
        Set<String> fields = new HashSet<String>();
        
        for (TupleFieldDef def: prototype) {
          field = def.getFieldName();
          if (!fields.add(field)) {break;}
          else {field = null;}
        }  

        String stream = e.getName();  
        if (field != null) {
          throw new ValidationException(format("Duplicate field name in stream declaration %1$s: %2$s.", stream, field));
        }
        
        if (fields.contains(stream)) {
          throw new ValidationException(format("Field with same name as containing stream: %1$s", stream));
        }
      }
      



    // $ANTLR start "topdown"
    // /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g:92:1: topdown : ^(e= EXTERNAL ^(p= TUPLE_PROTOTYPE ( . )* ) ) ;
    public final void topdown() throws RecognitionException {
        CommonTree e=null;
        CommonTree p=null;

        try {
            // /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g:92:8: ( ^(e= EXTERNAL ^(p= TUPLE_PROTOTYPE ( . )* ) ) )
            // /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g:92:10: ^(e= EXTERNAL ^(p= TUPLE_PROTOTYPE ( . )* ) )
            {
            e=(CommonTree)match(input,EXTERNAL,FOLLOW_EXTERNAL_in_topdown65); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            p=(CommonTree)match(input,TUPLE_PROTOTYPE,FOLLOW_TUPLE_PROTOTYPE_in_topdown70); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g:92:43: ( . )*
                loop1:
                do {
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( ((LA1_0>=ANNOTATION && LA1_0<=97)) ) {
                        alt1=1;
                    }
                    else if ( (LA1_0==UP) ) {
                        alt1=2;
                    }


                    switch (alt1) {
                	case 1 :
                	    // /Volumes/scratch/jcottam/workspace/Stencil/Stencil/Core/stencil/parser/validators/StreamDeclarationValidator.g:92:43: .
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

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

                          uniqueFieldNames((External) e, (TuplePrototype) p);
                          hasSource((External) e, (TuplePrototype) p);
                       
            }

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


 

    public static final BitSet FOLLOW_EXTERNAL_in_topdown65 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TUPLE_PROTOTYPE_in_topdown70 = new BitSet(new long[]{0x0000000000000004L});

}