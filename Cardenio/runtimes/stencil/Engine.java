package stencil;

public interface Engine {

  /**Grabs a stable view of render-related tables**/
  public RenderableView renderCapture();

  /**Indicates that captured tables may be released**/
  public void release();

  /**Process elements from the input streams.  
   * Engine implementation determines how many that is
   **/
  public void process(QueueManager q);
}
