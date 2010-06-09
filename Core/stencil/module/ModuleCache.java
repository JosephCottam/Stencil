/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.module;

import java.io.InputStream;
import java.util.*;

import stencil.module.operator.StencilOperator;
import stencil.module.util.*;
import stencil.parser.tree.Specializer;


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
		
		public String toString() {return prefix + "." + module.getModuleData().getName();}
	}

	/**Modules that have been loaded from the registered modules.*/
	protected Queue<PrefixedModule> importedModules;

	/**Identifier for a ModuleCache instance.  Used for debugging, and cannot be null.
	 * Attempting to set this value to null will not return an error, but will not have an effect either.*/
	private String cacheName = "";

	public ModuleCache() {this(null);}
	public ModuleCache(String name) {
		if(name != null) {this.cacheName = name;}
		reset();
	}

	public String getName() {return cacheName;}

	/**Resets the imported modules list.*/
	public void reset() {
		importedModules = new LinkedList<PrefixedModule>();
		importedModules.add(new PrefixedModule("", new MutableModule(AD_HOC_NAME)));
		
		for (String module: defaultModules) {
			importModule(module, "");
		}
	}


	/**From the Modules imported into the stencil, find a given method and
	 * specialize it.
	 *
	 * @param name
	 * @param specializer
	 * @return
	 */
	public StencilOperator instance(String name, Specializer specializer) throws MethodInstanceException {		
		try {
			Module module = findModuleForOperator(name);
			String operatorName = Modules.removePrefix(name);

			StencilOperator operator = module.instance(operatorName, specializer);
			return operator;
		}
		catch (Exception e) {throw new MethodInstanceException(name, true, e);}		
	}
	
	/** @param name operator name to find (should not include facet). Must be prefixed if corresponding module was imported prefixed. 
	 * @return Prefixed Module which contains the operator.
	 * @throws IllegalArgumentException Name does not indicate any known operator 
	 */
	public Module findModuleForOperator(String name) throws IllegalArgumentException {
		for (PrefixedModule pm: importedModules) {
			for (String operatorName: pm.module.getModuleData().getOperatorNames()) {
				if (name.equals(Modules.prefixName(pm.prefix, operatorName))) {
					return pm.module;
				}
			}
		}
		throw new IllegalArgumentException("Could not find module for operator named " + name);
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
	}

	/**Get a module.  Only examines imported modules (does not consider "known" modules, 
	 * static methods should be used to access "known" modules).*/
	public Module getModule(String name) {
		for(PrefixedModule pm: importedModules) {
			if (pm.prefix.equals("")) {
				if (pm.getModuleName().equals(name)) {return pm.module;}	
			} else {
				if (pm.prefix.equals(name)) {return pm.module;}
			}
		}
		throw new RuntimeException("Request made for module that has not been imported: " + name);
	}

	/**Return the ad-hoc module of this module cache.*/
	public MutableModule getAdHoc() {
		Module m = importedModules.peek().module;
		if (m instanceof MutableModule) {return (MutableModule) m;}
		throw new Error("Could not locate a module suitable for ad-hoc use.");
	}


	/**Key on properties in a properties list that indicates module to be registered.*/
	public static final String MODULE_KEY = "module";

	/**Name that should be used to identify the ad-hoc module.**/
	public static final String AD_HOC_NAME = "AdHoc";

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
		for (Object ky: props.keySet()) {
			String key = (String) ky;
			if (key.startsWith(MODULE_KEY)) {
				String filename = props.getProperty(key);
				
				Module m;
				ModuleData md;
				
				InputStream stream = ModuleCache.class.getResourceAsStream(filename);

				try {md = ModuleDataParser.load(stream);} 
				catch (Exception e) {throw new RuntimeException(String.format("Error parsing meta-data file %1$s.", filename), e);}
				
				if (!key.endsWith(md.getName())) {
					throw new RuntimeException(String.format("Configuration key did not match meta-data: Key: %1$s, meta-data: %2$s.", key.substring(key.indexOf(":")+1), md.getName()));
				}
 				
				try {m = md.getModule();}
				catch (Exception e) {throw new RuntimeException(String.format("Error instantiating module %1$s.", md.getName()), e);}
				
				register(m);
			}
		}
		
		String defaults = props.get(DEFAULT_MODULES_KEY).toString();
		if (defaults  != null) {
			defaultModules = defaults.trim().split("\\s*,\\s*");
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
