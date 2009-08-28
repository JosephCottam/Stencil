package stencil;

import java.util.Properties;
import java.io.FileInputStream;



/**Methods to configure the Stencil system based
 * upon properties files specified on the system.
 * The loadProperties method should be invoked at least
 * once per application instance before using the Stencil library.
 *
 * (Multiple invocations are allowed, but results are cumulative...
 * there is no guarantee that you will get a 'clean' environment).
 *
 */
public class Configure {
	private Configure() {/*Utility, non-instantiable class.*/}

	public static void loadProperties(Properties props) {
		//Setup database driver map
		try {stencil.util.streams.sql.DriverManager.addDrivers(props);}
		catch (Exception e) {
			System.err.println("Error loading database drivers.");
			e.printStackTrace();
		}


		stencil.rules.ModuleCache.registerModules(props);
	}

	public static void loadProperties(String... files) throws Exception {
		Properties p = new Properties();

		for (String f:files) {p.loadFromXML(new FileInputStream(f));}

		loadProperties(p);
	}
}
