package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.parser.tree.Layer;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;

public final class IdentitySampler implements SampleOperator {
	private final Layer layerDef;
	
	public IdentitySampler(Layer layer) {
		this.layerDef = layer;
	}

	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		for (Tuple t: getDisplayLayer()) {l.add(t);}
		return l;
	}
	
	private final DisplayLayer<Tuple> getDisplayLayer() {return layerDef.getDisplayLayer();}
}
