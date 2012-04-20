package stencil.modules.java;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject.Kind;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Operator;
import stencil.module.util.ann.Module;
import stencil.module.util.ann.StreamTypes;
import stencil.parser.string.DefaultSpecializers;
import stencil.types.Converter;
import stencil.interpreter.tree.Specializer;

/**Module for java compiler**/
@Module()
@StreamTypes(classes=GeneratorStream.class)
public class JavaC extends BasicModule {
	private static final String HEADER_KEY = "header";
	private static final String BODY_KEY = "body";
	private static final String CLASS_KEY = "class";
	
	@Operator(name="Java", spec="[ body:\"\", header:\"\"]", defaultFacet="####error####")
	@Description("Compile a java operator without state.")
	public static final class Function extends AbstractOperator<Function> {
		private final StencilOperator operator;
		private final OperatorData opData;
		
		public Function(OperatorData opData, Specializer spec) {
			super(opData);
			String code = Converter.toString(spec.get(BODY_KEY));
			String header = Converter.toString(spec.get(HEADER_KEY));

			String[] parts = code.split("=>");
			operator = JavaCompilerWrapper.compileFunc(header, parts[0].trim(), parts[1].trim());
			this.opData = blendOpData(operator.getOperatorData(), super.operatorData);
		}

		@Override
		public Invokeable getFacet(String name) {return operator.getFacet(name);}
		@Override
		public OperatorData getOperatorData() {return opData;}
		
		@Facet(alias="####error####")
		public String errorOut() {throw new RuntimeException("Mal-formed java function invoked.");}		
	}
	
	
	@Operator(name="JavaC", spec="[body:\"\", header:\"\", class:\"AbstractOperator.Statefull\"]", defaultFacet="####error####")
	@Description("Compile a java operator.  Expects full method declarations.")
	public static final class Stateful extends AbstractOperator.Statefull<Stateful> {
		private final StencilOperator operator;
		private final OperatorData opData;

		public Stateful(OperatorData opData, Specializer spec) {
			super(opData);
			
			String body = (String) spec.get(BODY_KEY);
			String header = (String) spec.get(HEADER_KEY);
			String clss = (String) spec.get(CLASS_KEY);				
			operator = JavaCompilerWrapper.compileOp(header, clss, body);
			this.opData = blendOpData(operator.getOperatorData(), super.operatorData);

		}		
		
		@Override
		public Invokeable getFacet(String name) {return operator.getFacet(name);}
		@Override
		public OperatorData getOperatorData() {return opData;}
		
		@Facet(alias="####error####")
		public String errorOut() {throw new RuntimeException("Mal-formed java function invoked.");}
	}

	
	/**Combines the default specializer from two OperatorData objects.
	 * Produces a new OperatorData object, based on the first argument but with the blended specializer.**/ 
	protected static OperatorData blendOpData(OperatorData custom, OperatorData defaults) {
		Specializer spec = DefaultSpecializers.blend(custom.defaultSpecializer(), defaults.defaultSpecializer());
		OperatorData copy = new OperatorData(custom);
		return copy.defaultSpecializer(spec);
	}
	
	
	/**Compiler infrastructure shared between all of the compiled methods.**/
	static class JavaCompilerWrapper {
		private static final String PACKAGE = "stencil.modules.adHoc";
		
		private static final String FUNCTION_BODY =
				"\n@Facet(alias={\"map\",\"query\"})\n public Object map%1$s {return %2$s;}";
		
