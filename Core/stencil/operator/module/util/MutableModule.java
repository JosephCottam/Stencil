package stencil.operator.module.util;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.parser.tree.Specializer;

import java.util.Map;
import java.util.HashMap;

/**Simple MutableModule.  Does not allow specialization of the Legends.*/
public class MutableModule implements Module {
	protected Map<String, StencilOperator> operators;
	protected ModuleData moduleData;

	public MutableModule(String name) {
		operators = new HashMap<String, StencilOperator>();
		moduleData = new ModuleData(this, name);
	}

	public ModuleData getModuleData() {return moduleData;}

	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {
		return moduleData.getOperator(name);		
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		return operators.get(name);
	}

	public void addOperator(StencilOperator target) {
		try {
			addOperator(target, target.getOperatorData());
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error adding jython legend %1$s to module %2$s", target.getName(), getModuleData().getName()), e);
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
