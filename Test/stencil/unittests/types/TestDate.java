package stencil.unittests.types;

import junit.framework.TestCase;
import stencil.types.date.*;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestDate extends TestCase {
	static final SimpleDateFormat FORMAT = new SimpleDateFormat("ddMMMyy");
	static final Date Jan2000, Jun2000, Dec2000, Dec2001, Jan2001, Jan2010;

	static {
		try {
			Jan2000 = FORMAT.parse("1Jan2000");
			Jun2000 = FORMAT.parse("1Jun2000");
			Dec2000 = FORMAT.parse("15Dec2000");
			Jan2001 = FORMAT.parse("1Jan2001");
			Dec2001 = FORMAT.parse("15Dec2001");
			Jan2010 = FORMAT.parse("1Jan2010");		
		} catch (ParseException e) {
			throw new Error("Error initing dates");
		}
	}
	public void testDiff() throws Exception {
		assertEquals(0, DateUtils.dateDiff("MILLIS", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("SECS", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("MINS", Jan2000, Jan2000));
		assertEquals(0, DateUtils.dateDiff("HOURS", Jan2000, Jan2000));
		assertEquals(0, DateUtils.dateDiff("DAYS", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("WEEKS", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("MONTHS", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("YEARS", Jan2000, Jan2000));	
		
		assertEquals(366, DateUtils.dateDiff("DAYS", Jan2000, Jan2001));	//leap year
		assertEquals(365, DateUtils.dateDiff("DAYS", Dec2000, Dec2001));	//not leap year
		assertEquals(1, DateUtils.dateDiff("YEARS", Jan2000, Jan2001));
		assertEquals(11, DateUtils.dateDiff("MONTHS", Jan2000, Dec2000));
		assertEquals(12, DateUtils.dateDiff("MONTHS", Jan2000, Jan2001));
		assertEquals(1, DateUtils.dateDiff("YEARS", Jan2000, Jan2001));
		assertEquals(10, DateUtils.dateDiff("YEARS", Jan2000, Jan2010));
		assertEquals(1, DateUtils.dateDiff("YEARS", Dec2000, Jan2001));
	}
	
	public void testCurrentMillis() throws Exception {
		assertTrue("Millis diff exceeded tolerance", Math.abs(DateUtils.currentMillis() - System.currentTimeMillis()) < 10);
		assertTrue("Millis diff exceeded tolerance", Math.abs(DateUtils.currentMillis() - System.currentTimeMillis()) < 10);
		assertTrue("Millis diff exceeded tolerance", Math.abs(DateUtils.currentMillis() - System.currentTimeMillis()) < 10);
		assertTrue("Millis diff exceeded tolerance", Math.abs(DateUtils.currentMillis() - System.currentTimeMillis()) < 10);
		assertTrue("Millis diff exceeded tolerance", Math.abs(DateUtils.currentMillis() - System.currentTimeMillis()) < 10);
	}
	
	public void testNow() throws Exception {
		DateTuple t = DateUtils.now();
		Calendar cal = Calendar.getInstance(); 
		
		assertEquals(DateUtils.calendar(t, "Hour"), cal.get(Calendar.HOUR));
		assertEquals(DateUtils.calendar(t, "Hour_of_day"), cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(DateUtils.calendar(t, "Month"), cal.get(Calendar.MONTH));
		assertEquals(DateUtils.calendar(t, "Year"), cal.get(Calendar.YEAR));
		assertEquals(DateUtils.calendar(t, "Day_of_week"), cal.get(Calendar.DAY_OF_WEEK));
		
		boolean fail = false;
		try {
			DateUtils.calendar(t,  "Some_bad_Field");
		} catch (Exception e) {fail = true;}
		assertTrue("Failure to throw exception on invalid field.", fail);
	}
	
}
