package stencil.data;

import java.util.List;
import java.util.ArrayList;

public class InternalTuples<T> implements BasicStream<T> {
  private final List<T> values = new ArrayList<T>();
    
  public boolean done() {return false;}
  public void add(T v) {values.add(v);}
  public T next() {
    if (values.size() >0) {return values.remove(0);}
    else {return null;}
  }
}
