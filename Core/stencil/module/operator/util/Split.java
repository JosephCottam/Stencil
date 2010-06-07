package stencil.module.operator.util;

import stencil.parser.string.ValidationException;
import stencil.parser.tree.Atom;

/**Class to capture the split sub-concept of specialization.
 * Grammar is: count,pre/post,ord
 *    Delimited by commas
 *    Whitespace is ignored
 *    Any prefix set may be supplied.
 *    
 *    Examples: 
 *        3,pre
 *        1,post, ord
 *        2,pre, unord   (anything otehr than 'ord' indicats unordered, but 'unord' is suggested)
 *        0,pre, ord	(with 0 in field count, will not do any splitting)
 * */
public class Split {
	private final boolean ordered;	//Is it an ordered split (incoming values are guaranteed to be in order, default is false)?
	private final boolean pre;		//Should split occur before range? (default is false)
	private final int fields;		//How many fields are involved in the split (default is 1)
	
	public Split(Atom source) {this(source.getText());}
	public Split(String source) {
		String[] parts = source.trim().split("\\s*,\\s*");
		
		fields = Integer.parseInt(parts[0]);
		pre = (parts.length > 1) && parts[1].equals("pre");
		ordered = (parts.length >2) && parts[2].equals("ord");
		
		if (fields <0) {throw new ValidationException("Must supply positive fields count.");}
	}

	public boolean isOrdered() {return ordered;}
	public boolean isPre() {return pre;}
	public boolean isPost() {return !pre;}
	
	/**No split action required.*/
	public boolean isVoid() {return fields ==0;}
	
	public int getFields() {return fields;}

	
	
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof Split)) {return false;}
		
		Split alter = (Split) other;
		
		return this.ordered == alter.ordered
			&& this.pre == alter.pre
			&& this.fields == alter.fields;
	}

	public int hashCode() {
		int p = isPre() ? 0x55555555 : 0xAAAAAAAA;		//1010 vs. 0101
		int o = isOrdered() ? 0x99999999 : 0x66666666;    //1001 vs. 0110
		return fields * p *o;
	}
}