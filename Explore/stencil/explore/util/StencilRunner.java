package stencil.explore.util;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import stencil.adapters.TupleLoader;
import stencil.display.StencilPanel;
import static stencil.explore.Application.reporter;
import stencil.explore.model.Model;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.ConcurrentStream;
import stencil.interpreter.tree.Order;
import stencil.interpreter.tree.Program;

/**Thread to manage running a stencil run.
 *
 * This contains the heart of the data coordination engine that is part
 * of the Explore extension to Stencil.
 * 
 * TODO: Move ALMOST all of this to a proper dispatcher.  Merge with the stream/stream stuff in the main interpreter branch.
 * 
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
		super("StencilRunner");
		this.model = model;
		this.setDaemon(true);	//Don't wait for it to finish before quitting
	}

	public void run() {
		try {
			running = true;

			JFrame windows = null;
			StencilPanel panel = model.getStencilPanel();
			Program program = panel.getProgram();
			Map<String, TupleStream> streamSources = panel.preRun();
			Order order = program.order();

			List<JPanel> panels = new ArrayList();
			for (Object op: program.operators()) {
				if (op instanceof NeedsPanel) {
					panels.add(((NeedsPanel) op).panel());
				}
			}
			if (panels.size() >0) {
				windows = new JFrame();
				windows.getContentPane().setLayout(new ResizeLayout(windows.getContentPane(), ResizeLayout.Y_AXIS));
				for(JPanel p: panels) {
					windows.add(p);
				}
				windows.setVisible(true);
			}
			
			
			//TODO: Re-arrange this whole mess to use the dispatcher defined in the dissertation.
			for (String[] group: order) {
				if (!keepRunning) {break;}

				String names = "";
				TupleStream input;

				List<TupleStream> streams = new ArrayList<TupleStream>();
				for (String name: group) {
					if (!streamSources.containsKey(name)) {continue;}
					input = streamSources.get(name);
					if (input == null) {continue;} //HACK:  System streams currently return null from the StreamTypeRegistry...that needs to be handled better!
					
					names = names + " and " + name;
					
					streams.add(input);
				}

				if (streams.size() >1) {input = new ConcurrentStream(streams);}
				else {input =  streams.get(0);}
				names = names.substring(5);
				loader = new TupleLoader(panel, input);

				reporter.addMessage("Starting loading %1$s.", names);
				loader.load();
				input.stop();
				reporter.addMessage("Finished load of %1$s (%2$,d tuples).", names, loader.getRecordsLoaded());
				panel.repaint();
			}
			
			if (windows != null) {
				while(windows.isVisible()) {Thread.sleep(1000);}
				windows.dispose();
			}
			
		} catch (Throwable e) {
			running = false;
			throwable = e;
			reporter.addError("Error executing Stencil program: %1$s.", e.getMessage());
		}
		running = false;
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
			if (this.isAlive()) {
				try {stop();}	//You can write infinite loops for the interpreter...
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
