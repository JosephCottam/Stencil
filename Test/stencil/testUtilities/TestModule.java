package stencil.testUtilities;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.OperatorData.OpType;
import stencil.operator.module.util.*;
import stencil.operator.wrappers.InvokeableLegend;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import java.lang.reflect.*;

public class TestModule implements Module {
	private static final MutableModuleData MODULE_DATA = new MutableModuleData(instance(), "TestModule");
	private static final TestModule instance = new TestModule();
	
	static {
		try {MODULE_DATA.addOperator("FilterFail", ParseStencil.parseSpecializer("[1 .. n]"));}
		catch (Exception e) {throw new Error("Error creating default specializer.");}
		
		MutableOperatorData ld = (MutableOperatorData) MODULE_DATA.getOperatorData("FilterFail");
		ld.addFacet(new BasicFacetData("Map", OpType.PROJECT, "VALUE"));
	}
	
	public static TestModule instance() {return instance;}
	

	
	public static class Filter {
		static int count = 0;
		public static Tuple doFilter(String ID) {
			count++;
			if (count ==10) {count =0; return null;}
			return PrototypedTuple.singleton(ID);
		}
	}
	
	public ModuleData getModuleData() {return MODULE_DATA;}

	public StencilOperator instance(String name, Specializer specializer)
			throws SpecializationException {
		
		try {
			if (name.equals("FilterFail")) {
				Method m = Filter.class.getMethod("doFilter", String.class);
				return new InvokeableLegend(name, m);
			}
		} catch (Exception e) {throw new RuntimeException(e);}
		throw new RuntimeException("No such legend " + name);
	}

	public String getName() {return MODULE_DATA.getName();}

	public OperatorData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		if (name.equals("FilterFail")) {return MODULE_DATA.getOperatorData("FilterFail");}
		throw new IllegalArgumentException("No legend of name " + name);
	}

}
