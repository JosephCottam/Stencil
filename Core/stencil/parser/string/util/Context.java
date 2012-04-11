package stencil.parser.string.util;

import java.util.*;
import stencil.parser.tree.StencilTree;

public final class Context {
     private final String target;
     private final int maxArgCount;
     private final List<StencilTree> callSites;
     private final List<StencilTree> args;
     
     public Context(String target) {
    	 this.target = target;
    	 maxArgCount = 0;
    	 callSites = new ArrayList();
    	 args = new ArrayList();
     }
     
     public Context(String target, int maxArgCount, List<StencilTree> callSites, List<StencilTree> args) {
    	 this.target = target;
    	 this.maxArgCount = maxArgCount;
    	 this.callSites = callSites;
    	 this.args = args;
     }
     
     public List<StencilTree> args() {return args;}
     public Context args(Iterable<StencilTree> args) {
    	 ArrayList l = new ArrayList(this.args);
    	 for (StencilTree t:args) {l.add(t);}
    	 return new Context(target, maxArgCount, callSites, l);
     }
     
     public int maxArgCount() {return maxArgCount;}
     public Context maxArgCount(int maxArgCount) {
    	 return new Context(target,Math.max(this.maxArgCount, maxArgCount),callSites,args);
     }
     
     public List<StencilTree> callSites() {return callSites;}
     public Context addCallSite(StencilTree callSite) {
    	 List<StencilTree> t = new ArrayList(callSites);
    	 t.add(callSite);
    	 return new Context(target, maxArgCount, t, args);
     }
     
     public String target() {return target;}
     
     public String toString() {
    	 return String.format("Context for %1$s", target); 
     }

}