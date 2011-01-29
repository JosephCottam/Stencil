package stencil.modules.jung;

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
import static stencil.module.util.ModuleDataParser.operatorData;
import static stencil.module.util.ModuleDataParser.moduleData;

import java.lang.reflect.*;

@Module
@Description("Module to provide access to JUNG operators in Stencil")
public class JUNG extends BasicModule {
	private static final Class[] OPERATOR_CLASSES = new Class[]{BalloonLayout.class, CircleLayout.class, FRLayout.class, ISOMLayout.class, KKLayout.class, RadialTreeLayout.class, SpringLayout.class, TreeLayout.class};
	private static final Class[] CONSTRUCTOR_TYPES = new Class[]{OperatorData.class, Specializer.class};
	private static final String MODULE_NAME = "JUNG";
	
	protected ModuleData loadOperatorData() throws MetaDataParseException {
		OperatorData[] ods = new OperatorData[]{
			operatorData(BalloonLayout.class, MODULE_NAME),
			operatorData(CircleLayout.class, MODULE_NAME),
			operatorData(ISOMLayout.class, MODULE_NAME),
			operatorData(KKLayout.class, MODULE_NAME),
			operatorData(RadialTreeLayout.class, MODULE_NAME),
			operatorData(SpringLayout.class, MODULE_NAME),
		};
		
		ModuleData md = moduleData(this.getClass());
		for(OperatorData od:ods) {md.addOperator(od);}
		return md;
	}
	
	@Override
	public StencilOperator instance(String name, Context context, Specializer specializer)
			throws SpecializationException, IllegalArgumentException {
		validate(name, specializer);

		OperatorData operatorData = getModuleData().getOperator(name);
		
		try {
			for (Class clazz: OPERATOR_CLASSES) {
				if (clazz.getSimpleName().equals(name)) {
					Constructor c = clazz.getConstructor(CONSTRUCTOR_TYPES);
					return (StencilOperator) c.newInstance(operatorData, specializer);
				}
			}
		} 
		catch (IllegalAccessException e) {throw new Error("Could not access required information for JUNG operator construction.", e);}
		catch (SecurityException e) {throw new Error("Could not access required information for JUNG operator construction.", e);}
		catch (NoSuchMethodException e) {throw new Error("Required constructor not found for JUNG operator construction.", e);} 
		catch (InstantiationException e) {throw new Error("Could not instantiate JUNG operator",e);}
		catch (InvocationTargetException e) {throw new Error("Could not instantiate JUNG operator",e);}
		
		throw new ModuleIncompleteError(name);
	}

}
