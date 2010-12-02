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
package stencil.adapters.java2D;

import java.awt.Color;

import stencil.adapters.java2D.data.DoubleBufferLayer;
import stencil.adapters.java2D.data.Glyph2D;
import stencil.adapters.java2D.data.Guide2D;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Guide;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Program;
import stencil.adapters.java2D.data.glyphs.Basic;
import stencil.adapters.java2D.data.guides.*;
import stencil.adapters.java2D.util.MultiThreadPainter;
import stencil.adapters.java2D.util.ZoomPanHandler;
import twitter4j.Trend;

public final class Adapter implements stencil.adapters.Adapter<Glyph2D> {
	public static final Adapter ADAPTER = new Adapter();
	
	private boolean defaultMouse;
	
	public Panel generate(Program program) {
		Canvas canvas = new Canvas(program.getCanvasDef(), program.getLayers());
		constructGuides(canvas, program);
		Panel panel = new Panel(canvas, program);
		
		if (defaultMouse) {
			ZoomPanHandler zp = new ZoomPanHandler();
			panel.addMouseListener(zp);
			panel.addMouseMotionListener(zp);
		}
		return panel;
	}

	public Class getGuideClass(String name) {
		if (name.equals("axis")) {return Axis.class;}
		else if (name.equals("legend")) {return Legend.class;}
		else if (name.equals("trend")) {return Trend.class;}
		else if (name.equals("pointLabels")) {return PointLabel.class;}
		
		throw new IllegalArgumentException(String.format("Guide type %1$s not known in adapter.", name));
	}

	public stencil.display.DisplayLayer makeLayer(Layer l) {
		return DoubleBufferLayer.instance(l);
	}

	public void setDefaultMouse(boolean m) {this.defaultMouse = m;}
	public void setDebugColor(Color c) {Basic.DEBUG_COLOR = c;}
	

	public void setRenderQuality(String value) throws IllegalArgumentException {
		if (value.equals("LOW")) {
			MultiThreadPainter.renderQuality = MultiThreadPainter.LOW_QUALITY;
		} else if (value.equals("HIGH")) {
			MultiThreadPainter.renderQuality = MultiThreadPainter.HIGH_QUALITY;
		} else {
			throw new IllegalArgumentException("Could not set render quality to unknown value: " + value);
		}
	}
	
	public Panel compile(String programSource) throws Exception {
		return generate(ParseStencil.parse(programSource, this));
	}
	
	
	//TODO: Lift out into a grammar pass...
	private void constructGuides(Canvas canvas, Program program) {
		int legendCount = 0;//How many side-bars have been created?
		
		for (Guide guideDef : program.getCanvasDef().getGuides()) {
			stencil.parser.tree.Selector sel = guideDef.getSelector();
			String guideType = guideDef.getGuideType();
			
			if (guideType.equals("axis")) {
				Guide2D guide = new Axis(guideDef);
				canvas.addGuide(sel, guide);
			} else if (guideType.equals("legend")) {
				Guide2D guide = new Legend(guideDef, legendCount++);
				canvas.addGuide(sel, guide);
			} else if (guideType.equals("pointLabels")) {
				Guide2D guide = new PointLabel(guideDef);
				canvas.addGuide(sel, guide);
			} else if (guideType.equals("trend")) {
				Guide2D guide = new TrendLine(guideDef);
				canvas.addGuide(sel, guide);
			} else {
				throw new IllegalArgumentException("Unknown guide type requested: " +guideType);
			}
		}
	}
}
