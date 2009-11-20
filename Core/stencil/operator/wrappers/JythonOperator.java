package stencil.operator.wrappers;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


import static stencil.operator.util.BasicProject.packArguments;
import static stencil.parser.ParserConstants.*;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.operator.DynamicStencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.OperatorData.OpType;
import stencil.operator.module.util.*;
import stencil.operator.util.Invokeable;
import stencil.parser.tree.PythonFacet;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.Value;
import static stencil.parser.ParserConstants.MAIN_BLOCK_TAG;
import static stencil.parser.ParserConstants.QUERY_BLOCK_TAG;


public class JythonOperator implements DynamicStencilOperator {
	protected String operatorName;
	protected String module;
	protected Map<String, Invokeable<JythonEncapsulation, Tuple>> invokeables;
	protected MutableOperatorData operatorData;
	
	public JythonOperator(String module, String name) {
		this.module = module;
		this.operatorName = name;
		invokeables = new HashMap<String, Invokeable<JythonEncapsulation, Tuple>>();
		operatorData = new MutableOperatorData(module, name, SIMPLE_SPECIALIZER);
	}
		
	public String getName() {return operatorName;}

	public Tuple map(Object... args) {return getFacet(MAIN_BLOCK_TAG).invoke(args);}
	public Tuple query(Object... args) {return getFacet(QUERY_BLOCK_TAG).invoke(args);}

	/**What is the legend data object for this encapsulation? 
	 * This is not stored internally, so updates must be acquired by invoking
	 * this method again.
	 *
	 * @return LegendData object reflecting the current state of the Jython Legend
	 * @throws SpecializationException
	 */
	public OperatorData getOperatorData(Specializer spec) throws SpecializationException {
		if (spec !=null && !spec.equals(operatorData.getDefaultSpecializer())) {throw new SpecializationException(module, getName(), spec);}
		return operatorData;		
	}
	
	public void add(JythonEncapsulation enc, PythonFacet f) {
		invokeables.put(enc.getName(), new Invokeable<JythonEncapsulation, Tuple>(enc.getInvokeMethod(), enc));
		
		FacetData data = new BasicFacetData(f.getName(), f.getAnnotation("Type"),  enc.getReturnLabels());
		operatorData.addFacet(data);		
	}

	public Invokeable<JythonEncapsulation, Tuple> getFacet(String name) throws IllegalArgumentException {
		if (invokeables.containsKey(name)) {return invokeables.get(name);}
		throw new IllegalArgumentException(String.format("Method named '%1$s' not know in legend %2$s", name, operatorName));
	}

	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {
		if (!invokeables.containsKey(GUIDE_BLOCK_TAG)) {throw new UnsupportedOperationException("Guide block not defined in jython operator.");}
		Invokeable<JythonEncapsulation, Tuple> inv = getFacet(GUIDE_BLOCK_TAG);
		JythonEncapsulation enc = inv.getTarget();
		OpType guideType = OpType.valueOf(enc.getAnnotation("TYPE"));
		
		if (guideType == OpType.CATEGORIZE) {
			Tuple t = inv.invoke(new Object[0]); //TODO: Verify somewhere that this block always takes zero arguments
			return (List) t.get(t.getPrototype().get(0));	//TODO: Verify that this block always returns one value
		} else if (guideType == OpType.PROJECT) {
			Object[] results = new Object[sourceArguments.size()];
			int i=0;
			for (Object[] source: sourceArguments) {
				Object[] actual = packArguments(formalArguments, source, prototype);
				Tuple t = inv.invoke(actual);
				results[i++] = Tuples.toArray(t);
			}
			
			return Arrays.asList(results);			
		} else {
			throw new RuntimeException(String.format("Annotation on guide method not handled.  Please use PROJECT or CATEGORIZE (was %1$s).", guideType));
		}
	}

	public boolean refreshGuide() {
		if (invokeables.containsKey(DO_GUIDE_BLOCK_TAG) && invokeables.containsKey(GUIDE_BLOCK_TAG)) {
			Tuple t = getFacet(DO_GUIDE_BLOCK_TAG).invoke(new Object[0]); //TODO: Verify somewhere that this block always takes zero arguments
			return t.get(t.getPrototype().get(0)).equals("TRUE");	//TODO: Verify that this block always returns one value
		}
		throw new UnsupportedOperationException(String.format("Must supply '%1$s' and '%2$s' facets to use python block in a guide.", DO_GUIDE_BLOCK_TAG,GUIDE_BLOCK_TAG));
	}


	//TODO: Can we do something with all of the encapsulations to duplicate them and support duplicate?
	public JythonOperator duplicate() {throw new UnsupportedOperationException();}

}
