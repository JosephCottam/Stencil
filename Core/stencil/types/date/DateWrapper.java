package stencil.types.date;

import java.awt.Font;
import java.util.Calendar;
import java.util.Date;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.types.TypeWrapper;
import stencil.util.ConversionException;

public class DateWrapper implements TypeWrapper {
	private static Class[] ACCEPTS = {Date.class, Calendar.class};

	@Override
	public Class[] appliesTo() {return ACCEPTS;}

	@Override
	public Object convert(Object v, Class c) {
		if (c.equals(String.class) && v instanceof DateTuple) {return v.toString();}
		if (c.equals(String.class)) {return toTuple(v).toString();}
		
		if (c.isAssignableFrom(v.getClass())) {return v;}
		
		if ((v instanceof Font || v instanceof Date) 
				&& (c.equals(Tuple.class) || c.equals(PrototypedTuple.class) || c.equals(DateTuple.class))) {
			return toTuple(v);
		}
		
		if (v instanceof DateTuple && Date.class.equals(c)) {return new Date(((DateTuple) v).getTime());}
		if (v instanceof DateTuple && Calendar.class.equals(c)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) v);
		}

		if (v instanceof DateTuple 
				&& (Long.class.equals(c) || long.class.equals(c))) {
			return ((Date) v).getTime();
		}
		
		throw new ConversionException(v,c);
	}

	@Override
	public Tuple toTuple(Object o) {
		if (o instanceof Date) {return new DateTuple((Date) o);}
		if (o instanceof Calendar) {return new DateTuple(((Calendar) o).getTime());}
		if (o instanceof Long) {return new DateTuple(new Date((Long) o));}
		throw new RuntimeException("Error wrapping: " + o.toString());
	}

}
