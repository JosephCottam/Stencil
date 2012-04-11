package stencil.module;

import stencil.module.operator.StencilOperator;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;

/**A module is a collection of related operators.
 * The module interface describes the elements required
 * for a collection to managed.  The implementing class
 * is responsible for coordinating the package members
 * with the interpreter/compiler.  Packages need not contain
 * all of the methods/instructions for their exported methods,
 * but they are responsible for proper initialization
 * (any initialization not performed by the package root
 * will have to be explicitly included in the Stencil).
 *
 * In addition to the method specified, a module must have
 * a zero-argument constructor if it is to be loaded into the 
 * ModuleCache through the startup mechanisms.
 */
public interface Module {
	/**Get an instance of the operator of the given name, instantiated
	 * with the given specializer.  Name may should NOT have a prefix.
	 * 
	 * @throws IllegalArgumentException Name passed is not known by this module.
	 * @throws SpecializationException Specializer passed not permitted with the given operator.
	 * @return
	 */
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException, IllegalArgumentException;
	
	/**Get an optimized version of the operator given the current instance and context.**/
	public StencilOperator optimize(StencilOperator op, Context context) throws SpecializationException, IllegalArgumentException;
	
	/**Get the meta-data about a specific operator, given the specializer.
	 * 
	 * TODO: Replace "specializer" with "context" object
	 * 
	 * @throws IllegalArgumentException Name passed is not known by this module.
	 * @throws SpecializationException Specializer passed not permitted with the given operator.
	 * */
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException,IllegalArgumentException ;

	/**Get the meta-data object describing the module.*/
	public ModuleData getModuleData();
	
	public String getName();
}