		private static final String CLASS_PREFIX = 
			"package " + PACKAGE + ";\n" 
			+ "import stencil.module.util.OperatorData;\n"	//Meta-data classes
			+ "import stencil.module.util.ann.*;\n"			//Meta-data annotations
			+ "import stencil.module.operator.util.AbstractOperator;"
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
	
	
	/**
	 * Compile a String or other {@link CharSequence}, returning a Java
	 * {@link Class} instance that may be instantiated. This class is a Facade
	 * around {@link JavaCompiler} for a narrower use case, but a bit easier to use.
	 * <p>
	 * To compile a String containing source for a Java class which implements
	 * MyInterface:
	 * 
	 * <pre>
	 * ClassLoader classLoader = MyClass.class.getClassLoader(); // optional; null is also OK 
	 * List&lt;Diagnostic&gt; diagnostics = new ArrayList&lt;Diagnostic&gt;(); // optional; null is also OK
	 * JavaStringCompiler&lt;Object&gt; compiler = new JavaStringCompiler&lt;MyInterface&gt;(classLoader,
	 *       null);
	 * try {
	 *    Class&lt;MyInterface&gt; newClass = compiler.compile(&quot;com.mypackage.NewClass&quot;,
	 *          stringContaininSourceForNewClass, diagnostics, MyInterface);
	 *    MyInterface instance = newClass.newInstance();
	 *    instance.someOperation(someArgs);
	 * } catch (JavaStringCompilerException e) {
	 *    handle(e);
	 * } catch (IllegalAccessException e) {
	 *    handle(e);
	 * }
	 * </pre>
	 * 
	 * The source can be in a String, {@link StringBuffer}, or your own class which
	 * implements {@link CharSequence}. If you implement your own, it must be
	 * thread safe (preferably, immutable.)
	 * 
	 * @author <a href="mailto:David.Biesack@sas.com">David J. Biesack</a>
	 */
	public static final class CharSequenceCompiler<T> {
		/**
		 * An exception thrown when trying to compile Java programs from strings
		 * containing source.
		 * 
		 * @author <a href="mailto:David.Biesack@sas.com">David J. Biesack</a>
		 */
		public static final class CharSequenceCompilerException extends Exception {
		   private static final long serialVersionUID = 1L;
		   /**
		    * The fully qualified name of the class that was being compiled.
		    */
		   private Set<String> classNames;
		   // Unfortunately, Diagnostic and Collector are not Serializable, so we can't
		   // serialize the collector.
		   transient private DiagnosticCollector<JavaFileObject> diagnostics;

		   public CharSequenceCompilerException(String message,
		         Set<String> qualifiedClassNames, Throwable cause,
		         DiagnosticCollector<JavaFileObject> diagnostics) {
		      super(message, cause);
		      setClassNames(qualifiedClassNames);
		      setDiagnostics(diagnostics);
		   }
		   
		   @Override
		public String getMessage() {
			   StringBuilder base = new StringBuilder(super.getMessage() + "\n");
			   for (Diagnostic d: diagnostics.getDiagnostics()) {
				   base.append(d.getMessage(null));
				   base.append("\n");			   
			   }
			   return base.toString();
		   }

		   public CharSequenceCompilerException(String message,
		         Set<String> qualifiedClassNames,
		         DiagnosticCollector<JavaFileObject> diagnostics) {
		      super(message);
		      setClassNames(qualifiedClassNames);
		      setDiagnostics(diagnostics);
		   }

		   public CharSequenceCompilerException(Set<String> qualifiedClassNames,
		         Throwable cause, DiagnosticCollector<JavaFileObject> diagnostics) {
		      super(cause);
		      setClassNames(qualifiedClassNames);
		      setDiagnostics(diagnostics);
		   }

		   private void setClassNames(Set<String> qualifiedClassNames) {
		      // create a new HashSet because the set passed in may not
		      // be Serializable. For example, Map.keySet() returns a non-Serializable
		      // set.
		      classNames = new HashSet<String>(qualifiedClassNames);
		   }

		   private void setDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
		      this.diagnostics = diagnostics;
		   }

		   /**
		    * Gets the diagnostics collected by this exception.
		    * 
		    * @return this exception's diagnostics
		    */
		   public DiagnosticCollector<JavaFileObject> getDiagnostics() {
		      return diagnostics;
		   }

		   /**
		    * @return The name of the classes whose compilation caused the compile
		    *         exception
		    */
		   public Collection<String> getClassNames() {
		      return Collections.unmodifiableSet(classNames);
		   }
		}

		
		// Compiler requires source files with a ".java" extension:
		static final String JAVA_EXTENSION = ".java";

		private CharSequenceCompiler() {}

