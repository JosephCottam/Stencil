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
package stencil.explore.ui.interactive;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;

import javax.swing.*;

import stencil.WorkingDirectory;
import static stencil.explore.Application.reporter;
import stencil.explore.*;
import stencil.explore.coordination.*;
import static stencil.explore.coordination.StencilEvent.Type;
import stencil.explore.model.Model;
import stencil.explore.util.StencilIO;
import stencil.explore.util.StencilRunner;
import stencil.explore.ui.components.AdapterOptsPanel;
import stencil.explore.ui.components.MessagePanel;
import stencil.explore.ui.interactive.components.MainEditor;

/**Wraps the application with a GUI.**/
public class Interactive implements Runnable {
	private static final String SESSION_CONFIGURATION_FILE = "ExploreSession.properties";

	//Where should the session be auto-save?
	public static String sessionFile = null;

	public static float FONT_SIZE = 12;
	public static String PROGRAM_FONT_NAME = "Monaco";

	public static final String APPLICATION_NAME = "Stencil Explore";
	
	public static Font styleFont(String name) {
		return new Font(name, Font.PLAIN, 12).deriveFont(FONT_SIZE);
	}

	/**Re-style the passed font according to session styling rules.*/
	public static Font styleFont(Font base) {
		if (base.getSize() == FONT_SIZE) {return base;}
		return base.deriveFont(FONT_SIZE);
	}

	/**Utility class to terminate save the session in the session file
	 * and current settings into the explore configuration file.
	 * Then it terminates the application.
	 **/
	private static class AppCloser extends WindowAdapter {
		Interactive app;

		public AppCloser(Interactive interactiveApp) {this.app = interactiveApp;}
		public void windowClosed(WindowEvent e) {
			((JFrame)e.getSource()).dispose();
			if (app.stencilFrame != null) {app.stencilFrame.dispose();}
			if (app.messageFrame != null) {app.messageFrame.dispose();}
			if (app.editorFrame != null) {app.editorFrame.dispose();}

			PropertyManager.saveSessionProperties(SESSION_CONFIGURATION_FILE);

			StencilIO.save(Interactive.sessionFile, app.model);
			System.exit(0);
		}
	}

	protected JFrame editorFrame = new JFrame();
	protected JFrame messageFrame = new JFrame();
	protected JFrame stencilFrame = new JFrame();

	protected MessagePanel messages = new MessagePanel();
	protected AdapterOptsPanel adapterOpts = new AdapterOptsPanel();
	protected MainEditor editor = new MainEditor();
	protected JPanel stencilContentPanel = new JPanel();

	protected JButton execute = new JButton("Execute");

	protected Model model;
	protected Controller controller = new Controller();

