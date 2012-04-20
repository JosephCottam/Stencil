package stencil.testUtilities;

import stencil.module.Module;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.module.util.*;
import stencil.module.util.FacetData.MemoryUse;
import stencil.parser.ParserConstants;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.Singleton;

import java.lang.reflect.*;
import java.util.ArrayList;
 
public class TestModule implements Module {
	private static final ModuleData MODULE_DATA = new ModuleData("TestModule");
	private static final TestModule instance = new TestModule();
	
	static {
		try {
			ArrayList<FacetData> facets = new ArrayList();
			facets.add(new FacetData("map", MemoryUse.FUNCTION, "VALUE"));
			facets.add(new FacetData("query", MemoryUse.FUNCTION, "VALUE"));
			OperatorData od = new OperatorData("temp", "FilterFail", ParserConstants.EMPTY_SPECIALIZER, null, "map", facets);
			MODULE_DATA.addOperator(od);}
		catch (Exception e) {throw new Error("Error creating default specializer.");}

	}
	
	public static TestModule instance() {return instance;}

	public static class Filter {
		static int count = 0;
		public static Tuple doFilter(String ID) {
			count++;
			if (count ==10) {count =0; return null;}
			return Singleton.from(ID);
		}
	}
	
	@Override
	public ModuleData getModuleData() {return MODULE_DATA;}

	@Override
	public StencilOperator optimize(StencilOperator op, Context context) {return op;}
	
	@Override
	public StencilOperator instance(String name, Specializer specializer)
			throws SpecializationException {
		
		try {
			if (name.equals("FilterFail")) {
				Method m = Filter.class.getMethod("doFilter", String.class);
				Invokeable inv = new ReflectiveInvokeable(m, null);
				OperatorData od = getOperatorData(name, specializer);
				return new InvokeableOperator(od, inv);
			}
		} catch (Exception e) {throw new RuntimeException(e);}
		throw new RuntimeException("No such operator " + name);
	}

	@Override
	public String getName() {return MODULE_DATA.getName();}

	@Override
	public OperatorData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		if (name.equals("FilterFail")) {return MODULE_DATA.getOperator("FilterFail");}
		throw new IllegalArgumentException("No operator of name " + name);
	}
}
