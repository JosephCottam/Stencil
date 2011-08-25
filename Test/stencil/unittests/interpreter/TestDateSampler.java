package stencil.unittests.interpreter;

import java.util.Arrays;
import java.util.List;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.samplers.DateSampler;
import stencil.interpreter.tree.Specializer;
import stencil.parser.ParseStencil;
import stencil.parser.ProgramParseException;
import stencil.tuple.Tuple;
import junit.framework.TestCase;

public class TestDateSampler extends TestCase {

	public void testDays() throws ProgramParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"DAY\"]");
		List<String> dates = Arrays.asList("01-Jun-05", "30-Jun-05");
		test(spec, dates, 30);		
	}
	
	public void testWeeks() throws ProgramParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"WEEK\"]");
		List<String> dates = Arrays.asList("01-Jun-05", "30-Jun-05");
		test(spec, dates, 4);
	}

	public void testMonths() throws ProgramParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"MONTH\"]");
		List<String> dates = Arrays.asList("08-Jun-05", "23-Dec-08");
		test(spec, dates, 43);
	}
	
	public void testYears() throws ProgramParseException {
		Specializer spec = ParseStencil.specializer("[unit: \"YEAR\"]");
		List<String> dates = Arrays.asList("08-Jun-05", "23-Dec-08");
		test(spec, dates, 4);
	}



	private void test(Specializer spec, List<String> dates, int span) {
		DateSampler sampler = new DateSampler();
		SampleSeed seed = new SampleSeed(SampleSeed.SeedType.CATEGORICAL, dates);
		List<Tuple> sample = sampler.sample(seed, spec);
		
		assertEquals(sample.toString(), span, sample.size());		
	}
}
