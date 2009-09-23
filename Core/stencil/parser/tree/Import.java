package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.operator.module.ModuleCache;

public class Import extends StencilTree {
	Import(Token token) {super(token);}

	public String getName() {return token.getText();}
	public java.util.List<Atom> getArguments() {return (List) getChild(1);}
	public String getPrefix() {return ((Id) getChild(0)).getName();}


	/**Import the module specified by this node to the
	 * module cache indicated.
	 * @param target  ModuleCache to import into
	 */
	public void doImport(ModuleCache target) {
		target.importModule(getName(), getPrefix());
	}
}
