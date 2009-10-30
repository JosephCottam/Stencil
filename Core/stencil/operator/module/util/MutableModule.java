package stencil.operator.module.util;

import stencil.operator.DynamicStencilOperator;
import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.parser.tree.Specializer;

import java.util.Map;
import java.util.HashMap;

/**Simple MutableModule.  Does not allow specialization of the Legends.*/
public class MutableModule implements Module {
	protected Map<String, StencilOperator> legends;
	protected MutableModuleData moduleData;

	public MutableModule(String name) {
		legends = new HashMap<String, StencilOperator>();
		moduleData = new MutableModuleData(this, name);
	}

	public ModuleData getModuleData() {return moduleData;}

	//TODO: allow use of non-default specializer
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {
		Specializer defaultSpecailizer = getModuleData().getDefaultSpecializer(name);
		if (!specializer.equals(defaultSpecailizer)) {throw new SpecializationException(getName(),name, specializer);}
		return moduleData.getOperatorData(name);		
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		StencilOperator op = legends.get(name);
		if (op instanceof DynamicStencilOperator 
				&& !specializer.isSimple()) {
			throw new SpecializationException(getName(),name, specializer);
		}
		
		return legends.get(name);
	}

	public void addOperator(DynamicStencilOperator target) {
		try {
			addOperator(target, target.getOperatorData(null));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error adding jython legend %1$s to module %2$s", target.getName(), getModuleData().getName()), e);
		}
	}
	
	public void addOperator(StencilOperator target, OperatorData opData) {
		legends.put(target.getName(), target);
		moduleData.addOperator(opData);
	}
	
	public void addOperator(String name, StencilOperator op, OperatorData opData) {
		legends.put(name, op);
		moduleData.addOperator(opData);
	}
	
	public String getName() {return moduleData.getName();}
}
