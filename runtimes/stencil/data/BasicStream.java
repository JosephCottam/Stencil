package stencil.data;

public interface BasicStream<T> {
  public boolean done();
  public T next();
}
