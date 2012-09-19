package stencil.data;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.*;
import java.lang.reflect.Array;

public class GeneratorTuples<T> implements BasicStream<T> {
  private final MethodHandle method;
  private Queue<T> queue;

  /**Expects a zero-argument method handle that produces an array.
   * Each element of the array will be treated as a tuple.**/
  public GeneratorTuples(MethodHandle method) { this.method = method; }


  public boolean done() {
    if(queue == null) {init();}
    return queue.size() == 0;
  } 

  public T next() {
    if (queue == null) {init();}
    return queue.poll();
  }


  /**Delayed init because some operators MAY need to have the program up-and running. 
   * Eager intializiation could cause problems. This delay ensures that the 
   * intiailization happens once the main data processing loop has started.
   **/
  @SuppressWarnings("unchecked") 
  private void init() {
    final Object values;
    try {
      queue = new LinkedList<T>();
      values = method.invoke();
    } catch (Throwable t) {
      throw new RuntimeException("Error generating tuples.", t);
    }

    final MethodType mt =method.type();
    if (mt.returnType().isArray()) {
      final int length = Array.getLength(values);
      for (int i=0;i<length; i++) {
        queue.add((T) Array.get(values, i));
      }
    } else if (Collection.class.isAssignableFrom(mt.returnType())) {
      queue.addAll((Collection<T>) values);
    } else {
      throw new RuntimeException("Could not create generated stream from resutl of type " + mt.returnType());
    }
  }
}
