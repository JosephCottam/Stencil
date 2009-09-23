package stencil.operator.module;

import java.util.Collection;
import stencil.parser.tree.Specializer;

public interface ModuleData {
	/**Get a list of operators in this module.*/
	public Collection<String> getOperators();
	
	/**Get information about an operator, without regard to specialization.
	 * Information that changes based on specialization is reflected a LegendData
	 * object retrieved from the module.*/
	public OperatorData getOperatorData(String name);
		
	
	/**What is the default specializer for the named operator?*/
	public Specializer getDefaultSpecializer(String op);
	
	/**What is the name of this module?*/
	public String getName();
	
	/**What is the module associated with the Module Data descriptor?*/
	public Module getModule();

}