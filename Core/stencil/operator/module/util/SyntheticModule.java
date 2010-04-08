package stencil.operator.module.util;

import stencil.operator.StencilOperator;
import stencil.operator.module.Module;
import stencil.operator.module.SpecializationException;
import stencil.parser.tree.Specializer;

public class SyntheticModule implements Module {
	private final ModuleData md;
	private final Class clazz;
	
	public SyntheticModule(ModuleData md) throws Exception {
		this.md = md;
		clazz = Class.forName(md.getTargetClass());
	}
	
	public ModuleData getModuleData() {return md;}
	public String getName() {return md.getName();}

	public OperatorData getOperatorData(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		
		if (specializer.isSimple()) {return md.getOperator(name);}
		throw new IllegalArgumentException("Only simple specializers accepted.");
	}

	public StencilOperator instance(String name, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		
		if (!specializer.isSimple()) {
			throw new IllegalArgumentException("Only simple specializers accepted.");
		}
		
		OperatorData operatorData = getModuleData().getOperator(name);				
		return Modules.instance(clazz, operatorData, specializer);
	}

}
