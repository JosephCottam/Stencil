package stencil.modules.stencilUtil.range;

import static stencil.parser.ParserConstants.*;
import stencil.parser.string.util.ValidationException;

/**Class to capture the range sub-concept of specialization.
 * Gramar is:
 *    ALL | LAST | x .. y
 *    x and y can be any integer or the FINAL_VALUE token
 *    White space arround the '..' is ingored.
 * 
 * */
public class RangeDescriptor {
	private static final class RangeValidationException extends ValidationException {
		public RangeValidationException(String source) {
			super("Invalid range:" + source);
		}    
	}

	final int start;
	final int end;
	
	public RangeDescriptor(Object source) {
		this(source.toString());
	}
	
	public RangeDescriptor(String source) {
		if (source.equals(ALL)) {
			start = RANGE_START_INT;
			end = RANGE_END_INT;
		} else if (source.equals(LAST)) {
			start = RANGE_END_INT;
			end = RANGE_END_INT;
		} else {
			String[] parts = source.split("\\s*\\.\\.\\s*");
			if (parts.length != 2) {throw new RangeValidationException(source);}
			try {				
				start = parts[0].equals(FINAL_VALUE) ? RANGE_END_INT : Integer.parseInt(parts[0]);
				end = parts[1].equals(FINAL_VALUE) ? RANGE_END_INT : Integer.parseInt(parts[1]);
			} catch (Exception e) {
				throw new RangeValidationException(source);
			}
		}
		validate(source);
	}
	
	private void validate(String source) {
		if ((end == RANGE_END_INT)  //If the end is range-end, any start value can be used
				|| (relativeStart() == relativeEnd() && start < end)
				|| (relativeEnd() && !relativeStart())) {return;}
		
		throw new RangeValidationException(source);
	}

	protected final int getStartValue() {return start;}
	protected final int getEndValue() {return end;}
	
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
		if (!(other instanceof RangeDescriptor)) {return false;}
		RangeDescriptor alter = (RangeDescriptor) other;
		return this.getStartValue() == alter.getStartValue() 
			&& this.getEndValue() == alter.getEndValue(); 
	}
	
	public String rangeString() {
		if (isFullRange()) {return ALL;}
		if (isSimple()) {return LAST;}

		String ev = (end==RANGE_END_INT ? RANGE_END : Integer.toString(end)); 
		return String.format("%1$d .. %2$s", start, ev);
	}
	
	public int hashCode() {
		int a = getStartValue();
		int b = getEndValue();
		int c = a >> 16 | a<<16;
		int d = b >>16 | b<<16;
		return a * b * c *d;
	}
}