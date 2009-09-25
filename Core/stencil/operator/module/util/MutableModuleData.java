package stencil.operator.module.util;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import stencil.operator.module.Module;
import stencil.operator.module.ModuleData;
import stencil.operator.module.OperatorData;
import stencil.parser.tree.Specializer;

public class MutableModuleData implements ModuleData {
	protected Map<String, OperatorData> operators = new HashMap<String, OperatorData>();
	protected Module module;
	protected String moduleName;
	
	public MutableModuleData(Module module, String name) {
		this.module = module;
		this.moduleName = name;
	}
	
	
	public Specializer getDefaultSpecializer(String op) {return operators.get(op).getDefaultSpecializer();}

	public Module getModule() {return module;}

	public String getName() {return moduleName;}

	/**Creates a new operator entry and returns the related meta-data object for
	 * further refinement.  The returned object is mutable and recorded internally.
	 * 
	 * @param name
	 * @param defaultSpecializer
	 * @return
	 */
	public MutableOperatorData addOperator(String name, Specializer defaultSpecializer) {
		MutableOperatorData legendData = new MutableOperatorData(getName(), name, defaultSpecializer);
		operators.put(name, legendData);
		return legendData;
	}
	
	/**Add an operator, using the given legendData object.
	 * @param legendName
	 * @param defaultSpecializer
	 * @param legendData
	 */
	public void addOperator(OperatorData legendData) {
		operators.put(legendData.getName(), legendData);
	}
	
	public OperatorData getOperatorData(String name) {
		if (!operators.containsKey(name)) {throw new IllegalArgumentException(String.format("Operator %1$s not know in module %2$s.", name, getName()));}
		OperatorData legendData = operators.get(name);
		return legendData;
	}

	public Collection<String> getOperators() {return operators.keySet();}

}
