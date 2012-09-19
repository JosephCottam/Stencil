package stencil.data;

import java.util.Collection;

public class TupleStream<T> implements BasicStream<Tuple> {
  protected final String name;
  protected final BasicStream<T> base; 
  protected final Schema schema;

  public TupleStream(String name, Schema schema, BasicStream<T> base) {
    this.name = name;
    this.base = base;
    this.schema =schema;
  }

  public String name() {return name;}
  public boolean done() {return base.done();}
  public Tuple next() {
    Object values = base.next(); 
    return makeTuple(schema, base.next());
  }

  private static Tuple makeTuple(Schema schema, Object values)  {
    if (values == null) {return null;}
    if (schema.size() == 1) {return Tuple.Util.from(schema, values);}
    if (values.getClass().isArray()) {return Tuple.Util.from(schema, (Object[]) values);}
    if (values instanceof Collection) {return Tuple.Util.from(schema, ((Collection) values).toArray());}
    throw new IllegalArgumentException("Cannot build tuple from " + values);
  }

}
