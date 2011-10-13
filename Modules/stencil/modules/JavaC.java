package stencil.modules;

import static java.lang.String.format;

import java.lang.reflect.Constructor;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Operator;
import stencil.module.util.ann.Module;
import stencil.parser.string.util.CharSequenceCompiler;
import stencil.types.Converter;
import stencil.interpreter.tree.Specializer;

/**Module for java compiler**/
@Module()
public class JavaC extends BasicModule {
	private static final String HEADER_KEY = "header";
	private static final String BODY_KEY = "body";
	private static final String CLASS_KEY = "class";
	
	@Operator(name="Java", spec="[ body:\"\", header:\"import static java.util.Math.*;\"]")
	@Description("Compile a java operator without state.")
	public static class Function extends AbstractOperator<Function> {
		final StencilOperator operator;
		
		public Function(OperatorData opData, Specializer spec) {
			super(opData);
			String code = Converter.toString(spec.get(BODY_KEY));
			String header = Converter.toString(spec.get(HEADER_KEY));

			String[] parts = code.split("=>");
			operator = JavaCompiler.compileFunc(header, parts[0].trim(), parts[1].trim());			
		}

		public Invokeable getFacet(String name) {return operator.getFacet(name);}
		public OperatorData getOperatorData() {return operator.getOperatorData();}
	}
	
	
	@Operator(name="JavaC", spec="[body:\"\", header:\"\", class:\"\"]")
	@Description("Compile a java operator.  Expects full method declarations.")
	public static class Stateful extends AbstractOperator.Statefull<Stateful> {
		StencilOperator operator;
		
		public Stateful(OperatorData opData, Specializer spec) {
			super(opData);
			
			String body = (String) spec.get(BODY_KEY);
			String header = (String) spec.get(HEADER_KEY);
			String clss = (String) spec.get(CLASS_KEY);				
			operator = JavaCompiler.compileOp(header, clss, body);
		}		
		
		public Invokeable getFacet(String name) {return operator.getFacet(name);}
		public OperatorData getOperatorData() {return operator.getOperatorData();}
	}

	
	
	/**Compiler infrastructure shared between all of the compiled methods.**/
	private static class JavaCompiler {
		private static final String PACKAGE = "stencil.modules.adHoc";
		
		private static final String FUNCTION_BODY =
				"\n@Facet(alias={\"map\",\"query\"})\n public Object map%1$s {return %2$s;}";
		
		private static final String CLASS_PREFIX = 
			"package " + PACKAGE + ";\n" 
			+ "import stencil.module.util.OperatorData;\n"	//Meta-data classes
			+ "import stencil.module.util.ann.*;\n"			//Meta-data annotations
			+ "%1$s\n"										//For user-specified imports
			+ "@Operator\n"									//Operator tag
			+ "public class %2$s extends %3$s {\n %4$s %5$s}";
		
		
		private static final String DEFAULT_CONSTRUCTOR1 = "public %1$s(OperatorData od)";
		private static final String DEFAULT_CONSTRUCTOR2 = " {super(od);}";

		//Reasonable name for uniqueness...
		public static String genName() {return "__JavaCompilerNamed__" + number++;}
		private static int number =0;

		
		public static StencilOperator compileFunc(String header, String args, String methodBody) {
			String method = String.format(FUNCTION_BODY, args, methodBody);
			
			return compileOp(header, AbstractOperator.class.getCanonicalName(), method);
		}
		
		public static StencilOperator compileOp(String header, String clss, String body) {
			String name = genName();

			String constructor = "";
			if (!body.contains(format(DEFAULT_CONSTRUCTOR1, name))) {
				constructor = format(DEFAULT_CONSTRUCTOR1, name) + DEFAULT_CONSTRUCTOR2;
			} 
			
			String toCompile = format(CLASS_PREFIX, header, name, clss, constructor, body);

			StencilOperator op = null;
			OperatorData od;
			
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
			
		private static String qualify(String name) {return PACKAGE + "." + name;}
	}
}
