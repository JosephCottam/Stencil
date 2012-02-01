package stencil.interpreter.tree;

import java.util.NoSuchElementException;

public class Program {
	private final Layer[] layers;			//TODO: Merge StreamDef and Layer to single Store type (requires true dispatcher)
	private final StreamDec[] streamDecs;
	private final StreamDef[] streamDefs;
	private final DynamicRule[] dynamics;
	private final Guide[] guides;
	private final Object[] operators;
	private final Order order;
	private final ViewOrCanvas view;
	private final ViewOrCanvas canvas;
	
	public Program(ViewOrCanvas view, ViewOrCanvas canvas, Layer[] layers, StreamDec[] streamDecs, StreamDef[] streamDefs, Order order, DynamicRule[] dynamics, Guide[] guides, Object[] operators) {
		this.view = view;
		this.canvas = canvas;
		this.order = order;
		this.layers = layers;
		this.streamDecs = streamDecs;
		this.streamDefs = streamDefs;
		this.dynamics = dynamics;
		this.guides = guides;
		this.operators = operators;
	}
	
	public ViewOrCanvas view() {return view;}
	public ViewOrCanvas canvas() {return canvas;}
	
	public Layer getLayer(String name) {
		for (Layer layer: layers) {
			if (layer.getName().equals(name)) {return layer;}
		}
		throw new NoSuchElementException(String.format("Could not find layer %1$s in layers list.", name));
	}

	public DynamicRule[] allDynamics() {return dynamics;}
	public Guide[] allGuides() {return guides;}
	
	public StreamDef[] streamDefs() {return streamDefs;}
	public StreamDec[] streamDecs() {return streamDecs;}
	
	public Layer[] layers() {return layers;}
	public Order order() {return order;}
	public Object[] operators() {return operators;}
}
