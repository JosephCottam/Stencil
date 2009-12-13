package stencil.types.datetime;

import java.util.Arrays;
import java.util.Calendar;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;

public final class DateTuple implements Tuple {
	private static final String HOUR_FIELD = "hour";
	private static final String MINUTE_FIELD = "minute";
	private static final String SECOND_FIELD = "second";
	private static final String DAY_FIELD = "day";
	private static final String MONTH_FIELD = "month";
	private static final String YEAR_FIELD = "year";
	
	private static final String[] FIELDS = new String[]{HOUR_FIELD, MINUTE_FIELD, SECOND_FIELD, DAY_FIELD, MONTH_FIELD, YEAR_FIELD};
	private static final Class[] TYPES   = new Class[]{Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
	
	private static final TuplePrototype PROTOTYPE = new SimplePrototype(FIELDS, TYPES);
	
	public static final int HOUR   = Arrays.asList(PROTOTYPE).indexOf(HOUR_FIELD);
	public static final int SECOND = Arrays.asList(PROTOTYPE).indexOf(SECOND_FIELD);
	public static final int MINUTE = Arrays.asList(PROTOTYPE).indexOf(MINUTE_FIELD);
	public static final int DAY    = Arrays.asList(PROTOTYPE).indexOf(DAY_FIELD);
	public static final int MONTH  = Arrays.asList(PROTOTYPE).indexOf(MONTH_FIELD);
	public static final int YEAR   = Arrays.asList(PROTOTYPE).indexOf(YEAR_FIELD);
	
	
	final Calendar cal;
	
	public DateTuple(long millis) {this(new java.util.Date(millis));}
	public DateTuple(Calendar cal) {this.cal = cal;}
	public DateTuple(java.util.Date date) {
		cal = Calendar.getInstance();
		cal.setTime(date);
	}
		
	
	public Object get(String name) {return Tuples.namedDereference(name, this);}
	public Object get(int idx) {
		if (idx == HOUR)   {return cal.get(Calendar.HOUR);}
		if (idx == MINUTE) {return cal.get(Calendar.MINUTE);}
		if (idx == SECOND) {return cal.get(Calendar.SECOND);}
		if (idx == MONTH)  {return cal.get(Calendar.MONTH);}
		if (idx == DAY)	   {return cal.get(Calendar.DATE);}
		if (idx == YEAR)   {return cal.get(Calendar.YEAR);}
		throw new TupleBoundsException(idx, size());
	}
	public int size() {return FIELDS.length;}

	public TuplePrototype getPrototype() {return PROTOTYPE;}

	/**There is no default date/time, so all fields have no default value.*/
	public boolean isDefault(String name, Object value) {return false;}
	
	public int hashCode() {return cal.hashCode();}
	
	public boolean equals(Object other) {
		return other instanceof DateTuple && ((DateTuple) other).cal.equals(cal);
	}

}
