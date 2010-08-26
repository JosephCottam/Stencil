package stencil.module.operator.wrappers;

import java.util.Map;
import java.util.HashMap;


import static stencil.parser.ParserConstants.*;

import stencil.tuple.Tuple;
import stencil.tuple.prototype.TuplePrototypes;
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

	/**What is the meta data for this encapsulation?*/
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
		if (name.equals("query")) {return invokeables.get("map");}	//HACK: Default to 'map' if 'query' wasn't found.
		throw new IllegalArgumentException(String.format("Method named '%1$s' not know in operator %2$s", name, operatorName));
	}
	
	//TODO: Can we do something with all of the encapsulations to duplicate them and support duplicate?
	public JythonOperator duplicate() {throw new UnsupportedOperationException();}

	//TODO: Implement properly
	//HACK: This keeps things running, but if the jython encapsulation is used the correctness guarantee cannot be made.
	public JythonOperator viewPoint() {
		return this;
	}
}
