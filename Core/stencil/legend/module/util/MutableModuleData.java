package stencil.legend.module.util;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import stencil.legend.module.LegendData;
import stencil.legend.module.Module;
import stencil.legend.module.ModuleData;
import stencil.parser.tree.Specializer;

public class MutableModuleData implements ModuleData {
	protected Map<String, LegendData> operators = new HashMap<String, LegendData>();
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
	public MutableLegendData addOperator(String name, Specializer defaultSpecializer) {
		MutableLegendData legendData = new MutableLegendData(getName(), name, defaultSpecializer);
		operators.put(name, legendData);
		return legendData;
	}
	
	/**Add an operator, using the given legendData object.
	 * @param legendName
	 * @param defaultSpecializer
	 * @param legendData
	 */
	public void addOperator(LegendData legendData) {
		operators.put(legendData.getName(), legendData);
	}
	
	public LegendData getOperatorData(String name) {
		if (!operators.containsKey(name)) {throw new IllegalArgumentException(String.format("Operator %1$s not know in module %2$s.", name, getName()));}
		LegendData legendData = operators.get(name);
		return legendData;
	}

	public Collection<String> getOperators() {return operators.keySet();}

}
