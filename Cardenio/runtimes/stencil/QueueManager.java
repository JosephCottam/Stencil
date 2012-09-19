package stencil;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import stencil.data.TupleStream;
import stencil.data.Tuple;

/**Keeps the queues of unprocessed tuples from the streams**/
public class QueueManager {
  private final Map<String, TupleStream<?>> streams;
  private final Map<String, Queue> queues;
  
  public QueueManager(TupleStream ... streams) {
     Map<String, TupleStream<?>> ss = new HashMap<String, TupleStream<?>>();
     Map<String, Queue> qs = new HashMap<String, Queue>();

     for (TupleStream<?> s: streams) {
       ss.put(s.name(), s);
       qs.put(s.name(), new Queue());
     }
     this.streams = Collections.unmodifiableMap(ss);
     this.queues =  Collections.unmodifiableMap(qs);     
  }

  /**Have all tuples been processed?*/
  public boolean done() {
    for (Queue q: queues.values()) {
       if (q.size() >0) {return false;}
    }
    for (TupleStream<?> s: streams.values()) {
      if (s.done() == false) {return false;}
    }
    return true;
  }

  /**Remove the indicate tuple from the indicated queue, return it.
   * This may cause data to load.**/
  public Tuple pop(String stream, int i) {
    loadData(stream, i);
    return queues.get(stream).remove(i);
  }

  /**Look at a tuple in a stream, does not remove it.
   * This may cause data to load.
   * Returns null if i is larger than the amount of data that can be loaded.
   **/
  public Tuple peek(String stream, int i) {
    loadData(stream, i);
    return queues.get(stream).get(i);
  }

  /**Ensure there is data in the queue.  
   * TODO: Add a timeout
   */
  private int loadData(String stream, int i) {
    Queue q = queues.get(stream);
    TupleStream<?> s = streams.get(stream);

    while (q.size() <= i && !s.done()) {
      Tuple t = s.next();
      if (t==null) {break;}
      q.add(t);
    }

    return q.size();
  }

  /**For use in stencil-defined streams**/
  public void add(String queue, Tuple t) {queues.get(queue).add(t);}


  /**Basic queue abstraction + indexed lookup.
   * Returns null when "get" is called on an illegal value.**/
  private class Queue {
    private final List<Tuple> values = new ArrayList<Tuple>();
    
    public int size() {return values.size();}
    public Tuple remove(int i) {
       if (i >= values.size() || i<0) {return null;}
       else {return values.remove(i);}
    }

    public Tuple get(int i) {
      if (i >= values.size() || i<0) {return null;}
      else {return values.get(i);}
    }
    
    public void add(Tuple t) {values.add(t);}
  }
}
