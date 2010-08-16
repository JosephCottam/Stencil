package stencil.unittests.module.operator;

import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.unittests.StencilTestCase;
import stencil.module.util.ModuleData;
import stencil.module.util.ModuleDataParser;
import stencil.modules.Temp;
import static stencil.modules.Temp.Oscillate;

public class TestOscillate extends StencilTestCase {
	Temp temp;

	public void setUp() throws Exception {
		ModuleData MD = ModuleDataParser.load("./modules/stencil/modules/Temp.yml");
		temp = new Temp(MD);
	}
	
	public void TestCycle() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[style: \"circle\", states: 5]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length; i++) {
			assertEquals(values[i], op.map());
		}
	}
	
	public void TestSine() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[style: \"sine\", states: 5]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length-2; i++) {
			assertEquals(values[values.length-i-1], op.map());
		}
	}
	
	public void TestSine2() throws Exception {
		Specializer spec = ParseStencil.parseSpecializer("[style: \"sine2\", states: 5]");
		Oscillate op  = (Oscillate) temp.instance("Oscillate", spec);
		
		Object[] values = new Object[5];
		for (int i=0; i<values.length; i++) {
			values[i] = op.map();
		}
		
		for (int i=0; i<values.length; i++) {
			assertEquals(values[values.length-i], op.map());
		}
	}

	public void TestStates() throws Exception {
		Specializer spec5 = ParseStencil.parseSpecializer("[style: \"cycle\", states: 5]");
		Specializer spec10 = ParseStencil.parseSpecializer("[style: \"cycle\", states: 10]");
		Oscillate op5  = (Oscillate) temp.instance("Oscillate", spec5);
		Oscillate op10  = (Oscillate) temp.instance("Oscillate", spec10);

		for (int i=0; i< 5; i++) {
			assertEquals("Oscillation not equal when expected; round " + i, op5.map(), op10.map());
		}
		
		for (int i=0; i<5; i++) {
			assertFalse("Oscillation equal where not expected; round " + i, op5.map() != op10.map());
		}
		
	}
}
