package stencil.data;

import java.util.*;

/**Table is a collection of tuples.**/
public interface Table extends Iterable<Tuple> {
  public Tuple find(Object id);
  public Schema schema();
  public int size(); 
  public Collection<Object> ids();

  public static interface Updateable extends Table {
    public void update(Tuple item);
    public void remove(Object id);
  }


  /**Not at all thread safe...**/
  public static final class SimpleIterator implements Iterator<Tuple> {
    final Iterator<Object> idIterator;
    final Table table;
    public SimpleIterator(Table table) {
      this.table=table;
      idIterator = table.ids().iterator();
    }

    public boolean hasNext() {return idIterator.hasNext();}
    public Tuple next() {return table.find(idIterator.next());}
    public void remove() {throw new UnsupportedOperationException();}
  }

  public static final class SimpleTable implements Table.Updateable {
    private final ArrayList<Tuple> store = new ArrayList<>();
    private final Map<Object, Integer> index = new HashMap<>();
    private final Schema schema;

    public SimpleTable(Schema schema) {this.schema = schema;}
    
    public void update(Tuple t) {
      Object id = t.get("id");
      if (store.contains(id)) {
        int idx = index.get(id);
        store.set(idx, t);
      } else {
        store.add(t);
        index.put(id, store.size()-1);
      }
    }

    public void remove(Object id) {
      int idx = index.get(id);
      index.remove(id);
      store.remove(idx);
    }

    public Tuple find(Object id) {return store.get(index.get(id));}
    public Schema schema() {return schema;}
    public int size() {return store.size();}
    public Collection<Object> ids(){return index.keySet();}
    public Iterator<Tuple> iterator() {return new SimpleIterator(this);}
  }


}
