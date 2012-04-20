package stencil.module;

import java.util.*;

import stencil.module.operator.StencilOperator;
import stencil.module.util.*;
import stencil.module.util.ann.StreamTypes;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.util.collections.PropertyUtils;


/**Utilities for handling module->method conversions.
 *
 * This is comprised of two halves: static and instance.
 *
 * The static half takes care of tracking the class path of Modules.  This is
 * shared by all concurrently running Stencils, but represents meta-data about
 * the Modules that the stencil system knows about.
 *
 * The instance half is specific to a Stencil program.  It is a list of
 * modules that have been imported into the name-space, as well as the contents
 * of those name-spaces for locating a Module, given a method name.  This instance
 * half adds all mapping functions defined in the Stencil itself (e.g. those done with
 * the operator or python keywords) to the name-space in a 'default' Module.
 *
 */
public class ModuleCache {
	/**Wrapper for keeping modules and prefixes separate.*/
	private static final class PrefixedModule {
		public String prefix;
		public Module module;
		public PrefixedModule(String prefix, Module module) {
			this.module = module;
			this.prefix = prefix;
		}

		public String getModuleName() {return  module.getModuleData().getName();}
		
		@Override
		public String toString() {return prefix + " [" + module.getModuleData().getName() + "]";}
	}

	/**Modules that have been loaded from the registered modules.*/
	protected Queue<PrefixedModule> importedModules;

	public ModuleCache() {reset();}

	/**Resets the imported modules list.*/
	public void reset() {
		StreamTypeRegistry.reset();
		
		importedModules = new LinkedList<PrefixedModule>();
		
		for (String module: defaultModules) {
			importModule(module, "");
		}
	}

	/**From the Modules imported into the stencil, find a given operator and specialize it.
	 *
	 * @param name
	 * @param specializer
	 * @return
	 */
	public StencilOperator instance(MultiPartName name, Context context, Specializer specializer) throws OperatorInstanceException {		
		try {
			Module module = findModuleForOperator(name);
			String operatorName = name.name();

			StencilOperator operator;
			operator = module.instance(operatorName, specializer);
			operator = module.optimize(operator, context);
			
			return operator;
		}
		catch (Exception e) {throw new OperatorInstanceException(name.toString(), true, e);}		
	}
		
	/** @param name operator name to find (should not include facet). Must be prefixed if corresponding module was imported prefixed. 
	 * @return Prefixed Module which contains the operator.
	 * @throws IllegalArgumentException Name does not indicate any known operator 
	 */
	public Module findModuleForOperator(MultiPartName name) throws IllegalArgumentException {
		String prefix = name.prefix();
		String opName = name.name();
		
		for (PrefixedModule pm: importedModules) {
			if (!prefix.equals(pm.prefix)) {continue;}
			for (String operatorName: pm.module.getModuleData().getOperatorNames()) {
				if (opName.equals(operatorName)) {
					return pm.module;
				}
			}
		}
		throw new IllegalArgumentException("Could not find module for operator named " + name.name());
	}
	
	/**Import a given Module into the method index so its members
	 * can be instantiated.
	 *
	 * @throws RuntimeException Thrown if a name is used twice in the methodIndex
	 */
	public void importModule(String moduleName, String prefix) {
		if (!registeredModules.containsKey(moduleName)) {throw new RuntimeException("Module name not registered: " + moduleName);}

		Module module = registeredModules.get(moduleName);
		PrefixedModule pm = new PrefixedModule(prefix, module);
		
		importedModules.offer(pm);

		//Import stream types from the module
		if (module.getClass().isAnnotationPresent(StreamTypes.class)) {
			StreamTypes st = module.getClass().getAnnotation(StreamTypes.class);
			for (Class c: st.classes()) {
				StreamTypeRegistry.register(c);
			}
		}
	}

	/**Get a module.  Only examines imported modules (does not consider "known" modules, 
	 * static methods should be used to access "known" modules).*/
	public Module getModule(String moduleName) {
		for(PrefixedModule pm: importedModules) {
			if (pm.prefix.equals("")) {
				if (pm.getModuleName().equals(moduleName)) {return pm.module;}	
			} else {
				if (pm.prefix.equals(moduleName)) {return pm.module;}
			}
		}
		throw new RuntimeException("Request made for operator that has not been imported or created: " + moduleName);
	}

	/**Key on properties in a properties list that indicates module to be registered.*/
	public static final String MODULE_KEY = "module";

	public static final String DEFAULT_MODULES_KEY ="defaultModules";
	
	/**An index of known Modules.
	 * The Module store is retained for an entire run, it feeds information to the
	 * more transient methodIndex.
	 * */
	protected static Map<String, Module> registeredModules = new TreeMap();

	protected static String[] defaultModules = new String[0];
	
	/**Load module entries from a property set.
	 * 
	 * Given a properties listing, looks for entries
	 * that conform to the modules property entry style (dictated by the MODULE_KEY).
	 * Each such entry found will be recorded in the static part of
	 * the Module cache.
	 * 
	 * @param props
	 */
	public static void registerModules(Properties props) {
		for (String key: PropertyUtils.filter(props, MODULE_KEY)) {
			String className = props.getProperty(key);
			
			Module m;
			ModuleData md;
			
			
			Class moduleClass;
			try {
				if (className.startsWith("file://")) {
					moduleClass = ClassLoader.getSystemClassLoader().loadClass(className);
				} else {
					moduleClass = Class.forName(className);
				}
				m = (Module) moduleClass.getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(String.format("Error accessing module class: " + className), e);
			}
			
			try {md = m.getModuleData();} 
			catch (Exception e) {throw new RuntimeException(String.format("Error parsing meta-data file %1$s.", className), e);}
			
			if (!key.endsWith(md.getName())) {
				throw new IllegalArgumentException(String.format("Configuration key did not match meta-data: Key: %1$s, meta-data: %2$s.", key.substring(key.indexOf(":")+1), md.getName()));
			}
			
			register(m);
		}
		
		Object defaults = props.get(DEFAULT_MODULES_KEY);
		if (defaults  != null) {
			defaultModules = defaults.toString().trim().split("\\s*,\\s*");
		}
	}
	
	/**Ensures that module is no longer known by the static module
	 * cache.  Removes the module if needed, but throws
	 * now exception if module was not already known.
	 * @param moduleName
	 */
	public static void remove(String moduleName) {registeredModules.remove(moduleName);}
	
	/**Remove all known modules from the static modules cache (nothing
	 * can be imported after this operation until more modules are registered.*/
	public static void clear() {registeredModules.clear();}
	
	/**Registers a Module (given by path) with a given name.  Registering
	 * makes the Stencil system aware of the Module, it can then be used
	 * in a stencil.
	 *
	 * The class specified in path must implement the Stencil Module interface
	 * and have a zero-argument constructor.
	 *
	 * @throws Error Any error while trying to find and construct the class indicated by path
	 * */
	public static void register(Module m) {
		ModuleData md = m.getModuleData();
		registeredModules.put(md.getName(), m);
	}


	/**Returns a read-only copy of the registered modules list.*/
	public static Map<String, Module> registeredModules() {
		return Collections.unmodifiableMap(registeredModules);
	}
}
