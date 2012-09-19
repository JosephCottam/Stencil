package stencil.data;

import java.lang.invoke.MethodHandle;
import java.util.*;

public class GeneratorTuples<T> implements BasicStream<T> {
  private final MethodHandle method;
  private final Queue<T> queue = new LinkedList();

  /**Expects a zero-argument method handle that produces an array.
   * Each element of the array will be treated as a tuple.**/
  public GeneratorTuples(MethodHandle method) { this.method = method;} 

  public boolean done() {
    if(queue == null) {init();}
    return queue.size() == 0;
  } 

  public T next() {
    if (queue == null) {init();}
    return queue.poll();
  }

  private void init() {
    try {
      Collection<T> values = (Collection<T>) method.invokeExact();
      queue.addAll(values);
    } catch (Throwable t) {
      throw new RuntimeException("Error generating tuples.", t);
    }
  }
}
