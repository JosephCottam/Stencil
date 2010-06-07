package stencil.modules.layouts;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.ModuleIncompleteError;
import stencil.module.util.OperatorData;
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
