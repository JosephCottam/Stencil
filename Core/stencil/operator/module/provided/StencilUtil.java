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
package stencil.operator.module.provided;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.guide.SeedOperator;
import stencil.operator.StencilOperator;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.FacetData;
import stencil.operator.module.util.ModuleData;
import stencil.operator.module.util.OperatorData;
import stencil.operator.util.Invokeable;
import stencil.operator.util.ReflectiveInvokeable;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.tuple.ArrayTuple;
import stencil.tuple.Tuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import static stencil.parser.ParserConstants.FALSE_STRING;

public class StencilUtil extends BasicModule {
	public static abstract class EchoBase implements StencilOperator, SeedOperator {
		private final String FIELDS = "fields";
		
		final OperatorData operatorData;
		final TuplePrototype samplePrototype;
		
		protected final Object STATE_LOCK = new Object();
		protected int stateID=Integer.MIN_VALUE; 
		
		protected EchoBase(OperatorData opData, TuplePrototype p) {
			operatorData = opData;
			samplePrototype = p;
		}
		
		protected EchoBase(OperatorData opData, Specializer s) throws SpecializationException {
			this.operatorData = opData;
			samplePrototype  = new SimplePrototype(s.get(FIELDS).getText().split("\\s*,\\s*"));
		}
		
		public TuplePrototype getSamplePrototype() {return samplePrototype;}

		public Invokeable getFacet(String facet) {
			try {return new ReflectiveInvokeable(facet, this);}
			catch (Exception e) {
				throw new IllegalArgumentException(format("Facet %1$s not known.", facet),e);
			}
		}

		public OperatorData getOperatorData() {return operatorData;}
				
		/**Complete the legend data, given the specializer.*/
		protected static OperatorData complete(OperatorData base, Specializer spec) {
			OperatorData ld = new OperatorData(base);
			FacetData fd = ld.getFacet(StencilOperator.MAP_FACET);
			fd = new FacetData(fd.getName(), fd.getType(), false, new String[0]);
			ld.addFacet(fd);
			
			fd = ld.getFacet(StencilOperator.QUERY_FACET);
			fd = new FacetData(fd.getName(), fd.getType(), false, new String[0]);
			ld.addFacet(fd);
			
			fd = ld.getFacet(StencilOperator.STATE_FACET);
			ld.addFacet(fd);
			
			return ld;
		}
		
		public int stateID() {return stateID;}
	}

	
	/**Returns what was passed in, but records the range of elements
	 * seen.  Used for continuous legends and should probably never
	 * be used directly.
	 * 
	 * TODO: Lift rangeLock out so locked ranges are a separate class...its a much simpler class!
	 * 
	 */
	public static final class EchoContinuous extends EchoBase {
		public static final String NAME = EchoContinuous.class.getSimpleName();
		public static final String MAX_KEY = "max";
		public static final String MIN_KEY = "min";
		public static final String LOCK_KEY = "lock";

		private double max = Double.MIN_VALUE;	//Largest value in last reporting cycle
		private double min = Double.MAX_VALUE;	//Smallest value in last reporting cycle
		private final boolean rangeLock;
		
		public EchoContinuous(OperatorData opData, TuplePrototype p, boolean lock) {super(opData, p); this.rangeLock=lock;}
		public EchoContinuous(OperatorData opData, Specializer spec) throws SpecializationException {
			super(opData, spec);
			
			Map<String, Atom> map = spec.getMap();
			if (map.containsKey(MAX_KEY)) {max = Converter.toDouble(map.get(MAX_KEY));}
			if (map.containsKey(MIN_KEY)) {min = Converter.toDouble(map.get(MIN_KEY));}
			rangeLock = map.containsKey(LOCK_KEY) && map.get(LOCK_KEY).getValue().equals(FALSE_STRING);
		}
		
		public StencilOperator duplicate() {return new EchoContinuous(operatorData, samplePrototype, rangeLock);}

		public String getName() {return NAME;}

		public SampleSeed getSeed() {
			return new SampleSeed(true, min, max);
		}

		public Tuple map(Object... args) {
			assert args.length == 1;
			double value = Converter.toNumber(args[0]).doubleValue();  //TODO: Remove when typed calls work

			if (!rangeLock) {
			
				double oldMax = max;
				double oldMin = min;
				
				synchronized(this) {
					max = Math.max(value, max);
					min = Math.min(value, min);
				}
				if ((max != oldMax) || (min != oldMin)) {synchronized (STATE_LOCK) {stateID++;}}
			}
			
			
			return new ArrayTuple(args);
		}

		public Tuple query(Object... args) {return new ArrayTuple(args);}
	}
	
	/**Returns exactly what it was passed, but
	 * records elements seen (for categorization).
	 */
	public static final class EchoCategorize extends EchoBase {
		public static final String NAME = EchoCategorize.class.getSimpleName();
		
		private final List<Object[]> seen = Collections.synchronizedList(new ArrayList());
				
		public EchoCategorize(OperatorData opData, TuplePrototype p) {super(opData, p);}
		public EchoCategorize(OperatorData opData, Specializer s) throws SpecializationException {super(opData, s);}
		public StencilOperator duplicate() throws UnsupportedOperationException {return new EchoCategorize(operatorData, samplePrototype);}

		public final String getName() {return "Echo";}

		public synchronized SampleSeed getSeed() {
			return new SampleSeed(false, new ArrayList(seen));
		}

		public Tuple map(Object... args) {
			if (!deepContains(seen, args)) {seen.add(args); stateID = seen.size();}
			return new ArrayTuple(args);
		}
		
		private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
			for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
			return false;
		}

		public Tuple query(Object... args) {
			return new ArrayTuple(args);
		}
	}
	
	public StencilUtil(ModuleData md) {super(md);}

	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData ld = moduleData.getOperator(name);

		ld = EchoBase.complete(ld, specializer);

		if (ld.isComplete()) {return ld;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		OperatorData opData = getOperatorData(name, specializer);
		if (name.equals("EchoCategorize")) {return new EchoCategorize(opData, specializer);}
		else if (name.equals("EchoContinuous")) {return new EchoContinuous(opData, specializer);}

		throw new RuntimeException(String.format("Legend name not known: %1$s.", name));
	}
}
