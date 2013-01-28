package stencil;

import java.awt.Rectangle;
import java.awt.Color;
import stencil.data.Tuple;

public interface View {
  /**The screen-dimensions of the view**/
  public Rectangle viewport();

  /**The bounds of what can be seen on the canvas**/
  public Rectangle size();

  /**Set the background color to render this view with**/
  public View background(Color background);

  /**What are the maximum bounds of the data?**/
  public Tuple canvas();
}
