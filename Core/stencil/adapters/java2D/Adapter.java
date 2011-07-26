
package stencil.adapters.java2D;

import java.awt.Color;

import stencil.adapters.java2D.columnStore.Table;
import stencil.parser.ParseStencil;
import stencil.interpreter.TupleStore;
import stencil.interpreter.tree.Program;
import stencil.interpreter.tree.Guide;
import stencil.interpreter.tree.Specializer;
import stencil.adapters.java2D.interaction.CanvasAsStore;
import stencil.adapters.java2D.interaction.ViewAsStore;
import stencil.adapters.java2D.interaction.ZoomPanHandler;
import stencil.adapters.java2D.render.guides.Guide2D;
import stencil.adapters.java2D.render.guides.*;
import stencil.adapters.java2D.render.Renderer;
import stencil.adapters.java2D.util.MultiThreadPainter;
import stencil.display.DisplayLayer;


public final class Adapter implements stencil.adapters.Adapter {
	public static final Adapter ADAPTER = new Adapter();
	
	private boolean defaultMouse;

	private Panel currentPanel;
	public synchronized Panel compile(String programSource) throws Exception {
		currentPanel = new Panel();
		Program program = ParseStencil.program(programSource, this);
		Canvas canvas = new Canvas(program.canvas().specializer(), program.layers());
		constructGuides(canvas, program);

		currentPanel.init(canvas, program);

		
		if (defaultMouse) {
			ZoomPanHandler zp = new ZoomPanHandler();
			currentPanel.addMouseListener(zp);
			currentPanel.addMouseMotionListener(zp);
		}
		return currentPanel;
	}

	public Class getGuideClass(String name) {
		if (name.equals("axis")) {return Axis.class;}
		else if (name.equals("legend")) {return Legend.class;}
		else if (name.equals("trend")) {return TrendLine.class;}
		else if (name.equals("pointLabels")) {return PointLabel.class;}
		
		throw new IllegalArgumentException(String.format("Guide type %1$s not known in adapter.", name));
	}

	public Table makeLayer(String name, Specializer spec) {
		String type = (String) spec.get(DisplayLayer.TYPE_KEY);
		return LayerTypeRegistry.makeTable(name, type);
	}

	public void setDefaultMouse(boolean m) {this.defaultMouse = m;}
	public void setDebugColor(Color c) {Renderer.Util.DEBUG_COLOR = c;}
	

	public void setRenderQuality(String value) throws IllegalArgumentException {
		if (value.equals("LOW")) {
			MultiThreadPainter.renderQuality = MultiThreadPainter.LOW_QUALITY;
		} else if (value.equals("HIGH")) {
			MultiThreadPainter.renderQuality = MultiThreadPainter.HIGH_QUALITY;
		} else {
			throw new IllegalArgumentException("Could not set render quality to unknown value: " + value);
		}
	}
	
	
	//TODO: Lift out into a grammar pass...
	private void constructGuides(Canvas canvas, Program program) {
		int legendCount = 0;//How many side-bars have been created?
		
		for (Guide guideDef : program.allGuides()) {
			if (guideDef.type().equals("axis")) {
				Guide2D guide = new Axis(guideDef);
				canvas.addGuide(guideDef.identifier(), guide);
			} else if (guideDef.type().equals("legend")) {
				Guide2D guide = new Legend(guideDef, legendCount++);
				canvas.addGuide(guideDef.identifier(), guide);
			} else if (guideDef.type().equals("pointLabels")) {
				Guide2D guide = new PointLabel(guideDef);
				canvas.addGuide(guideDef.identifier(), guide);
			} else if (guideDef.type().equals("trend")) {
				Guide2D guide = new TrendLine(guideDef);
				canvas.addGuide(guideDef.identifier(), guide);
			} else if (guideDef.type().equals("crossLegend")) {
				Guide2D guide = new CrossLegend(guideDef, legendCount++);
				canvas.addGuide(guideDef.identifier(), guide);
			} else {
				throw new IllegalArgumentException("Unknown guide type requested: " + guideDef.type());
			}
		}
	}

	@Override
	public TupleStore makeCanvas(Specializer spec) {return new CanvasAsStore();}

	@Override
	public TupleStore makeView(Specializer spec) {return new ViewAsStore();}
}
