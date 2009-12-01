package stencil.unittests.types;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import junit.framework.TestCase;
import stencil.types.datetime.*;

public class TestDate extends TestCase {

	public void testNumeric() throws Exception {
		Date d = new Date();
		
		assertEquals(new DateTuple(1), d.create(Arrays.asList(1)));
		assertEquals(new DateTuple(1), d.create(Arrays.asList(1.11f)));
		assertEquals(new DateTuple(3578946), d.create(Arrays.asList(3578946.93847d)));
		assertEquals(new DateTuple(100), d.create(Arrays.asList(100)));
		assertEquals(new DateTuple(13948), d.create(Arrays.asList(13948)));
		assertEquals(new DateTuple(98745), d.create(Arrays.asList("98745")));
	}
	
	public void testString() throws Exception {
		Date d = new Date();
		
		assertEquals(new DateTuple(98745), d.create(Arrays.asList("98745")));
		SimpleDateFormat f = new SimpleDateFormat("ddMMMyy");
		assertEquals(new DateTuple(f.parse("12Dec09")), d.create(Arrays.asList("ddMMMyy", "12Dec09")));

		f = new SimpleDateFormat("mm/dd/yy");
		assertEquals(new DateTuple(f.parse("1/10/84")), d.create(Arrays.asList("mm/DD/yy", "1/10/84")));
	}
	
}
