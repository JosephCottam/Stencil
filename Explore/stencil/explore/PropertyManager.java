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
package stencil.explore;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static stencil.explore.Application.reporter;
import stencil.explore.model.AdapterOpts;
import stencil.explore.ui.interactive.Interactive;
import stencil.WorkingDirectory;


public class PropertyManager {
	private static final String DEFAULT_SESSION_CONFIGURATION_FILE = "ExploreSession.properties";
	private static final String DEFAULT_EXPLORE_CONFIGURATION_FILE = "Explore.properties";

	public static String stencilConfig = stencil.Configure.stencilConfig;
	public static String exploreConfig = DEFAULT_EXPLORE_CONFIGURATION_FILE;
	public static String sessionConfig = DEFAULT_SESSION_CONFIGURATION_FILE;

	
	//Keys configuration file information
	public static final String SESSION_FILE_KEY = "defaultStencil";
	public static final String FONT_SIZE_KEY = "fontSize";
	public static final String RASTER_RESOLUTION_KEY = "exportResolution";
	public static final String ADAPTER_PREFIX = "adapter:";
	public static final String WORKING_DIR_KEY = "workingDir";
	public static final String CONCURRENT_BIAS_KEY = "concurrentBias";
	public static final String DEFAULT_ADAPTER = "defaultAdapter";
	
	/**Load properties from either the default file or an argument specified in args.
	 * If the settings flag appears in args, it will load that file.  Otherwise, it
	 * uses the default.
	 *
	 * @param args
	 * @return
	 */
	public static String[] getConfigFiles(String[] args) {
		List<String> files = new ArrayList();
		
		if (args != null) {
			for (int i=0; i< args.length; i++) {
				if (Application.SETTINGS_FLAG.equals(args[i])) {files.add(args[i+1]);}
			}
		}
		return files.toArray(new String[files.size()]);
	}

	/**Loads the configuration information from the specified files.
	 * Elements in the array have precedence over elements directly passed
	 * (and thus are loaded later).*/
	public static Properties loadProperties(String[] configs, String...additionalConfigs) {
		String[] allConfigs = new String[configs.length + additionalConfigs.length];
		
		System.arraycopy(additionalConfigs, 0, allConfigs, 0, additionalConfigs.length);
		System.arraycopy(configs, 0, allConfigs, additionalConfigs.length, configs.length);
		
		return loadProperties(allConfigs);
	}
	
	/**Loads the configuration information from the specified files.
	 * The file is assumed to conform to the java standard XML properties file schema.
	 *
	 * To provide 'cascading' values, files are loaded first to last.
	 * The last setting of a property 'wins'.
	 *
	 * @param configFile
	 * @return
	 */
	private static Properties loadProperties(String... configFiles) {
		Properties p = new Properties();
		URL base;
		
		try {base = new java.io.File(System.getProperty("user.dir")).toURI().toURL();}
		catch (Exception e) {throw new Error("Error initailizing context.");}

		Properties props = new Properties();
		for (String file: configFiles) {
			try {props.loadFromXML(new URL(base, file).openStream());}
			catch (Exception e) {
				System.err.println(String.format("Error loading properties from %1$s. (%2$s) Ignoring error and continuing.", file, e.getMessage()));
			}
			p.putAll(props);
		}
		
		if (p.size() == 0) {throw new Error("No configuration files were loaded.  Aborting Stencil startup.");}

		//Setup session file information for interactive application
		Interactive.sessionFile = p.getProperty(SESSION_FILE_KEY);

		//Setup database driver map
		try {stencil.util.streams.sql.DriverManager.addDrivers(props);}
		catch (Exception e) {
			reporter.addError("Error loading database drivers: %1$s.", e.getMessage());
			e.printStackTrace();
		}

		//Inform stencil of its options
		stencil.Configure.loadProperties(props);

		//Setup adapter map
		Map<String, String> adapterMap = new TreeMap<String, String>();
		for (Object ky: p.keySet()) {
			String key = (String) ky;
			if (key.startsWith(ADAPTER_PREFIX)){
				String name = key.substring(ADAPTER_PREFIX.length());
				adapterMap.put(name, p.getProperty(key));
			}
		}
		String defaultAdapter = p.getProperty(DEFAULT_ADAPTER);
		if (defaultAdapter == null) {defaultAdapter = adapterMap.values().iterator().next();}
		if (defaultAdapter == null) {throw new Error("No stencil adapters found.  Stencil configuration invalid.");}
		
		adapterMap.put("(" + defaultAdapter + ")", adapterMap.get(defaultAdapter));
		
		AdapterOpts.adapterMap = adapterMap;

		if (p.containsKey(FONT_SIZE_KEY)) {
			try {Interactive.FONT_SIZE = Float.parseFloat(p.getProperty(FONT_SIZE_KEY));}
			catch(Exception e) {System.err.println("Error setting font size from settings file.");}
		}

		if (p.containsKey(RASTER_RESOLUTION_KEY)) {
			try {Application.EXPORT_RESOLUTION = Integer.parseInt(p.getProperty(RASTER_RESOLUTION_KEY));}
			catch(Exception e) {System.err.println("Error setting raster resolution from settings file.");}
		}

		if (p.containsKey(CONCURRENT_BIAS_KEY)) {
			try {Application.CONCURRENT_BIAS= Integer.parseInt(p.getProperty(CONCURRENT_BIAS_KEY));}
			catch(Exception e) {System.err.println("Error setting concurrent bias from settings file.");}
		}

		if (p.containsKey(WORKING_DIR_KEY)) {
			try {WorkingDirectory.setWorkingDir(p.getProperty(WORKING_DIR_KEY));}
			catch(Exception e) {System.err.println("Error setting last file from settings file.");}
		} else {
			//If no working directory is specified, use the system user directory
			try {WorkingDirectory.setWorkingDir(System.getProperty("user.dir"));}
			catch (Exception e) {System.out.println("Error determining working directory (default is user directory).");}
		}
		return props;
	}

	/**Save a configuration file name.*/
	public static void setExploreConfig(String filename) {exploreConfig = filename;}

	/**Save explore-based configuration information to the indicated file.
	 * This does not save information that is stencil-wide.  That must be added
	 * to the stencil configuration file manually.
	 * */
	public static void saveSessionProperties(String filename) {
		Properties p = new Properties();

		p.setProperty(WORKING_DIR_KEY, WorkingDirectory.getWorkingDir());

		try {p.storeToXML(new java.io.FileOutputStream(filename), "");}
		catch (Exception e) {throw new RuntimeException(String.format("Error saving configuration info to %1$s.", filename), e);}
	}
}
