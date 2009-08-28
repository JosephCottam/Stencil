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
package stencil.util.streams.sql;

import java.net.URLClassLoader;
import java.net.URL;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Properties;


/**Manager of database drivers with related short and class names.
 * General principles:
 * 	+ URLs are parsed but not resolved until needed (e.g. a call to loadDriver is made for the associated class)
 *  + Short names are handled in a case insensitive way
 *  + Class names are handled in a case sensitive way
 */
public class DriverManager {
	public static final String DATABASE_KEY = "jdbcDriver";
	//Class loader for getting class references
	private static URLClassLoader loader;

	//Stores a list of JAR url's to search when a class needs to be loaded
	private static HashSet<URL> URLs = new HashSet<URL>();

	//Converts short names to full driver class names
	private static HashMap<String, String> nameToClass = new HashMap<String, String> ();

	/**Adds drivers as found in a properties list.   The properties are expected
	 * to conform to the following format:
	 * 		key=jdbcDriver:<shortName>
	 * 		value = <className>,<jarURL>
	 *
	 * @param props
	 * @throws Exception
	 */
	public static void addDrivers(Properties props) throws Exception {
		for (Object ky: props.keySet()) {
			String key = (String) ky;
			if (key.startsWith(DATABASE_KEY)) {
				String[] vals = props.getProperty(key).split(",");
				String shortName = key.substring(key.indexOf(":")+1);
				addDriver(shortName, vals[0], vals[1]);
			}
		}
	}

	/**Register a database driver with the manager.
	 *
	 * @param shortName The name the driver will be referred to ass
	 * @param className The actual class path to use
	 * @param jarURL The URL of the jar the class is found in.
	 * @throws Exception
	 */
	public static void addDriver(String shortName, String className, String jarURL) throws Exception {
		nameToClass.put(shortName.toUpperCase(), className);

		//Prep loader and remember URL (for future updates)
		URL url = new URL(jarURL);
		URLs.add(url);
		loader = new URLClassLoader(URLs.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
	}

	/**Load the driver associated with the given name.
	 *
	 * @param driverName Short Name or driver class name to use.
	 * @throws Exception Any/all URLClassLoader intialization or class loading errors
	 */
	public static java.sql.Connection connect(String driverName, String connect) throws Exception {
		if (loader == null) {throw new RuntimeException("No database drivers have been registered.");}

		String className = null;
		if (nameToClass.keySet().contains(driverName.toUpperCase())) {className = nameToClass.get(driverName.toUpperCase());}
		else if (nameToClass.entrySet().contains(driverName)) {className = driverName;}
		if (className == null) {
			throw new IllegalArgumentException(String.format("Could not identify %1$s as either a class name or short name for database drivers.", driverName));
		}

		Class c = Class.forName(className, true, loader);
		java.sql.Driver d = (java.sql.Driver) c.getConstructor().newInstance();

		String connectURL = getURL(connect);
		Properties p = getProperties(connect);
		return d.connect(connectURL, p);
	}

	private static Properties getProperties(String connect) {
		String propSet = connect.substring(getURL(connect).length()+1);
		String[] pairs = propSet.split(";");
		Properties p = new Properties();
		for (String pair:pairs) {
				try {
				String[] keyValue = pair.split("=");
				p.setProperty(keyValue[0], keyValue[1]);
			} catch (Exception e) {throw new RuntimeException("Database property format not recognized: " + pair,e);}
		}
		return p;
	}

	private static String getURL(String connect) {
		return connect.substring(0, connect.indexOf(";"));
	}

}
