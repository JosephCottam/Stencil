package stencil.explore.ui;

import java.io.BufferedReader;
import java.io.FileReader;

import stencil.explore.*;


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

		if (Application.SETTINGS_FLAG.equals(args[0])) {inputFile = 3;}

		java.io.BufferedReader input = new BufferedReader(new FileReader(args[inputFile]));
		String line =  input.readLine();

		while (line != null && !line.equals("")) {
			batchInvoke(line);
			line=input.readLine();
		}
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
		
		Headless.main(prefix, args);
	}
}
