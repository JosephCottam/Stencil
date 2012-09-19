package stencil.test.examples.cannonical;

import java.awt.Color;
import java.awt.Rectangle;
import java.lang.invoke.*;

import stencil.data.*;
import stencil.operators.*;
import stencil.renderer.*;
import static stencil.data.Schema.Field;

public class SimpleLines extends stencil.StencilPanel {
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final String VALUES_STREAM_NAME = "values";
  private static final Schema VALUES_SCHEMA = new Schema().extend(new Field("v", Integer.class));
  private static final Schema PLOT_SCHEMA = new Schema().extend(new Field("id",Integer.class), 
                                               new Field("x1",Integer.class), new Field("x2", Integer.class),
                                               new Field("y1",Integer.class,0), new Field("y2",Integer.class,10), 
                                               new Field("fillColor",java.awt.Color.class,java.awt.Color.GRAY));

   private final MethodHandle __RANGE1_M;
   private final TupleStream values;
   private final stencil.QueueManager queues;
   private final Engine engine = new Engine();

   public SimpleLines() {
     try {
       MethodType mt = MethodType.methodType(int[].class, int.class, int.class);
       MethodHandle mh = LOOKUP.findStatic(Generators.class, "range", mt);
       __RANGE1_M = MethodHandles.insertArguments(mh, 0, 0, 10);
       values = new TupleStream("values", VALUES_SCHEMA, new GeneratorTuples(__RANGE1_M));
       queues = new stencil.QueueManager(values);
     } catch (Exception e) {throw new RuntimeException("Error intializing panel.", e);}
   }

   public void innerRun() {
     if (queues.done()) {stop(); return;}

     engine.process(queues);
     RenderableView view = engine.renderCapture();
     if (view == null) {return;}

     view.plotRenderer().render(view.plotTable());

     engine.release();
   }

   private static final class Engine implements stencil.Engine {
     private final Table.Updateable plotTable =new Table.SimpleTable(PLOT_SCHEMA);
     private final java.util.concurrent.Semaphore lock = new java.util.concurrent.Semaphore(1);
     public View view = new View();


     public RenderableView renderCapture() {
       return new RenderableView(view, plotTable);
     }

     public void release() {}

     public void process(stencil.QueueManager qs) {
       Tuple t = qs.pop(VALUES_STREAM_NAME,0);

       if (t==null) {return;}
       Object[] updates = new Object[6];
       updates[0] = t.get(0);

       Object temp1 = ((Integer) t.get(0) * 10);
       updates[1] = temp1;
       updates[2] = temp1;
       updates[3] = 0;
       updates[4] = 10;
       updates[5] = java.awt.Color.GRAY; 

       plotTable.update(Tuple.Util.from(PLOT_SCHEMA, updates));
     }
   }

   public static final class View implements stencil.View {
      private static final Renderer plotRenderer = new TextRenderer("simpleLines_test.tuples");
      final Color background;

      public View() {this.background = Color.WHITE;}
      public View(Color background) {this.background = background;}


      public Tuple canvas() {return null;}
      public Rectangle viewport() {return null;}
      public Rectangle size() {return null;}
      public View background(Color color) {return new View(color);}

      public Renderer plotRenderer() {return plotRenderer;}
   }

   public static final class RenderableView implements stencil.RenderableView {
      private final Table plotTable;
      private final View view;
      
      public RenderableView(View view, Table plotTable) {
        this.view = view;
        this.plotTable = plotTable;
      }

      public Color backGround() {return view.background;}
      public Table plotTable() {return plotTable;}
      public Renderer plotRenderer() {return view.plotRenderer();}
   }

}
