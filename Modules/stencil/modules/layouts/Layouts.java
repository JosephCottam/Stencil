package stencil.modules.layouts;

import static stencil.module.util.ModuleDataParser.moduleData;
import static stencil.module.util.ModuleDataParser.operatorData;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.ModuleIncompleteError;
import stencil.module.util.OperatorData;
import stencil.module.util.ModuleDataParser.MetaDataParseException;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;

@Module
@Description("Layouts groups of nodes")
public class Layouts extends BasicModule {
	private static final String MODULE_NAME = "Layouts";
	
	protected ModuleData loadOperatorData() throws MetaDataParseException {
		OperatorData[] ods = new OperatorData[]{
			operatorData(Circular.class, MODULE_NAME),
			operatorData(Layout.class, MODULE_NAME),
			operatorData(RadialTree.class, MODULE_NAME),
			operatorData(TreeMap.class, MODULE_NAME),
		};
		
		ModuleData md = moduleData(this.getClass());
		for(OperatorData od:ods) {md.addOperator(od);}
		return md;
	}

	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {			
		validate(name, specializer);
		
		OperatorData operatorData = getModuleData().getOperator(name);
		if (operatorData.name().equals(Circular.NAME)) {
			return new Circular(operatorData, specializer);
		} else if (operatorData.name().equals(RadialTree.NAME)) {
			return new RadialTree(operatorData, specializer);
		} else if (operatorData.name().equals(TreeMap.NAME)) {
			return new TreeMap(operatorData, specializer);
		}
		
		throw new ModuleIncompleteError(name);
	}

}
