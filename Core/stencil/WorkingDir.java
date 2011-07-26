package stencil;

import java.io.File;
import java.io.IOException;

/**Handles working directory information.*/
public class WorkingDir {
	
	private static File workingDir;
	
	/**Set the working directory.  If the specified filename starts with
	 * the relative prefix, it will be resolved against the current working
	 * directory, unless userDirRoot is set to true.
	 * 
	 * Null path is treated as an empty path.
	 * 
	 * @param filename
	 * @param abs
	 */
	public static void set(String path) {
		if (path == null) {path ="";}
		
		File f = new File(path);
		set(f);
	}

	public static void set(File path) {
		if (path == null) {path = new File("");}
		if (!path.isAbsolute()) {path = new File(get(), path.toString());}
		if (path.isFile()) {path = path.getParentFile();}
		workingDir = path;
	}

	
	public static File get() {
		if (workingDir == null) {workingDir = new File(System.getProperty("user.dir"));}
		return workingDir;
	}

	/** @param filename
	 * @return Cannonical path after resolving filename against working directory
	 * @throws IOException 
	 */
	public static String resolve(String filename) {
		File f = new File(filename);
		if (!f.isAbsolute()) {f =new File(get(), filename);}
		try {return f.getCanonicalPath();}
		catch (Exception e) {throw new RuntimeException("Error resolving path", e);}
	}

	/**If the filename is prefixed by the working directory, replace
	 * that prefix with the relative prefix.  Otherwise it is untouched.
	 * @param filename
	 * @return
	 */
	public static String relativize(String filename) {
		File newPath = new File(resolve(filename));
		return get().toURI().relativize(newPath.toURI()).getPath();
	}
}
