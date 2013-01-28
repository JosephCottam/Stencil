package stencil.data;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public interface Tuple {
  public Object get(int idx);
  public Object get(String field);
  public Schema schema();

  public class IllegalEntryRef extends RuntimeException { 
    public IllegalEntryRef(int idx, int max) {super(String.format("Illegal index %$s in tuple of size %$s", idx, max));} 
    public IllegalEntryRef(String field, String[] fields) {super(String.format("Illegal field ref %$s, valid fields are %$s.", field, Arrays.deepToString(fields)));}
    public IllegalEntryRef(String field, List<Schema.Field> fields) {this(field, names(fields));}

    private static String[] names(List<Schema.Field> fields) {
      final String[] names = new String[fields.size()];
      for(int i=0; i<names.length;i++) {
        names[i] = fields.get(i).name;
      }
      return names;
    }



  } 

  public final class Util {
    public static Tuple from(Schema schema, List<?> values) {return new ArrayTuple(schema, values.toArray());}
    public static Tuple from(Schema schema, Object[] values) {return new ArrayTuple(schema, values);}
    public static Tuple from(Schema schema, Object value) {return new ArrayTuple(schema, new Object[]{value});}
    public static Tuple from(Schema schema) {
      Object[] values = new Object[schema.size()];
      for (int i=0; i< schema.size(); i++) {values[i] = schema.get(i).defaultValue;}
      return from(schema, values);
    }

    /**Remove all fields from t1 that are found in t2 and have the same value.**/
    public static Tuple subtract(Tuple t1, Tuple t2) {
      Schema s1 = t1.schema();
      Schema s2 = t2.schema();
      ArrayList<Object> values = new ArrayList<Object>();
      ArrayList<Schema.Field> schema = new ArrayList<Schema.Field>();


      for (int i=0; i< s2.size(); i++) {
        Schema.Field f = s2.get(i);
        int idx = s1.find(f.name);
        if (idx >=0) {
          Object v2 = t2.get(i);
          Object v1 = t1.get(idx);
          if (!(v1==v2 || (v2 != null && v2.equals(v1)))) {
            values.add(v1);
            schema.add(f);
          }
        }
      }
      return from(new Schema(schema), values.toArray());
    }

    public static String toString(Tuple t) {
      final Schema s = t.schema();
      
      final StringBuilder b = new StringBuilder();
      b.append("(");
      
      for (int i=0; i< s.size(); i++) {
        b.append(s.get(i).name);
        b.append(": ");
        b.append(t.get(i));
        b.append(", ");
      }

      b.delete(b.length()-2, b.length()-1);
      b.append(")");
      return b.toString();
    }
  }

  public class ArrayTuple implements Tuple {
    private final Object[] values;
    private final Schema schema;

    public ArrayTuple(Schema schema, Object[] values) {
      assert schema != null;
      assert values != null;
      assert schema.size() == values.length;

      this.values = values;
      this.schema = schema;
    }
  

    public Object get(String field) {
      int idx = schema.find(field);
      if (idx < 0) {throw new Tuple.IllegalEntryRef(field, schema.fields());}
      return get(idx);
    }

    public Object get(int idx) {
      try {return values[idx];}
      catch (ArrayIndexOutOfBoundsException e) {throw new IllegalEntryRef (idx, values.length);}
    }

    public Schema schema() {return schema;}
    public String toString() {return Util.toString(this);}
  }
}
