package stencil.data;

import java.lang.invoke.MethodHandle;
import java.io.*;


public class FileTuples implements BasicStream<Tuple> {
  private final MethodHandle makeTuple;
  private final boolean strict;
  private final BufferedReader reader;
  private boolean closed = false;
  private int lineNum = 0;
  private final String filename;
  
  public FileTuples(String filename, int skip, boolean strict, MethodHandle makeTuple){
    this.filename = filename;
    this.makeTuple = makeTuple;
    this.strict = strict;
    lineNum = skip;

    try {reader = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {throw new IllegalArgumentException("Could not create input stream for " + filename, e);}

    while (skip>0) {
      try {reader.readLine();}
      catch (Exception e) {throw new IllegalArgumentException("Could not advance file stream sufficiently.  Needed " + skip + " more lines.", e);}
      skip--;
    }
  }

  private void stop() {
    closed = true;
    try{reader.close();}
    catch (Exception e) {throw new RuntimeException("Error closing " + filename, e);}
  }

  public boolean done() {return closed;}
  public Tuple next() {
    if (closed) {return null;}

    try {
      String line = reader.readLine();

      if (line == null) {stop(); return null;}
      lineNum++;

      return (Tuple) makeTuple.invokeExact(line);
    } catch (Throwable e) {
      if (!strict && !(e instanceof IOException)) {return next();}
      else { 
        throw new TupleParseException(String.format("Error parsing line %$s from %$s.", lineNum, filename), e);}
    }
  }

  public class TupleParseException extends RuntimeException {public TupleParseException(String msg, Throwable e) {super(msg, e);}}
}
