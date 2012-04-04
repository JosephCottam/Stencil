package stencil.unittests.module.operator;


import stencil.interpreter.tree.Specializer;
import stencil.module.Module;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.modules.java.JavaC;
import stencil.parser.ParseStencil;
import stencil.unittests.StencilTestCase;

public class TestJavaC extends StencilTestCase {
	private final Module javac = new JavaC();

	
	//TODO: More tests...
	public void testJava_Add() throws Exception {
		String add = "[body: \"(int a, int b) =>  a+b\", header:\"\"]";
		Specializer s = ParseStencil.specializer(add);
		StencilOperator o = javac.instance("Java", null, s);
		Invokeable inv = o.getFacet("map");
		assertNotNull(inv);

		
		Object r = inv.invoke(new Object[]{1,2});
		assertEquals(new Integer(3), r); 
		
	}

	//TODO: More tests...
	public void testJavaC() throws Exception {
		String triangular = "[body:\"@Facet(memUse=\\\"FUNCTION\\\", prototype=\\\"(X,Y)\\\", alias={\\\"map\\\",\\\"query\\\"}) "
							+ "public double[] query(int seq) {"
							+ "seq = seq+1;"
							+ "int base = (int) Math.ceil(Math.sqrt(seq));"
							+ "int X = (seq/base) * 10 *base;"
							+ "int Y = (seq%base) * 10 *base;"
							+ "return new double[]{X,Y};"
							+ "}\", "
							+ "header:\"import stencil.module.operator.util.AbstractOperator;\", "
							+ "class:\"AbstractOperator.Statefull\"]";
		Specializer s = ParseStencil.specializer(triangular);
		StencilOperator o = javac.instance("JavaC", null, s);
		Invokeable inv = o.getFacet("map");
		assertNotNull(inv);
		
		double[] r = (double[]) inv.invoke(new Object[]{1});
		assertEquals(20d, r[0]);
		assertEquals(0d, r[1]);
	}
}
