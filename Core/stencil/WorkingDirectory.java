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

/**Handles working directory information.*/
public class WorkingDirectory {
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String RELATIVE_PREFIX  = "." + FILE_SEPARATOR;
	private static String workingDir = RELATIVE_PREFIX;
	

	/**Set working directory, resolved against current working directory.*/
	public static void setWorkingDir(String filename) {setWorkingDir(filename, false);}
	
	/**Set the working directory.  If the specified filename starts with
	 * the relative prefix, it will be resolved against the current working
	 * directory, unless userDirRoot is set to true.
	 * 
	 * @param filename
	 * @param userDirRoot
	 */
	public static void setWorkingDir(String filename, boolean userDirRoot) {
		if (userDirRoot) {
			workingDir = System.getProperty("user.dir");
			if (!workingDir.endsWith(FILE_SEPARATOR)) {workingDir = workingDir + FILE_SEPARATOR;} //Different systems will with or without the terminal separtor...unfortunately
		}

		java.io.File f = new java.io.File(filename);
		if (!f.isDirectory()) {filename = f.getParent();}
		
		if (!filename.endsWith(FILE_SEPARATOR)) {filename = filename + FILE_SEPARATOR;}
		if (filename.startsWith(RELATIVE_PREFIX)) {filename = resolvePath(filename);}
		workingDir = filename;
	}
	public static String getWorkingDir() {return workingDir;}

	/**Resolve a path relative to the working directory, but only if
	 * it is relative.
	 *
	 * @param filename
	 * @return
	 */
	public static String resolvePath(String filename) {
		if (filename.startsWith(RELATIVE_PREFIX)) {
			filename = workingDir + filename.substring(RELATIVE_PREFIX.length());
		}
		return filename;
	}

	/**If the filename is prefixed by the working directory, replace
	 * that prefix with the relative prefix.  Otherwise it is untouched.
	 * @param filename
	 * @return
	 */
	public static String relativePath(String filename) {
		if (filename.startsWith(workingDir)) {
			return RELATIVE_PREFIX + filename.substring(workingDir.length());
		}
		return filename;
	}
}
