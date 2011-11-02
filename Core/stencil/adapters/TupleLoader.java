package stencil.adapters;

import stencil.display.StencilPanel;
import stencil.tuple.SourcedTuple;
import stencil.tuple.stream.TupleStream;

/**Load tuples from a stream source into the program
 * via the application engine.
 */
public class TupleLoader implements Runnable {
	private enum STATE {UNSTARTED, RUNNING, STOPPED, EXCEPTION}
	private static final long DEFAULT_UPDATE_FREQUENCY = 10;

	protected TupleStream input;
	protected StencilPanel panel;
	protected STATE state;
	protected Exception exception;
	protected long recordsLoaded;
	protected long updateFrequency;
	protected volatile boolean keepRunning = true;

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

		while(keepRunning() && input.hasNext()) {
			SourcedTuple tuple;
			tuple = input.next();

			if (tuple == null) {Thread.yield(); continue;} //Ignore null tuples; Stream is not over, but has no immediate contents
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

	private synchronized boolean keepRunning() {return keepRunning;}
	
	/**Used to signal the loader to stop, even if it has not reached the end of the stream.
	 * Stop will actually occur after the current tuple (if any) has finished processing.
	 * @param value
	 */
	public synchronized void signalStop() {
		keepRunning = false;
	}
}
