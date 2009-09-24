package stencil.parser.tree;

import static stencil.parser.ParserConstants.INIT_BLOCK_TAG;
import static stencil.parser.ParserConstants.ITERATE_BLOCK_TAG;
import static stencil.parser.ParserConstants.MAIN_BLOCK_TAG;
import static stencil.parser.ParserConstants.QUERY_BLOCK_TAG;

import org.antlr.runtime.Token;

import stencil.util.ANTLRTree;
import stencil.util.ANTLRTree.NameNotFoundException;

/**In a Python group, there may be more than one block of executable
 * code.  Each of these is a Facet...captured here!
 */
public final class PythonFacet extends StencilTree {
	PythonFacet(Token token) {super(token);}

	public String getName() {return token.getText();}
	
	/**What is the prototype pair?*/
	protected Yields getYields() {return (Yields) getChild(0);}

	/**What annotations were included?*/
	public List<Annotation> getAnnotations() {return (List<Annotation>) getChild(1);}
	
	/**Get a particular annotations value.*/
	public String getAnnotation(String name) throws NameNotFoundException {
		return ANTLRTree.search(getAnnotations(), name).getChild(0).getText();
	}
	
	/**What is the facet body?  This is the actual executable code.*/
	public String getBody() {return getChild(2).getText();}
	
	/**Should only be used internally, had to be public though because of the package restrictions.*/
	public void setBody(String newBody) {((StencilTree) getChild(2)).getToken().setText(newBody);}
	
	/**What are the incoming arguments?*/
	public TuplePrototype getArguments() {return getYields().getInput();}

	/**What is the result prototype?*/
	public TuplePrototype getResults() {return getYields().getOutput();}

	//Blocks to test for special cases...
	public boolean isInit() {return token.getText().endsWith(INIT_BLOCK_TAG);}
	public boolean isMain() {return token.getText().endsWith(MAIN_BLOCK_TAG);}
	public boolean isQuery() {return token.getText().endsWith(QUERY_BLOCK_TAG);}
	public boolean isIterate() {return token.getText().endsWith(ITERATE_BLOCK_TAG);}
	
}
