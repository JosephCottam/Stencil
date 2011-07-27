package stencil.module.util;

import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;

import java.util.Map;
import java.util.HashMap;

/**Simple MutableModule.  Does not allow specialization of the operators.*/
public class MutableModule implements Module {
	protected Map<String, StencilOperator> operators;
	protected ModuleData moduleData;

	public MutableModule(String name) {
		operators = new HashMap<String, StencilOperator>();
		moduleData = new ModuleData(name);
	}

	public ModuleData getModuleData() {return moduleData;}

	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {
		return moduleData.getOperator(name);		
	}

	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		return operators.get(name);
	}

	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException {
		return operators.get(name);
	}

	
	public void addOperator(StencilOperator target) {
		try {
			addOperator(target, target.getOperatorData());
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error adding operator %1$s to module %2$s", target.getName(), getModuleData().getName()), e);
		}
	}
	
	public void addOperator(StencilOperator target, OperatorData opData) {
		operators.put(target.getName(), target);
		moduleData.addOperator(opData);
	}
	
	public void addOperator(String name, StencilOperator op, OperatorData opData) {
		operators.put(name, op);
		moduleData.addOperator(opData);
	}
	
	public String getName() {return moduleData.getName();}
}
