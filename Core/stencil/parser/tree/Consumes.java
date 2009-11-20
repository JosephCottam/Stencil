package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;
import stencil.tuple.Tuple;

public class Consumes extends StencilTree {
	public Consumes(Token source) {super(source);}

	public String getStream() {return token.getText();}
	
	public Layer getLayer() {return (Layer) this.getAncestor(StencilParser.LAYER);}

	public List<Filter> getFilters() {return (List<Filter>) getChild(0);}
	public List<Rule> getLocalRules() {return (List<Rule>) getChild(1);}
	public List<Rule> getGlyphRules() {return (List<Rule>) getChild(2);}
	public List<Rule> getViewRules() {return (List<Rule>) getChild(3);}
	public List<Rule> getCanvasRules() {return (List<Rule>) getChild(4);}
		
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
