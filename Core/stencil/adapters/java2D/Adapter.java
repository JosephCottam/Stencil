
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
		else if (name.equals("sumLegend")) {return Legend.class;}
		else if (name.equals("gridlines")) {return Gridlines.class;}
		else if (name.equals("title")) {return Title.class;}
		
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
		

		Guide xAxis =null,yAxis=null;
		for (Guide guideDef : program.allGuides()) {
			if (guideDef.type().equals("axis")) {
				if (guideDef.identifier().contains("X")) {xAxis = guideDef;}
				if (guideDef.identifier().contains("Y")) {yAxis = guideDef;}
			} else if (guideDef.type().equals("legend")) {
				canvas.addGuide(new Legend(guideDef, legendCount++));
			} else if (guideDef.type().equals("sumLegend")) {
					canvas.addGuide(new Legend(guideDef, legendCount++));				
			} else if (guideDef.type().equals("pointLabels")) {
				canvas.addGuide(new PointLabel(guideDef));
			} else if (guideDef.type().equals("trend")) {
				canvas.addGuide(new TrendLine(guideDef));
			} else if (guideDef.type().equals("crossLegend")) {
				canvas.addGuide(new CrossLegend(guideDef, legendCount++));
			} else if (guideDef.type().equals("gridlines")) {
				canvas.addGuide(new Gridlines(guideDef));
			} else if (guideDef.type().equals("title")) {
				canvas.addGuide(new Title(guideDef));
			} else {
				throw new IllegalArgumentException("Unknown guide type requested: " + guideDef.type());
			}
		}
		
		
		if (xAxis != null && yAxis != null) {
			if (yAxis.isNumeric()) {
				canvas.addGuide(new Axis(xAxis, yAxis.generators()[0]));
			} else {
				canvas.addGuide(new Axis(xAxis, null));
			}
			if (xAxis.isNumeric()) {
				canvas.addGuide(new Axis(yAxis, xAxis.generators()[0]));
			} else {
				canvas.addGuide(new Axis(yAxis, null));				
			}
		} else if (xAxis != null) {
			canvas.addGuide(new Axis(xAxis, null));			
		} else if (yAxis != null) {
			canvas.addGuide(new Axis(yAxis, null));			
		}
	}

	@Override
	public TupleStore makeCanvas(Specializer spec) {return new CanvasAsStore();}

	@Override
	public TupleStore makeView(Specializer spec) {return new ViewAsStore();}
}
