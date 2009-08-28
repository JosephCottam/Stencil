package stencil.unittests.parser.string;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

import stencil.parser.string.StencilLexer;
import stencil.parser.string.StencilParser;

public class ParserUtils {
	public static StencilParser MakeParser(String source) {
		ANTLRStringStream input = new ANTLRStringStream(source);

		StencilLexer lexer = new StencilLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilParser parser = new StencilParser(tokens);
		return parser;
	}
}
