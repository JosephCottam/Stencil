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
package stencil.explore.ui;

import java.io.BufferedReader;
import java.io.FileReader;

import stencil.explore.*;
import stencil.WorkingDirectory;


/**Batch invocation runs a series of Stencils in headless mode.
 * Each Stencil is run in a fresh copy of the interpreter, but
 * batch invocation saves the JVM startup, jython initialization
 * and class load costs.
 *
 * @author jcottam
 *
 */
public class Batch {

	/**Runs a group of stencils, each in headless mode.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int inputFile = 2;
		preRun(args);

		if (Application.SETTINGS_FLAG.equals(args[0])) {inputFile = 3;}

		java.io.BufferedReader input = new BufferedReader(new FileReader(args[inputFile]));
		String line =  input.readLine();

		while (line != null && !line.equals("")) {
			batchInvoke(line);
			line=input.readLine();
		}
	}

	/**Utility for test tools that directly call batchInvoke.
	 * Need to initialize the batch system, but will not be using main.
	 * This method must be called before the first call to batchInvoke,
	 * and may be called more often (but is not required).
	 * The 'args' section is identical to the command line args, but
	 * most are ignored.
	 *
	 * @param args
	 */
	public static void preRun(String[] args) {
		String[] configs = PropertyManager.getConfigFiles(args);
		PropertyManager.loadProperties(configs, PropertyManager.exploreConfig, PropertyManager.stencilConfig);
	}

	//Parse an individual line of arguments in a batch invocation.
	public static void batchInvoke(String argSet) throws Exception {
		String[] rawArgs;
		String[] args;
		String prefix;

		rawArgs = argSet.split(" ");  //TODO: Fix this so spaces can be in arguments
		args = new String[rawArgs.length-1];
		System.arraycopy(rawArgs, 1, args, 0, args.length);
		prefix = rawArgs[0];
		WorkingDirectory.setWorkingDir(prefix, true);

		Headless.main(WorkingDirectory.getWorkingDir(), args);
	}
}
