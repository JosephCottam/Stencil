package stencil.tuple.instances;

import stencil.tuple.Tuple;

public final class Doubles implements Tuple {
	private final double[] values;

	public Doubles(double[] values) {
		this.values = values;
	}
	
	@Override
	public Double get(int idx) {return values[idx];}
	public boolean isDefault(String name, Object value) {return false;}
	@Override
	public int size() {return values.length;}
}