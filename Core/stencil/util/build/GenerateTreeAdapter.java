package stencil.util.build;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.stringtemplate.*;
import java.io.*;

import stencil.parser.tree.*;

public class GenerateTreeAdapter {
	public String loadem(ANTLRStringStream input, String template) throws Exception {
		StencilTreeAdapterLexer lexer = new StencilTreeAdapterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilTreeAdapterParser parser = new StencilTreeAdapterParser(tokens);

		StringTemplateGroup templates = new StringTemplateGroup(new FileReader(template));
		parser.setTemplateLib(templates);

		StencilTreeAdapterParser.entries_return rv = parser.entries();
		return rv.toString();
	}

	public static void main(String[] args) throws Exception {
		if (args.length <3) {
			System.out.println("Usage: GenerateTreeAdapter <tokens> <template> <java-output>");
			System.exit(-1);
		}

		ANTLRFileStream input = new ANTLRFileStream(args[0]);
		String template = args[1];
		String rv = new GenerateTreeAdapter().loadem(input, template);

		FileWriter writer = new FileWriter(args[2]);
		writer.write(rv);
		writer.close();
	}
}
