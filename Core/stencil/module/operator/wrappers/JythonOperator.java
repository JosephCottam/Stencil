package stencil.module.operator.wrappers;

import java.util.Map;
import java.util.HashMap;


import static stencil.parser.ParserConstants.*;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.*;
import stencil.parser.tree.PythonFacet;


//TODO: Add guide support (again)
public class JythonOperator implements StencilOperator {
	protected String operatorName;
	protected String module;
	protected Map<String, Invokeable<Tuple>> invokeables;
	protected OperatorData operatorData;
	
	public JythonOperator(String module, String name) {
		this.module = module;
		this.operatorName = name;
		invokeables = new HashMap<String, Invokeable<Tuple>>();
		operatorData = new OperatorData(module, name, BASIC_SPECIALIZER);
	}
		
	public String getName() {return operatorName;}

	/**What is the legend data object for this encapsulation? 
	 * This is not stored internally, so updates must be acquired by invoking
	 * this method again.
	 *
	 * @return LegendData object reflecting the current state of the Jython Legend
	 * @throws SpecializationException
	 */
	public OperatorData getOperatorData() {
		return operatorData;		
	}
	
	public void add(JythonEncapsulation enc, PythonFacet f) {
		invokeables.put(enc.getName(), new ReflectiveInvokeable<JythonEncapsulation, Tuple>(enc.getInvokeMethod(), enc));
		String opType = f.getAnnotations().get("TYPE").getText();
		String[] fields = TuplePrototypes.getNames(enc.getReturns());
		FacetData data = new FacetData(f.getName(), opType, false, fields);
		operatorData.addFacet(data);		
	}

	public Invokeable<Tuple> getFacet(String name) throws IllegalArgumentException {
		if (invokeables.containsKey(name)) {return invokeables.get(name);}
		throw new IllegalArgumentException(String.format("Method named '%1$s' not know in legend %2$s", name, operatorName));
	}
	
	//TODO: Can we do something with all of the encapsulations to duplicate them and support duplicate?
	public JythonOperator duplicate() {throw new UnsupportedOperationException();}
}
