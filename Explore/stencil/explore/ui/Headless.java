package stencil.explore.ui;

import java.util.Arrays;

import static stencil.explore.Application.reporter;
import stencil.explore.*;
import stencil.explore.model.Model;
import stencil.explore.util.StencilIO;
import stencil.explore.util.StencilRunner;
import stencil.interpreter.tree.Specializer;
import stencil.interpreter.tree.StreamDec;
import stencil.parser.ParseStencil;
import stencil.parser.string.DefaultSpecializers;

import stencil.WorkingDir;

public class Headless {

	/**Parse the arguments and modifies the decs array passed with new stream definitions
	 *
	 * @param args
	 * @return
	 */
	private static void rewriteSources(String[] args, StreamDec[] decs) {
		for (int i=0; i< args.length; i++) {
			if (args[i].equals(Application.SOURCE_FLAG)) {
				String name = args[i+1];
				try {
					String type = args[i+2];
					Specializer spec = ParseStencil.specializer(args[i+3]);
					
					for (int d=0; d<decs.length; d++) {
						StreamDec dec = decs[d];
						if (!dec.name().equals(name)) {continue;}
						decs[d] = new StreamDec(name, dec.prototype(), type, DefaultSpecializers.blend(spec, dec.specializer()));
					}

				} catch (Exception e) {throw new IllegalArgumentException("Error in source-redefine arguments for: " + name);}

				}
			}
	}

	public static void main(String[] args) throws Exception {main("", args);}

	/**Run a single stencil, but not in a window.
	 * Once all data streams or loaded, it returns (but does not necessarily call system.exit).
	 * In this mode, keyboard and mouse streams are ignored.
	 *
	 * @param args
	 * @param prefix Pre-pended to filenames.  Default is empty-string.
	 */
	public static void main(String prefix, String[] args) throws Exception {
		//Set headless flag...
		try {System.setProperty("java.awt.headless", "true");}
		catch (Exception e) {
			reporter.addError("Set headless error: %1$s", e.getMessage());
			e.printStackTrace();
		}

		String[] configs = PropertyManager.getConfigFiles(args);
		PropertyManager.loadProperties(configs, PropertyManager.exploreConfig, PropertyManager.stencilConfig);
		WorkingDir.set(prefix);
		
		//Load file
		Model model = new Model();
		String file = Application.getOpenFile(args);
		if (file == null) {throw new IllegalArgumentException(String.format("%1$s flag must be used in headless mode.", Application.OPEN_FLAG));}
		reporter.addMessage("%n%nLoading stencil %1$s.%n", file);

		StencilIO.load(WorkingDir.resolve(file), model);
		WorkingDir.set(file);
		model.compile();


		//Load new stream sources
		rewriteSources(args, model.getStencilPanel().getProgram().streamDecs());
		
		
		
		try {
			StencilRunner t = model.execute();
			t.join();
			
			if (t.getThrowable() != null) {throw new Exception("Stencil stopped with an error.", t.getThrowable());}
			model.getStencilPanel().postRun();
			
	
			//Save output
			for (int i =0; i < args.length; i++) {
				int idx = Arrays.binarySearch(Application.FORMATS, args[i].substring(1).toUpperCase());
	
				if (idx >=0) {
					String format = Application.FORMATS[idx];
					int argCount = Application.ARG_CLASS.get(format);
	
					Object[] arguments = new Object[argCount];
					for (int arg=0; arg< argCount; arg++) {
						arguments[arg] = args[i+arg+1];
					}
					model.export(WorkingDir.resolve(args[i+argCount+1]), format, arguments);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.getStencilPanel().dispose();
		}
	}

}
