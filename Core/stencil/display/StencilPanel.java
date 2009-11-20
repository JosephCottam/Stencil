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
package stencil.display;


import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import stencil.adapters.Glyph;
import stencil.interpreter.Interpreter;
import stencil.parser.tree.Canvas;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Program;
import stencil.parser.tree.Rule;
import stencil.parser.tree.View;
import stencil.tuple.Tuple;

/**Wraps the layers and glyphs to tie them to a display context.
 * 
 * T -- The glyph type stored in tables
 * L -- The layer type
 * C -- The canvas type
 * */
public abstract class StencilPanel<T extends Glyph, L extends DisplayLayer<T>, C extends Component> extends javax.swing.JPanel {
	/**Set flag to true when default interaction states are desired.
	 * Set to false when all interaction should be handled in stencil rules.
	 * Default state is true.
	 * Setting this will only affect newly created panels, not existing ones.
	 */
	public static boolean DEFAULT_INTERACTION= true;

	protected Program program;
	protected C canvas;
	private Interpreter interpreter;
	
	
	public static int ABSTRACT_SCREEN_RESOLUTION = 72;

	public StencilPanel() {super();}
	public StencilPanel(boolean isDoubleBuffered) {super(isDoubleBuffered);}
	public StencilPanel(LayoutManager manager, boolean isDoubleBuffered) {super(manager, isDoubleBuffered);}
	public StencilPanel(LayoutManager manager) {super(manager);}
	public StencilPanel(Program program, C canvas) {
		super();
		this.program = program;
		this.canvas = canvas;
		this.interpreter = new Interpreter(this);

		
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
	}
	
	/**Clean up any system resources/threads/etc. required
	 * by this panel.  The default implementation does nothing
	 * as some adapters may have nothing to dispose of.
	 */
	public void dispose() {/*By default, no action is taken*/}

	/**Returns an unmodifiable copy of the current layers mapping.*/
	public List<String> getLayers() {
		String[] layers = new String[program.getLayers().size()];
		
		for (int i=0; i<layers.length; i++) {
			layers[i] = program.getLayers().get(i).getName();
		}
		return Arrays.asList(layers);
	}

	/**Get a named layer from the layers map.  Returns null if no layer was found.
	 * TODO: Is this cast really the right way to go?  Can we do a runtime-type check?  
	 * Can we use the generics to do it at compile time (ensure that the Layer generic arguments equals the Parser argument)?
	 *    --> Would require us to parameterize the TreeAdapter with the instance type.  Then we would need to instantiate
	 *        the TreeAdapter, adapter and panel through the same process.  Maybe the adapter could provide the tree adapter...is that wierd?
	 * */
	public L getLayer(String name) {return (L) program.getLayer(name).getDisplayLayer();}

	/**Returns an unmodifiable set of the underlying rules.*/
	public Program getProgram() {return program;}

	/**Get a tuple representation of the canvas.*/
	public abstract CanvasTuple getCanvas();

	/**Get a tuple representation of the view.*/
	public abstract ViewTuple getView();
	
	/**Exports the panel to a the file specified.  Export type is determined
	 * by the type parameter; panel instance determines the appropriate
	 * type indicators (thought 'VECTOR', 'RASTER' and 'TUPLE' are suggested as a minimum).
	 * The 'info' parameter determines any additional arguments required (e.g., dpi)
	 * that are needed by the actual export.
	 *
	 * By default, no export is implemented and an UnsupportedOperationException is thrown instead.
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
	 * @param filename File to tuples items in
	 * @throws Exception This method is not expected to resolve file issues.
	 */

	/**Export a list of tuples describing all glyphs (even those out of the view), plus the view and canvas.*/
	protected void exportTuples(String filename) throws Exception {
		java.io.FileWriter writer = new java.io.FileWriter(filename);
		IDOrdered comp = new IDOrdered();

		writer.write(getCanvas().toString() + "\n");
		writer.write(getView().toString() + "\n");

		for (Layer l: program.getLayers()) {
			TreeSet<Tuple> s  =new TreeSet<Tuple>(comp);
			DisplayLayer<? extends Tuple> layer = l.getDisplayLayer();
			for (Tuple t: layer) {s.add(t);}
			for (Tuple t: s) {
				writer.write(t.toString().replace("\n", "\\n"));
				writer.write("\n");
			}
		}
		writer.close();
	}
	
	protected static final class IDOrdered implements Comparator<Tuple> {
		public int compare(Tuple o1, Tuple o2) {
			String s1 = o1.get("ID").toString();
			String s2 = o2.get("ID").toString();
			return s1.compareTo(s2);
		}
		
	}
	
	/**Return a list of the valid 'type' arguments to the Export command.
	 * May return a zero-length array, but should never return null.*/
	public String[] getExports() {return new String[0];}
	
	
	//------------------------------------------------------------------------------------------
	//Interpreter Operators
	public void processTuple(Tuple source) throws Exception {interpreter.processTuple(source);}
	
	public void preRun() {
		//TODO: Modify when view and canvas can have multiple instances
		View.global = getView();
		Canvas.global = getCanvas();
	}
	
	//------------------------------------------------------------------------------------------
	//Runtime support operations.  These are methods called by the interpreter.
	
	/**Add a dynamic binding that will apply the given rule with the passed data
	 * to the passed glyph object. 
	 */
	public abstract void addDynamic(T g, Rule rule, Tuple source);
}
