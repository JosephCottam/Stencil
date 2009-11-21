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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.operator.StencilOperator;
import stencil.operator.module.FacetData;
import stencil.operator.module.ModuleData;
import stencil.operator.module.OperatorData;
import stencil.operator.module.SpecializationException;
import stencil.operator.module.util.BasicFacetData;
import stencil.operator.module.util.BasicModule;
import stencil.operator.module.util.MutableOperatorData;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.StencilNumber;
import stencil.parser.tree.StencilString;
import stencil.parser.tree.Value;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;

public class StencilUtil extends BasicModule {
	private static abstract class EchoBase implements StencilOperator{
		final String[] names;
		
		protected EchoBase(String... names) {this.names = names;}
		protected EchoBase(Specializer s) throws SpecializationException {
			if (s.getArgs().get(0) instanceof StencilNumber && s.getArgs().size() ==1) {
				names = Tuples.defaultNames(((StencilNumber) s.getArgs().get(0)).getValue().intValue(),null);
			} else if (s.getArgs().size() >0){
				names = new String[s.getArgs().size()];
				Atom atom = s.getArgs().get(0);
				for (int i=0; i< names.length; atom = s.getArgs().get(++i)) {
					if (atom instanceof StencilString) {
						names[i] = atom.getText();
					} else {
						throw new SpecializationException("StencilUtil", getName(), s);
					}
				}
			} else {
				throw new SpecializationException("StencilUtil", getName(), s);				
			}
		}
		
		/**Does this legend accept the given specializer?*/
		protected static boolean accepts(Specializer specializer) {
			return specializer.getRange().isFullRange() && 
				   ((specializer.getArgs().get(0) instanceof StencilNumber && specializer.getArgs().size() ==1) ||
					(specializer.getArgs().get(0) instanceof StencilString));
		}
		
		/**Complete the legend data, given the specializer.*/
		protected static OperatorData complete(OperatorData base, Specializer spec) {
			MutableOperatorData ld = new MutableOperatorData(base);
			FacetData fd = ld.getFacetData("Map");
			fd = new BasicFacetData(fd.getName(), fd.getFacetType(), spec.getArgs());
			ld.addFacet(fd);
			
			fd = ld.getFacetData("Query");
			fd = new BasicFacetData(fd.getName(), fd.getFacetType(), spec.getArgs());
			ld.addFacet(fd);
			
			return ld;
			
		}
	}

	
	/**Returns what was passed in, but records the range of elements
	 * seen.  Used for continuous legends and should probably never
	 * be used directly.
	 * 
	 */
	public static final class EchoContinuous extends EchoBase {
		public static final String NAME = "EchoContinuous";

		private boolean useIntegers = false;
		private int tickCount = 10;
		
		private double max = Double.MIN_VALUE;	//Largest value in last reporting cycle
		private double min = Double.MAX_VALUE;	//Smallest value in last reporting cycle
		private boolean expanded = false;				//TODO: This strategy can result in a missed update. 
		
		protected EchoContinuous(String... names) {super(names);}
		
		public EchoContinuous(Specializer spec) throws SpecializationException {
			super(spec);
		}
		
		public StencilOperator duplicate() throws UnsupportedOperationException {
			return new EchoContinuous(names);
		}

		public String getName() {return NAME;}

		public List<Object[]> guide(List<Value> formalArguments, List<Object[]> sourceArguments, List<String> prototype) {
			assert sourceArguments == null && prototype == null : "Non-null sourceArgument or prototpye passed to echo operator's Guide facet.";
			
			expanded = false;
			
			List ranges = buildRange(max, min, tickCount, useIntegers);
			
			return ranges;
		}

		public Tuple map(Object... args) {
			assert args.length == 1;
			assert args[0] instanceof Number : "Expected Number, recieved " + args[0].getClass().getName();
			
			double value = ((Number) args[0]).doubleValue();
			
			double oldMax = max;
			double oldMin = min;
			
			max = Math.max(value, max);
			min = Math.min(value, min);
			
			expanded = expanded || (max != oldMax) || (min != oldMin);
			
			return new PrototypedTuple(names, args);
		}

		public Tuple query(Object... args) {return new PrototypedTuple(names, args);}

		public boolean refreshGuide() {return expanded;}
		
		
		private static List<Number[]> buildRange(double max, double min, int tickCount, boolean useIntegers) {
			double range = niceNum(max-min, false);							//'Nice' range
			double spacing = niceNum(range/(tickCount-1), true);			//'Nice' spacing;
			if (spacing < 1) {spacing =1;}									//Ensure some spacing occurs
			double graphMin = Math.floor(min/spacing) * spacing;			//Smallest value on the graph
			double graphMax = Math.ceil(max/spacing) * spacing;				//Largest value on the graph
			List<Number[]> nums = new ArrayList();
			
			for (double v=graphMin; v<(graphMax+.5*spacing); v+=spacing) {
				nums.add(new Number[]{v});
			}
			
			return nums;
		}
		

		
		/**Finds a multiple of 1,2 or 5 or a power of 10 near the passed number.
		 * 
		 * From: Graphic Gems, "Nice Numbers for Graph Labels," by Paul Heckbert*/
		private static double niceNum(double num, boolean round) {
			int exp;
			double f;
			double nf;
			
			exp = (int) Math.floor(Math.log10(num));
			f = num/Math.pow(10, exp);
			if (round) {
				if (f< 1.5) {nf=1;}
				else if (f<3) {nf=2;}
				else if (f<7) {nf=5;}
				else {nf=10;}
			} else {
				if (f<=1) {nf=1;}
				else if (f<=2) {nf=2;}
				else if (f<=5) {nf=5;}
				else {nf=10;}
			}		
			return (float) (nf*Math.pow(10, exp));
		}
	}
	
	/**Returns exactly what it was passed, but
	 * records elements seen (for categorization).
	 * 
	 * TODO: Merge with a generalized echo. Requires better 'split' semantics
	 */
	public static final class EchoCategorize extends EchoBase {
		private final List<Object[]> seen = new ArrayList();
		private int priorCount;								//TODO: This strategy can result in a missed update.
				
		public EchoCategorize(String...names) {super(names);}
		public EchoCategorize(Specializer s) throws SpecializationException {super(s);}
		public StencilOperator duplicate() throws UnsupportedOperationException {return new EchoCategorize(names);}

		public final String getName() {return "Echo";}

		public synchronized List guide(List<Value> formalArguments, List<Object[]> sourceArguments, List<String> prototype) {
			assert sourceArguments == null && prototype == null : "Non-null sourceArgument or prototpye passed to categorize operator's Guide facet.";
			
			priorCount = seen.size();
			
			return new ArrayList(seen); 
		}

		public Tuple map(Object... args) {
			if (!deepContains(seen, args)) {seen.add(args);}
			return new PrototypedTuple(names, args);
		}
		
		private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
			for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
			return false;
		}

		public Tuple query(Object... args) {
			return new PrototypedTuple(names, args);
		}

		public synchronized boolean refreshGuide() {
			return seen.size() != priorCount;
		}
	}

	public StencilUtil(ModuleData md) {super(md);}


	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		
		if (!EchoBase.accepts(specializer)) {throw new SpecializationException(moduleData.getName(), name, specializer);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData ld = moduleData.getOperatorData(name);

		ld = EchoBase.complete(ld, specializer);

		if (ld.isComplete()) {return ld;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		
		if (name.equals("EchoCategorize")) {return new EchoCategorize(specializer);}
		else if (name.equals("EchoContinuous")) {return new EchoContinuous(specializer);}

		throw new RuntimeException(String.format("Legend name not known: %1$s.", name));
	}
}
