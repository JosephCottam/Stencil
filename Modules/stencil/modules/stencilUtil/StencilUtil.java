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
package stencil.modules.stencilUtil;


import java.util.*;

import stencil.module.MethodInstanceException;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.module.util.ModuleDataParser.MetaDataParseException;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;

import static stencil.module.util.ModuleDataParser.operatorData;
import static stencil.module.util.ModuleDataParser.moduleData;
import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;


/**Operators used in various stencil transformations.*/
@Module
@Description("Utilities to manipulate the tuple data representation.")
public final class StencilUtil extends BasicModule {
	/**Some modules provide optimizations for some range sets.  
	 * If an operator instance is range optimized, it must include this tag in its operator data to indicate
	 * that no further ranging is required.  
	 * If this tag is included, the range wrapper factory will return the passed operator without further wrapping.
	 */
	public static final String RANGE_OPTIMIZED_TAG = "#__RANGE_OPTIMIZED";
	
	/**Some operators can be efficiently implemented for ranging if the list of lists of formals
	 * of arguments are first converted is combined into a single list of formals.  The conversion
	 * is called "flattening" and this tag indicates that it should be done.  This is the only way
	 * that a non-mutative facet can be used in range.
	 */
	public static final String RANGE_FLATTEN_TAG = "#__RANGE_FLATTEN";

	
	/**Indicates that range is explicitly disallowed.*/
	public static final String RANGE_DISALLOW_TAG = "#__NO_RANGE_ALLOWED";

	/**Indicates that range is explicitly disallowed.*/
	public static final String MAP_DISALLOW_TAG = "#__NO_MAP_ALLOWED";

	/**Indicates that split is explicitly disallowed.*/
	public static final String DISALLOW_TAG = "#__NO_SPLIT_ALLOWED";
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Use the Converter to change the value(s) passed into a tuple.")
	public static final Tuple toTuple(Object... values) {return Converter.toTuple(values);} 
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Repackage the the values passed as a tuple (simple wrapping, no conversion attempted).")
	public static final Tuple valuesTuple(Object... values) {return new ArrayTuple(values);} 

	@Override
	protected ModuleData loadOperatorData() throws MetaDataParseException {
		final String MODULE_NAME = this.getClass().getSimpleName();
		
		ModuleData md = moduleData(this.getClass());	//Loads the meta-data for operators defined in this class
		
		
		md.addOperator(operatorData(SeedCategorize.class, MODULE_NAME));
		md.addOperator(operatorData(SeedContinuous.class, MODULE_NAME));

		md.addOperator(operatorData(MapWrapper.class, MODULE_NAME));
		md.addOperator(operatorData(RangeHelper.class, MODULE_NAME));
		md.addOperator(operatorData(SplitHelper.class, MODULE_NAME));
		
		return md;
	}
	
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		//HACK: Avoid the direct name-based stuff...its difficult to maintain
		if (od.getName().contains("Seed")) {od = SeedBase.complete(od, specializer);}
		
		if (od.isComplete()) {return od;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, od);
	}
	
	public StencilOperator instance(String name, Context context, Specializer specializer) throws SpecializationException {
		if (name.equals("ToTuple") || name.equals("ValuesTuple")) {
			return super.instance(name, context, specializer);
		} else if (name.equals("SeedCategorize")) {
			OperatorData operatorData = getOperatorData(name, specializer);
			return new SeedCategorize(operatorData, specializer);
		} else if (name.equals("SeedContinuous")) {
			OperatorData operatorData = getOperatorData(name, specializer);
			return new SeedContinuous(operatorData, specializer);			
		} 
		
		throw new Error("Could not instantiate regular operator " + name);
	}

	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException {
		List<StencilOperator> opArgs = new ArrayList();

		
		//TODO: This is a horrible way to resolve things, have the operator as value in a CONST in the specializer instead of the name
		for (String key: specializer.keySet()) {
			if (key.startsWith("Op")) {
				StencilOperator op;
				try {
					op = modules.instance("", (String) specializer.get(key), null, EMPTY_SPECIALIZER, false);
					opArgs.add(op);
				} catch (MethodInstanceException e) {throw new IllegalArgumentException("Error instantiate operator-as-argument " + specializer.get(key), e);}
			}
		}
		
		assert opArgs.size() >0;
		if (opArgs.size() >1) {throw new IllegalArgumentException(name + " can only accept one higher order arg, recieved " + opArgs.size());}
		
		
		if (name.equals("Map")) {
			return new MapWrapper(opArgs.get(0));
		} else if (name.equals("Range")) {
			//TODO: Fix Range helper so it works for stateful operators CORRECTLY
					//Use the duplicate method for stateful operators, instantiate a new operator for each call 
					//Stateless operators can just have the range values applied directly (after conversion)
			return RangeHelper.makeOperator(specializer, opArgs.get(0));	
		} else if (name.equals("Split")) {
			return SplitHelper.makeOperator(specializer, opArgs.get(0));			
		}
		
		throw new Error("Could not instantiate higher-order operator " + name);
	}
}
