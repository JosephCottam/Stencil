package stencil.types.datetime;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import stencil.types.SigilType;
import stencil.types.TypeCreationException;
import stencil.util.ConversionException;

import java.util.Calendar;

public class Date implements SigilType<Calendar, DateTuple> {

	public Object convert(Object value, Class target) {
		try {
			if (target.equals(DateTuple.class)) {
				return create(Arrays.asList(value));
			}
			
			if (target.equals(Calendar.class)) {
				if (value instanceof DateTuple) {
					return toExternal((DateTuple) value);
				}
				if (value instanceof Integer) {
					return create(Arrays.asList(value)).cal;
				}
			}
			
			if (target.equals(java.util.Date.class)) {
				if (value instanceof DateTuple) {
					return ((DateTuple) value).cal.getTime();
				}
				if (Number.class.isAssignableFrom(Number.class)) {
					return new java.util.Date(((Number) value).longValue());
				}
			}
				
			
			if (value instanceof DateTuple) {
				DateTuple v = (DateTuple) value;
				if (Number.class.isAssignableFrom(target)) { //double, float, long int
					v.cal.getTimeInMillis();
				}
				
				if (target.equals(String.class)) {return v.toString();}
			}
		} catch (Exception e) {throw new ConversionException(value, target, e);}

		throw new ConversionException(value, target);
	}

	/**
	 * One argument:  
	 * 	Number is treated as millis (long)
	 * 	String -- Tries to make into a number and treat as long
	 * 			  Tries to parse with default date format
	 * 
	 * Two argument: Format, Value
	 *   	Parsed with java.util.SimpleDateFormat
	 */
	public DateTuple create(List args) throws TypeCreationException {
		if (args.size() == 1) {
			Object value = args.get(0);
			if (value instanceof Number) {
				return new DateTuple(((Number)value).longValue());
			} else {
				String s = value.toString();
				try {
					long l = (long) Double.parseDouble(s);
					return new DateTuple(l);
				} catch (Exception e) {/*ignored*/}
				
				SimpleDateFormat f = new SimpleDateFormat();
				try {
					return new DateTuple(f.parse(value.toString()));
				} catch (Exception e) {throw new TypeCreationException(args, e);}
			}
		} else if (args.size() ==2) {
			try {
				String format = args.get(0).toString();
				String value = args.get(1).toString();
				SimpleDateFormat f= new SimpleDateFormat(format);
				return new DateTuple(f.parse(value));
			} catch (Exception e) {throw new TypeCreationException(args, e);}
		} 
		throw new TypeCreationException(args);
	}

	public java.util.Calendar toExternal(DateTuple source) {
		return (Calendar) source.cal.clone();
	}

	public String toString(Calendar source) {
		return toTuple(source).toString();
	}

	public DateTuple toTuple(Calendar source) {return new DateTuple(source);}

}
