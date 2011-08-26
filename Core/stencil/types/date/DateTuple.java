package stencil.types.date;

import java.util.Calendar;
import java.util.Date;

import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;

public class DateTuple extends Date implements PrototypedTuple {
	
	private static String SELF = "self";
	private static String DAY = "day";
	private static String MONTH= "month";
	private static String YEAR = "year";
	private static String WEEKDAY = "weekday";
	private static String MILLIS = "millis";
	
	private static final TuplePrototype PROTO = new TuplePrototype(SELF, WEEKDAY, MONTH, DAY, YEAR, MILLIS);
	private static int _SELF = PROTO.indexOf(SELF);
	private static int _DAY = PROTO.indexOf(DAY);
	private static int _MONTH = PROTO.indexOf(MONTH);
	private static int _YEAR = PROTO.indexOf(YEAR);
	private static int _WEEKDAY = PROTO.indexOf(WEEKDAY);
	private static int _MILLIS = PROTO.indexOf(MILLIS);

	public static final String PROTOTYPE="(DateTuple self, int day, int month, int year, String weekday, long millis)";

	
	private final Calendar cal;
	
	public DateTuple(Date d) {
		super();
		this.setTime(d.getTime());
		cal = Calendar.getInstance();
		cal.setTime(this);
	}
	
	public DateTuple(Calendar c) {this.cal = c;}
	
	@Override
	public Object get(int idx) throws TupleBoundsException {
		if (idx == _SELF) {return this;}
		if (idx == _DAY) {return cal.get(Calendar.DATE);}
		if (idx == _MONTH) {return cal.get(Calendar.MONTH);}
		if (idx == _YEAR) {return cal.get(Calendar.YEAR);}
		if (idx == _WEEKDAY) {return cal.get(Calendar.DAY_OF_WEEK);}
		if (idx == _MILLIS) {return cal.getTime().getTime();}
		return null;
	}

	@Override
	public int size() {return PROTO.size();}

	@Override
	public TuplePrototype prototype() {return PROTO;}

	@Override
	public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}
}
