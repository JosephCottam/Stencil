package stencil.types.datetime;

import java.util.List;
import java.util.Calendar;
import stencil.streams.InvalidNameException;
import stencil.streams.Tuple;
import stencil.types.Converter;
import stencil.util.enums.EnumUtils;

public final class DateTuple implements Tuple {
	private static enum FIELD {
		HOUR, MINUTE, SECOND, MONTH, DAY, YEAR
	};
	
	private Calendar cal;
	
	public DateTuple(Calendar cal) {this.cal = cal;}
	
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
		} catch (Exception e) {}
		throw new InvalidNameException(name, EnumUtils.allNames(FIELD.class));
	}

	public Object get(String name, Class<?> type)
			throws IllegalArgumentException, InvalidNameException {
		return Converter.convert(get(name), type);
	}

	public List<String> getFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasField(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDefault(String name, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

}
