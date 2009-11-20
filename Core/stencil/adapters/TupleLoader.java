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
package stencil.adapters;

import stencil.display.StencilPanel;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;
import stencil.util.streams.txt.InvalidInputLineException;

/**Load tuples from a stream source into the program
 * via the application engine.
 */
public class TupleLoader implements Runnable {
	private enum STATE {UNSTARTED, RUNNING, STOPPED, EXCEPTION};
	private static final long DEFAULT_UPDATE_FREQUENCY = 10;

	protected TupleStream input;
	protected StencilPanel panel;
	protected STATE state;
	protected Exception exception;
	protected long recordsLoaded;
	protected long updateFrequency;
	protected boolean keepRunning = true;

	public TupleLoader(StencilPanel panel, TupleStream input) {
		this.panel = panel;
		this.input = input;
		state = STATE.UNSTARTED;
		exception = null;
		recordsLoaded = 0;
		updateFrequency = DEFAULT_UPDATE_FREQUENCY;
	}


	/**Execute loader, conforming to the Runnable interface and using
	 * the internal error catching/caching mechanisms.
	 */
	public void run() {
		try {
			load();
		} catch (Exception e) {
			state = STATE.EXCEPTION;
			exception = e;
		}
	}

	/**Loading items (this is some work is coordinate).
	 *
	 * While the stream still returns true from 'hasNext', a
	 * loop will run looking for items to load.  Each tuple
	 * from the stream will be passed through the application
	 * engine.  Null and InvalidInputLineException are ignored
	 * (so the loop keeps going).
	 *
	 *  Since this method invokes Application engine apply method,
	 *  it also invokes the engine's pre-run method to ensure the
	 *  interpreter is ready to receive values for the current
	 *  program/canvas/view.
	 *
	 *
	 * @throws Exception
	 */
	public void load() throws Exception {
		state = STATE.RUNNING;
		panel.preRun();

		while(input.hasNext() && keepRunning) {
			Tuple tuple;
			try {
				if (input.ready()) {tuple = input.next();}
				else {continue;}
			}
			catch (InvalidInputLineException e) {Thread.yield(); continue;} //Ignore when a full line does not parse right.
			if (tuple == null) {panel.repaint(); Thread.yield(); continue;} //Ignore null tuples; Stream is not over, but has not immediate contents
			panel.processTuple(tuple); 
			recordsLoaded++;
		}
		state = STATE.STOPPED;
	}

	public boolean isRunning() {return state == STATE.RUNNING;}
	public boolean isStopped() {return state == STATE.STOPPED;}
	public boolean unstarted() {return state == STATE.UNSTARTED;}

	/**has an error been seen?  Returns only if there is a cached exception.*/
	public boolean hasException() {return exception != null;}

	/**Return a copy of the caught exception.*/
	public Exception getException() {return exception;}

	/**Find out how many records have been read by this loader*/
	public long getRecordsLoaded() {return recordsLoaded;}

	/**Used to signal the loader to stop, even if it has not reached the end of the stream.
	 * Passing a false will cause it to stop next time it iterates the loading loop, regardless of the 'hasNext' value
	 * on the source stream.
	 *
	 * @param value
	 */
	public void signalStop() {keepRunning = false;}
}
