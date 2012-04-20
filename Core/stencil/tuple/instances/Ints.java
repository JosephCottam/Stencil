package stencil.tuple.instances;

import stencil.tuple.Tuple;

public final class Ints implements Tuple {
	private final int[] values;

	public Ints(int[] values) {this.values = values;}
	
	@Override
	public Integer get(int idx) {return values[idx];}
	@Override
	public int size() {return values.length;}
}