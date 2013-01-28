package stencil.test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import stencil.*;
import stencil.test.examples.cannonical.*; //Example compiler outputs are placed here.
//import stencil.test.examples.generated.*;  //Actual compiler outputs are generated into this space

public class TestGeometry {

  @Test
  public void simpleLines() {
    StencilPanel panel = new SimpleLines();
    panel.run();
    assertEquals("result",true,true);
  }

}
