package stencil.adapters.java2D.util;

import java.lang.reflect.Array;
import java.util.List;

import stencil.interpreter.tree.DynamicRule;
import stencil.adapters.java2D.columnStore.Table;
import stencil.adapters.java2D.columnStore.TableShare;
import stencil.adapters.java2D.columnStore.column.Column;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

/**Executes a dynamic update rule on all relevant glyphs.
 * 
 * TODO: Implement using data state operators
 * */
public final class DynamicUpdateTask extends UpdateTask<DynamicRule> {
	private final Table table;
	private final int[] targets;
	public DynamicUpdateTask(Table table, DynamicRule rule) {
		//TODO: Re-arrange when dynamic rule is frozen
		super(rule, rule.action().prototype().toString());
		this.table = table;
		
		TuplePrototype schema = table.prototype();
		TuplePrototype resultProto = rule.action().prototype();
		targets = new int[resultProto.size()];
		for (int i=0; i<resultProto.size(); i++) {
			String field = rule.action().prototype().get(i).name();
			targets[i] = schema.indexOf(field);
			if (targets[i] <0) {throw new IllegalArgumentException("Dynamic binding requested for field not found in table: " + field);}
		}
	}
	
	public Finisher update() {
		TableShare share = table.viewpoint();
		final List<Tuple> result = viewpointFragment.apply(share);
		return new Finish(share, targets, result);
	}
	
	private static class Finish implements Finisher {
		private final TableShare share;
		private final Column[] newCols;
		private final int[] targets;
		
		public Finish(TableShare share, int[] targets, List<Tuple> results) {
			this.share = share;
			this.targets = targets;

			//Calculate necessary updates for columns based on results
			newCols = new Column[targets.length];
			Object[] data = new Object[targets.length];
			Class[] types = new Class[targets.length];
			for (int i=0; i<data.length; i++) {
				Column replacing = share.columns()[targets[i]];
				types[i] = replacing.type();
				data[i] = Array.newInstance(types[i], results.size());	
			}
			
			for (int resultIndex=0; resultIndex< results.size(); resultIndex++) {
				for (int field=0; field<targets.length; field++) {
					Tuple result = results.get(resultIndex);
					Object value;

					if (result == null) {value = share.columns()[field].get(resultIndex);}
					else {value = result.get(field);}

					Array.set(data[field], resultIndex, Converter.convert(value, types[field]));		//TODO: Remove call to convert when conversions are explicit in the stencil program
				}
			}

			for (int i=0; i < data.length; i++) {
				Column replacing = share.columns()[targets[i]];
				newCols[i] = replacing.replaceAll(data[i]);
			}
			
		}
		
		public void finish() {
			for (int i=0; i<targets.length; i++) {share.setColumn(targets[i], newCols[i]);}
		}
		
	}
}
