package stencil.modules.layouts;

import stencil.operator.StencilOperator;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.ModuleIncompleteError;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;

public class Layouts extends BasicModule {

	public Layouts(ModuleData md) {super(md);}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {			
		validate(name, specializer);
		
		OperatorData operatorData = getModuleData().getOperator(name);
		if (operatorData.getName().equals(Circular.NAME)) {
			return new Circular(operatorData, specializer);
		} else if (operatorData.getName().equals(RadialTree.NAME)) {
			return new RadialTree(operatorData, specializer);
		} else if (operatorData.getName().equals(TreeMap.NAME)) {
			return new TreeMap(operatorData, specializer);
		}
		
		throw new ModuleIncompleteError(name);
	}

}
