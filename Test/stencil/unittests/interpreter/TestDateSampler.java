package stencil.unittests.interpreter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.samplers.DateSampler;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.Tuple;
import junit.framework.TestCase;

public class TestDateSampler extends TestCase {
	private SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MMM-yy");

	public void testDays() throws ProgramParseException, ParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"DAY\"]");
		List<Date> dates = Arrays.asList(FORMAT.parse("01-Jun-05"), FORMAT.parse("30-Jun-05"));
		test(spec, dates, 30);		
	}
	
	public void testWeeks() throws ProgramParseException, ParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"WEEK\"]");
		List<Date> dates = Arrays.asList(FORMAT.parse("01-Jun-05"), FORMAT.parse("30-Jun-05"));
		test(spec, dates, 4);
	}

	public void testMonths() throws ProgramParseException, ParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"MONTH\"]");
		List<Date> dates = Arrays.asList(FORMAT.parse("08-Jun-05"), FORMAT.parse("23-Dec-08"));
		test(spec, dates, 43);
	}
	
	public void testYears() throws ProgramParseException, ParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"YEAR\"]");
		List<Date> dates = Arrays.asList(FORMAT.parse("08-Jun-05"), FORMAT.parse("23-Dec-08"));
		test(spec, dates, 4);
	}



	private void test(Specializer spec, List<Date> dates, int span) {
		DateSampler sampler = new DateSampler();
		SampleSeed seed = new SampleSeed(SampleSeed.SeedType.CONTINUOUS, dates);
		List<Tuple> sample = sampler.sample(seed, spec);
		
		assertEquals(sample.toString(), span, sample.size());		
	}
}
