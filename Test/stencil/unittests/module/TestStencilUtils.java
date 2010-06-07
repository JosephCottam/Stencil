package stencil.unittests.module;

import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.prototype.TuplePrototype;
import stencil.modules.StencilUtil;
import junit.framework.TestCase;

public class TestStencilUtils extends TestCase {
	public void testPrototype() throws Exception {
		String fieldDef = "one, two, three";
		String[] fields = fieldDef.split("\\s*,\\s*");
		Specializer spec = ParseStencil.parseSpecializer("[range: ALL, split: 0, fields: \"" + fieldDef + "\"]");
		
		StencilUtil.EchoCategorize ec = new StencilUtil.EchoCategorize(null, spec);
		TuplePrototype proto = ec.getSamplePrototype();
		
		assertEquals(fields.length, proto.size());
		for (int i=0; i< fields.length; i++) {
			assertEquals(fields[i], proto.get(i).getFieldName());
		}		
	}
}
