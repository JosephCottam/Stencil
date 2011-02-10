package stencil.modules.stencilUtil;

import stencil.parser.string.ValidationException;

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
	/**Default key under which a split descriptor should be found in a specializer**/
	public static final String SPLIT_KEY = "split";
	public static final String ORDERED_KEY = "ordered";
	
	private final boolean ordered;	//Is it an ordered split (incoming values are guaranteed to be in order, default is false)?
	private final int fields;		//How many fields are involved in the split (default is 1)
	
	public Split(int keysize, boolean ordered) {
		fields = keysize;
		this.ordered = ordered;
		
		if (fields <0) {throw new ValidationException("Must supply positive fields count.");}
		//TODO: Verify that fewer fields than arguments 
	}

	public boolean isOrdered() {return ordered;}
	
	/**No split action required.*/
	public boolean isVoid() {return fields ==0;}
	
	public int getFields() {return fields;}

	
	
	public boolean equals(Object other) {
		if (this == other) {return true;}
		if (!(other instanceof Split)) {return false;}
		
		Split alter = (Split) other;
		
		return this.ordered == alter.ordered
			&& this.fields == alter.fields;
	}

	public int hashCode() {
		int o = isOrdered() ? 0x99999999 : 0x66666666;    //1001 vs. 0110
		return fields * o;
	}
}