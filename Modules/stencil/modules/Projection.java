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

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.awt.Color;
import org.pcollections.*;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.Modules;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.InvalidNameException;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.types.color.ColorCache;

@Module
public class Projection extends BasicModule {
	
	/**Given a bin-size, determines what bin a particular value falls into.**/
	@Description("Calculates a bin that something falls into.  Bins are fixed size, with a potentially infinite number.  Bin size must be supplied in specializer.")
	@Operator(spec="[size: 1, range:ALL, split: 0]")
	public static class Bin extends AbstractOperator {
		private static final String SIZE="size";
		private final Number binSize;
		
		public Bin(OperatorData opData, Specializer spec) {
			super(opData);
			binSize = Converter.toNumber(spec.get(SIZE));
		}
		
		@Facet(memUse="FUNCTION", prototype="(long bin)", alias={"map","query"})
		public long query(Number value, Number min) {
			if (binSize instanceof Double || value instanceof Double || min instanceof Double) {
				return (long) Math.ceil((value.doubleValue() - min.doubleValue())/binSize.doubleValue());
			} else {
				return (long) Math.ceil((value.longValue() - min.longValue())/binSize.doubleValue());				
			}
		}

	}
	
	/**Projects a range of numbers onto a red/white scale.*/
	@Description("Project between two colors; records the max/min seen.")
	@Operator(spec="[range: ALL, split: 0, cold: \"RED\", hot: \"WHITE\", throw: \"TRUE\"]")
	public static final class HeatScale extends AbstractOperator.Statefull {
		private float min = Float.NaN;
		private float max = Float.NaN;
		
		private final boolean throwExceptions;
		private final Color cold;
		private final Color hot;
		
		public HeatScale(OperatorData opData, Specializer spec) {
			super(opData);
			cold = ColorCache.get(spec.get("cold").toString());
			hot = ColorCache.get(spec.get("hot").toString());
			throwExceptions = (Boolean) Converter.convert(spec.get("throw"), Boolean.class);
		}
		
		private HeatScale(OperatorData opData, Color cold, Color hot, boolean throwException) {
			super(opData);
			this.cold = cold;
			this.hot = hot;
			this.throwExceptions = throwException;
		}
		
		/**Returns a value between Red (low) and White (high) that represents
		 * the percentage of the difference seen between the highest and the lowest value.
		 *
		 * If multiple keys are passed, only the first one is used.
		 * If the key passed cannot be parsed as number and 'throwExceptions' is set to false, black is returned.
		 * If the key passed cannot be parsed and 'throwExceptions' is set to true, an exception is thrown.
		 */
		@Facet(memUse="WRITER", prototype="(Color VALUE)")
		public Color map(float d) {
			float p =-1;			
				
			try {
				if (d>max || Float.isNaN(max)) {max = d; stateID++;}
				if (d<min || Float.isNaN(min)) {min = d; stateID++;}
				if (max == min) {p = 1;}
				else {p = 1-((max-d)/(max-min));}
				return averageColors(p);
			} catch (Exception e) {
				if (throwExceptions) {throw new RuntimeException("Error creating colors with range point:" + p, e);}
				return new Color(0,0,0);
			}
		}
		
		private Color averageColors(float distance) {
//			double[] hotLab = converter.RGBtoLAB(hot.getRed(), hot.getGreen(), hot.getBlue());
//			double[] coldLab = converter.RGBtoLAB(cold.getRed(), cold.getGreen(), cold.getBlue());
//			
//			double L = weightedAverage(hotLab[0], coldLab[0], distance);
//			double a = weightedAverage(hotLab[1], coldLab[1], distance);
//			double b = weightedAverage(hotLab[2], coldLab[2], distance);
//			int alpha = (int) weightedAverage(hot.getAlpha(), cold.getAlpha(), distance);						
//			
//			int[] rgb = converter.LABtoRGB(new double[]{L,a,b});
//		
//			System.out.println(distance);
//			return new java.awt.Color(rgb[0], rgb[1], rgb[2], alpha);

			int r = (int) weightedAverage(hot.getRed(), cold.getRed(), distance);
			int g = (int) weightedAverage(hot.getGreen(), cold.getGreen(), distance);
			int b = (int) weightedAverage(hot.getBlue(), cold.getBlue(), distance);
			int a = (int) weightedAverage(hot.getAlpha(), cold.getAlpha(), distance);						
			return new java.awt.Color(r,g,b,a);
		}  
		