		/**
		 * Compile Java source in <var>javaSource</name> and return the resulting
		 * class.
		 * <p>
		 * Thread safety: this method is thread safe if the <var>javaSource</var>
		 * and <var>diagnosticsList</var> are isolated to this thread.
		 * 
		 * @param qualifiedClassName
		 *           The fully qualified class name.
		 * @param javaSource
		 *           Complete java source, including a package statement and a class,
		 *           interface, or annotation declaration.
		 * @param diagnosticsList
		 *           Any diagnostics generated by compiling the source are added to
		 *           this collector.
		 * @param types
		 *           zero or more Class objects representing classes or interfaces
		 *           that the resulting class must be assignable (castable) to.
		 * @return a Class which is generated by compiling the source
		 * @throws CharSequenceCompilerException
		 *            if the source cannot be compiled - for example, if it contains
		 *            syntax or semantic errors or if dependent classes cannot be
		 *            found.
		 * @throws ClassCastException
		 *            if the generated class is not assignable to all the optional
		 *            <var>types</var>.
		 */
		public static synchronized Class compile(final String qualifiedClassName, final CharSequence javaSource) 
			throws CharSequenceCompilerException, ClassCastException {
			
			assert javaSource != null && qualifiedClassName != null : "Must supply both a name and a source.";

			final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {
				throw new IllegalStateException("Cannot find the system Java compiler. "
						+ "Check that your class path includes tools.jar");
			}

			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();      
			final JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
			final ClassLoaderImpl classLoader = new ClassLoaderImpl(CharSequenceCompiler.class.getClassLoader());
			final FileManagerImpl javaFileManager = new FileManagerImpl(fileManager, classLoader);

			diagnostics = new DiagnosticCollector<JavaFileObject>();

			Map<String, CharSequence> classes = new HashMap<String, CharSequence>(1);
			classes.put(qualifiedClassName, javaSource);

			List<JavaFileObject> sources = new ArrayList<JavaFileObject>();
			final int dotPos = qualifiedClassName.lastIndexOf('.');
			final String className = dotPos == -1 ? qualifiedClassName
					: qualifiedClassName.substring(dotPos + 1);
			final String packageName = dotPos == -1 ? "" 
					: qualifiedClassName.substring(0, dotPos);
			final JavaFileObjectImpl source = new JavaFileObjectImpl(className, javaSource);
			javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, className + JAVA_EXTENSION, source);
			sources.add(source);

			// Get a CompliationTask from the compiler and compile the sources
			final CompilationTask task = compiler.getTask(null, javaFileManager, diagnostics, null, null, sources);
			final Boolean result = task.call();
			if (result == null || !result.booleanValue()) {
				throw new CharSequenceCompilerException("Compilation failed.", classes.keySet(), diagnostics);
			}

			try {
				final Class newClass = loadClass(classLoader, qualifiedClassName);
				return newClass;
			} catch (ClassNotFoundException e) {
				throw new CharSequenceCompilerException(classes.keySet(), e, diagnostics);
			} catch (IllegalArgumentException e) {
				throw new CharSequenceCompilerException(classes.keySet(), e, diagnostics);
			} catch (SecurityException e) {
				throw new CharSequenceCompilerException(classes.keySet(), e, diagnostics);
			}

		}

		/**
		 * Load a class that was generated by this instance or accessible from its
		 * parent class loader. Use this method if you need access to additional
		 * classes compiled by
		 * {@link #compile(String, CharSequence, DiagnosticCollector, Class...) compile()},
		 * for example if the primary class contained nested classes or additional
		 * non-public classes.
		 * 
		 * @param qualifiedClassName
		 *           the name of the compiled class you wish to load
		 * @return a Class instance named by <var>qualifiedClassName</var>
		 * @throws ClassNotFoundException
		 *            if no such class is found.
		 */
		@SuppressWarnings("unchecked")
		private static Class loadClass(final ClassLoader classLoader, final String qualifiedClassName)
		throws ClassNotFoundException {
			return classLoader.loadClass(qualifiedClassName);
		}

