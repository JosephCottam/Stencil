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
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.SeedOperator;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.FacetData;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.module.util.FacetData.MemoryUse;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.FALSE_STRING;


/**Operators used in various stencil transformations.*/
public class StencilUtil extends BasicModule {

	/**Rename the components of a tuple with new names; 
	 * like an echo but with variable names updated in the prototype.
	 */
	public static final class Rename extends AbstractOperator {
		private static String NAMES = "names";

		private final String[] keys;

		public Rename(OperatorData opData, Specializer specializer) {
			super(opData);
			keys = getNames(specializer);
		}

		public Rename(OperatorData opData, String...keys) {
			super(opData);
			this.keys = keys;
		}

		
		public Tuple map(Object... values) {return query(values);}
		public Tuple query(Object... values) {
			assert keys.length == values.length : "Keys and values lengths do not match.";
			
			return new ArrayTuple(values);
		}
		
		public Rename duplicate() {return new Rename(operatorData, keys);}
		
		private static String[] getNames(Specializer spec) {
			return spec.get(NAMES).getText().split("\\s+,\\s+");
		}
		
		public static OperatorData complete(OperatorData base, Specializer spec) {
			OperatorData od = new OperatorData(base);
			String[] keys  = getNames(spec);
			FacetData fd = od.getFacet(StencilOperator.MAP_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.FUNCTION, keys);
			od.addFacet(fd);
			
			fd = od.getFacet(StencilOperator.QUERY_FACET);
			fd = new FacetData(fd.getName(), MemoryUse.FUNCTION, keys);
			od.addFacet(fd);
			return od;
		}
		
	}

	
	public static abstract class SeedBase extends AbstractOperator implements  SeedOperator, Cloneable {
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
		
		public Tuple query(Object... args) {return Tuples.EMPTY_TUPLE;}
	}

	
	/**Returns what was passed in, but records the range of elements
	 * seen.  Used for continuous operators and should probably never
	 * be used directly.	 */
	public static final class SeedContinuous extends SeedBase {
		public static final String NAME = SeedContinuous.class.getSimpleName();
		public static final String MAX_KEY = "max";
		public static final String MIN_KEY = "min";
		public static final String LOCK_KEY = "lock";

		private double max = Double.MIN_VALUE;	//Largest value in last reporting cycle
		private double min = Double.MAX_VALUE;	//Smallest value in last reporting cycle
		private final boolean rangeLock;

		public SeedContinuous(OperatorData opData, boolean lock) {super(opData); this.rangeLock=lock;}
		public SeedContinuous(OperatorData opData, Specializer spec) throws SpecializationException {
			super(opData);
			
			Map<String, Atom> map = spec.getMap();
			if (map.containsKey(MAX_KEY)) {max = Converter.toDouble(map.get(MAX_KEY));}
			if (map.containsKey(MIN_KEY)) {min = Converter.toDouble(map.get(MIN_KEY));}
			rangeLock = map.containsKey(LOCK_KEY) && map.get(LOCK_KEY).getValue().equals(FALSE_STRING);
		}
		
		public StencilOperator duplicate() {return new SeedContinuous(operatorData, rangeLock);}

		public SampleSeed getSeed() {
			synchronized(this) {return new SampleSeed(true, min, max);}
		}

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
	}
	
	/**Returns exactly what it was passed, but
	 * records elements seen (for categorization).
	 */
	public static final class SeedCategorize extends SeedBase {
		public static final String NAME = SeedCategorize.class.getSimpleName();
		
		private final List<Object[]> seen = new CopyOnWriteArrayList();
				
		public SeedCategorize(OperatorData opData) {super(opData);}
		public SeedCategorize(OperatorData opData, Specializer s) throws SpecializationException {super(opData);}
		public StencilOperator duplicate() throws UnsupportedOperationException {return new SeedCategorize(operatorData);}
		
		public synchronized SampleSeed getSeed() {return new SampleSeed(false, new ArrayList(seen));}

		public Tuple map(Object... args) {
			if (!deepContains(seen, args)) {seen.add(args);}
			return Tuples.EMPTY_TUPLE;
		}
		
		private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
			for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
			return false;
		}
		
		public int stateID() {return seen.size();}
	}
	
	public StencilUtil(ModuleData md) {super(md);}

	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		if (name.equals("Rename")) {return Rename.complete(od, specializer);}
		od = SeedBase.complete(od, specializer);

		if (od.isComplete()) {return od;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, od);
	}
}
