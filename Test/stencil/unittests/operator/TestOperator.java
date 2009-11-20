package stencil.unittests.operator;

import stencil.adapters.Adapter;
import stencil.operator.*;
import stencil.operator.wrappers.SyntheticOperator;
import stencil.operator.wrappers.SyntheticOperator.NoMatchException;
import stencil.parser.tree.Program;
import stencil.parser.string.ParseStencil;
import stencil.tuple.Tuple;
import junit.framework.TestCase;

public class TestOperator extends TestCase {
	public static final String fullLegendSource = "operator full(X,Y,Z) -> (X,Y,Z) (X !~ \"[null]\") => (X,Y,Z) : (Z,Y,X)";			
	public static final String basicLegendSource = "operator basic(A,B,C,D,E,F,G) -> (Z,Y,X) (A !~ \"[null]\") => (Z,Y,X): (A,E,G)";
	public static final Adapter ADAPTER = stencil.adapters.java2D.Adapter.INSTANCE;
	
	
	public void testGenerate() throws Exception {
		Program program = ParseStencil.parse(fullLegendSource, ADAPTER);
		StencilOperator legend = new SyntheticOperator(null, program.getOperators().get(0));
		
		assertEquals("full", legend.getName());
		assertEquals(SyntheticOperator.class, legend.getClass());

		program = ParseStencil.parse(basicLegendSource, ADAPTER);
		legend = new SyntheticOperator(null, program.getOperators().get(0));
		assertEquals("basic", legend.getName());
		assertEquals(SyntheticOperator.class, legend.getClass());
	}

	public void testMap() throws Exception {
		Program program = ParseStencil.parse(fullLegendSource, ADAPTER);
		StencilOperator legend = new SyntheticOperator(null, program.getOperators().get(0));
		Tuple rv;
		
		boolean error = false;
		try {legend.map(null, "2","3");}
		catch (NoMatchException e) {error = true;}
		assertTrue("NoMatchException not thrown when expected.", error);

		rv = legend.map("0", "1", "2");
		assertEquals(3, rv.getPrototype().size());
		assertEquals("0", rv.get("Z"));
		assertEquals("1", rv.get("Y"));
		assertEquals("2", rv.get("X"));
	}

	public void testMapNulls() throws Exception {
		Program program = ParseStencil.parse(basicLegendSource, ADAPTER);

		StencilOperator legend = new SyntheticOperator(null, program.getOperators().get(0));


		boolean error = false;
		try {
			legend.map(null, null, null,null,null,null,null);
		} catch (NoMatchException e) {error = true;}
		assertTrue("NoMatchException not thrown when expected.", error);
	}

	public void testGenerateFail() throws Exception {
		String source = "legend bad(X,Y) -> (X,Y, Z) (X=~\".*\") => (Z,Y,X): (X,Y)";
		boolean failed = false;
		try {
			ParseStencil.parse(source, ADAPTER);
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Exception not thrown when spec has argument mismatch in return.", failed);}

		//Unknown argument in filter
		source = "legend bad(X,Y,Z) -> (X,Y,Z) (A=~ \".*\") => (X,Y,Z) : (Z,Y,X)";
		failed = false;
		try {
			ParseStencil.parse(source, ADAPTER);
		} catch (Exception e) {failed = true;}
		finally {assertTrue("Exception not thrown with unknown argument in filter.", failed);}
	}

	public void testMapfail() throws Exception {
		Program program = ParseStencil.parse(fullLegendSource, ADAPTER);
		StencilOperator legend = new SyntheticOperator(null, program.getOperators().get(0));

		boolean failed = false;
		try {
			legend.map("1", "2","3", "4");
		} catch (IllegalArgumentException e) {failed = true;}
		finally {assertTrue("Legend did not throw too many arguments exception.", failed);}

		failed = false;
		try {
			legend.map("1", "2");
		} catch (IllegalArgumentException e) {failed = true;}
		finally {assertTrue("Legend did not throw too few arguments exception.", failed);}
	}
}
