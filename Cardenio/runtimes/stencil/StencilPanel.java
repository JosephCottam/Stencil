package stencil;

import javax.swing.JPanel;

/**The interface between Stencil and the rest screen.**/
public abstract class StencilPanel extends JPanel implements Runnable {
  protected boolean stop = false;

  public abstract void innerRun();

  public void stop() {stop = true;}
  public void run() {while (!stop && !Thread.interrupted()) {innerRun();}}
}
