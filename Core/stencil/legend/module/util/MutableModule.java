package stencil.legend.module.util;

import stencil.parser.tree.Specializer;
import stencil.legend.module.*;
import stencil.legend.DynamicStencilLegend;
import stencil.legend.StencilLegend;

import java.util.Map;
import java.util.HashMap;

/**Simple MutableModule.  Does not allow specialization of the Legends.*/
public class MutableModule implements Module {
	protected Map<String, StencilLegend> legends;
	protected MutableModuleData moduleData;

	public MutableModule(String name) {
		legends = new HashMap<String, StencilLegend>();
		moduleData = new MutableModuleData(this, name);
	}

	public ModuleData getModuleData() {return moduleData;}

	//TODO: allow use of non-default specializer
	public LegendData getOperatorData(String name, Specializer specializer) throws SpecializationException {
		Specializer defaultSpecailizer = getModuleData().getDefaultSpecializer(name);
		if (!specializer.equals(defaultSpecailizer)) {throw new SpecializationException(getName(),name, specializer);}
		return moduleData.getOperatorData(name);		
	}

	public StencilLegend instance(String name, Specializer specializer) throws SpecializationException {
		if (!specializer.isSimple()) {
			throw new SpecializationException(getName(),name, specializer);
		}
		
		return legends.get(name);
	}

	public void addOperator(DynamicStencilLegend target) {
		try {
			addOperator(target, target.getLegendData(null));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error adding jython legend %1$s to module %2$s", target.getName(), getModuleData().getName()), e);
		}
	}
	
	public void addOperator(StencilLegend target, LegendData legendData) {
		legends.put(target.getName(), target);
		moduleData.addOperator(legendData);
	}
	
	public String getName() {return moduleData.getName();}
}
