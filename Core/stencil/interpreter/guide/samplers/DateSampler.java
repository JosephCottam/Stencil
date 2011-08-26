package stencil.interpreter.guide.samplers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.types.Converter;

//TODO: Implement a date seed operator that can treat them as continuous (save the parse)
/**Sample  between two dates.*/
public class DateSampler implements SampleOperator {
	/**Should this sample in units of days, weeks, months or years?**/
	public static final String UNIT_KEY = "unit";
	
	/**How many units at a time?  Must be a positive whole number.*/
	public static final String STRIDE_KEY = "stride";
	
	/**How should inputs be parsed and outputs returned?
	 * Outputs may be modified, based on the selected unit.
	 */
	public static final String PARSE_KEY = "parse";
	private static final String DEFAULT_PARSE = "dd-MMM-yy";

	
	private static enum Unit {DAY, WEEK, MONTH, YEAR, DECADE, CENTURY;}	
	
	public List<Tuple> sample(SampleSeed seed, Specializer spec) {
		if (!seed.isContinuous()) {throw new RuntimeException("Can only use continuous sample for dates.");}
		if (seed.size() <=1) {return new ArrayList();}//No seed -> No sample
		
		Unit unit = (Unit) Converter.convert(spec.get(UNIT_KEY, Unit.YEAR), Unit.class);
		int stride = (Integer) Converter.convert(spec.get(STRIDE_KEY, 1), Integer.class);
		String format = (String) Converter.convert(spec.get(PARSE_KEY, DEFAULT_PARSE), String.class);
		Parser parser = new Parser(format);

		List<Date> dates = new ArrayList();

		Date start = (Date) seed.get(0);
		Date end = (Date) seed.get(1);
		
		switch(unit) {
			case DAY: dates = days(start, end, stride); break;
			case WEEK: dates = weeks(start, end, stride); break;
			case MONTH: dates = months(start, end, stride); break;
			case YEAR: dates = years(start, end, stride); break;
			case DECADE: dates = decades(start, end, stride); break;
			case CENTURY: dates = centuries(start, end, stride); break;
		}
		
		List<Tuple> sample = new ArrayList();
		for (Date d: dates) {
			sample.add(Converter.toTuple(d));
		}
		return sample;		
	}
	
	public static List<Date> days(Date start, Date end, int stride) {
		return runOf(start, end, Calendar.DATE, stride);
	}

	public static List<Date> weeks(Date start, Date end, int stride) {
		start = find(start, Calendar.DATE, 1, WEEK_START);
		return runOf(start, end, Calendar.DATE, 7*stride);
	}
	
	public static List<Date> months(Date start, Date end, int stride) {
		return runOf(start, end, Calendar.MONTH, stride);		
	}
	
	public static List<Date> years(Date start, Date end, int stride) {
		return runOf(start, end, Calendar.YEAR, stride);				
	}
	
	public static List<Date> decades(Date start, Date end, int stride) {
		start = find(start, Calendar.DATE, 1, DECADE);
		return runOf(start, end, Calendar.YEAR, 10*stride);
	}
	
	public static List<Date> centuries(Date start, Date end, int stride) {
		start = find(start, Calendar.DATE, 1, CENTURY);
		return runOf(start, end, Calendar.YEAR, 100*stride);
	}
	
	private static Date find(Date start, int field, int amount, Predicate p) {
		Calendar c = Calendar.getInstance();
		c.setTime(start);
		while(!p.is(c)) {
			c.add(field, amount);
		}
		return c.getTime();
	}
	
	private static List<Date> runOf(Date start, Date end, int field, int amount) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(end);
		cal.add(Calendar.DATE, 1);		//HACK: Makes the  ranges work  out the way I think they should; +1 to the day, so you get the last day/month but doesn't mess up weeks or years because it only adds on more unit
		end = cal.getTime();
		
		ArrayList dates = new ArrayList();
		Date now = start;
		while (now.before(end)) {
			dates.add(now);
			cal.setTime(now);
			cal.add(field, amount);
			now = cal.getTime();
		}
		return dates;				
	}
	
	
	private static class Parser {
		private static final long DAY_MILLIS = 1000 * 60 * 60 * 24;
		
		private final SimpleDateFormat formatter;
		
		public Parser(String format) {
			if (format.toUpperCase().equals("EPOCHDAYS")) {
				formatter = null;
			} else {
				formatter = new SimpleDateFormat(format);
			}
		}
		
		public Object out(Date d) {
			if (formatter != null) {return formatter.format(d);}
			else {
				return d.getTime()/DAY_MILLIS ;
			}
		}
	}

	
	private static interface Predicate {public boolean is(Calendar c);}

	private static final Predicate DECADE = new Predicate() {
		public boolean is(Calendar c) {
			int year=c.get(Calendar.YEAR);
			return year%10==0;
		}
	};
	
	private static final Predicate CENTURY  = new Predicate() {
		public boolean is(Calendar c) {
			int year=c.get(Calendar.YEAR);
			return year%100==0;
		}		
	};
	
	private static final Predicate WEEK_START = new Predicate() {
		public boolean is(Calendar c) {
			return c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek();
		}
	};
}

 	