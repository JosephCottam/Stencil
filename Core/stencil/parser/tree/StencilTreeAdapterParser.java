// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g 2010-04-29 18:00:40

	package stencil.parser.tree;

	import java.util.Set;
	import java.util.HashSet;
	import java.util.Map;
	import java.util.HashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
@SuppressWarnings("all")
public class StencilTreeAdapterParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "EQ", "INT", "ID_IGNORE", "WS"
    };
    public static final int WS=8;
    public static final int ID_IGNORE=7;
    public static final int INT=6;
    public static final int EQ=5;
    public static final int ID=4;
    public static final int EOF=-1;

    // delegates
    // delegators


        public StencilTreeAdapterParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public StencilTreeAdapterParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected StringTemplateGroup templateLib =
      new StringTemplateGroup("StencilTreeAdapterParserTemplates", AngleBracketTemplateLexer.class);

    public void setTemplateLib(StringTemplateGroup templateLib) {
      this.templateLib = templateLib;
    }
    public StringTemplateGroup getTemplateLib() {
      return templateLib;
    }
    /** allows convenient multi-value initialization:
     *  "new STAttrMap().put(...).put(...)"
     */
    public static class STAttrMap extends HashMap {
      public STAttrMap put(String attrName, Object value) {
        super.put(attrName, value);
        return this;
      }
      public STAttrMap put(String attrName, int value) {
        super.put(attrName, new Integer(value));
        return this;
      }
    }

    public String[] getTokenNames() { return StencilTreeAdapterParser.tokenNames; }
    public String getGrammarFileName() { return "/Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g"; }


       	private static abstract class Util {
            	private static Map<String, String> classes = new HashMap<String,String>();
            	private static Set<String> error = new HashSet<String>();

            	static {
        			error.add("ARG");
        			error.add("CLOSE_ARG");
        			error.add("CLOSE_GROUP");
        			error.add("COMMENT");
        			error.add("DIGITS");
        			error.add("ESCAPE_SEQUENCE");
        			error.add("JOIN");
        			error.add("OPEN_GROUP");
        			error.add("SEPARATOR");
        			error.add("WS");

           			try {getClasses("stencil.parser.tree");}
            			catch (Exception e) {throw new RuntimeException("Error loading classes list.",e);}
                	}


            		//Based on http://forums.sun.com/thread.jspa?threadID=341935
            		public static void getClasses(String pckgname)
            				throws ClassNotFoundException {
            			// Get a File object for the package
            			java.io.File directory = null;
            			try {
            				ClassLoader cld = Thread.currentThread().getContextClassLoader();
            				if (cld == null) {
            					throw new ClassNotFoundException("Can't get class loader.");
            				}
            				String path = pckgname.replace('.', '/');
            				java.net.URL resource = cld.getResource(path);
            				if (resource == null) {
            					throw new ClassNotFoundException("No resource for " + path);
            				}
            				directory = new java.io.File(resource.getFile());
            			} catch (NullPointerException x) {
            				throw new ClassNotFoundException(pckgname + " (" + directory
            						+ ") does not appear to be a valid package");
            			}
               			if (directory.exists()) {
                				// Get the list of the files contained in the package
                				String[] files = directory.list();
                				for (int i = 0; i < files.length; i++) {
                					// we are only interested in .class files
                					String name = files[i];
                					if (name.endsWith(".class")) {
                						// removes the .class extension
                						name = files[i].substring(0, files[i].length() - 6);
                						String key = name.toUpperCase();
                						
                						
                						if (key.startsWith("STENCIL")) {
                							key = key.substring("STENCIL".length());
                						}
            							classes.put(key, name);
                					}
                				}
                			} else {
                				throw new ClassNotFoundException(pckgname
                						+ " does not appear to be a valid package");
                			}

                		}


                 	public static String lookupClass(String name) {return classes.get(properCase(name));}

                	public static String properCase(String name) {
                		String rslt = name.toUpperCase().replace("_", "");
                		return rslt;
                	}

            	public static boolean inDefaults(String name) {return !classes.containsKey(properCase(name));}
            	public static boolean inError(String name) {return error.contains(name);}

            }


    public static class entries_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "entries"
    // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:126:1: entries : (e+= entry )+ -> adapter(clauses=$e);
    public final StencilTreeAdapterParser.entries_return entries() throws RecognitionException {
        StencilTreeAdapterParser.entries_return retval = new StencilTreeAdapterParser.entries_return();
        retval.start = input.LT(1);

        List list_e=null;
        RuleReturnScope e = null;
        try {
            // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:126:8: ( (e+= entry )+ -> adapter(clauses=$e))
            // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:126:10: (e+= entry )+
            {
            // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:126:11: (e+= entry )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||LA1_0==ID_IGNORE) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:126:11: e+= entry
            	    {
            	    pushFollow(FOLLOW_entry_in_entries48);
            	    e=entry();

            	    state._fsp--;

            	    if (list_e==null) list_e=new ArrayList();
            	    list_e.add(e.getTemplate());


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



            // TEMPLATE REWRITE
            // 126:20: -> adapter(clauses=$e)
            {
                retval.st = templateLib.getInstanceOf("adapter",
              new STAttrMap().put("clauses", list_e));
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entries"

    public static class entry_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "entry"
    // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:127:1: entry : (i= ID EQ v= INT -> {Util.inError($i.text)}? errorEntry(token=$i.textval=$v.text) -> {Util.inDefaults($i.text)}? defaultEntry(token=$i.text) -> entry(token=$i.textval=$v.textclass=Util.lookupClass($i.text)) | ID_IGNORE EQ INT );
    public final StencilTreeAdapterParser.entry_return entry() throws RecognitionException {
        StencilTreeAdapterParser.entry_return retval = new StencilTreeAdapterParser.entry_return();
        retval.start = input.LT(1);

        Token i=null;
        Token v=null;

        try {
            // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:127:7: (i= ID EQ v= INT -> {Util.inError($i.text)}? errorEntry(token=$i.textval=$v.text) -> {Util.inDefaults($i.text)}? defaultEntry(token=$i.text) -> entry(token=$i.textval=$v.textclass=Util.lookupClass($i.text)) | ID_IGNORE EQ INT )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==ID) ) {
                alt2=1;
            }
            else if ( (LA2_0==ID_IGNORE) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:127:9: i= ID EQ v= INT
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_entry67); 
                    match(input,EQ,FOLLOW_EQ_in_entry69); 
                    v=(Token)match(input,INT,FOLLOW_INT_in_entry73); 


                    // TEMPLATE REWRITE
                    // 128:4: -> {Util.inError($i.text)}? errorEntry(token=$i.textval=$v.text)
                    if (Util.inError((i!=null?i.getText():null))) {
                        retval.st = templateLib.getInstanceOf("errorEntry",
                      new STAttrMap().put("token", (i!=null?i.getText():null)).put("val", (v!=null?v.getText():null)));
                    }
                    else // 129:4: -> {Util.inDefaults($i.text)}? defaultEntry(token=$i.text)
                    if (Util.inDefaults((i!=null?i.getText():null))) {
                        retval.st = templateLib.getInstanceOf("defaultEntry",
                      new STAttrMap().put("token", (i!=null?i.getText():null)));
                    }
                    else // 130:4: -> entry(token=$i.textval=$v.textclass=Util.lookupClass($i.text))
                    {
                        retval.st = templateLib.getInstanceOf("entry",
                      new STAttrMap().put("token", (i!=null?i.getText():null)).put("val", (v!=null?v.getText():null)).put("class", Util.lookupClass((i!=null?i.getText():null))));
                    }


                    }
                    break;
                case 2 :
                    // /Volumes/scratch/jcottam/Stencil/Stencil/Core/stencil/parser/grammars/StencilTreeAdapter.g:131:6: ID_IGNORE EQ INT
                    {
                    match(input,ID_IGNORE,FOLLOW_ID_IGNORE_in_entry136); 
                    match(input,EQ,FOLLOW_EQ_in_entry138); 
                    match(input,INT,FOLLOW_INT_in_entry140); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "entry"

    // Delegated rules


 

    public static final BitSet FOLLOW_entry_in_entries48 = new BitSet(new long[]{0x0000000000000092L});
    public static final BitSet FOLLOW_ID_in_entry67 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EQ_in_entry69 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_entry73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_IGNORE_in_entry136 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_EQ_in_entry138 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_entry140 = new BitSet(new long[]{0x0000000000000002L});

}