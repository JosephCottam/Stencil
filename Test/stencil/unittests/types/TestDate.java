package stencil.unittests.types;

import junit.framework.TestCase;
import stencil.types.date.*;
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
		assertEquals(0, DateUtils.dateDiff("MILLI", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("SEC", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("MIN", Jan2000, Jan2000));
		assertEquals(0, DateUtils.dateDiff("HOUR", Jan2000, Jan2000));
		assertEquals(0, DateUtils.dateDiff("DAY", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("WEEK", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("MONTH", Jan2000, Jan2000));	
		assertEquals(0, DateUtils.dateDiff("YEAR", Jan2000, Jan2000));	
		
		assertEquals(366, DateUtils.dateDiff("DAY", Jan2000, Jan2001));	//leap year
		assertEquals(365, DateUtils.dateDiff("DAY", Dec2000, Dec2001));	//not leap year
		assertEquals(1, DateUtils.dateDiff("YEAR", Jan2000, Jan2001));
		assertEquals(11, DateUtils.dateDiff("MONTH", Jan2000, Dec2000));
		assertEquals(12, DateUtils.dateDiff("MONTH", Jan2000, Jan2001));
		assertEquals(1, DateUtils.dateDiff("YEAR", Jan2000, Jan2001));
		assertEquals(10, DateUtils.dateDiff("YEAR", Jan2000, Jan2010));
		assertEquals(1, DateUtils.dateDiff("YEAR", Dec2000, Jan2001));
	}
}
