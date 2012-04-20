package stencil.module.util;

import stencil.module.MetadataHoleException;
import stencil.module.Module;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.ModuleDataParser.MetadataParseException;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;

/**Basic implementation for modules where meta-data is determined largely
 * by the static module data in the configuration file.  
 * 
 * TODO: Should this merge with mutable module?  All of the meta-data are now mutable anyway....
 * 
 */
public abstract class BasicModule implements Module {
	protected ModuleData moduleData;

	protected BasicModule() throws MetadataParseException {this.moduleData = loadOperatorData();}
	
	/**Actually load the operator data into the module.
	 * This method is called by the constructor and should be over-ridden 
	 * if the default loading mechanism (scanning the module for operator definitions) should not be used.
	 * @throws MetadataParseException 
	 */
	protected ModuleData loadOperatorData() throws MetadataParseException {
		return ModuleDataParser.moduleData(this.getClass());
	}
	
	@Override
	public ModuleData getModuleData() {return moduleData;}

	@Override
	public String getName() {return moduleData.getName();}
	
	/**Return meta-data for the operator of the given name with the
	 * given specializer.
	 * 
	 * Will only accept what instance will accept (calls instance itself).
	 * If instance returns an operator, but the meta-data is incomplete a 
	 * MetaDataHole exception is thrown.  This allows a sub-class to catch
	 * the exception and complete the meta-data.
	 * 
	 *  @throws IllegalArgumentException Unknown operator requested
	 *  @throws SpecializationException Non-default specializer provided.
	 *  @throws MetadataHoleException Default meta-data object is incomplete
	 */
	@Override
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException, MetadataHoleException {
		validate(name, specializer);
		OperatorData ld = moduleData.getOperator(name);
		return ld;
	}
	

	/**Retrieve an instance based on the name and specializer presented. 
	 * This uses the Modules.instance method exclusively.  
	 * Validates arguments before attempting specialization.
	 * Context is ignored by default.
	 */
	@Override
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {			
		validate(name, specializer);
		
		OperatorData operatorData = getOperatorData(name, specializer);		
		StencilOperator operator = Modules.instance(this.getClass(), operatorData, specializer);
		
		return operator;
		
	}	
	
	/**No optimization is attempted (by default).**/
	@Override
	public StencilOperator optimize(StencilOperator op, Context context) {return op;}
	
	/**Verify that the name/specializer combination is valid, throws appropriate exceptions
	 * if they are not valid.
	 *   
	 * Defaults to only accepting specializers that match the default specializer.
	 * Override this method to change validation behavior in both instance and getOperatorData.
	 *  
	 * @param name
	 * @param specializer
	 */
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
		//if (!specializer.equals(moduleData.getDefaultSpecializer(name))) {throw new SpecializationException(moduleData.getName(), name, specializer);}
	}
}
