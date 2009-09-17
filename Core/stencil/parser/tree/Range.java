package stencil.parser.tree;

import org.antlr.runtime.Token;

import static stencil.parser.ParserConstants.*;

/**Class to capture the range sub-concept of specialization.*/
public class Range extends StencilTree {
	public Range(Token source) {super(source);}

	protected final int getStartValue() {return ((StencilNumber) getChild(0)).getNumber().intValue();}
	protected final int getEndValue() {return ((StencilNumber) getChild(1)).getNumber().intValue();}
	
	public int getStart() {return Math.abs(getStartValue());}
	public int getEnd() {return Math.abs(getEndValue());}

	public boolean isFullRange() {
		return getEndValue()   == RANGE_END_INT
			&& getStartValue() == RANGE_START_INT;
	}
	
	/**A simple range contains just the current values (e.g. n..n).*/
	public boolean isSimple() {return startsWithStreamEnd() && endsWithStream();}

	/**Does the range end with end of stream?*/
	public boolean endsWithStream() {return getEndValue() == RANGE_END_INT;}
	
	/**Does the range start with the end of the stream?*/
	public boolean startsWithStreamEnd() {return getStartValue() == RANGE_END_INT;}
	
	/**Is the start value relative to the end-of-stream?*/
	public boolean relativeStart() {return getStartValue() <= RANGE_END_INT;}
	
	/**Is the end value relative to the end-of-stream?*/
	public boolean relativeEnd() {return getEndValue() <= RANGE_END_INT;}
	
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof Range)) {return false;}
		Range alter = (Range) other;
		return this.getStartValue() == alter.getStartValue() 
			&& this.getEndValue() == alter.getEndValue(); 
	}
	
	public String rangeString() {return getStart() + this.getText() + getEnd();}

	public int hashCode() {
		int a = getStartValue();
		int b = getEndValue();
		int c = a >> 16 | a<<16;
		int d = b >>16 | b<<16;
		return a * b * c *d;
	}
}