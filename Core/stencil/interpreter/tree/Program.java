package stencil.interpreter.tree;

import java.util.NoSuchElementException;

public class Program {
	private final Layer[] layers;			//TODO: Merge StreamDef and Layer to single Store type (requires true dispatcher)
	private final StreamDef[] streamDefs;
	private final DynamicRule[] dynamics;
	private final Order order;
	private final Canvas canvas;
	
	public Program(Canvas canvas, Layer[] layers, StreamDef[] streams, Order order, DynamicRule[] dynamics) {
		this.canvas = canvas;
		this.order = order;
		this.layers = layers;
		this.streamDefs = streams;
		this.dynamics = dynamics;
	}
	
	public Canvas canvas() {return canvas;}
	public Layer getLayer(String name) {
		for (Layer layer: layers) {
			if (layer.getName().equals(name)) {return layer;}
		}
		throw new NoSuchElementException(String.format("Could not find layer %1$s in layers list.", name));
	}
	public DynamicRule[] allDynamics() {return dynamics;}
	public StreamDef[] streamDefs() {return streamDefs;}
	public Layer[] layers() {return layers;}
	public Order order() {return order;}
}
