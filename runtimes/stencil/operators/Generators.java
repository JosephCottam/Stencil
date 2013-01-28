package stencil.operators;

public class Generators {
  public static int[] range(int end) {return range(0, end);}

  public static int[] range(int start, int end) {
    final int size = end-start;
    final int[] vals = new int[size];
    for (int i=0; i<size; i++) {vals[i] = start+i;}
    return vals;
  }

  public static double[] range(double start, double end, double step) {
    final int size = (int) ((end-start)/step);
    double[] vals = new double[size];
    for (int i=0; i<size; i++) {vals[i] = start+(i*step);}
    return vals;
  }

  public static abstract class xrange implements java.util.Iterator<Number> {
    public xrange from(int start, int end, int step) {return new irange(start, end, step);}
    public xrange from(double start, double end, double step) {return new drange(start, end, step);}

    public void remove() {throw new UnsupportedOperationException();}

    private static final class irange extends xrange {
      private final int end, step;
      private int at;
      public irange(int start, int end, int step) {
        this.at=start;
        this.end=end;
        this.step=step;
      }

      public boolean hasNext() {return at < end;}
      public Number next() {
        if (at >= end) {return null;}
        int rv = at;
        at += step;
        return rv;
      }
    }

    private static final class drange extends xrange {
      private final double end, step;
      private double at;
      public drange(double start, double end, double step) {
        this.at=start;
        this.end=end;
        this.step=step;
      }

      public boolean hasNext() {return at < end;}
      public Number next() {
        if (at >= end) {return null;}
        double rv = at;
        at += step;
        return rv;
      }
    }
  }
}
