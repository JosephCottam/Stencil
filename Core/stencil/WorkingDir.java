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
package stencil;

import java.io.File;
import java.io.IOException;

/**Handles working directory information.*/
public class WorkingDir {
	private static File workingDir = new File(System.getProperty("user.dir"));
	
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
		if (!f.isAbsolute()) {f = new File(workingDir, path);}
		if (f.isFile()) {f = f.getParentFile();}
		workingDir = f;
	}

	public static File get() {return workingDir;}

	/** @param filename
	 * @return Cannonical path after resolving filename against working directory
	 * @throws IOException 
	 */
	public static String resolve(String filename) {
		File f = new File(filename);
		if (!f.isAbsolute()) {f =new File(workingDir, filename);}
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
		return workingDir.toURI().relativize(newPath.toURI()).getPath();
	}
}