		private double weightedAverage(double v1, double v2, double weight) {
			return (v1 -v2) * weight + v2;
		}

		/**Returns a color value if the first key object is between the current max and min.
		 * Otherwise it returns null.
		 */
		@Facet(memUse="READER", prototype="(Color VALUE)")
		public Color query(float d) {
			if (Float.isNaN(min) && Float.isNaN(max)) {return averageColors(.5f);}
			else if (d < min) {return cold;}
			else if (d > max) {return hot;}
			else              {return map(d);} //value is in the range of requested values, no risk of mutation
		}

		public HeatScale duplicate() {return new HeatScale(operatorData, cold, hot, throwExceptions);} 
	}
	
	/**Projects a set of values onto their presentation order.
	 * 
	 * Retains the order the items were presented in.
	 * If an item has been presented before, the original
	 * presentation rank is returned.  (So the first item
	 * presented always returns index one).  Ordering
	 * is independent for each IndexScale created.
	 */
	@Operator(spec="[range: ALL, split: 0]")
	@Description("Record the original presentation order of the combination of arguments")
	public static final class Index extends AbstractOperator {
		private PVector<String> labels = TreePVector.empty();

		public Index(OperatorData opData) {super(opData);}

		@Facet(memUse="READER", prototype="(double index)")
		public int query(Object key) {
			if (labels.contains(key)) {return labels.indexOf(key);}
			return 0;
		}

		@Facet(memUse="WRITER", prototype="(double index)")
		public int map(Object key) {
			if (!labels.contains(key)) {labels = labels.plus(key.toString());}
			return labels.indexOf(key);
		}

		@Facet(memUse="READER", prototype="(int VALUE)") 
		public int stateID() {return labels.size();}
		public Index duplicate() {return new Index(operatorData);}
	}

	
	/**Keeps count of things that have been seen.  
	 * For mapping, items that have not been seen before return 1 the first time, incremented there-after.
	 * For query, items that have not been seen return 0 and are not added to the map.
	 */

	@Description("Count the number of times the argument combination has been seen (including the current one)")
	@Operator(spec="[range: ALL, split: 0]")
	public static final class Count extends AbstractOperator.Statefull {
		private static final class CompoundKey {
			final Object[] values;
			public CompoundKey(Object... values) {this.values = Arrays.copyOf(values, values.length);}
			public int hashCode() {return Arrays.hashCode(values);}
			public boolean equals(Object o) {
				return o != null 
					&& o instanceof CompoundKey  
					&& Arrays.equals(values, ((CompoundKey) o).values);
			}
		}
		
		private PMap<CompoundKey, Long> counts = HashTreePMap.empty();
		
		public Count(OperatorData opData) {super(opData);}
		
		@Facet(memUse="WRITER", prototype="(long count)")
		public long map(Object... keys) {
			long value;
			CompoundKey key = new CompoundKey(keys);
			
			if (counts.containsKey(key)) {
				Long l = counts.get(key);
				value = l.longValue();
				value++;
			} else {
				value = 1;
			}

			counts = counts.plus(key, value);
			stateID++;
			return value;
		}

		@Facet(memUse="READER", prototype="(long count)")
		public long query(Object... keys) {
			long value =0;
			CompoundKey key = new CompoundKey(keys);
			
			if (counts.containsKey(key)) {
				value = counts.get(key);
			}
			return value;
		}

		public Count duplicate() {return new Count(operatorData);}
	}

	/**Counting when there are no keys to worry about.**/
	@Suppress @Operator(spec="[range: ALL, split: 0]")
	public static final class Counter extends AbstractOperator {
		private long count =1;
		public Counter(OperatorData opData) {super(opData);}
		public StencilOperator duplicate() {return new Counter(operatorData);}
		public String getName() {return "Count";}

