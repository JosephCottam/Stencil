package stencil.module.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.interpreter.tree.Specializer;


public final class ModuleData {
	private String name;
	private Map<String, OperatorData> operators = new HashMap();
	private String clazz;
	private String description;
	
	public ModuleData() {}
	
	public ModuleData(String name) {this.name = name;}
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public Specializer getDefaultSpecializer(String op) {
		return getOperator(op).getDefaultSpecializer();
	}
	
	public void setTargetClass(String clazz) {this.clazz = clazz;}
	public String getTargetClass() {return clazz;}
	

	public String getName() {return name;}
	public void setName(String name) {
		this.name = name;
	}
	
	/**Get a list of all of the operator data objects.*/
	public List<String> getOperatorNames() {return new ArrayList(operators.keySet());}
	public List<OperatorData> getOperators() {return new ArrayList(operators.values());}
	public void setOperators(List<OperatorData> newOperators) {
		if (newOperators == null) {newOperators = new ArrayList();}
		operators.clear();
		for (OperatorData od:newOperators) {addOperator(od);}
	}
	
	/**Adds an operator to the module;  will ignore nulls.*/
	public void addOperator(OperatorData od) {
		if (od == null) {return;}
		operators.put(od.getName(), od);
	}

	public OperatorData getOperator(String name) {
		if (operators.containsKey(name)) {return operators.get(name);}
		throw new IllegalArgumentException(String.format("Operator %1$s not found in module %2$s.", name, this.name));
	}
}
