package stencil.parser.tree;

import java.lang.reflect.Constructor;

import org.antlr.runtime.Token;

import stencil.module.operator.StencilOperator;
import stencil.module.util.*;
import stencil.parser.string.util.CharSequenceCompiler;
import static java.lang.String.format;

public class Java extends StencilTree {
	private static final String PACKAGE = "stencil.modules.adHoc";
	private static final String CLASS_PREFIX = 
		"package " + PACKAGE + ";\n" 
		+ "import stencil.module.util.OperatorData;\n"	//Meta-data classes
		+ "import stencil.module.util.ann.*;\n"			//Meta-data annotations
		+ "%1$s\n"										//For user-specified imports
		+ "@Operator\n"									//Operator tag
		+ "public class %2$s extends stencil.module.operator.util.AbstractOperator {\n %3$s %4$s}";
	
	
	private static final String DEFAULT_CONSTRUCTOR1 = "public %1$s(OperatorData od)";
	private static final String DEFAULT_CONSTRUCTOR2 = " {super(od);}";
	
	private OperatorData od;
	private StencilOperator op;
	
	public Java(Token token) {super(token);}

	public String name() {return this.getText();}
	public OperatorData operatorData() {
		if (od == null) {operator();}//touching the operator also calculates the meta-data
		return od;
	}
	
	private String bodyText() {
		String base =getChild(1).getText().trim();
		base = base.substring(1, base.length()-1);//Remove braces
		return base;
	}
	
	private String headerText() {
		String base =getChild(0).getText().trim();
		base = base.substring(1, base.length()-1);//Remove braces
		return base;
	}
		
	public StencilOperator operator() {
		if (op == null) {
			String constructor = "";
			if (!bodyText().contains(format(DEFAULT_CONSTRUCTOR1, name()))) {
				constructor = format(DEFAULT_CONSTRUCTOR1, name()) + DEFAULT_CONSTRUCTOR2;
			} 
			
			String toCompile = format(CLASS_PREFIX, headerText(), name(), constructor, bodyText());
			try {
				Class opClass = CharSequenceCompiler.compile(qualify(name()),toCompile);
				Constructor<StencilOperator> c = opClass.getConstructor(OperatorData.class);
	
				try {od = ModuleDataParser.operatorData(opClass, "<AdHoc>");}
				catch (Exception e) {throw new RuntimeException("Metadata error for ad-hoc operator " + name(), e);}
				assert od != null : "Null meta-data found in operator data definition";
				
				op = c.newInstance(od);
			} catch (Exception e) {
				System.err.println(toCompile);
				throw new RuntimeException("Error compiling class " + name(),e);
			}
			assert op != null : "Operator not created when expected";
		}
		return op;
	}
	
	public static String qualify(String name) {return PACKAGE + "." + name;}
	
	public Java dupNode() {
		Java n = (Java) super.dupNode();
		n.od = od;
		n.op = op;
		return n;
	}

}