		@Facet(memUse="WRITER", prototype="(long count)")
		public long map() {return count++;}
		
		@Facet(memUse="READER", prototype="(long count)")
		public long query() {return count;}		
		
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return (int) count % Integer.MAX_VALUE;}
	}
	
	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	@Operator(spec="[range:ALL, split:0, start: 0]")
	public static class Rank extends AbstractOperator.Statefull {
		/**Compare groups of object, often pair-wise.
		 * The first non-zero comparison wins.
		 * If all elements match, the longest array is 'after' the shorter one (so an array is always less than a non-array).
		 * If all elements match and the arrays are of the same length, the second object is after the first.
		 * 
		 */
		private static class CompoundCompare implements Comparator {
			protected CompoundCompare() {super();}
	
			private int compareOne(Comparable first, Comparable second) {
				return first.compareTo(second);
			}
	
			private int compareArrays(Object[] firstArray, Object[] secondArray) {
				for (int i =0; i< firstArray.length && i < secondArray.length; i++) {
					Object first = firstArray[i];
					Object second = secondArray[i];
	
					if (first == second) {continue;}
	
					if (first instanceof Comparable && first.getClass().isInstance(second)) {
						int d = compareOne((Comparable) first, (Comparable)second);
						if (d != 0) {return d;}
					}
				}
	
				return firstArray.length-secondArray.length;
			}
	
			public int compare(Object f, Object s) {
				if (f instanceof Object[] && s instanceof Object[]) {return compareArrays((Object[]) f, (Object[]) s);}
				else if (f instanceof Object[]) {return -1;}
				else if (s instanceof Object[]) {return 1;}
				else if (f instanceof Comparable && s instanceof Comparable && f.getClass().equals(s.getClass())) {
					return compareOne((Comparable) f, (Comparable) s);
				} else if (f instanceof Number && s instanceof  Number){
					return (int) Math.signum(((Number) f).doubleValue() - ((Number) s ).doubleValue());
				} else if (f instanceof Number) {
					return 1;
				} else if (s instanceof Number) {
					return -1;
				} else {
					return f.toString().compareTo(s.toString());
				}
			}
		}
		private static final CompoundCompare COMPARE = new CompoundCompare();
		
		private SortedSet set = new TreeSet(COMPARE);
	
		public Rank(OperatorData opData, Specializer spec) {super(opData);}
	
		@Facet(memUse="WRITER", prototype="(int rank)")
		public int map(Object... values) {return rank(true, values);} 
		
		@Facet(memUse="READER", prototype="(int rank)")
		public int query(Object... values) {return rank(false, values);}
	
		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private synchronized int rank(boolean add, Object... values) {
			int rank;
			if (add && !set.contains(values)) {
				SortedSet newSet = new TreeSet(COMPARE);		//Creates viewpoints as it goes, so the next one is always ready immediately
				newSet.addAll(set);								//TODO: Investigate self-viewpoint only at requested time or pcollections alternatives
				newSet.add(values);
				set = newSet;
				rank = set.headSet(values).size();
			}else if (set.contains(values)) {
				rank = set.headSet(values).size();
			}else {
				rank =-1;
			}
	
			return rank;
		}
		
		@Override
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return set.size();}
	}

	/**Projects one range of values into another.
	 * The target range is statically specified by the specializer.
	 * The source range is constructed based upon input.
	 * 
	 * This operator only works with numbers, but using Rank to convert
	 * arbitrary values to numbers can be used to scale arbitrary items. 
	 * @author jcottam
	 */
	@Operator(spec="[min: 0, max: 1, inMin: NULL, inMax: NULL, range: ALL, split:0]")
	public static final class Scale extends AbstractOperator.Statefull {
		private static final String IN_MAX = "inMax";
		private static final String IN_MIN = "inMin";
		private static final String OUT_MAX = "max";
		private static final String OUT_MIN = "min";
		final double outMin, outMax, span;

		double inMin = Double.MAX_VALUE;
		double inMax = Double.MIN_VALUE;		
		
		public Scale(OperatorData od, Specializer spec) {
			super(od);
			outMin = Converter.toDouble(spec.get(OUT_MIN));
			outMax = Converter.toDouble(spec.get(OUT_MAX));
			
			if (spec.containsKey(IN_MAX) && spec.get(IN_MAX) != null) {
				inMax = Converter.toDouble(spec.get(IN_MAX));
			} 
			
			if (spec.containsKey(IN_MIN) && spec.get(IN_MIN) != null) {
				inMin = Converter.toDouble(spec.get(IN_MIN));
			}
			
			span = outMax-outMin;
		}

		public Scale(OperatorData od, double outMin, double outMax) {
			super(od);
			this.outMin = outMin;
			this.outMax = outMax;
			span = outMax-outMin;
		}
		
		@Facet(memUse="WRITER", prototype="(double value)")
		public double map(double dv) {
			double newInMin = Math.min(dv, inMin);
			double newInMax = Math.max(dv, inMax);
			if (newInMin != inMin) {
				inMin = newInMin;
				stateID++;
			}
			
			if (newInMax != inMax) {
				inMax = newInMax;
				stateID++;
			}
			
			return query(dv);   
		}

		@Facet(memUse="READER", prototype="(double value)")
		public double query(double v) {
			double percent = (v-inMin)/inMax;
			double value = span*percent + outMin;
			
			return value;
		}
		
		public Scale duplicate() {return new Scale(operatorData, outMin, outMax);}
	}
	

	/**Perform a linear interpolation between zero and one, returning the Percent, mIn and maX (thus PIX).*/
	@Operator
	public static final class PIX extends AbstractOperator.Statefull {
		private static final class PIXTuple implements Tuple {
			private static final TuplePrototype PROTOTYPE = new SimplePrototype(new String[]{"p","min","max"}, new Class[]{Number.class, Number.class, Number.class});
			private final Number[] values;
			private PIXTuple(Number percent, Number min, Number max){values=new Number[]{percent, min, max};}			
			public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}
			public Object get(int idx) throws TupleBoundsException {return values[idx];}
			public TuplePrototype getPrototype() {return PROTOTYPE;}
			public boolean isDefault(String name, Object value) {return false;}
			public int size() {return PROTOTYPE.size();}			
		}
		
		private static final String MIN = "min";
		private static final String MAX = "max";
		
		private Number min = null;
		private Number max = null;
		
		public PIX(OperatorData opData, Specializer spec) {
			super(opData);
			
			min = spec.containsKey(MIN) ? Converter.toNumber(spec.get(MIN)) : null;
			max = spec.containsKey(MAX) ? Converter.toNumber(spec.get(MAX)) : null;

		}
		
		@Facet(memUse="WRITER", prototype="(Number p, Number min, Number max)")
		public Tuple map(Number value) {
			if (min == null) {min = value;}
			if (max == null) {max = value;}
			
			if (min.doubleValue() >= value.doubleValue()) {min = value; stateID++;}
			if (max.doubleValue() <= value.doubleValue()) {max = value; stateID++;}
			
			return query(value);
		}
		
		@Facet(memUse="READER", prototype="(Number p, Number min, Number max)")
		public Tuple query(Number value) {
			Number qmin = min == null ? value : min;   //local copies for query, so they are guaranteed to have a value
			Number qmax = max == null ? value : max;

			if (value instanceof Double || qmin instanceof Double || qmax instanceof Double) {
				double span = (qmax.doubleValue()-qmin.doubleValue());
				if (span ==0d) {value = 1;}
				else {value = (value.doubleValue())/(qmin.doubleValue() + span);}
			} else {
				long span = (qmax.longValue()-qmin.longValue());
				if (span ==0) {value =1;}
				else {value = value.longValue()/((double) (qmin.longValue() + span));} 				
			}
			return new PIXTuple(value, qmin, qmax);
		}
	}
	
	public StencilOperator instance(String name, Context context, Specializer spec) throws SpecializationException {
		if (context != null && name.equals("Count")) {
			if (context.maxArgCount() == 0) {
				OperatorData od = ModuleDataParser.operatorData(Counter.class, Projection.class.getSimpleName());
				return Modules.instance(this.getClass(), od, spec);
			}
		}
		
		return super.instance(name, context, spec);
	}
}
