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

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import static stencil.explore.Application.reporter;
import stencil.explore.*;
import stencil.explore.model.Model;
import stencil.explore.model.sources.FileSource;
import stencil.explore.model.sources.StreamSource;
import stencil.explore.util.StencilIO;
import stencil.explore.util.StencilRunner;
import stencil.types.Converter;


import stencil.WorkingDirectory;

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
		int i=0;
		for (StreamSource s: sources) {
			if (name.equals(s.name())) {return s;}
			i++;
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

		//Load file
		Model model = new Model();
		String file = Application.getOpenFile(args);
		if (file == null) {throw new IllegalArgumentException(String.format("%1$s flag must be used in headless mode.", Application.OPEN_FLAG));}
		reporter.addMessage("%n%nLoading stencil %1$s.%n", file);

		String filename = prefix + file;
		StencilIO.load(filename, model);
		WorkingDirectory.setWorkingDir(filename);

		//Load new stream sources
		String[] sourceRewrites = getSourceRewrites(args);

		//TODO: Move to a source-rewrite file (instead of the command switch series)
		Collection<StreamSource> sources = model.getSources();

		for (int i=0; i< sourceRewrites.length; i= i+1) {
			StreamSource source = findSource(sourceRewrites[i], sources);
			if (source == null) {continue;}

			sources.remove(source);
			if (source instanceof FileSource) {
				source = ((FileSource) source).filename(prefix + sourceRewrites[i+1]);
				sources.add(source);
			} else {
				FileSource newSource = new FileSource(source.name());
				newSource.name(source.name());
				newSource.header(source.header());
				newSource.filename(prefix + sourceRewrites[i+1]);
				sources.add(source);
			}
		}
		model.setSources(sources);

		//Run
		model.compile();
		try {
			StencilRunner t = model.execute();
			t.join();
			
			if (t.getThrowable() != null) {throw new Exception("Stencil stopped with an error.", t.getThrowable());}
			
	
			//Save output
			for (int i =0; i < args.length; i++) {
				int idx = Arrays.binarySearch(Application.FORMATS, args[i].substring(1).toUpperCase());
	
				if (idx >=0) {
					String format = Application.FORMATS[idx];
					Class argClass = Application.ARG_CLASS.get(format);
	
					if (argClass == null) {
						model.export(prefix + args[i+1], format, null);
					} else {
						Object arg = Converter.convert(args[i+1], argClass);
						model.export(prefix + args[i+2], format,  arg);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			model.getStencilPanel().dispose();
		}
	}

}