		/**
		 * COnverts a String to a URI.
		 * 
		 * @param name
		 *           a file name
		 * @return a URI
		 */
		static URI toURI(String name) {
			try {
				return new URI(name);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * A JavaFileManager which manages Java source and classes. This FileManager
	 * delegates to the JavaFileManager and the ClassLoaderImpl provided in the
	 * constructor. The sources are all in memory CharSequence instances and the
	 * classes are all in memory byte arrays.
	 */
	final static class FileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
		// the delegating class loader (passed to the constructor)
		private final ClassLoaderImpl classLoader;

		// Internal map of filename URIs to JavaFileObjects.
		private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

		/**
		 * Construct a new FileManager which forwards to the <var>fileManager</var>
		 * for source and to the <var>classLoader</var> for classes
		 * 
		 * @param fileManager
		 *           another FileManager that this instance delegates to for
		 *           additional source.
		 * @param classLoader
		 *           a ClassLoader which contains dependent classes that the compiled
		 *           classes will require when compiling them.
		 */
		public FileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
			super(fileManager);
			this.classLoader = classLoader;
		}

		/**
		 * For a given file <var>location</var>, return a FileObject from which the
		 * compiler can obtain source or byte code.
		 * 
		 * @param location
		 *           an abstract file location
		 * @param packageName
		 *           the package name for the file
		 * @param relativeName
		 *           the file's relative name
		 * @return a FileObject from this or the delegated FileManager
		 * @see javax.tools.ForwardingJavaFileManager#getFileForInput(javax.tools.JavaFileManager.Location,
		 *      java.lang.String, java.lang.String)
		 */
		@Override
		public FileObject getFileForInput(Location location, String packageName,
				String relativeName) throws IOException {
			FileObject o = fileObjects.get(uri(location, packageName, relativeName));
			if (o != null)
				return o;
			return super.getFileForInput(location, packageName, relativeName);
		}

		/**
		 * Store a file that may be retrieved later with
		 * {@link #getFileForInput(javax.tools.JavaFileManager.Location, String, String)}
		 * 
		 * @param location
		 *           the file location
		 * @param packageName
		 *           the Java class' package name
		 * @param relativeName
		 *           the relative name
		 * @param file
		 *           the file object to store for later retrieval
		 */
		public void putFileForInput(StandardLocation location, String packageName,
				String relativeName, JavaFileObject file) {
			fileObjects.put(uri(location, packageName, relativeName), file);
		}

		/**
		 * Convert a location and class name to a URI
		 */
		private URI uri(Location location, String packageName, String relativeName) {
			return CharSequenceCompiler.toURI(location.getName() + '/' + packageName + '/'
					+ relativeName);
		}

		/**
		 * Create a JavaFileImpl for an output class file and store it in the
		 * classloader.
		 * 
		 * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location,
		 *      java.lang.String, javax.tools.JavaFileObject.Kind,
		 *      javax.tools.FileObject)
		 */
		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName,
				Kind kind, FileObject outputFile) throws IOException {
			JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
			classLoader.add(qualifiedName, file);
			return file;
		}


