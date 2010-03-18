package stencil.parser.tree;

import static stencil.parser.ParserConstants.INIT_FACET;
import static stencil.parser.ParserConstants.MAIN_FACET;
import static stencil.parser.ParserConstants.QUERY_FACET;

import java.util.Map;
import org.antlr.runtime.Token;

/**In a Python group, there may be more than one block of executable
 * code.  Each of these is a Facet...captured here!
 */
public final class PythonFacet extends StencilTree {
	PythonFacet(Token token) {super(token);}

	public String getName() {return token.getText();}
	
	/**What is the prototype pair?*/
	protected Yields getYields() {return (Yields) getChild(0);}

	/**What annotations were included?*/
	public Map<String, Atom> getAnnotations() {return new MapEntry.MapList((List) getChild(1));}
	
	/**What is the facet body?  This is the actual executable code.*/
	public String getBody() {return getChild(2).getText();}
	
	/**Should only be used internally, had to be public though because of the package restrictions.*/
	public void setBody(String newBody) {((StencilTree) getChild(2)).getToken().setText(newBody);}
	
	/**What are the incoming arguments?*/
	public TuplePrototype getArguments() {return getYields().getInput();}

	/**What is the result prototype?*/
	public TuplePrototype getResults() {return getYields().getOutput();}

	//Blocks to test for special cases...
	public boolean isInit() {return token.getText().endsWith(INIT_FACET);}
	public boolean isMain() {return token.getText().endsWith(MAIN_FACET);}
	public boolean isQuery() {return token.getText().endsWith(QUERY_FACET);}
	
}
