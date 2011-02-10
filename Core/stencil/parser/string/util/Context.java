package stencil.parser.string.util;

import java.util.*;
import stencil.parser.tree.StencilTree;

public final class Context {
     private final String target;
     private int maxArgCount;
     
     /**How many times does this appear as part of a high-order container?**/
     private final Map<String, Set<StencilTree>> highOrderUses = new HashMap();
          
     public Context(String target) {this.target = target;}
     
     public int maxArgCount() {return maxArgCount;}
     public void update(int argCount) {
    	 maxArgCount = (int) Math.max(argCount, maxArgCount);
     }
     public String target() {return target;}
     
     /**When is this used in a higher-order function as an argument?
      * StencilTree must be an operator reference
      * Type is typically the type of the operator base associated with it
      * */
     public void addHighOrderUse(String type, StencilTree reference) {
    	 Set<StencilTree> uses = _highOrderUses(type);
    	 uses.add(reference);
    	 highOrderUses.put(type, uses);
     }
     
     private Set<StencilTree> _highOrderUses(String useType) {
    	 if (highOrderUses.containsKey(useType)) {
    		 return highOrderUses.get(useType);
    	 }
    	 
    	 return new HashSet();
     }

     public List<StencilTree> highOrderUses(String useType) {
    	 return new ArrayList(_highOrderUses(useType));
     }
     
     public Set<String> highOrderUses() {return Collections.unmodifiableSet(highOrderUses.keySet());}
     
     
     public String toString() {
    	 return String.format("Context for %1$s: %2$s args; High-order uses: %3$s", 
    			 target, 
    			 maxArgCount, 
    			 Arrays.deepToString(highOrderUses.keySet().toArray()));
     }

}