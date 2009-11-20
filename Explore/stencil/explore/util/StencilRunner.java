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
package stencil.explore.util;

 import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import stencil.WorkingDirectory;
import stencil.adapters.TupleLoader;
import stencil.display.StencilPanel;
import static stencil.explore.Application.reporter;
import stencil.explore.model.Model;
import stencil.explore.model.sources.FileSource;
import stencil.explore.model.sources.StreamSource;
import stencil.adapters.Adapter;
import stencil.tuple.TupleStream;
import stencil.util.streams.ConcurrentStream;
import stencil.parser.tree.Order;
import stencil.parser.tree.Atom;

/**Thread to manage running a stencil run.
 *
 * This contains the heart of the data coordination engine that is part
 * of the Explore extension to Stencil.
 * 
 * TODO: Add error reporting
 * @author jcottam
 *
 */
public final class StencilRunner extends Thread {
	public static class AbnormalTerminiationException extends RuntimeException {
		public AbnormalTerminiationException(String message) {super(message);}
		public AbnormalTerminiationException(String message, Throwable t) {super(message, t);}
	}
	
	private boolean running =false;
	private Model model;
	private TupleLoader loader;
	private Throwable throwable;

	private boolean keepRunning = true;

	public StencilRunner(Model model) {
		this.model = model;
		this.setDaemon(true);	//Don't wait for it to finish before quitting
	}

	public void run() {
		try {
			running = true;

			Map<String, StreamSource> streamSources = model.getSourcesMap();
			Adapter adapter = model.getAdapterOpts().getAdapter();
			StencilPanel panel = model.getStencilPanel();
			Order order = panel.getProgram().getStreamOrder();

			for (List<? extends Atom> group: order) {
				if (!keepRunning) {break;}

				String names = "";
				TupleStream input;

				List<TupleStream> streams = new ArrayList<TupleStream>();
				for (Atom name: group) {
					if (!name.isString() && !name.isName()) {continue;}
					if (!streamSources.containsKey(name.getValue())) {continue;}

					names = names + " and " + name.getValue();
					StreamSource stream = streamSources.get(name.getValue());
					if (stream instanceof FileSource) {stream=resolvePaths(((FileSource) stream));}
					input = stream.getStream(model);
					streams.add(input);
				}

				if (streams.size() >1) {input = new ConcurrentStream(streams);}
				else {input =  streams.get(0);}
				names = names.substring(5);
				loader = new TupleLoader(panel, input);

				reporter.addMessage("Starting loading %1$s.", names);
				loader.load();
				reporter.addMessage("Finished load of %1$s.", names);
				panel.repaint();
			}
			adapter.finalize(panel);
		} catch (Throwable e) {
			running = false;
			throwable = e;
			reporter.addError("Error executing Stencil program: %1$s.", e.getMessage());
		}
		running = false;
	}

	/**Make sure the filename is resolved relative to the working directory.*/
	private FileSource resolvePaths(FileSource source) {
		String filename = source.filename();
		filename =WorkingDirectory.resolvePath(filename);
		return source.filename(filename);
	}

	public boolean isRunning() {return isAlive() && running;}
	
	
	/**Stop the currently running stencil.  Tries to do so 'nicely', but
	 * after a short delay it will do so via more direct means (Thread.stop).
	 * 
	 * If an error occurs while trying to stop the program,
	 * an AbnormalTerminiationException is thrown.  The program may, or may not have stopped if this 
	 * exception is raised.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void signalStop() throws AbnormalTerminiationException{
		keepRunning = false;
		if (loader == null) {return;}
		
		loader.signalStop();
		
		try {Thread.sleep(500);}
		catch (Exception e) {/*Left empty, all cases are handled in finally.*/}
		finally {
			if (isAlive()) {
				try {stop();}	//When working with Jython, you can write infinite loops that are outside of the normal thread control flow.
							//When that happens, you can't just signal a stop.  If we signaled and waited half of a second and the thread did
							//not stop, we kill it.  This is only safe to do because the Stencil environment is independent of the 
							//explore environment (except the display surface).
			
				catch (Exception ex) {throw new AbnormalTerminiationException("Abnormal termination of Stencil program (may not have stopped).", ex);}
				throw new AbnormalTerminiationException("Abnormal termination of Stencil program: Program did not respond to stop signal.");
			}
		}
	}
	
	public Throwable getThrowable() {return throwable;}
}
