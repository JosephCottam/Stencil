package stencil.parser.tree;

import java.util.List;

import org.antlr.runtime.Token;

import stencil.parser.string.StencilParser;

public class StreamDef extends StencilTree {
	private List<Consumes> groups;

	public StreamDef(Token source) {super(source);}

	public String getName() {return token.getText();}
	public TuplePrototype getPrototype() {return (TuplePrototype) getChild(0);}
	public List<Rule> getDefaults() {
		return (List<Rule>) findChild(StencilParser.LIST, "Defaults");
	}
	
	public List<Consumes> getGroups() {
		if (groups == null) {groups = (List<Consumes>) findChild(StencilParser.LIST, "Consumes");}
		return groups;
	} 
}
