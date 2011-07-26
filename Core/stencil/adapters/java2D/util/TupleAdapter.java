package stencil.adapters.java2D.util;

import java.util.HashMap;
import java.util.Map;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimpleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TupleFieldDef;

/**Creates a mapping from one tuple/prototype to another; 
 * index translation is the principal means but names can be used in prototypes.
 * 
 * @author jcottam
 *
 */
public final class TupleAdapter {

	private static final TuplePrototype buildPrototype(int[] translation, TuplePrototype basis) {
		String[] names = new String[translation.length];
		for (int i=0; i< names.length; i++) {names[i] = basis.get(i).name();}
		return buildPrototype(names, translation, basis);
	}

	private static final TuplePrototype buildPrototype(String[] names, int[] translation, TuplePrototype basis) {
		assert names.length == translation.length : "Name set and translation set must be of the same length";
		TupleFieldDef[] defs = new TupleFieldDef[names.length];
		for (int i=0; i< defs.length; i++) {
			SimpleFieldDef def = new SimpleFieldDef(names[i], basis.get(translation[i]).type(), basis.get(translation[i]).defaultValue());
			defs[i] = def;
		}
		return new TuplePrototype(defs);
	}

	
	/**Wrap a prototype so that indicies in the wrapp go to potentially different indicies in the wrapped.**/
	public static final class APrototype<T extends TupleFieldDef> extends TuplePrototype<T> {
		private final Map<String, Integer> translation;
		
		public APrototype(String[][] translation, TuplePrototype<T> base) {
			super(base);
			this.translation = new HashMap();
			for (String[] pair: translation) {
				assert pair.length ==2;
				assert base.indexOf(pair[1]) >=0;
				this.translation.put(pair[0], base.indexOf(pair[1]));
			}
			
		}

		
		public APrototype(Map<String, Integer> translation, TuplePrototype<T> base) {
			this.translation = translation;
		}
		
		@Override
		public boolean contains(String name) {return translation.containsKey(name) || super.contains(name);}

		@Override
		public int indexOf(String name) {
			if (translation.containsKey(name)) {return translation.get(name);}
			else {return super.indexOf(name);}
		}

		@Override
		public int indexOf(TupleFieldDef def) {
			if (translation.containsKey(def.name())) {return translation.get(def.name());}
			return super.indexOf(def);
		}
	}
	
	/**Wrap a tuple so that indicies in one go to potentially different indicies in the other.**/
	public static final class ATuple implements PrototypedTuple { 
		private final Tuple base;
		private final int[] translation;
		private final TuplePrototype prototype;
		
		public ATuple(int[] translation, PrototypedTuple base) {
			this.base = base;
			this.translation = translation;
			this.prototype = buildPrototype(translation, base.prototype());
		}
	
		@Override
		public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}
	
		@Override
		public Object get(int idx) throws TupleBoundsException {return base.get(translation[idx]);}
	
		@Override
		public TuplePrototype prototype() {return prototype;}
	
		@Override
		public int size() {return translation.length;}
		
		public String toString() {return Tuples.toString(this);}
	}
}
