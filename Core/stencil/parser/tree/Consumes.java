package stencil.parser.tree;

import org.antlr.runtime.Token;
import stencil.streams.Tuple;

public class Consumes extends StencilTree {
	public Consumes(Token source) {super(source);}

	public String getStream() {return token.getText();}
	
	public List<Filter> getFilters() {return (List<Filter>) getChild(0);}
	public List<Rule> getRules() {return (List<Rule>) getChild(1);}
	public Layer getLayer() {return (Layer) getParent();}
	
	public boolean matches(Tuple tuple) {
		//Check the tuple source and stream name
		
		Object source= tuple.get(Tuple.SOURCE_KEY);
		if ((getStream() != null) &&
			((source == null) ||
			 (!source.equals(getStream())))) {return false;}

		//Check all of the filters
		int count=0;
		for (Filter filter:getFilters()) {
			count++;
			try {if (!filter.matches(tuple)) {return false;}}
			catch (Exception e) {throw new RuntimeException(String.format("Error applying filter chain %1$d.", count), e);}
		}
		return true;
	}
}
