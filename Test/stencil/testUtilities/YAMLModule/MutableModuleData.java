package stencil.testUtilities.YAMLModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.operator.module.Module;
import stencil.parser.tree.Specializer;


public final class MutableModuleData {
	private String name;
	private Map<String, MutableOperatorData> operators = new HashMap();
	private Module module;
	private String clazz;
	private MutableOperatorData opDataDefault;
	private String description;
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public Specializer getDefaultSpecializer(String op) {
		return getOperator(name).getDefaultSpecializer();
	}

	public void setModule(Module module) {this.module = module;}
	public Module getModule() {return module;}
	
	public void setTargetClass(String clazz) {this.clazz = clazz;}
	public String getTargetClass() {return clazz;}
	

	public String getName() {return name;}
	public void setName(String name) {
		this.name = name; 
		if (opDataDefault != null) {opDataDefault.setModule(name);}
	}
	
	
	public MutableOperatorData getDefaults() {return opDataDefault;}
	public void setDefaults(MutableOperatorData od) {
		this.opDataDefault = od; 
		opDataDefault.setModule(name);
	}

	/**Get a list of all of the operator data objects.*/
	public List<MutableOperatorData> getOperators() {return new ArrayList<MutableOperatorData>(operators.values());}
	public void setOperators(List<MutableOperatorData> newOperators) {
		if (newOperators == null) {newOperators = new ArrayList();}
		operators.clear();
		for (MutableOperatorData od:newOperators) {addOperator(od);}
	}
	public void addOperator(MutableOperatorData od) {
		od.mergeWith(opDataDefault);
		operators.put(od.getName(), od);
	}

	public MutableOperatorData getOperator(String name) {
		if (operators.containsKey(name)) {return operators.get(name);}
		throw new IllegalArgumentException(String.format("Operator %1$s not found in module %2$s.", name, this.name));
	}
}
