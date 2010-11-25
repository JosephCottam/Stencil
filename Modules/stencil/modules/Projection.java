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

import java.util.*;
import java.awt.Color;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Range;
import stencil.module.operator.util.Split;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import stencil.types.color.ColorCache;

public class Projection extends BasicModule {
	public static final String MODULE_NAME = "Projection";
	
	/**Projects a range of numbers onto a red/white scale.*/
	//TODO: Lift the throws-exception out to a wrapping class.  If throws Exception is requested, use the wrapper; otherwise use this class directly.  Change argument types to float.
	public static final class HeatScale extends AbstractOperator {
		public static final String NAME = "HeatScale";
		public static final boolean DEFAULT_THROW_EXCEPTIONS = true;

		private float min = Float.NaN;
		private float max = Float.NaN;
		private boolean throwExceptions = DEFAULT_THROW_EXCEPTIONS;

		private final Color cold;
		private final Color hot;
		
		public HeatScale(OperatorData opData, Specializer spec) {
			super(opData);
			cold = ColorCache.get(spec.getMap().get("cold").getValue().toString());
			hot = ColorCache.get(spec.getMap().get("hot").getValue().toString());
		}
		
		private HeatScale(OperatorData opData, Color cold, Color hot) {
			super(opData);
			this.cold = cold;
			this.hot = hot;
		}
		
		/**Returns a value between Red (low) and White (high) that represents
		 * the percentage of the difference seen between the highest and the lowest value.
		 *
		 * If multiple keys are passed, only the first one is used.
		 * If the key passed cannot be parsed as number and 'throwExceptions' is set to false, black is returned.
		 * If the key passed cannot be parsed and 'throwExceptions' is set to true, an exception is thrown.
		 */
		public Color map(float d) {
			float p =-1;			
				
			try {
				if (d>max || Double.isNaN(max)) {max = d; stateID++;}
				if (d<min || Double.isNaN(min)) {min = d; stateID++;}
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
		public Color query(float d) {
		         if (d < min) {return cold;}
			else if (d > max) {return hot;}
			else              {return map(d);} //value is in the range of requested values 
		}

		public String getName() {return NAME;}
		public boolean getThrowExceptions() {return throwExceptions;}
		public void setThrowExceptions(boolean v) {throwExceptions = v;}
		
		public HeatScale duplicate() {return new HeatScale(operatorData, cold, hot);} 
	}
	
	/**Projects a set of values onto their presentation order.
	 * 
	 * Retains the order the items were presented in.
	 * If an item has been presented before, the original
	 * presentation rank is returned.  (So the first item
	 * presented always returns index one).  Ordering
	 * is independent for each IndexScale created.
	 *
	 * TODO: Update so it can use a comparator for arbitrary sorting orders, then have it default to 'natural' order
	 */
	public static final class Index extends AbstractOperator {
		public static final String NAME = "Index";
		private List<String> labels = new ArrayList<String>();

		public Index(OperatorData opData) {super(opData);}
		
		public int query(Object key) {
			if (labels.contains(key)) {return labels.indexOf(key);}
			return 0;
		}

		public int map(Object key) {
			if (!labels.contains(key)) {labels.add(key.toString());}
			return labels.indexOf(key);
		}

		public String getName() {return NAME;}
		public int stateID() {return labels.size();}
		public Index duplicate() {return new Index(operatorData);}
	}

	
	/**Keeps count of things that have been seen.  
	 * For mapping, items that have not been seen before return 1 the first time, incremented there-after.
	 * For query, items that have not been seen return 0 and are not added to the map.
	 */
	public static final class Count extends AbstractOperator {
		private Map<Object, Long> counts = new HashMap<Object, Long>();
		private int stateID = Integer.MIN_VALUE;
		
		public Count(OperatorData opData) {super(opData);}
		
		public String getName() {return "Count";}

		public long map(Object key) {
			long value;
			if (counts.containsKey(key)) {
				Long l = counts.get(key);
				value = l.longValue();
				value++;
				counts.put(key, value);
			} else {
				value = 1;
				counts.put(key, value);
			}
			stateID++;
			return value;
		}

		public long query(Object key) {
			long value =0;
			if (counts.containsKey(key)) {
				value = counts.get(key);
			}
			return value;
		}

		public int stateID() {return stateID;}
		
		public Count duplicate() {return new Count(operatorData);}
	}

	/**Counting when there are no keys to worry about.**/
	public static final class Counter extends AbstractOperator {
		private long count =1;
		public Counter(OperatorData opData) {super(opData);}
		public StencilOperator duplicate() {return new Counter(operatorData);}
		public String getName() {return "Count";}
		public long map() {return count++;}
		public long query() {return count;}		
		public int stateID() {return (int) count % Integer.MAX_VALUE;}
	}
	
	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	public static class Rank extends AbstractOperator {
		public static final String NAME = "Rank";
		
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
	
		public int map(Object... values) {return rank(true, values);} 
		public int query(Object... values) {return rank(false, values);}
	
		public String getName() {return NAME;}
	
		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private synchronized int rank(boolean add, Object... values) {
			int rank;
			if (add && !set.contains(values)) {
				SortedSet newSet = new TreeSet(COMPARE);
				newSet.addAll(set);
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
		
		public int stateID() {return set.size();}
	}

	/**Projects one range of values into another.
	 * The target range is statically specified by the specializer.
	 * The source range is constructed based upon input.
	 * 
	 * This operator only works with numbers, but using Rank to convert
	 * arbitrary values to numbers can be used to scale arbitrary items. 
	 * @author jcottam
	 *
	 */
	public static final class Scale extends AbstractOperator {
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
			
			if (spec.containsKey(IN_MAX) && spec.get(IN_MAX).getValue() != null) {
				inMax = Converter.toDouble(spec.get(IN_MAX));
			} 
			
			if (spec.containsKey(IN_MIN) && spec.get(IN_MIN).getValue() != null) {
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

		public double query(double v) {
			double percent = (v-inMin)/inMax;
			double value = span*percent + outMin;
			
			return value;
		}
		
		public Scale duplicate() {return new Scale(operatorData, outMin, outMax);}
	}
	
	public Projection(ModuleData md) {super(md);}

	protected void validate(String name, Specializer specializer) throws SpecializationException {
		Range r = new Range(specializer.get(Specializer.RANGE));
		Split s = new Split(specializer.get(Specializer.SPLIT));
		if (!r.isFullRange() || !s.isVoid())  {
			throw new SpecializationException(MODULE_NAME, name, specializer);
		}

		super.validate(name, specializer);
	}
}
