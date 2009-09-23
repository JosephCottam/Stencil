package stencil.operator.module.util;

import stencil.operator.StencilOperator;
import stencil.operator.module.Module;
import stencil.operator.module.ModuleData;
import stencil.operator.module.OperatorData;
import stencil.operator.module.SpecializationException;
import stencil.operator.wrappers.InvokeableLegend;
import stencil.parser.tree.Specializer;

/**Basic implementation for modules where meta-data is determined largely
 * by the static module data in the configuration file.  
 * 
 */
public abstract class BasicModule implements Module {
	/**Indicate that an meta-data object has an incomplete field, when
	 * all fields are expected to be complete.
	 */
	public static class MetaDataHoleException extends SpecializationException {
		private OperatorData operatorData;
		
		public MetaDataHoleException(String module, String operator, Specializer spec, OperatorData operatorData) {
			super(module, operator, spec);
			this.operatorData = operatorData;
		}
		
		public OperatorData getLegendData() {return operatorData;} 
	}
	
	protected ModuleData moduleData;

	protected BasicModule(ModuleData md) {this.moduleData = md;}
	
	public ModuleData getModuleData() {return moduleData;}

	public String getName() {return moduleData.getName();}
	
	/**Return legend data for the operator of the given name with the
	 * given specializer.
	 * 
	 * Will only accept what instance will accept (calls instance itself).
	 * If instance returns an operator, but the meta-data is incomplete a 
	 * MetaDataHole exception is thrown.  This allows a sub-class to catch
	 * the exception and complete the meta-data.
	 * 
	 *  @throws IllegalArgumentException Unknown operator requested
	 *  @throws SpecializationException Non-default specializer provided.
	 *  @throws MetaDataHoleException Default meta-data object is incomplete
	 */
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException, MetaDataHoleException {
		validate(name, specializer);
		OperatorData ld = moduleData.getOperatorData(name);
		if (ld.isComplete()) {return ld;}
		
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}
	

	/**Retrieve an instance based on the name and specializer presented. 
	 * This uses the Modules.instance method exclusively.  
	 * Validates arguments before attempting specialization.
	 */
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {			
		validate(name, specializer);
		
		OperatorData ld = getModuleData().getOperatorData(name);
		String targetName = ld.getAttribute("Target");
		
		StencilOperator operator = Modules.instance(this.getClass(), targetName, getModuleData().getName(), name);
		
		if (operator instanceof InvokeableLegend && ld.getAttribute("DisallowAutoGuide") != null) {
			((InvokeableLegend) operator).allowAutoGuide(false);
		}
		
		return operator;
		
	}
	
	/**Verify that the name/specializer combination is valid, throws appropriate exceptions
	 * if they are not valid.
	 *   
	 * Defaults to only accepting specializers that match the default specializer.
	 * Override this method to change validation behavior in both instance and getLegendData.
	 *  
	 * @param name
	 * @param specializer
	 */
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
		if (!specializer.equals(moduleData.getDefaultSpecializer(name))) {throw new SpecializationException(moduleData.getName(), name, specializer);}
	}
}
