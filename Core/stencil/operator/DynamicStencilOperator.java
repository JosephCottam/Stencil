package stencil.operator;

import stencil.operator.module.OperatorData;
import stencil.operator.module.SpecializationException;
import stencil.operator.util.Invokeable;
import stencil.parser.tree.Specializer;

/**A dynamic legend is one that is defined during Java runtime.
 * It is distinct in that the exact mapping method names cannot be
 * statically determined, so a lookup function is added.  Furthermore,
 * it maybe not be part of a module when it is created, so the LegendData
 * object is independently accessible through through this interface.
 */
public interface DynamicStencilOperator extends StencilOperator {

	/**Given a method name, return the method/target pair that can
	 * be used to invoke that method.
	 * 
	 * @throws IllegalArgumentException Thrown when 'name' is not known in this dynamic legend
	 */
	public Invokeable getFacet(String name) throws IllegalArgumentException;
	
	/**What is the meta-data for this Operator?
	 *   
	 * This method must accept a null specializer, which indicates that only static operator 
	 * data is required.
	 * @param spec
	 * @return
	 * @throws SpecializationException
	 */
	public OperatorData getOperatorData(Specializer spec) throws SpecializationException;
 }
