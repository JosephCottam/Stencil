package stencil.testUtilities;

import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.module.util.*;
import stencil.module.util.FacetData.MemoryUse;
import stencil.parser.ParseStencil;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.Singleton;

import java.lang.reflect.*;
 
public class TestModule implements Module {
	private static final ModuleData MODULE_DATA = new ModuleData("TestModule");
	private static final TestModule instance = new TestModule();
	
	static {
		try {MODULE_DATA.addOperator(new OperatorData("temp", "FilterFail", ParseStencil.specializer("[range: ALL]"), null));}
		catch (Exception e) {throw new Error("Error creating default specializer.");}
		
		OperatorData od = MODULE_DATA.getOperator("FilterFail");
		od.addFacet(new FacetData("map", MemoryUse.FUNCTION, "VALUE"));
		od.addFacet(new FacetData("query", MemoryUse.FUNCTION, "VALUE"));
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
	
	public ModuleData getModuleData() {return MODULE_DATA;}

	public StencilOperator instance(String name, Context context, Specializer specializer)
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

	public String getName() {return MODULE_DATA.getName();}

	public OperatorData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		if (name.equals("FilterFail")) {return MODULE_DATA.getOperator("FilterFail");}
		throw new IllegalArgumentException("No operator of name " + name);
	}

	@Override
	public StencilOperator instance(String name, Context context,
			Specializer specializer, ModuleCache modules)
			throws SpecializationException, IllegalArgumentException {
		throw new UnsupportedOperationException("No higher-order ops implemented.");
	}

}
