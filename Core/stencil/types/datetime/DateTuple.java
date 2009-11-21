package stencil.types.datetime;

import java.util.Arrays;
import java.util.List;
import java.util.Calendar;

import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;

public final class DateTuple implements Tuple {
	private static final String HOUR_FIELD = "hour";
	private static final String MINUTE_FIELD = "minute";
	private static final String SECOND_FIELD = "second";
	private static final String DAY_FIELD = "day";
	private static final String MONTH_FIELD = "month";
	private static final String YEAR_FIELD = "year";
	
	private static final List<String> PROTOTYPE = Arrays.asList(HOUR_FIELD, MINUTE_FIELD, SECOND_FIELD, DAY_FIELD, MONTH_FIELD, YEAR_FIELD);
	
	public static final int HOUR = PROTOTYPE.indexOf(HOUR_FIELD);
	public static final int MINUTE = PROTOTYPE.indexOf(MINUTE_FIELD);
	public static final int SECOND = PROTOTYPE.indexOf(SECOND_FIELD);
	public static final int DAY = PROTOTYPE.indexOf(DAY_FIELD);
	public static final int MONTH = PROTOTYPE.indexOf(MONTH_FIELD);
	public static final int YEAR = PROTOTYPE.indexOf(YEAR_FIELD);
	
	
	private Calendar cal;
	
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
	public int size() {return PROTOTYPE.size();}

	public List<String> getPrototype() {return PROTOTYPE;}

	/**There is no default date/time, so all fields have no default value.*/
	public boolean isDefault(String name, Object value) {return false;}

}
