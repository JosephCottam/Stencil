package stencil.unittests.module.java;


import stencil.interpreter.tree.Specializer;
import stencil.modules.java.GeneratorStream;
import stencil.parser.ParseStencil;
import stencil.tuple.SourcedTuple;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.unittests.StencilTestCase;

public class TestGeneratorStream extends StencilTestCase {

	public void testCountStream() throws Exception {
		Specializer spec = ParseStencil.specializer("[f: \"x\", start:0, stop:1, step:.1]");
		TuplePrototype p = ParseStencil.prototype("(v)", false);
		GeneratorStream s = new GeneratorStream("count", p, spec);
		
		for (double i=0;i<1; i=i+.1) {
			SourcedTuple t = s.next();
			double v = Converter.toDouble(t.getValues().get(0));
			assertEquals("Unexpected sin value", i, v);
		}
	}

	
	public void testSinStream() throws Exception {
		Specializer spec = ParseStencil.specializer("[f: \"sin(x)\", start:0, stop:1, step:.1]");
		TuplePrototype p = ParseStencil.prototype("(v)", false);
		GeneratorStream s = new GeneratorStream("sin", p, spec);
		
		for (double i=0;i<1; i+=.1) {
			SourcedTuple t = s.next();
			double v = Converter.toDouble(t.getValues().get(0));
			assertEquals("Unexpected sin value", Math.sin(i), v);
		}
	}
}
