package stencil.testUtilities;

import stencil.legend.StencilLegend;
import stencil.legend.wrappers.InvokeableLegend;
import stencil.legend.module.*;
import stencil.legend.module.LegendData.OpType;
import stencil.legend.module.util.*;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.streams.Tuple;
import stencil.util.BasicTuple;
import java.lang.reflect.*;

public class TestModule implements Module {
	private static final MutableModuleData MODULE_DATA = new MutableModuleData(instance(), "TestModule");
	private static final TestModule instance = new TestModule();
	
	static {
		try {MODULE_DATA.addOperator("FilterFail", ParseStencil.parseSpecializer("[1 .. n]"));}
		catch (Exception e) {throw new Error("Error creating default specializer.");}
		
		MutableLegendData ld = (MutableLegendData) MODULE_DATA.getOperatorData("FilterFail");
		ld.addFacet(new BasicFacetData("Map", OpType.PROJECT, "VALUE"));
	}
	
	public static TestModule instance() {return instance;}
	

	
	public static class Filter {
		static int count = 0;
		public static Tuple doFilter(String ID) {
			count++;
			if (count ==10) {count =0; return null;}
			return BasicTuple.singleton(ID);
		}
	}
	
	public ModuleData getModuleData() {return MODULE_DATA;}

	public StencilLegend instance(String name, Specializer specializer)
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

	public LegendData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		if (name.equals("FilterFail")) {return MODULE_DATA.getOperatorData("FilterFail");}
		throw new IllegalArgumentException("No legend of name " + name);
	}

}
