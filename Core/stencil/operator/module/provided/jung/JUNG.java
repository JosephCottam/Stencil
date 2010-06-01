package stencil.operator.module.provided.jung;

import stencil.operator.StencilOperator;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.ModuleIncompleteError;
import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;

import java.lang.reflect.*;

public class JUNG extends BasicModule {
	private static final Class[] OPERATOR_CLASSES = new Class[]{BalloonLayout.class, CircleLayout.class, FRLayout.class, ISOMLayout.class, KKLayout.class, RadialTreeLayout.class, SpringLayout.class, TreeLayout.class};
	private static final Class[] CONSTRUCTOR_TYPES = new Class[]{OperatorData.class, Specializer.class};
	
	public JUNG(ModuleData md) {super(md);}

	@Override
	public StencilOperator instance(String name, Specializer specializer)
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
