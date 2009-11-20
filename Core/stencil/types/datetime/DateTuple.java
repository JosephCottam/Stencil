package stencil.types.datetime;

import java.util.List;
import java.util.Calendar;
import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.util.enums.EnumUtils;

public final class DateTuple implements Tuple {
	private static enum FIELD {HOUR, MINUTE, SECOND, MONTH, DAY, YEAR};
	private static final List<String> FIELDS = EnumUtils.allNames(FIELD.class);
	
	private Calendar cal;
	
	public DateTuple(Calendar cal) {this.cal = cal;}
	public DateTuple(java.util.Date date) {
		cal = Calendar.getInstance();
		cal.setTime(date);
	}
		
	
	public Object get(String name) throws InvalidNameException {
		try {
			FIELD f = FIELD.valueOf(name);
			switch (f) {
				case HOUR: return cal.get(Calendar.HOUR);
				case MINUTE: return cal.get(Calendar.MINUTE);
				case SECOND: return cal.get(Calendar.SECOND);
				case MONTH: return cal.get(Calendar.MONTH);
				case DAY: return cal.get(Calendar.DATE);
				case YEAR:return cal.get(Calendar.YEAR);
			}			
		} catch (Exception e) {/*Exception thrown in next statement, if needed.*/}
		throw new InvalidNameException(name, EnumUtils.allNames(FIELD.class));
	}

	public List<String> getPrototype() {return FIELDS;}

	public boolean hasField(String name) {return FIELDS.contains(name);}

	/**There is no default date/time, so all fields have no default value.*/
	public boolean isDefault(String name, Object value) {return false;}

}
