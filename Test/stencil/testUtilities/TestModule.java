package stencil.testUtilities;

import stencil.module.Module;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.operator.wrappers.InvokeableOperator;
import stencil.module.util.*;
import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.PrototypedTuple;
import static stencil.module.util.OperatorData.*;

import java.lang.reflect.*;
 
public class TestModule implements Module {
	private static final ModuleData MODULE_DATA = new ModuleData(instance(), "TestModule");
	private static final TestModule instance = new TestModule();
	
	static {
		try {MODULE_DATA.addOperator(new OperatorData("temp", "FilterFail", ParseStencil.parseSpecializer("[range: ALL]")));}
		catch (Exception e) {throw new Error("Error creating default specializer.");}
		
		OperatorData od = MODULE_DATA.getOperator("FilterFail");
		od.addFacet(new FacetData("map", TYPE_PROJECT, true, "VALUE"));
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
				Invokeable inv = new ReflectiveInvokeable(m, null);
				OperatorData od = getOperatorData(name, specializer);
				return new InvokeableOperator(name,  od, inv);
			}
		} catch (Exception e) {throw new RuntimeException(e);}
		throw new RuntimeException("No such legend " + name);
	}

	public String getName() {return MODULE_DATA.getName();}

	public OperatorData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		if (name.equals("FilterFail")) {return MODULE_DATA.getOperator("FilterFail");}
		throw new IllegalArgumentException("No legend of name " + name);
	}

}
