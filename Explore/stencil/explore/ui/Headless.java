package stencil.explore.ui;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import static stencil.explore.Application.reporter;
import stencil.explore.*;
import stencil.explore.model.Model;
import stencil.explore.model.sources.BinarySource;
import stencil.explore.model.sources.FileSource;
import stencil.explore.model.sources.StreamSource;
import stencil.explore.util.StencilIO;
import stencil.explore.util.StencilRunner;

import stencil.WorkingDir;

public class Headless {

	/**Parse the arguments and return an array of source changes.
	 * Even-numbered array entries will be names, the odd entries the
	 * file sources.
	 *
	 * @param args
	 * @return
	 */
	private static String[] getSourceRewrites(String[] args) {
		List<String> result = new ArrayList<String>();
		try {
			for (int i=0; i< args.length; i++) {
				if (args[i].equals(Application.SOURCE_FLAG)) {
					result.add(args[i+1]);
					result.add(args[i+2]);
				}
			}
		} catch (Exception e) {throw new IllegalArgumentException("Error in source-redefine arguments.");}

		return result.toArray(new String[]{});
	}

	private static StreamSource findSource(String name, Collection<StreamSource> sources) {
		for (StreamSource s: sources) {
			if (name.equals(s.name())) {return s;}
		}
		return null;
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

		//Load new stream sources
		String[] sourceRewrites = getSourceRewrites(args);

		Collection<StreamSource> sources = model.getSources();

		for (int i=0; i< sourceRewrites.length; i= i+1) {
			StreamSource source = findSource(sourceRewrites[i], sources);
			if (source == null) {continue;}

			sources.remove(source);
			if (source instanceof FileSource) {
				source = ((FileSource) source).filename(sourceRewrites[i+1]);
				sources.add(source);
			} else if (sourceRewrites[i+1].endsWith(".tuples")){
				BinarySource newSource = new BinarySource(source.name());
				newSource = newSource.filename(sourceRewrites[i+1]);
				sources.add(newSource);
			} else {
				FileSource newSource = new FileSource(source.name());
				newSource = newSource.filename(sourceRewrites[i+1]);
				sources.add(newSource);
			}
		}
		model.setSources(sources);

		//Run
		model.compile();
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
