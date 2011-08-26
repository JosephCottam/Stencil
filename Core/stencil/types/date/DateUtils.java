package stencil.types.date;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Module;
import stencil.module.util.ann.Operator;
import stencil.types.Converter;
import stencil.util.collections.ArrayUtil;

@Description("Date support, based on the date tuple")
@Module(name="Dates")
public class DateUtils extends BasicModule {
	private static final String[] months = new String[12];
	static {
		months[0] = "January";
		months[1] = "February";
		months[2] = "March";
		months[3] = "April";
		months[4] = "May";
		months[5] = "Jun";
		months[6] = "July";
		months[7] = "August";
		months[8] = "September";
		months[9] = "October";
		months[10] = "November";
		months[11] = "December";
	}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(int monthNum)", alias={"map","query"})
	public static int monthNum(String month) {
		month = nice(month);
		int idx =ArrayUtil.indexOf(month, months); 
		if (idx <0) {
			idx = ArrayUtil.indexOf(fullMonth(month), months);
		}
		if (idx <0) {throw new RuntimeException("Input does not correspond to a month: " + month);}
		return  idx + 1;
	}

	@Operator()
	@Facet(memUse="FUNCTION", prototype="(String month)", alias={"map","query"})
	public static String num2Month(int month) {return months[month-1];}


	@Operator()
	@Facet(memUse="FUNCTION", prototype="(String month)", alias={"map","query"})
	public static String fullMonth(String abr) {
		abr = nice(abr);
		for (String month: months) {
			if (month.startsWith(abr)) {return month;}
		}
		throw new RuntimeException("Input does not correspond to an abbreviation:" + abr);
	}

	private static final String nice(String s) {
		s = s.trim().toLowerCase();
		if (s.length() >0) {s = s.substring(0,1).toUpperCase() + s.substring(1);}
		return s;
	}



	@Description("Parse text into a date.  Uses the SimpleDateFormat parser.")
	@Operator(spec="[f:\"dd-MMM-yy\"]")
	public static class Parse extends AbstractOperator {
		final SimpleDateFormat formatter;
		public Parse(OperatorData od, Specializer spec) {
			super(od);
			String f = Converter.toString(spec.get("f"));
			formatter = new SimpleDateFormat(f);
		}
	
		@Description("Parse the passed string, usinging the passed format string.")
		@Facet(memUse="FUNCTION", prototype=DateTuple.PROTOTYPE)
		public DateTuple parse2(String s, String f) throws ParseException {
			SimpleDateFormat formatter = new SimpleDateFormat(f);
			Date d = formatter.parse(s);
			return new DateTuple(d);
		}
		
		@Description("Parse the passed string, using the format string from the specializer.")
		@Facet(memUse="FUNCTION", prototype=DateTuple.PROTOTYPE, alias={"map","query","parse"})
		public DateTuple parse(String s) throws ParseException {
			Date d = formatter.parse(s);
			return new DateTuple(d);
		}

		@Description("Format the passed date, using the format string from the specializer.")
		@Facet(memUse="FUNCTION", prototype="(String date)", alias={"format"})
		public String format(Date d) {
			return formatter.format(d);
		}
		
		@Description("Format the passed date, using the passed format string.")
		@Facet(memUse="FUNCTION", prototype="(String date)")
		public String format2(Date d, String format) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(d);
		}

	}
	
	@Operator()
	@Facet(prototype="(double diff)", alias={"map","query"})
	public static long dateDiff(String field, Date early, Date late) {
		long millisDiff = late.getTime()-early.getTime();
		field = field.toUpperCase();
		if (field.equals("MILLI")) {
			return millisDiff;
		} else if (field.equals("SEC")) {
			return millisDiff/1000;
		} else if (field.equals("MIN")) {
			return millisDiff / (1000*60);
		} else if (field.equals("HOUR")) {
			return millisDiff / (1000*60*60);
		} else if (field.equals("DAY")) {
			return millisDiff / (1000*60*60*24);
		} else if (field.equals("WEEK")) {
			return millisDiff / (1000*60*60*24*7);
		} 
		
		Calendar earlier = Calendar.getInstance();
		earlier.setTime(early);
		Calendar later = Calendar.getInstance();
		later.setTime(late);

		if (field.equals("MONTH")) {
			int years = 12*(later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
			int monthShift = later.get(Calendar.MONTH) - earlier.get(Calendar.MONTH); 
			return years ==0 ? monthShift : years-monthShift;
		} else if (field.equals("YEAR")) {
			return (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
		}
		
		throw new IllegalArgumentException("Unknown diff field: " + field);
		
	}
	
	
	@Description("What is the calendar field value from the given date?  Valid fields are those from java.util.Calendar.get(int).")
	@Operator()
	@Facet(prototype="(int calField)")
	public static int calendar(Date t, String fieldName)  {
		Calendar cal = java.util.Calendar.getInstance();
		Field f;
		int field;
		try {
			f = cal.getClass().getField(fieldName);
			field = (Integer) f.get(cal);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error accessing calendar field `" + fieldName + "'", e);
		}
		cal.setTime(t);
		return cal.get(field);
	}
}
