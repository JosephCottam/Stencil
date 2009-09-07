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
package stencil.adapters.piccoloDynamic;



import edu.umd.cs.piccolo.util.PPaintContext;

import stencil.adapters.piccoloDynamic.util.PiccoloGlyph;
import stencil.adapters.piccoloDynamic.util.ZPLayer;
import stencil.adapters.piccoloDynamic.glyphs.Node;
import stencil.adapters.piccoloDynamic.guides.*;
import stencil.display.DisplayGuide;
import stencil.display.StencilPanel;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Program;

/**Given a parsed program, creates the necessary objects to
 * run a piccolo-driven visualization.
 * @author jcottam
 *
 */
public class Adapter implements stencil.adapters.Adapter<PiccoloGlyph> {
	public static final Adapter INSTANCE = new Adapter();
	
	/**Maximum number of dynamic update cycles to run after all data is loaded.
	 * This attempts to put the visualization into a stable state.
	 *
	 */
	public static final int FINALIZE_ATTEMPT_MAX = 100;

	private boolean lowQuality = false;
	
	private Adapter() {/**Adapter is a singleton.*/}

	public Panel generate(Program program) {
		Panel panel = new Panel(program);
		constructGuides(panel, program);

		if (lowQuality) {panel.getCanvas().canvas.setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);}
		
		return panel;
	}

	public void setDebugColor(java.awt.Color c) {stencil.adapters.piccoloDynamic.glyphs.Node.BOUNDING_BOX_COLOR = c;}
	public void setDefaultMouse(boolean m) {stencil.display.StencilPanel.DEFAULT_INTERACTION =m;}

	/**Create the Piccolo DisplayLayer for the Stencil AST layer specified.*/
	public DisplayLayer makeLayer(Layer l) {
		DisplayLayer layer = new DisplayLayer(new ZPLayer(), l.getName(),l.getImplantation());
		return layer;
	}
	
	
	public Class getGuideClass(String name) {
		if (name.equals("axis")) {return Axis.class;}
		else if (name.equals("sidebar")) {return Sidebar.class;}
		
		throw new IllegalArgumentException(String.format("Guide type %1$s not known in adapter.", name));
	}
	
	
	private void constructGuides(Panel panel, Program program) {
		int sidebarCount = 0;//How many side-bars have been created?
		
		for (Layer layer: program.getLayers()) {
			for (Guide guideDef: layer.getGuides()) {
				String guideType = guideDef.getGuideType();
				DisplayLayer l = panel.getLayer(layer.getName());
				String attribute = guideDef.getAttribute();
				
				if (guideType.equals("axis")) {
					DisplayGuide guide = new Axis(attribute, guideDef.getArguments());
					l.addGuide(attribute, guide);
					
					if (l.hasGuide("X") && l.hasGuide("Y")) {
						((Axis) l.getGuide("X")).setConnect(true);
						((Axis) l.getGuide("Y")).setConnect(true);
					}
				} else if (guideType.equals("sidebar")) {
					DisplayGuide guide = new Sidebar(attribute, guideDef.getArguments(), sidebarCount++);
					l.addGuide(attribute, guide);	
				} else {
					throw new IllegalArgumentException("Unknown guide type requested: " +guideType);
				}
			}
		}
	}
	
	
	/**Will set the render default render quality to low if the passed value
	 * is upper-case equal to "LOW".  Otherwise the default quality is used.
	 */
	public void setRenderQuality(String value) {
		if (value == null) {return;}
		
		if ("LOW".equals(value.toUpperCase())) {lowQuality = true;}
		else {lowQuality = false;}
	}
	

	public void finalize(StencilPanel p) {
		Panel panel = (Panel) p;
		
		int attempts =0;
		panel.setTransfers();
		
		while (panel.hasUpdates() && attempts < FINALIZE_ATTEMPT_MAX) {
			for (Layer layer: panel.getProgram().getLayers()) {
				for (Object t: layer.getDisplayLayer()) {
					Node n = ((NodeTuple) t).getNode();
					n.prePaint();
				}
			}
			attempts++;
		}
	}

}
