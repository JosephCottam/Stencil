package stencil.adapters.java2D.columnStore;


import java.awt.Rectangle;

import stencil.display.Glyph;
import stencil.display.SchemaFieldDef;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

public interface ColumnStore<T extends Tuple> extends Iterable<T>{
	public T find(Comparable name);
	public T get(int row);
	public int stateID();

	/**How many tuples are currently stored?*/
	public int size();
	
	public TuplePrototype<SchemaFieldDef> schema();
	public ColumnStore viewpoint();
	

	public static final class Util {
		/**Takes a tuple that may be stored in the next generation change,
		 * and presents it as-if it had already been stored.
		 * This includes converting the types of the fields.
		 */
		public static final PrototypedTuple fillToSchema(PrototypedTuple source, TuplePrototype<SchemaFieldDef> schema) {
			final Object[] values = new Object[schema.size()];
			TuplePrototype sourceProto = source.prototype();
			for (int i=0; i< values.length; i++) {
				SchemaFieldDef def = schema.get(i); 
				Object val = sourceProto.contains(def.name()) ? source.get(def.name()) : def.defaultValue();
				values[i] = Converter.convert(val, def.type());
			}
			
			return new PrototypedArrayTuple(schema, values);
		}
		
		public static final Glyph fillAndGlyph(PrototypedTuple source, TuplePrototype<SchemaFieldDef> schema, int idColumn, int visibleColumn) {
			return new ExternalWrapper(ColumnStore.Util.fillToSchema(source, schema), idColumn, visibleColumn);
		}
		
		/**Wrap a tuple as a glyph, suitable for returning from the layer.**/
		private static final class ExternalWrapper implements Glyph {
			private final PrototypedTuple source;
			private final int idIdx;
			private final int visibleIdx;
			
			public ExternalWrapper(PrototypedTuple source, int idIdx, int visibleIdx) {
				this.source = source;
				this.idIdx = idIdx;
				this.visibleIdx = visibleIdx;
			}
			
			public Rectangle getBoundsReference() {return null;}
			public String getID() {return (String) source.get(idIdx);}
			public boolean isVisible() {return (Boolean) source.get(visibleIdx);}

			public Object get(String name) throws InvalidNameException {return source.get(name);}
			public Object get(int idx) throws TupleBoundsException {return source.get(idx);}
			public TuplePrototype prototype() {return source.prototype();}
			public int size() {return source.size();}
			public String toString() {return Tuples.toString(this);}
		}

	}
}
