package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.List;

import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableView;
import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

import static stencil.parser.ParserConstants.INPUT_FIELD;
import static stencil.display.Guide2D.FIELDS_KEY;

/**Iterates the tuples of a layer creating a cross between the label field and the value field.*/
public final class LayerCrossSampler extends LayerSampler {
	private final String[] inNames;
	private final TuplePrototype outPrototype;
	
	public LayerCrossSampler(DisplayLayer layer, Specializer spec) {
		super(layer);
		
		inNames = Converter.toString(spec.get(FIELDS_KEY)).split("\\s*,\\s*");
		
		if (inNames.length != 2) {throw new IllegalArgumentException("Can only handle two-element crosses at this time; recieved: " + spec.get(FIELDS_KEY));}
		
		
		String[] outNames = new String[inNames.length+1];
		System.arraycopy(inNames,0, outNames, 0, inNames.length);
		outNames[outNames.length-1] = INPUT_FIELD;
		outPrototype = new TuplePrototype(outNames);
	}

	public List<Tuple> sample(SampleSeed seed, Specializer details) {
		List<Tuple> l = new ArrayList();
		TableView view = ((Table) layer).tenured();		
		for (Glyph mark: view) {
			final Object[] vals = new Object[outPrototype.size()];
			for (int i=0; i<vals.length-1; i++) {vals[i] = mark.get(inNames[i]);}
			vals[vals.length-1] = mark.get(inNames[inNames.length-1]);
			Tuple t = new PrototypedArrayTuple(outPrototype, vals);
			if (!l.contains(t)) {l.add(t);}
		}
		return l;
	}
	
	public TuplePrototype prototype() {return outPrototype;}
}
