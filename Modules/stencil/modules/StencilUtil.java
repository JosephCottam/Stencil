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
package stencil.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.SeedOperator;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.FacetData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.FALSE_STRING;

/**Operators used in various stencil transformations.*/
@Module
@Description("Utilities to manipulate the tuple data representation.")
public class StencilUtil extends BasicModule {
	
	@Operator(spec="[range: ALL, split: 0]")
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Use the Converter to change the value(s) passed into a tuple.")
	public static final Tuple toTuple(Object... values) {return Converter.toTuple(values);} 
	
	@Operator(spec="[range: ALL, split: 0]")
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Repackage the the values passed as a tuple (simple wrapping, no conversion attempted).")
	public static final Tuple valuesTuple(Object... values) {return new ArrayTuple(values);} 

	
	public static abstract class SeedBase extends AbstractOperator.Statefull implements SeedOperator, Cloneable {
		protected SeedBase(OperatorData opData) {super(opData);}
						
		/**Complete the operator data, given the specializer.*/
		protected static OperatorData complete(OperatorData base, Specializer spec) {
			OperatorData od = new OperatorData(base);
			FacetData fd = od.getFacet(StencilOperator.MAP_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.WRITER, new String[0]);
			od.addFacet(fd);
			
			fd = od.getFacet(StencilOperator.QUERY_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.WRITER, new String[0]);
			od.addFacet(fd);
			
			return od;
		}

		public abstract Tuple map(Object...args);
		
		@Facet(memUse="OPAQUE", prototype="(VALUE)")
		public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}

		@Override
		public SampleSeed getSeed() {throw new RuntimeException("No implemented.");}
		public SeedBase viewpoint() {return this;}
	}

	
	/**Returns what was passed in, but records the range of elements
	 * seen.  Used for continuous operators and should probably never
	 * be used directly.*/
	@Operator(spec="[range: ALL, split: 0]")
	public static final class SeedContinuous extends SeedBase {
		public static final String NAME = SeedContinuous.class.getSimpleName();
		public static final String MAX_KEY = "max";
		public static final String MIN_KEY = "min";
		public static final String LOCK_KEY = "lock";

		private double max = Double.MIN_VALUE;	/**Largest value in last reporting cycle*/
		private double min = Double.MAX_VALUE;	/**Smallest value in last reporting cycle*/
		private final boolean rangeLock;

		public SeedContinuous(OperatorData opData, boolean lock) {super(opData); this.rangeLock=lock;}
		public SeedContinuous(OperatorData opData, Specializer spec) throws SpecializationException {
			super(opData);
			
			if (spec.containsKey(MAX_KEY)) {max = Converter.toDouble(spec.get(MAX_KEY));}
			if (spec.containsKey(MIN_KEY)) {min = Converter.toDouble(spec.get(MIN_KEY));}
			rangeLock = spec.containsKey(LOCK_KEY) && spec.get(LOCK_KEY).equals(FALSE_STRING);
		}
		
		public StencilOperator duplicate() {return new SeedContinuous(operatorData, rangeLock);}

		public SampleSeed getSeed() {
			synchronized(this) {return new SampleSeed(true, min, max);}
		}

		@Facet(memUse="OPAQUE", prototype="(VALUE)")
		public Tuple map(Object... args) {
			assert args.length == 1;
			double value = Converter.toNumber(args[0]).doubleValue();
			
			if (!rangeLock) {
			
				double oldMax = max;
				double oldMin = min;
				
				synchronized(this) {
					max = Math.max(value, max);
					min = Math.min(value, min);
				}
				if ((max != oldMax) || (min != oldMin)) {
					stateID++;
				}
			}
			
			return Tuples.EMPTY_TUPLE;
		}
		
		public SeedContinuous viewpoint() {
			SeedContinuous rv = new SeedContinuous(operatorData, rangeLock);
			rv.max = this.max;
			rv.min = this.min;
			try {return (SeedContinuous) clone();}
			catch (Exception e) {throw new Error("Error making viewpoint of seed operator.");}
		}
	}
	
	/**Returns exactly what it was passed, but
	 * records elements seen (for categorization).*/
	@Operator(spec="[range: ALL, split: 0]")	
	public static final class SeedCategorize extends SeedBase {
		public static final String NAME = SeedCategorize.class.getSimpleName();
		
		private final List<Object[]> seen = new CopyOnWriteArrayList();
				
		public SeedCategorize(OperatorData opData) {super(opData);}
		public SeedCategorize(OperatorData opData, Specializer s) throws SpecializationException {super(opData);}
		public StencilOperator duplicate() throws UnsupportedOperationException {return new SeedCategorize(operatorData);}
		
		public synchronized SampleSeed getSeed() {return new SampleSeed(false, new ArrayList(seen));}

		@Facet(memUse="OPAQUE", prototype="(VALUE)")
		public Tuple map(Object... args) {
			if (!deepContains(seen, args)) {seen.add(args);}
			return Tuples.EMPTY_TUPLE;
		}
		
		private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
			for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
			return false;
		}
		
		@Facet(memUse="READER", prototype="(int VALUE)")		
		public int stateID() {return seen.size();}
		
		public SeedCategorize viewpoint() {
			try {return (SeedCategorize) clone();}
			catch (Exception e) {throw new Error("Error making viewpoint of seed operator.");}
		}
	}
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		od = SeedBase.complete(od, specializer);

		if (od.isComplete()) {return od;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, od);
	}	
}