	public Interactive(Model model) {
		reporter = messages;  //Set message reporting to the window system
		
		editor.getStencilEditor().addStencilChangedListener(controller);
		editor.getSourcesEditor().addStencilChangedListener(controller);

		adapterOpts.addStencilChangedListener(controller);
		adapterOpts.setAdapterOpts(model.getAdapterOpts());


		controller.addMutable(editor.getStencilEditor(), Type.Stencil);
		controller.addMutable(editor.getSourcesEditor(), Type.Sources);
		controller.addMutable(adapterOpts, Type.Config);
		set(model);


		JPanel controls = new JPanel();
		controls.add(execute);
		controls.add(adapterOpts);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(controls, BorderLayout.NORTH);
		mainPanel.add(editor, BorderLayout.CENTER);


		editorFrame.setContentPane(mainPanel);
		messageFrame.setContentPane(messages);
		stencilFrame.setContentPane(stencilContentPanel);
		stencilContentPanel.setLayout(new BorderLayout());

		execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {executeApplication();}

		});

		setupMenus();
	}

	public void executeApplication() {
		if (model.isRunning()) {
			messages.addMessage("Stopping prior execution...");
			try {
				model.signalStop();
				messages.addMessage("Execution stopped.");
			} catch (StencilRunner.AbnormalTerminiationException e) {
				messages.addError("Error stopping Stencil program (may not be 100% stopped).");
				messages.addError(e.getMessage());
			}
		}

		reporter.clear();
		try {model.compile();}
		catch (Exception e) {
			stencilContentPanel.removeAll();
			messages.addError(e.getMessage());
			messages.addError("Error in compile.  Execution not attempted.");
			throw new RuntimeException("Error compiling stencil.", e);
		}

		try {
			//Make sure things are up front
			stencilFrame.setVisible(true);
			stencilFrame.toFront();
			messageFrame.toFront();
			editorFrame.toFront();

			//Reset the content pane
			stencilContentPanel.removeAll();
			stencilContentPanel.add(model.getStencilPanel(), BorderLayout.CENTER);
			stencilContentPanel.revalidate();  //Forces the layout to execute NOW

			//Run it!
			model.execute();
		}
		catch (Exception e) {
			reporter.addError(e.getMessage());
			reporter.addError("Execution aborted.");
			e.printStackTrace();
		}
	}

	/**What application is this interactive application currently displaying?*/
	public Model getModel() {return model;}

	/**Synch the interactive application to the state of the passed application.*/
	public void set(Model model) {
		controller.removeMutable(model, Type.All);

		this.model = model;
		controller.addMutable(model, Type.All);
		model.addAllListeners(controller);
		model.fireAll();

		stencilContentPanel.removeAll();
		if (model.getStencilPanel() != null) {
			stencilContentPanel.add(model.getStencilPanel(), BorderLayout.CENTER);
		}
		stencilContentPanel.revalidate();
		stencilContentPanel.repaint();
	}

	
	//TODO: Implement dynamic export menu that lists all options supported by the adapter (and no others)
	public void setupMenus() {
		JMenuBar b = new JMenuBar();
		editorFrame.setJMenuBar(b);

		//File menu
		final JMenu file = new JMenu("File");
		b.add(file);

		final JMenuItem open = new JMenuItem("Open");
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));

		final JMenuItem save = new JMenuItem("Save");
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));

		final JMenuItem exportPNG = new JMenuItem("Export Raster");
		final JMenuItem exportEPS = new JMenuItem("Export Vector");
		final JMenuItem exportTuples = new JMenuItem("Export Tuples");
		

		final JMenuItem close = new JMenuItem("Close");
		close.setMnemonic(KeyEvent.VK_W);
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.META_MASK));

		ActionListener fileMenuListener = new ActionListener(){
			public void actionPerformed(ActionEvent arg) {
				if (arg.getSource() == open) {
					JFileChooser fc = new JFileChooser();

					fc.setSelectedFile(new java.io.File(WorkingDirectory.getWorkingDir()+"test.stencil"));

					int stat = fc.showOpenDialog(editorFrame);
					if (stat == JFileChooser.APPROVE_OPTION) {
						String filename = fc.getSelectedFile().getAbsolutePath();
						StencilIO.load(filename, model);
						WorkingDirectory.setWorkingDir(filename);
						set(model);
					} else if (stat == JFileChooser.ERROR_OPTION){
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
				} else if (arg.getSource() == save) {
					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new java.io.File(WorkingDirectory.getWorkingDir()+"test.stencil"));

					int stat = fc.showSaveDialog(editorFrame);
					if (stat == JFileChooser.APPROVE_OPTION) {
						String filename = fc.getSelectedFile().getAbsolutePath();
						StencilIO.save(filename, model);
						WorkingDirectory.setWorkingDir(filename);
					} else if (stat == JFileChooser.ERROR_OPTION) {
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
				} else if (arg.getSource() == exportTuples) {
					JFileChooser fc = new JFileChooser();
					int stat = fc.showSaveDialog(editorFrame);
					if (stat == JFileChooser.APPROVE_OPTION) {
						String filename = fc.getSelectedFile().getAbsolutePath();

						try {model.export(filename, "TUPLES", null);}
						catch(Exception e) {throw new RuntimeException("Error exporting image.", e);}

					} else if (stat == JFileChooser.ERROR_OPTION) {
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
				} else if (arg.getSource() == exportPNG) {
					JFileChooser fc = new JFileChooser();
					int stat = fc.showSaveDialog(editorFrame);
					if (stat == JFileChooser.APPROVE_OPTION) {
						String filename = fc.getSelectedFile().getAbsolutePath();

						try {model.export(filename, "RASTER", Application.EXPORT_RESOLUTION);}
						catch(Exception e) {throw new RuntimeException("Error exporting image.", e);}
					} else if (stat == JFileChooser.ERROR_OPTION) {
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
				} else if (arg.getSource() == exportEPS) {
					JFileChooser fc = new JFileChooser();
					int stat = fc.showSaveDialog(editorFrame);
					if (stat == JFileChooser.APPROVE_OPTION) {
						String filename = fc.getSelectedFile().getAbsolutePath();

						try {model.export(filename, "VECTOR", null);}
						catch(Exception e) {throw new RuntimeException("Error exporting image.", e);}
					} else if (stat == JFileChooser.ERROR_OPTION) {
						java.awt.Toolkit.getDefaultToolkit().beep();
					}
				} else if (arg.getSource() == close) {editorFrame.dispose();}
			}
		};

		open.addActionListener(fileMenuListener);
		save.addActionListener(fileMenuListener);
		exportPNG.addActionListener(fileMenuListener);
		exportEPS.addActionListener(fileMenuListener);
		exportTuples.addActionListener(fileMenuListener);
		close.addActionListener(fileMenuListener);

		file.add(open);
		file.add(save);
		file.add(new JSeparator());
		file.add(exportTuples);
		file.add(exportPNG);
		file.add(exportEPS);
		file.add(new JSeparator());
		file.add(close);

		JMenu edit =  this.editor.getEditMenu();
		b.add(edit);


		final JMenu window = new JMenu("Window");
		b.add(window);

		final JMenuItem editor = new JMenuItem("Editor");
		editor.setMnemonic(KeyEvent.VK_E);
		editor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.META_MASK));

		final JMenuItem stencil = new JMenuItem("Stencil");
		stencil.setMnemonic(KeyEvent.VK_S);
		stencil.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.META_MASK));

		final JMenuItem messages = new JMenuItem("Messages");
		messages.setMnemonic(KeyEvent.VK_M);
		messages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.META_MASK));

 		ActionListener windowMenuListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				if (arg.getSource() == editor) {
					editorFrame.setVisible(true);
					editorFrame.toFront();
				} else if (arg.getSource() == stencil) {
					stencilFrame.setVisible(true);
					stencilFrame.toFront();
				} else if (arg.getSource() == messages) {
					messageFrame.setVisible(true);
					messageFrame.toFront();
				} else {
					throw new RuntimeException("Menu action not handled: " + arg.getSource());
				}
			}
		};

		editor.addActionListener(windowMenuListener);
		stencil.addActionListener(windowMenuListener);
		messages.addActionListener(windowMenuListener);

		window.add(editor);
		window.add(stencil);
		window.add(messages);

	}

	public void run() {
		editorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		//Setup size and location
		java.awt.Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = dim.width/2;
		if (width < editorFrame.getMinimumSize().width) {width =editorFrame.getMinimumSize().width;}
		editorFrame.setSize(width, (int) (dim.height*.6));
		editorFrame.setLocation(10, 10);

		stencilFrame.setSize(((int) (dim.getWidth() * .3)), ((int) (dim.getHeight() * .3)));
		stencilFrame.setLocation(editorFrame.getWidth() + editorFrame.getX() + 10, 0);

		messageFrame.setSize(editorFrame.getWidth(), ((int) (editorFrame.getHeight() * .3)));
		messageFrame.setLocation(editorFrame.getX(), editorFrame.getHeight() +editorFrame.getY() + 20);

		editorFrame.addWindowListener(new AppCloser(this));

		editorFrame.setVisible(true);
		editor.naturalSize();
		messageFrame.setVisible(true);
		stencilFrame.setVisible(true);
		editorFrame.toFront();
	}

	/**Default mode, pulls up a window and waits for mouse stuff
	 * to do more.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		try {
			//Make things nice for OS X
			if (System.getProperty("os.name").contains("Mac OS X")) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPLICATION_NAME);
			}
		} catch (Exception e) {/*Ignore, changes are cosmetic.*/}
		
		String[] configs = PropertyManager.getConfigFiles(args);
		PropertyManager.loadProperties(configs, PropertyManager.exploreConfig, PropertyManager.stencilConfig, SESSION_CONFIGURATION_FILE);

		//OS-Specific items...
		String os;
		try {os = System.getProperty("os.name");}
		catch (Exception e) {os = "";}

		if (os.contains("Mac") && os.contains("OS X")) {
			System.setProperty("com.apple.mrj.application.growbox.intrudes","false");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}


		Model model = new Model();
		Interactive app = new Interactive(model);
		String sessionFile = Application.getOpenFile(args);
		if (sessionFile != null) {
			Interactive.sessionFile = sessionFile;
			WorkingDirectory.setWorkingDir(sessionFile);
		}


		try {
			StencilIO.load(Interactive.sessionFile, model);
			app.set(model);
		} catch (Exception e) {
			reporter.addMessage("Error restoring state.  Continuing anyway...");
			e.printStackTrace();
		}

		SwingUtilities.invokeAndWait(app);
	}
}
