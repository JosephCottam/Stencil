package stencil.types.date;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import stencil.interpreter.tree.Specializer;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Module;
import stencil.module.util.ann.Operator;
import stencil.parser.string.util.Context;
import stencil.types.Converter;

@Description("Date support, based on the date tuple")
@Module(name="Dates")
public class DateUtils extends BasicModule {

	@Description("Parse text into a date.  Uses the SimpleDateFormat formatting strings.")
	@Operator(spec="[f:\"dd-MMM-yy\"]")
	public static class Parse extends AbstractOperator {
		private static final ThreadLocal<SimpleDateFormat> FORMAT = new ThreadLocal<SimpleDateFormat>() {
			protected SimpleDateFormat initialValue() {return new SimpleDateFormat();}
		};
		
		final SimpleDateFormat instanceFormatter;
		public Parse(OperatorData od, Specializer spec) throws SpecializationException {
			super(od);
			String f = Converter.toString(spec.get("f"));
			try {instanceFormatter = new SimpleDateFormat(f);}
			catch (Exception e) {throw new SpecializationException(od.module(), od.name(), spec, e);}
		}
	
		@Description("Parse the passed string, usinging the passed format string.")
		@Facet(memUse="FUNCTION", prototype=DateTuple.PROTOTYPE)
		public DateTuple parse2(String s, String f) throws ParseException {
			FORMAT.get().applyPattern(f);
			Date d = FORMAT.get().parse(s);
			return new DateTuple(d);
		}
		
		@Description("Parse the passed string, using the format string from the specializer.")
		@Facet(memUse="FUNCTION", prototype=DateTuple.PROTOTYPE, alias={"map","query","parse"})
		public DateTuple parse(String s) throws ParseException {
			Date d = instanceFormatter.parse(s);
			return new DateTuple(d);
		}

		@Description("Format the passed date, using the format string from the specializer.")
		@Facet(memUse="FUNCTION", prototype="(String date)", alias={"format"})
		public String format(Date d) {
			return instanceFormatter.format(d);
		}
		
		@Description("Format the passed date, using the passed format string.")
		@Facet(memUse="FUNCTION", prototype="(String date)")
		public String format2(Date d, String f) {
			FORMAT.get().applyPattern(f);
			return FORMAT.get().format(d);
		}
		
		@Description("Given a string (partial date), parse it according to the 2nd arg and reformat using the 3rd arg.")
		@Facet(memUse="FUNCTION", prototype="(String date)")
		public String reformat(String s, String f1, String f2) throws ParseException {
			FORMAT.get().applyPattern(f1);
			Date d = FORMAT.get().parse(s);
			FORMAT.get().applyPattern(f2);
			return FORMAT.get().format(d);
		}
		
	}
	
	@Description("Number of units between two dates (reports whole unit increments).\n  Valid units are MILLIS, SECS, MINS, HOURS, DAYS, WEEKS, MONTHS, YEARS.")
	@Operator()
	@Facet(prototype="(double diff)", alias={"map","query"})
	public static long dateDiff(String field, Date early, Date late) {
		long millisDiff = late.getTime()-early.getTime();
		field = field.toUpperCase();
		if (field.equals("MILLIS")) {
			return millisDiff;
		} else if (field.equals("SECS")) {
			return millisDiff/1000;
		} else if (field.equals("MINS")) {
			return millisDiff / (1000*60);
		} else if (field.equals("HOURS")) {
			return millisDiff / (1000*60*60);
		} else if (field.equals("DAYS")) {
			return millisDiff / (1000*60*60*24);
		} else if (field.equals("WEEKS")) {
			return millisDiff / (1000*60*60*24*7);
		} 
		
		Calendar earlier = Calendar.getInstance();
		earlier.setTime(early);
		Calendar later = Calendar.getInstance();
		later.setTime(late);

		if (field.equals("MONTHS")) {
			int years = 12*(later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
			int monthShift = later.get(Calendar.MONTH) - earlier.get(Calendar.MONTH); 
			return years ==0 ? monthShift : years-monthShift;
		} else if (field.equals("YEARS")) {
			return (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
		}
		
		throw new IllegalArgumentException("Unknown diff field: " + field);
		
	}
	
	@Description("Current date/time;  Use with `calendar' operator to get details.")
	@Operator
	@Facet(memUse="READER", prototype=DateTuple.PROTOTYPE)
	public static DateTuple now() {
		return new DateTuple(java.util.Calendar.getInstance());
	}
	
	@Description("Current date/time as milliseconds.")
	@Operator
	@Facet(memUse="READER", prototype="(long millis)")
	public static long currentMillis() {return System.currentTimeMillis();}
	
	@Description("What is the calendar field value from the given date?  Valid fields are those from java.util.Calendar.get(int).")
	@Operator()
	@Facet(prototype="(int calField)")
	public static int calendar(Date t, String fieldName)  {
		Calendar cal = java.util.Calendar.getInstance();
		Field f;
		int field;
		try {
			f = cal.getClass().getField(fieldName.toUpperCase());
			field = (Integer) f.get(cal);
		} catch (Exception e) {
			throw new IllegalArgumentException("Error accessing calendar field `" + fieldName + "'", e);
		}
		cal.setTime(t);
		return cal.get(field);
	}
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		return super.instance(name, context, specializer);
	}
}
