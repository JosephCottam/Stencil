package stencil.parser.string.util;

import static java.lang.String.format;

import java.lang.reflect.Constructor;

import stencil.module.operator.StencilOperator;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;

public class JavaCompiler {
	private static final String PACKAGE = "stencil.modules.adHoc";
	private static final String CLASS_PREFIX = 
		"package " + PACKAGE + ";\n" 
		+ "import stencil.module.util.OperatorData;\n"	//Meta-data classes
		+ "import stencil.module.util.ann.*;\n"			//Meta-data annotations
		+ "%1$s\n"										//For user-specified imports
		+ "@Operator\n"									//Operator tag
		+ "public class %2$s extends %3$s {\n %4$s %5$s}";
	
	
	private static final String DEFAULT_CONSTRUCTOR1 = "public %1$s(OperatorData od)";
	private static final String DEFAULT_CONSTRUCTOR2 = " {super(od);}";
	
	
	public static StencilOperator compile(String name, String superClass, String header, String body) {
		header = clean(header);
		body = clean(body);
		
		StencilOperator op = null;
		OperatorData od;
		
		String constructor = "";
		if (!body.contains(format(DEFAULT_CONSTRUCTOR1, name))) {
			constructor = format(DEFAULT_CONSTRUCTOR1, name) + DEFAULT_CONSTRUCTOR2;
		} 
		
		String toCompile = format(CLASS_PREFIX, header, name, superClass, constructor, body);
		try {
			Class opClass = CharSequenceCompiler.compile(qualify(name),toCompile);
			Constructor<StencilOperator> c = opClass.getConstructor(OperatorData.class);

			try {od = ModuleDataParser.operatorData(opClass, "<AdHoc>");}
			catch (Exception e) {throw new RuntimeException("Metadata error for ad-hoc operator " + name, e);}
			assert od != null : "Null meta-data found in operator data definition";
			
			op = c.newInstance(od);
		} catch (Exception e) {
			System.err.println(toCompile);
			throw new RuntimeException("Error compiling class " + name,e);
		}
		assert op != null : "Operator not created when expected";
		return op;
	}
	
	private static String clean(String text) {
		text = text.trim();
		text = text.substring(1, text.length()-1);
		return text;
	}

	private static String qualify(String name) {return PACKAGE + "." + name;}
}
