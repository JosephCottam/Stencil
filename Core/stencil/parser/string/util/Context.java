package stencil.parser.string.util;

public final class Context {
     private final String target;
     private int maxArgCount;
          
     public Context(String target) {this.target = target;}
     
     public int maxArgCount() {return maxArgCount;}
     public void update(int argCount) {
    	 maxArgCount = (int) Math.max(argCount, maxArgCount);
     }
     public String target() {return target;}
}