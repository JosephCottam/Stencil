package stencil.parser.tree;

import static stencil.parser.string.StencilParser.TUPLE_REF;

import org.antlr.runtime.Token;

import stencil.tuple.Tuple;

/**Base class of all Value objects.
 *
 * Non-Value tokens can be used as certain types of values (Id for example),
 * to facilitate this, if the type passed to the two-argument constructor
 * does not match the token type, a new token with the passed tokens text but
 * of the passed type instead of passed tokens type will be created.
 *
 *
 */
public abstract class Value extends StencilTree {
	public Value(Token source) {super(source);}
	public Value(Token source, int type) {
		super((source.getType() == type)?source:new org.antlr.runtime.CommonToken(type, source.getText()));
	//	if (source.getType() != type) {throw new IllegalArgumentException("Cannot construct Value tree with token of type " + StencilTree.typeName(source.getType()));}
	}

	public abstract Object getValue();
	public abstract Object getValue(Tuple source);

	/**Specific instances should override this if appropriate.*/
	public boolean isAtom() {return false;}

	/**Specific instances should override this if appropriate.*/
	public boolean isTupleRef() {return false;}

	public boolean isName() {return false;}

	public boolean isString() {return false;}
	public boolean isNumber() {return false;}
	public boolean isColor() {return false;}
	public boolean isAll() {return false;}


	public static Value instance(Token source) {
		if (source.getType() == TUPLE_REF) {return new TupleRef(source);}

		try {return Atom.instance(source);}
		catch (Exception e) {throw new IllegalArgumentException("Cannot make value from tree of type " + StencilTree.typeName(source.getType()), e);}
	}
}
