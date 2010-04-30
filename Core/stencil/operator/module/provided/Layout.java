package stencil.operator.module.provided;

import stencil.operator.StencilOperator;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.provided.layouts.CircularLayout;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;

public class Layout extends BasicModule {

	public Layout(ModuleData md) {super(md);}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {			
		validate(name, specializer);
		
		OperatorData operatorData = getModuleData().getOperator(name);		
		return new CircularLayout(operatorData, specializer);
		
	}

}
