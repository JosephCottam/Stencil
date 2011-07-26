package stencil.adapters.java2D.columnStore.util;

import java.util.Comparator;

public class FieldSorter implements Comparator<StoreTuple> {
	private final int basisField;
	public FieldSorter(int basisField) {this.basisField = basisField;}

	public int compare(StoreTuple o1, StoreTuple o2) {
		double diff = ((Number) o1.get(basisField)).doubleValue() - ((Number) o2.get(basisField)).doubleValue();
		return (int) Math.signum(diff);
	}
}
