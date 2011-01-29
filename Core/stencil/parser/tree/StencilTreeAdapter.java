package stencil.parser.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

import stencil.parser.string.StencilParser;

public class StencilTreeAdapter extends CommonTreeAdaptor {

	@Override
	public Object create(Token token) {
		Object t;
		if (token == null) {t = new StencilTree(null);}
		else if (token.getType() == StencilParser.AST_INVOKEABLE) {t = new AstInvokeable(token);}
		else if (token.getType() == StencilParser.CONST) {t = new Const(token);}
		else if (token.getType() == StencilParser.OPERATOR_PROXY) {t = new OperatorProxy(token);}
		else {t = new StencilTree(token);}
		return t;
	}
}