package stencil.explore.model;

import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import stencil.display.DisplayLayer;
import stencil.display.StencilPanel;
import static stencil.explore.Application.reporter;
import stencil.explore.model.sources.StreamSource;
import stencil.explore.util.ListModel;
import stencil.explore.util.StencilRunner;
import stencil.adapters.Adapter;
import stencil.explore.coordination.*;

/**Central data repository and control structure for a stencil application.
 *
 * Various application modes manipulate model objects to achieve
 * interactive, head-less,etc operation.
 */
public final class Model implements StencilMutable.Config, StencilMutable.Sources<StreamSource>, StencilMutable.Stencil {
	public static final String NEW_LINE = System.getProperty("line.separator");
	public static boolean VERBOSE_END_OF_RUN = false;
	
	protected ListenerQueues listeners = new ListenerQueues();

	protected String stencil;
	protected List<StreamSource> sources;
	protected AdapterOpts adapterOpts;
	protected StencilPanel stencilPanel;

	protected StencilRunner runner;

	/**Construct a new model for the Stencil application.*/
	public Model() {
		sources = new ListModel();
		adapterOpts = new AdapterOpts();
		runner = new StencilRunner(this);
	}

	public String getStencil() {return stencil;}
	public AdapterOpts getAdapterOpts() {return adapterOpts;}
	public List<StreamSource> getSources() {return sources;}
	public Map<String, StreamSource> getSourcesMap() {
		Map<String, StreamSource> map = new HashMap();
		for (StreamSource source: sources) {
			map.put(source.name(), source);
		}
		return map;
	}

	public StencilPanel getStencilPanel() {return stencilPanel;}
	public void setStencilPanel(StencilPanel stencilPanel) {
		this.stencilPanel = stencilPanel;
	}


	/**Compile the source found in the editor, set the panel accordingly**/
	public void compile() throws Exception {
		if (stencilPanel != null) {stencilPanel.dispose();}
		
		Adapter adapter = adapterOpts.getAdapter();
		StencilPanel panel = adapter.compile(stencil);
		setStencilPanel(panel);
	}

	/**Execute the stencil represented by this application.
	 * Application must have been compiled already.  If it has not been, an exception is thrown.
	 * @return Thread running the stencil.
	 * @throws Exception
	 */
	public StencilRunner execute() throws Exception {
		if (stencilPanel == null) {throw new RuntimeException("Cannot execute an application prior to successful compilation.");}
		if (runner.isRunning()) {throw new RuntimeException("Model is already executing.  Cannot execute twice concurrently.");}
		
		reporter.addMessage("Starting executing.");

		if (runner == null || !runner.isAlive()) {runner = new StencilRunner(this);}
		final long startTime= System.currentTimeMillis();
		runner.start();

		Thread listener = new Thread("Stencil Termination Listener") {
			public void run() {
				try {runner.join();}
				catch (Exception e) {System.err.println("Waiter interrupted before runner terminated.");}

				long endTime = System.currentTimeMillis();
				final long duration = endTime - startTime;
				
				if (runner.getThrowable() == null) {
					reporter.addMessage("Execution terminated.");
					if (VERBOSE_END_OF_RUN) {
						for (Object l: stencilPanel.layers()) {
							DisplayLayer layer = (DisplayLayer) l;
							reporter.addMessage("\tLayer %1$s has %2$s glyphs.", layer.name(), layer.size());
						}
					}
				}
				else {
					reporter.addMessage("Execution terminated abnormally.");
					runner.getThrowable().printStackTrace();
				}
				
				long remaining = duration;
				long hours = TimeUnit.MILLISECONDS.toHours(remaining);
				remaining = remaining - (hours *3600000);
				long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
				remaining = remaining - (minutes * 60000);
				long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
				
				reporter.addMessage("Approximate runtime  %d h: %d m: %d s (%d ms total).",
							hours, minutes, seconds, duration);
			}
		};

		listener.setPriority(Thread.MIN_PRIORITY);
		listener.start();
		return runner;
	}

	/**Wrapper for a StencilPanel export, but includes additional reporting.
	 *
	 * @throws Exception Any exception thrown by the adapter's StencilPanel
	 */
	public void export(String filename, String type, Object info) throws Exception {
		String infoString;
		if (info == null) {infoString = "none";}
		else if(info.getClass().isArray()) {
			infoString = Arrays.deepToString((Object[]) info);
		} else {
			infoString = info.toString();
		}
		
		reporter.addMessage("Starting %1$s export to %2$s (additional args: %3$s).", type, filename, infoString);

		try {
			stencilPanel.export(filename, type, info);
			reporter.addMessage("Export to %1$s done.", filename);
		} catch (Exception e) {
			reporter.addError("Export to %1$s aborted.", filename);
			reporter.addError(e.getMessage());
			throw e;
		}
	}

	public boolean isRunning() {return runner.isRunning();}
	public void signalStop() {if (runner.isRunning()) {runner.signalStop();}}

	/**Remove all sources.*/
	public void clearSources(boolean raise) {
		sources.clear();
		if (raise) {listeners.fireSourceChanged(this, getSources());}
	}

	/**Set all sources.  Discards any existing sources.
	 * Will only add one source per name if there are duplicate
	 * named sources in the incoming collection.
	 * @param sources
	 * @param raise
	 */
	public void setSources(Collection<? extends StreamSource> sources) {
		if (this.sources == sources) {return;}
		this.sources.clear();
		for (StreamSource source: sources) {
			this.sources.add(source);
		}
		listeners.fireSourceChanged(this, getSources());
	}

	/**Set the Stencil source text.*/
	public void setStencil(String stencil) {
		String oldStencil = this.stencil;
		if (stencil == null && oldStencil == null || (stencil != null && stencil.equals(oldStencil))) {return;}
		this.stencil = stencil;
		if (runner.isRunning()) {runner.signalStop();}
		listeners.fireStencilChanged(this, stencil);
	}

	/**Set the adapter options settings.*/
	public void setAdapterOpts(AdapterOpts opts) {
		if ((opts == null && adapterOpts == null) || (opts!=null && opts.equals(this.adapterOpts))) {return;}
		adapterOpts = opts;
	}

	public void fireAll() {
		listeners.fireConfigChanged(this, adapterOpts);
		listeners.fireSourceChanged(this, getSources());
		listeners.fireStencilChanged(this, stencil);
	}

	/**Will register the target object as a listener on as many
	 * StencilEvents as possible.
	 * @param target
	 */
	public void addAllListeners(Object target) {
		if (target instanceof StencilListener.ConfigChanged) {listeners.addListener((StencilListener.ConfigChanged) target);}
		if (target instanceof StencilListener.SourcesChanged) {listeners.addListener((StencilListener.SourcesChanged) target);}
		if (target instanceof StencilListener.StencilChanged) {listeners.addListener((StencilListener.StencilChanged) target);}
	}
	public void addConfigChangedListener(StencilListener.ConfigChanged l) {listeners.addListener(l);}
	public void addSourceChangedListener(StencilListener.SourcesChanged l) {listeners.addListener(l);}
	public void addStencilChangedListener(StencilListener.StencilChanged l) {listeners.addListener(l);}
}
