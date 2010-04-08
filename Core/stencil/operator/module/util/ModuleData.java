package stencil.operator.module.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stencil.operator.module.Module;
import stencil.parser.tree.Specializer;


public final class ModuleData {
	private String name;
	private Map<String, OperatorData> operators = new HashMap();
	private Module module;
	private String clazz;
	private OperatorData opDataDefault;
	private String description;
	
	public ModuleData() {}
	
	public ModuleData(Module module, String name) {
		this.module = module;
		this.name = name;
	}
	
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public Specializer getDefaultSpecializer(String op) {
		return getOperator(op).getDefaultSpecializer();
	}

	public void setModule(Module module) {this.module = module;}
	public Module getModule() throws Exception {
		if (module == null) {createModule();}
		return module;
	}
	
	private void createModule() throws Exception {
		Class c = Class.forName(clazz);
		if (Module.class.isAssignableFrom(c)) {
			module = (Module) Class.forName(clazz).getConstructor(ModuleData.class).newInstance(this);			
		} else {
			module = new SyntheticModule(this);
		}
	}
	
	public void setTargetClass(String clazz) {this.clazz = clazz;}
	public String getTargetClass() {return clazz;}
	

	public String getName() {return name;}
	public void setName(String name) {
		this.name = name; 
		if (opDataDefault != null) {opDataDefault.setModule(name);}
	}
	
	
	public OperatorData getDefaults() {return opDataDefault;}
	public void setDefaults(OperatorData od) {
		this.opDataDefault = od; 
		opDataDefault.setModule(name);
	}

	/**Get a list of all of the operator data objects.*/
	public List<String> getOperatorNames() {return new ArrayList(operators.keySet());}
	public List<OperatorData> getOperators() {return new ArrayList(operators.values());}
	public void setOperators(List<OperatorData> newOperators) {
		if (newOperators == null) {newOperators = new ArrayList();}
		operators.clear();
		for (OperatorData od:newOperators) {addOperator(od);}
	}
	public void addOperator(OperatorData od) {
		if (opDataDefault != null) {od.mergeWith(opDataDefault);}
		operators.put(od.getName(), od);
	}

	public OperatorData getOperator(String name) {
		if (operators.containsKey(name)) {return operators.get(name);}
		throw new IllegalArgumentException(String.format("Operator %1$s not found in module %2$s.", name, this.name));
	}
}
