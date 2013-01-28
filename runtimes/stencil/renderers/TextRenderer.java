package stencil.renderer;

import java.awt.image.BufferedImage; 
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.io.PrintStream;

import stencil.data.Tuple;
import stencil.data.Table;

/**Render to a text file, return an fill-in image**/
public class TextRenderer implements Renderer {
  private static final BufferedImage IMAGE = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
  static {
    Graphics2D g = IMAGE.createGraphics();
    g.setPaint(Color.RED);
    g.fillRect(0,0,IMAGE.getWidth(), IMAGE.getHeight());
  }
          
  private final String filename;
  private final boolean useStdOut;

  public TextRenderer(String file) {
    this.filename = file;
    useStdOut = filename == null; 
  }

  
  public BufferedImage render(Table table) {
    try { 
      PrintStream w = useStdOut ? System.out : new PrintStream(filename);
      Tuple defaults = Tuple.Util.from(table.schema());

      for (Object id: table.ids()) {
        Tuple t = table.find(id);
        w.println(Tuple.Util.subtract(t, defaults));
      }

      if (!useStdOut) {w.close();}
    } catch (Exception e) {throw new RuntimeException("Error in rendering to text file.", e);}
    
    return IMAGE;
  }

}
