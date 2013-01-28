package stencil.renderer;

import stencil.data.Table;
import java.awt.image.BufferedImage;

public interface Renderer {
  public BufferedImage render(Table table);
}
