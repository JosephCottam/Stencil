package stencil.unittests.module.operator;

import java.util.Arrays;

import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Specializer;
import stencil.unittests.StencilTestCase;
import stencil.modules.Temp;
import static stencil.modules.Temp.Oscillate;

public class TestOscillate extends StencilTestCase {
	Temp temp;

	public void setUp() throws Exception {
		super.setUp();
		temp = new Temp();
	}
	
	public void testCycle() throws Exception {
		Specializer spec = ParseStencil.specializer("[style: \"circle\", states: 5, split:0]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length; i++) {
			assertEquals(values[i], op.map());
		}
	}
	
	public void testSine() throws Exception {
		Specializer spec = ParseStencil.specializer("[style: \"sine\", states: 5, split:0]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length-2; i++) {
			assertEquals("Error in sequence: " + Arrays.deepToString(values), values[values.length-i-2], op.map());
		}
	}
	
	public void testSine2() throws Exception {
		Specializer spec = ParseStencil.specializer("[style: \"sine2\", states: 5, split:0]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length; i++) {
			assertEquals("Error in sequence: " + Arrays.deepToString(values), values[values.length-i-1], op.map());
		}
	}

	public void testStates() throws Exception {
		Specializer spec5 = ParseStencil.specializer("[style: \"circle\", states: 5, split:0]");
		Specializer spec10 = ParseStencil.specializer("[style: \"circle\", states: 10, split:0]");
		Oscillate op5  = (Oscillate) temp.instance("Oscillate", spec5);
		Oscillate op10  = (Oscillate) temp.instance("Oscillate", spec10);

		for (int i=0; i< 5; i++) {
			assertEquals("Oscillation not equal when expected; round " + i, op5.map(), op10.map());
		}
		
		for (int i=0; i<5; i++) {
			assertFalse("Oscillation equal where not expected; round " + i, op5.map() == op10.map());
		}
		
	}
}
