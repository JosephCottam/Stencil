package stencil.parser.tree;

import org.antlr.runtime.Token;
import stencil.parser.string.StencilParser;

/**Class to capture the split sub-concept of specialization.*/
public class Split extends StencilTree {
	public Split(Token source) {super(source);}

	public boolean isOrdered() {return getChild(0) instanceof Order;}
	public boolean isPre() {return getChild(1).getType() == StencilParser.PRE;}
	public boolean isPost() {return getChild(1).getType() == StencilParser.POST;}
	
	public boolean hasSplitField() {return getChild(2).getText() != null;}
	public String splitField() {return getChild(2).getText();}

	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof Split)) {return false;}
		
		Split alter = (Split) other;
		
		return 
			this.hasSplitField() == alter.hasSplitField() &&
			this.isOrdered() == alter.isOrdered() &&
			this.isPre() == alter.isPre() &&
			(!this.hasSplitField() || this.splitField().equals(alter.splitField()));
	}
}