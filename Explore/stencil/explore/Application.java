package stencil.explore;

import java.util.Map;
import java.util.TreeMap;

import stencil.explore.ui.Batch;
import stencil.explore.ui.Headless;
import stencil.explore.ui.interactive.Interactive;
import stencil.explore.util.MessageReporter;
import stencil.explore.util.SystemMessageReporter;

/**Entry point for the application.  This takes care of general
 * state initialization restoration and UI selection.
 *
 * @author jcottam
 *
 */
public class Application {
	//Global semi-constants (set during initialization and probably never again)
	public static int CONCURRENT_BIAS = 1;
	public static int EXPORT_RESOLUTION = 300;

	//Message reporting infrastructure
	public static MessageReporter reporter = new SystemMessageReporter();
	
	//Flags are command line options
	public static final String INTERACTIVE_FLAG = "-interactive";
	public static final String BATCH_FLAG = "-batch";
	public static final String HEADLESS_FLAG = "-headless";
	public static final String SETTINGS_FLAG = "-settings";
	public static final String OPEN_FLAG = "-open";
	public static final String SOURCE_FLAG = "-source";
	public static final String HELP_FLAG = "-h";

	/**Which formats may be used?
	 * This array must be sorted.*/
	public static final String[] FORMATS = new String[]{"PDF", "PNG", "PNG2", "TXT"};

	/**How many arguments does the format require?*/
	public static final Map<String, Integer> ARG_CLASS = new TreeMap<String, Integer>();

	static {
		for (String format: FORMATS) {ARG_CLASS.put(format, 0);}
		ARG_CLASS.put("PNG",  1);
		ARG_CLASS.put("PNG2", 2);
	}

	/**Get the file flagged with the open flag (if any).
	 * If no flag is set, null is returned.
	 */
	public static String getOpenFile(String[] args) {
		for (int i=0; i< args.length; i++) {
			if (OPEN_FLAG.equals(args[i])) {return args[i+1];}
		}
		return null;
	}

	public static void help() throws Exception {
		System.out.println("Usage: Explore [mode] [-settings <file>]");
		System.out.printf("      mode : %1$s, %2$s, %3$s\n", INTERACTIVE_FLAG, BATCH_FLAG, HEADLESS_FLAG);
		System.out.println("      -settings <file> : File in the java properties XML format specifying");
		System.out.println("            settings for the application.  For details, see other documentation.");
		System.out.println("            These are settings for the application, not for Stencil itself.");
		System.out.println("      Settings may be used for any mode, but it must be immediately after the mode.");
		System.out.println("      If the settings flag is used, a mode must be specified.  More than one settings");
		System.out.println("      file may be given, but each one must be pre-ceeded by a settings flag.");
		System.out.println("\n");
		System.out.printf("Usage: Explore %1$s [-%2$s <file>]\n", INTERACTIVE_FLAG, OPEN_FLAG);
		System.out.println("    Start an interactive session.  The interactive flag is option, unless");
		System.out.println("    you wish to specify a session or settings file. ");
		System.out.printf("      -%1$s <file> : File to be loaded at the start of the application and ", OPEN_FLAG);
		System.out.println("             auto-saved to");
		System.out.println("\n");
		System.out.printf("Usage: Explore %1$s %2$s <file> [%3$s <name> <type> <spec>]* [-(png|-png2) <file>]\n", HEADLESS_FLAG, OPEN_FLAG, SOURCE_FLAG);
		System.out.printf("      -%1$s <file> : Base stencil and default sources file.\n", OPEN_FLAG);
		System.out.printf("      -%1$s <name> <type> <spec>: Replace a stream definitions type with the specified values.", SOURCE_FLAG);
		System.out.println("             Any type may be specified BUT any specializer arguments must be named.");
		System.out.println("             Specializer will be MERGED with the one present in the stencil program.");
		System.out.println("             Prototypes CANNOT be over-ridden at the command line.");
		System.out.println("      -(png|eps|pdf|tuples) <file> :  Output to the file named using the specified");
		System.out.println("             format. Export failure may or may not be a terminal failure.");
		System.out.println("\n");
		System.out.printf("Usage: Explore %1$s %2$s <file>\n", BATCH_FLAG, OPEN_FLAG);
		System.out.println("      Run in batch mode.  Each line of the batch file specifies a headless");
		System.out.println("      execution and follows the same format as the headless parameter specification.");
		System.out.println("      Each line should begin with the -stencil flag, -headless should not be");
		System.out.println("      included.");

	}

	public static void main(String[] args) throws Exception {
		if ((args.length == 0) || args[0].equals(INTERACTIVE_FLAG)) {
			Interactive.main(args);
		} else if (args[0].equals(HEADLESS_FLAG)) {
			Headless.main(args);
			System.exit(0);
		} else if (args[0].equals(BATCH_FLAG)) {
			Batch.main(args);
			System.exit(0);
		} else if (args[0].startsWith(HELP_FLAG)) {
			help();
		} else {
			System.out.printf("Mode argument %1$s not recognized.  Please pick a valid mode.\n", args[0]);
			help();
		}
	}

}
