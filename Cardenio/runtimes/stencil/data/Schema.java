package stencil.data;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

public class Schema {
  public static class Field {
     public final String name;
     public final Class<?> type;
     public final Object defaultValue;

     public Field(String name, Class<?> type) {this(name, type, null);}
     public Field(String name, Class<?> type, Object defaultValue) {
       assert name != null;
       assert type != null;
       assert defaultValue == null || type.isInstance(defaultValue);

       this.name = name;
       this.type = type;
       this.defaultValue = defaultValue;
     }
  }

  private final Field[] fields;

  public Schema() {this.fields = new Field[0];}
  public Schema(List<Field> fields) {this.fields = fields.toArray(new Field[fields.size()]);}
  public Schema(Field[] fields) {this.fields = Arrays.copyOf(fields, fields.length);}

  public Schema extend(Field ... newFields) {
    Field[] mergedFields = Arrays.copyOf(fields, fields.length+newFields.length);
    System.arraycopy(newFields, 0, mergedFields, fields.length, newFields.length);
    return new Schema(mergedFields);
  }

  public int size() {return fields.length;}
  public List<Field> fields() {return Collections.unmodifiableList(Arrays.asList(fields));}
  public Field get(int idx) {return fields[idx];}
  public int find(String name) {
    for (int i=0; i<fields.length;i++) {
      if (fields[i].name.equals(name)) {return i;}
    } 
    return -1;
  }
}
