package stencil.display;


import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import stencil.display.Display;
import stencil.interpreter.Interpreter;
import stencil.interpreter.tree.Program;
import stencil.interpreter.tree.Layer;
import stencil.tuple.SourcedTuple;
import stencil.tuple.Tuple;
import stencil.util.IndexTupleSorter;

/**Wraps the layers and glyphs to tie them to a display context.
 * 
 * T -- The glyph type stored in tables
 * L -- The layer type
 * C -- The canvas type
 * */
public abstract class StencilPanel<T extends Glyph, L extends DisplayLayer<T>, C extends DisplayCanvas> extends javax.swing.JPanel {
	/**Set flag to true when default interaction states are desired.
	 * Set to false when all interaction should be handled in stencil rules.
	 * Default state is true.
	 * Setting this will only affect newly created panels, not existing ones.
	 */
	public static boolean DEFAULT_INTERACTION= true;

	/**Should an adapter paint continuously, or only when directly requested?
	 * This should be set to true whenever painting timing is controlled by 
	 * a host application.
	 */
	public static boolean continuousPainting = true;

	
	protected Program program;
	protected C canvas;
	protected Interpreter interpreter;
	
	
	public static int ABSTRACT_SCREEN_RESOLUTION = 72;

	public StencilPanel() {super(); init();}
	public StencilPanel(boolean isDoubleBuffered) {super(isDoubleBuffered); init();}
	public StencilPanel(LayoutManager manager, boolean isDoubleBuffered) {super(manager, isDoubleBuffered); init();}
	public StencilPanel(LayoutManager manager) {super(manager); init();}
	public StencilPanel(Program program, C canvas) {
		super();
		init();
		setProgram(program);
		setCanvas(canvas);
	}
	
	protected void init() {
		this.setLayout(new BorderLayout());		
	}
	
	protected void setProgram(Program program) {
		this.program = program;
		this.interpreter = new Interpreter(this);
	}
	protected void setCanvas(C canvas) {
		this.canvas = canvas;
		this.add(canvas, BorderLayout.CENTER);
		this.validate();
	}
	
	
	/**Returns an unmodifiable copy of the current layers mapping.*/
	public List<L> layers() {
		List<L> layers = new ArrayList(program.layers().length);
		
		for (Layer l:program.layers()) {
			layers.add((L) l.implementation());
		}
		return Collections.unmodifiableList(layers);
	}

	/**Get a named layer from the layers map.  Returns null if no layer was found.
	 **/
	public L getLayer(String name) {return (L) program.getLayer(name).implementation();}

	/**Returns an unmodifiable set of the underlying rules.*/
	public Program getProgram() {return program;}

	/**Get a tuple representation of the canvas.*/
	public abstract CanvasTuple getCanvas();

	/**Get a tuple representation of the view.*/
	public abstract ViewTuple getView();
	
	/**Run clean-up actions.*/
	public abstract void dispose();	
	
	/**Exports the panel to a the file specified.  Export type is determined
	 * by the type parameter; panel instance determines the appropriate
	 * type indicators (thought 'VECTOR', 'RASTER' and 'TUPLE' are suggested as a minimum).
	 * The 'info' parameter determines any additional arguments required (e.g., dpi)
	 * that are needed by the actual export.
	 *
	 * By default, no export is implemented and an UnsupportedOperationException is thrown instead.
	 * 
	 * Export should make an attempt at relevant updates.
	 *
	 * @param filename File to save in
	 * @param type Flag indicating type (may be literal or logical, consult implementing class documentation)
	 * @param info Any additional parameters required by the eventual export method (consult implementing class)
	 * @throws Exception This method is not expected to resolve file issues.
	 */
	@SuppressWarnings("all") //Stub, so parameters are generally all unused...
	public void export(String filename, String type, Object info) throws Exception {
		type = type.toUpperCase();
		if ("TUPLES".equals(type) || "TXT".equals(type)) {exportTuples(filename);}
		else {throw new UnsupportedOperationException(String.format("Export of %1$s not implemented in panel %2$s.", type, this.getClass().getName()));}
	}
	
	
	/**Saves all of the tuples currently held in this panel.  This is
	 * a text representation of the graphic, independent of other file formats.  The format
	 * is one tuple per line. 
	 * 
	 * Tuples are ordered by layering and then lexicographically by ID within layers.
	 * Special tuples (like view and canvas) preface layer tuples.
	 *
	 * To completely represent
	 * the graphic, a tuple to represent the canvas and view are also need to be exported.
	 * These special tuples should have no layer.
	 *
	 * Export should make an attempt at relevant updates.
	 *
	 * @param filename File to tuples items in
	 * @throws Exception This method is not expected to resolve file issues.
	 */
	protected void exportTuples(String filename) throws Exception {
		java.io.FileWriter writer = new java.io.FileWriter(filename);

		writer.write(getCanvas().toString() + "\n");
		writer.write(getView().toString() + "\n");

		for (Layer l: program.layers()) {
			DisplayLayer<? extends Tuple> layer = l.implementation();
			int idIndex = layer.prototype().indexOf("ID");
			IndexTupleSorter sorter = new IndexTupleSorter(idIndex);
			TreeSet<Tuple> s  = new TreeSet(sorter);
			for (Tuple t: layer.viewpoint()) {s.add(t);}
			for (Tuple t: s) {
				writer.write(t.toString().replace("\n", "\\n"));
				writer.write("\n");
			}
		}
		writer.close();
	}
		
	/**Shutdown any panel-related threads.*/
	public void signalStop() {}
	
	
	//------------------------------------------------------------------------------------------
	//Interpreter Operators
	/**Process a tuple for this visualization.  This is the preferred means
	 * to add values to a visualization because it is the only means that is
	 * guaranteed to preserve consistency.
	 */
	public final void processTuple(SourcedTuple source) throws Exception {
		synchronized(canvas.visLock) {interpreter.processTuple(source);}
	}
	
	/**Actions that must be taken before the run will be valid.*/
	public void preRun() {
		//TODO: Modify when view and canvas can have multiple instances
		Display.canvas = getCanvas();
		Display.view = getView();
	}
	
	
	/**Optional diagnostic method.  Returns how many times the panel has been painted; returns -1 by default.**/
	public int paintCount() {return -1;}
	
	/**Perform post-run tasks preparatory to a "final" rendering (usually an export; may not actually be final).
	 * This typically involves forcing the pre-render tasks to run.**/
	public void postRun() {}

}
