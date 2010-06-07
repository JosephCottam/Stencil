grammar StencilTreeAdapter;
options {
	language = Java;
	output=template;
}

@header{
	package stencil.parser.tree;

	import java.util.Set;
	import java.util.HashSet;
	import java.util.Map;
	import java.util.HashMap;
}
@lexer::header{package stencil.parser.tree;}

@members{
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
}

entries: e+=entry+ -> adapter(clauses={$e});
entry : i=ID EQ v=INT
			-> {Util.inError($i.text)}?	 errorEntry(token={$i.text}, val={$v.text})
			-> {Util.inDefaults($i.text)}? defaultEntry(token={$i.text})
			-> entry(token={$i.text}, val={$v.text}, class={Util.lookupClass($i.text)})
	  | ID_IGNORE EQ INT;




ID: ('a'..'z' | 'A'..'Z' | '_')+;
INT: '0'..'9'+;
EQ: '=';

//These are auto-generated, internal token types for ANTLR.
//Ignored as they should never exist in a final Stencil AST.
ID_IGNORE
	: '\'' .* '\''
	| ID INT;
WS	:	(' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();};

