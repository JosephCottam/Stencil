/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.module.operator.wrappers;

import java.util.Arrays;

import stencil.interpreter.Interpreter;
import stencil.interpreter.tree.Freezer;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.UnknownFacetException;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.util.ReflectiveInvokeable;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
import stencil.display.Display;
import stencil.interpreter.tree.OperatorFacet;
import stencil.interpreter.tree.OperatorRule;
import stencil.interpreter.tree.StateQuery;
import stencil.parser.tree.StencilTree;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.tuple.prototype.TuplePrototype;

import static stencil.parser.string.StencilParser.*;

/**Operator defined through a stencil definition.
 * 
 * TODO: These aren't always projects...but they are for now!
 * */
public class SyntheticOperator implements StencilOperator {
	/**Exception to indicate that no rule matches the parameters passed to the given synthetic operator.*/
	public static class NoMatchException extends RuntimeException {
		public NoMatchException(String message) {super(message);}
	}
	
	protected final StencilTree opDef;
			
	protected StateQuery stateQuery;
	protected OperatorFacet map;
	protected OperatorFacet query;
	
	protected final OperatorData operatorData;

	protected final String module;
	
	/**Create a Stencil operator from a specification.
	 * TODO: Some synthetic operators facets are functions...figure out a way to detect this!
	 * */
	public SyntheticOperator(String module, StencilTree opDef) {
		this.module = module;
		this.opDef = opDef;

		OperatorData opData = new OperatorData(module, opDef.getText(), EMPTY_SPECIALIZER, null);
		
		opData.addFacet(new FacetData(MAP_FACET, MemoryUse.WRITER, findPrototype(MAP_FACET)));	
		opData.addFacet(new FacetData(QUERY_FACET, MemoryUse.READER, findPrototype(QUERY_FACET)));	
		opData.addFacet(new FacetData(STATE_ID_FACET, MemoryUse.READER, "VALUE"));
		
		this.operatorData = opData;
	}
	private SyntheticOperator(String module, OperatorData opData) {
		this.module = module;
		this.operatorData = opData;
		this.opDef = null;
	}
	
	private TuplePrototype findPrototype(String name) {
		return Freezer.prototype(findFacet(name).find(YIELDS).getChild(1));
	}
	private StencilTree findFacet(String name) {
		for(StencilTree facet: opDef.findAll(OPERATOR_FACET)) {
			if (facet.getText().equals(name)) {return facet;}
		}
		if (operatorData == null) {
			throw new Error(String.format("Facet %1$s presented without required name: %2$s.", opDef.getText(), name));
		} else {
			throw new UnknownFacetException(opDef.getText(), name, operatorData.getFacetNames());
		}
		
	}

	public Invokeable getFacet(String name) throws UnknownFacetException {
		try {
			if (name.equals(StencilOperator.MAP_FACET) 
				|| name.equals(StencilOperator.QUERY_FACET)
			    || name.equals(StencilOperator.STATE_ID_FACET)) {
				return new ReflectiveInvokeable(name, this);
			}
		} catch (Exception e) {throw new RuntimeException("Exception while creating invokeable for standard method", e);}
		throw new UnknownFacetException(operatorData.getName(), name, operatorData.getFacetNames());
	}

	public String getName() {return operatorData.getName();}
	public OperatorData getOperatorData() {return operatorData;}
	
	public Tuple query(Object... values) {return process(getQuery(), values);}
	private OperatorFacet getQuery() {
		if (query == null) {query = Freezer.operatorFacet(operatorData.getName(), findFacet(QUERY_FACET));}
		return query;
	}

	public Tuple map(Object... values) {return process(getMap(), values);}
	private OperatorFacet getMap() {
		if (map == null) {map = Freezer.operatorFacet(operatorData.getName(), findFacet(MAP_FACET));}
		return map;
	}
	
	public int stateID(Object... values) {return getStateQuery().compositeStateID();}
	private StateQuery getStateQuery() {
		if (stateQuery == null) {stateQuery = Freezer.stateQuery(opDef.find(STATE_QUERY));}
		return stateQuery;
	}
	
	//TODO: Can we do something to support duplicate here?  Maybe the 'pristine clone' trick?
	public SyntheticOperator duplicate() {throw new UnsupportedOperationException();}
	public SyntheticOperator viewpoint() {
		SyntheticOperator rv = new SyntheticOperator(module, operatorData);
		rv.query = getQuery().viewpoint();
		rv.map = getMap().viewpoint();
		rv.stateQuery = getStateQuery().viewpoint();
		return rv;
	}

	private Tuple process(OperatorFacet facet, Object... values) {
		if (facet.getArguments().size() != values.length) {
			int expected = facet.getArguments().size();
			throw new IllegalArgumentException(String.format("Incorrect number of arguments passed to synthetic operator.  Expected %1$s.  Recieved %2$d arguments.", expected, values.length));
		}
		Tuple prefilter;
		Tuple tuple = new ArrayTuple(values);
		Environment env = Environment.getDefault(Display.canvas, Display.view, Tuples.EMPTY_TUPLE, tuple);//Empty between view and stream tuple is the globals frame;  TODO: Replace with globals when runtime global exist
		
		try {prefilter = Interpreter.processEnv(env, facet.getPrefilterRules());}
		catch (Exception e) {throw new RuntimeException(String.format("Error with prefilter in %1$s.%2$s and tuple %3$s.", operatorData.getName(), facet.getName(), tuple.toString()));}
		env.setFrame(Environment.PREFILTER_FRAME, prefilter);
		
		OperatorRule action = matchingAction(facet, env);

		if (action != null) {
			try {
				 Tuple result = action.invoke(env);
				 result = Tuples.align(result, facet.getResults());
				 return result;
			} catch (Exception e) {
				throw new RuntimeException (String.format("Error executing method in %1$s.%2$s.", operatorData.getName(), facet.getName()),e);
			}
		}
		
		throw new NoMatchException("No rule to match " + Arrays.deepToString(values));
	}		
			
	/**Find the action that matches the given tuple.  If none does, return null.*/
	private OperatorRule matchingAction(OperatorFacet facet, Environment tuple) {
		for (OperatorRule action: facet.getRules()) {
			if (action.matches(tuple)) {
				return action;
			}
		}
		return null;
	}
}
