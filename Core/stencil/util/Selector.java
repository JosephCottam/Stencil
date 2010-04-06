package stencil.util;

import java.util.ArrayList;
import java.util.List;

public interface Selector {
	/**Selector implementation that can be backed with any List<String>*/
	public static final class ListSelector implements Selector {		
		private final List<String> base;
	
		public ListSelector(List<String> base) {
			this.base = new ArrayList(base);
		}
		
		public int size() {return base.size();}
		public String getName(int i) {return base.get(i);}
		public void setName(int i, String name) {base.set(i, name);}
		
		public String getLayer() {return base.get(0);}
		public String getAttribute() {
			if (base.size() >0) {return base.get(1);}
			else {return null;}
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			
			for (Object o: base) {
				b.append(o.toString());
				b.append(".");
			}
			b.deleteCharAt(b.length()-1);
			return b.toString();
		}
	
		public int hashCode() {return toString().hashCode();}
		
		public boolean equals(Object o) {
			if (!(o instanceof Selector)) {return false;}
		    Selector os = (Selector) o;
		    
		    if (os.size() != base.size()) {return false;}
		    
		    for (int i=0; i< size(); i++) {
		    	if (!(os.getName(i).equals(getName(i)))) {return false;}
		    }
			return true;
		}
	}
	
	public int size();
	public String getName(int i);
	
	public String getLayer();
	public String getAttribute();

}