		@Override
		public String inferBinaryName(Location loc, JavaFileObject file) {
			String result;
			// For our JavaFileImpl instances, return the file's name, else
			// simply run the default implementation
			if (file instanceof JavaFileObjectImpl)
				result = file.getName();
			else
				result = super.inferBinaryName(loc, file);
			return result;
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName,
				Set<Kind> kinds, boolean recurse) throws IOException {
			Iterable<JavaFileObject> result = super.list(location, packageName, kinds,
					recurse);
			ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();
			if (location == StandardLocation.CLASS_PATH
					&& kinds.contains(JavaFileObject.Kind.CLASS)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName))
						files.add(file);
				}
				files.addAll(classLoader.files());
			} else if (location == StandardLocation.SOURCE_PATH
					&& kinds.contains(JavaFileObject.Kind.SOURCE)) {
				for (JavaFileObject file : fileObjects.values()) {
					if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName))
						files.add(file);
				}
			}
			for (JavaFileObject file : result) {
				files.add(file);
			}
			return files;
		}
	}

	/**
	 * A JavaFileObject which contains either the source text or the compiler
	 * generated class. This class is used in two cases.
	 * <ol>
	 * <li>This instance uses it to store the source which is passed to the
	 * compiler. This uses the
	 * {@link JavaFileObjectImpl#JavaFileObjectImpl(String, CharSequence)}
	 * constructor.
	 * <li>The Java compiler also creates instances (indirectly through the
	 * FileManagerImplFileManager) when it wants to create a JavaFileObject for the
	 * .class output. This uses the
	 * {@link JavaFileObjectImpl#JavaFileObjectImpl(String, JavaFileObject.Kind)}
	 * constructor.
	 * </ol>
	 * This class does not attempt to reuse instances (there does not seem to be a
	 * need, as it would require adding a Map for the purpose, and this would also
	 * prevent garbage collection of class byte code.)
	 */
	final static class JavaFileObjectImpl extends SimpleJavaFileObject {
		// If kind == CLASS, this stores byte code from openOutputStream
		private ByteArrayOutputStream byteCode;

		// if kind == SOURCE, this contains the source text
		private final CharSequence source;

		/**
		 * Construct a new instance which stores source
		 * 
		 * @param baseName
		 *           the base name
		 * @param source
		 *           the source code
		 */
		JavaFileObjectImpl(final String baseName, final CharSequence source) {
			super(CharSequenceCompiler.toURI(baseName + CharSequenceCompiler.JAVA_EXTENSION),
					Kind.SOURCE);
			this.source = source;
		}

		/**
		 * Construct a new instance
		 * 
		 * @param name
		 *           the file name
		 * @param kind
		 *           the kind of file
		 */
		JavaFileObjectImpl(final String name, final Kind kind) {
			super(CharSequenceCompiler.toURI(name), kind);
			source = null;
		}

		/**
		 * Return the source code content
		 * 
		 * @see javax.tools.SimpleJavaFileObject#getCharContent(boolean)
		 */
		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
		throws UnsupportedOperationException {
			if (source == null)
				throw new UnsupportedOperationException("getCharContent()");
			return source;
		}

		/**
		 * Return an input stream for reading the byte code
		 * 
		 * @see javax.tools.SimpleJavaFileObject#openInputStream()
		 */
		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(getByteCode());
		}

		/**
		 * Return an output stream for writing the bytecode
		 * 
		 * @see javax.tools.SimpleJavaFileObject#openOutputStream()
		 */
		@Override
		public OutputStream openOutputStream() {
			byteCode = new ByteArrayOutputStream();
			return byteCode;
		}

		/**
		 * @return the byte code generated by the compiler
		 */
		byte[] getByteCode() {
			return byteCode.toByteArray();
		}
	}

	/**
	 * A custom ClassLoader which maps class names to JavaFileObjectImpl instances.
	 */
	final static class ClassLoaderImpl extends ClassLoader {
		private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

		ClassLoaderImpl(final ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		/**
		 * @return An collection of JavaFileObject instances for the classes in the
		 *         class loader.
		 */
		Collection<JavaFileObject> files() {
			return Collections.unmodifiableCollection(classes.values());
		}

		@Override
		protected Class<?> findClass(final String qualifiedClassName)
		throws ClassNotFoundException {
			JavaFileObject file = classes.get(qualifiedClassName);
			if (file != null) {
				byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
				return defineClass(qualifiedClassName, bytes, 0, bytes.length);
			}
			// Workaround for "feature" in Java 6
			// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434149
			try {
				Class<?> c = Class.forName(qualifiedClassName);
				return c;
			} catch (ClassNotFoundException nf) {
				// Ignore and fall through
			}
			return super.findClass(qualifiedClassName);
		}

		/**
		 * Add a class name/JavaFileObject mapping
		 * 
		 * @param qualifiedClassName
		 *           the name
		 * @param javaFile
		 *           the file associated with the name
		 */
		void add(final String qualifiedClassName, final JavaFileObject javaFile) {
			classes.put(qualifiedClassName, javaFile);
		}

		@Override
		protected synchronized Class<?> loadClass(final String name, final boolean resolve)
		throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}

		@Override
		public InputStream getResourceAsStream(final String name) {
			if (name.endsWith(".class")) {
				String qualifiedClassName = name.substring(0,
						name.length() - ".class".length()).replace('/', '.');
				JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(qualifiedClassName);
				if (file != null) {
					return new ByteArrayInputStream(file.getByteCode());
				}
			}
			return super.getResourceAsStream(name);
		}
	}

}
