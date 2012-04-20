
package stencil.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.awt.Color;
import org.pcollections.*;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.ViewpointCache;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleDataParser;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.InvalidNameException;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleBoundsException;
import stencil.tuple.Tuples;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;
import stencil.types.color.ColorCache;

@Module
public class Projection extends BasicModule {
	
	/**Given a bin-size, determines what bin a particular value falls into.**/
	@Description("Calculates a bin that something falls into.  Bins are fixed size, with a potentially infinite number.  Bin size must be supplied in specializer.")
	@Operator(spec="[size: 1]")
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
	@Operator(spec="[cold: \"RED\", hot: \"WHITE\", throw: \"TRUE\"]", defaultFacet="map")
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
		@Facet(memUse="WRITER", prototype="(Color VALUE)", counterpart="query")
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

		@Override
		public HeatScale duplicate() {return new HeatScale(operatorData, cold, hot, throwExceptions);} 
	}
	
	
	/**Projects a range of numbers onto a red/white scale.*/
	@Description("Use a rainbow of colors (categorical counterpart to heatscale).")
	@Operator(spec="[reserve: BLACK, throw: \"TRUE\"]")
	public static final class RainbowScale extends AbstractOperator.Statefull {
		private final List seen = new ArrayList();
		private final Color reserve;
		private final boolean throwExceptions;
		
		public RainbowScale(OperatorData opData, Specializer spec) {
			super(opData);
			reserve = ColorCache.get(spec.get("reserve").toString());
			throwExceptions = (Boolean) Converter.convert(spec.get("throw"), Boolean.class);
		}
		
		private RainbowScale(OperatorData opData, Color reserve, boolean throwException) {
			super(opData);
			this.reserve = reserve;
			this.throwExceptions = throwException;
		}
		
		@Facet(memUse="WRITER", prototype="(Color VALUE)", counterpart="query")
		public Color map(Object value) {
			int idx = Collections.binarySearch(seen, value);
			if (idx < 0) {
				idx = -(idx+1);
				seen.add(idx, value);
			} 
			return colorFor(idx, seen.size());
		}

		@Facet(memUse="READER", prototype="(Color VALUE)")
		public Color query(Object value) {
			int idx = Collections.binarySearch(seen, value);
			return colorFor(idx, seen.size());
		}

		public Color colorFor(int idx, float total) {
			if (idx <0) {return reserve;}
			float arc = 360-(360/total);
			float offset = idx/total * arc;
			
			return ColorCache.get(new java.awt.Color(Color.HSBtoRGB(offset, .8f, idx/total)));
		}
		
		@Override
		public RainbowScale viewpoint() {
			RainbowScale s = (RainbowScale) super.viewpoint();
			s.seen.addAll(seen);
			return s;
		}
		
		@Override
		public RainbowScale duplicate() {return new RainbowScale(operatorData, reserve, throwExceptions);} 
	}
	
	/**Projects a set of values onto their presentation order.
	 * 
	 * Retains the order the items were presented in.
	 * If an item has been presented before, the original
	 * presentation rank is returned.  (So the first item
	 * presented always returns index one).  Ordering
	 * is independent for each IndexScale created.
	 */
	@Operator(spec="[]")
	@Description("Record the original presentation order of the combination of arguments")
	public static final class Index extends AbstractOperator {
		private PVector labels = TreePVector.empty();

		public Index(OperatorData opData) {super(opData);}

		@Facet(memUse="READER", prototype="(double index)")
		public int query(Object key) {
			if (labels.contains(key)) {return labels.indexOf(key);}
			return 0;
		}

		@Facet(memUse="WRITER", prototype="(double index)", counterpart="query")
		public int map(Object key) {
			if (!labels.contains(key)) {labels = labels.plus(key);}
			return labels.indexOf(key);
		}

		@Facet(memUse="READER", prototype="(int VALUE)") 
		public int stateID() {return labels.size();}
		@Override
		public Index duplicate() {return new Index(operatorData);}
	}

	
	/**Keeps count of things that have been seen.  
	 * For mapping, items that have not been seen before return 1 the first time, incremented there-after.
	 * For query, items that have not been seen return 0 and are not added to the map.
	 */

	@Description("Count the number of times the argument combination has been seen (including the current one)")
	@Operator(spec="[]", defaultFacet="map")
	public static final class Count extends AbstractOperator.Statefull {
		private static final class CompoundKey {
			final Object[] values;
			public CompoundKey(Object... values) {this.values = Arrays.copyOf(values, values.length);}
			@Override
			public int hashCode() {return Arrays.hashCode(values);}
			@Override
			public boolean equals(Object o) {
				return o != null 
					&& o instanceof CompoundKey  
					&& Arrays.equals(values, ((CompoundKey) o).values);
			}
		}
		
		private PMap<CompoundKey, Long> counts = HashTreePMap.empty();
		
		public Count(OperatorData opData) {super(opData);}
		
		@Facet(memUse="WRITER", prototype="(long count)", counterpart="query")
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

		@Override
		public Count duplicate() {return new Count(operatorData);}
	}

	/**Counting when there are no keys to worry about.**/
	@Operator(spec="[]")
	public static final class Counter extends AbstractOperator {
		private long count =1;
		public Counter(OperatorData opData) {super(opData);}
		@Override
		public StencilOperator duplicate() {return new Counter(operatorData);}
		@Override
		public String getName() {return "Count";}

		@Facet(memUse="WRITER", prototype="(long count)", counterpart="query")
		public long map() {return count++;}
		
		@Facet(memUse="READER", prototype="(long count)")
		public long query() {return count;}		
		
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return (int) count % Integer.MAX_VALUE;}
	}
	
	
	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	@Operator(spec="[]")
	public static class RankSingle extends AbstractOperator.Statefull {
		private final ArrayList list;
	
		public RankSingle(OperatorData opData) {
			super(opData);
			list = new ArrayList(1000);
		}
		public RankSingle(OperatorData opData, ArrayList values) {
			super(opData);
			list = new ArrayList(values.size());
			list.addAll(values);
		}
	
		@Facet(memUse="WRITER", prototype="(int rank)", counterpart="query")
		public int map(Comparable value) {return rank(true, value);} 
		
		@Facet(memUse="READER", prototype="(int rank)")
		public int query(Comparable value) {return rank(false, value);}
	
		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private int rank(boolean add, Comparable value) {
			int rank = Collections.binarySearch(list, value);
			if (rank >=0) {return rank;}
			else if (add) {
				list.add(-rank-1, value);
				return -rank-1;
			} else {return -1;}
		}
		
		@Override
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int stateID() {return list.size();}
		
		@Override
		public RankSingle viewpoint() {return new RankSingle(super.operatorData, list);}

	}
	
	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	@Operator(spec="[]")
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
	
			@Override
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
		
		protected final SortedSet set = new TreeSet(COMPARE);
		protected final ViewpointCache<Rank> viewpoint = new ViewpointCache();
	
		public Rank(OperatorData opData, Specializer spec) {super(opData);}
		private Rank(OperatorData opData, SortedSet values) {
			super(opData);
			set.addAll(values);
		}
	
		@Facet(memUse="WRITER", prototype="(int rank)", counterpart="query")
		public int map(Object... values) {return rank(true, values);} 
		
		@Facet(memUse="READER", prototype="(int rank)")
		public int query(Object... values) {return rank(false, values);}
	
		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private int rank(boolean add, Object... values) {
			int rank;
			if (add && !set.contains(values)) {
				set.add(values);								//TODO: Investigate pcollection alternatives
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
		
		
		@Override
		public Rank viewpoint() {
			if (!viewpoint.useCached(stateID())) {
				Rank r = new Rank(super.operatorData, set);
				viewpoint.cache(r);
			}
			return viewpoint.cached();
		}
	}

	

	/**Indicates how many distinct items have been seen.*/
	@Operator(spec="[]")
	public static class Distinct extends AbstractOperator.Statefull {
		private final Rank rank;
		
		
		public Distinct(OperatorData opData, Specializer spec) {
			super(opData);
			rank = new Rank(opData, spec);
		}

		private Distinct(OperatorData opData, Rank rank) {
			super(opData);
			this.rank = rank;
		}
	
		@Facet(memUse="WRITER", prototype="(int count)", counterpart="query")
		public int map(Object... values) {
			rank.map(values);
			return rank.set.size();
		}
		
		@Facet(memUse="READER", prototype="(int count)")
		public int query(Object... values) {
			return rank.set.size();
		}
	
		@Override
		public Distinct viewpoint() {
			Rank r = rank.viewpoint();
			return new Distinct(super.operatorData, r);
		}
	}


	/**Projects one range of values into another.
	 * The target range is statically specified by the specializer.
	 * The source range is constructed based upon input.
	 * 
	 * This operator only works with numbers, but using Rank to convert
	 * arbitrary values to numbers can be used to scale arbitrary items. 
	 * @author jcottam
	 */
	@Operator(spec="[min: 0, max: 1, inMin: NULL, inMax: NULL]")
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
		
		@Facet(memUse="WRITER", prototype="(double value)", counterpart="query")
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
			double percent = (v-inMin)/(inMax-inMin);
			double value = span*percent + outMin;
			
			return value;
		}
		
		@Override
		public Scale duplicate() {return new Scale(operatorData, outMin, outMax);}
	}
	
	/**Projects one range of values into another.
	 * The target range is statically specified by the specializer.
	 * The source range is constructed based upon input.
	 * 
	 * This operator only works with numbers, but using Rank to convert
	 * arbitrary values to numbers can be used to scale arbitrary items. 
	 * @author jcottam
	 */
	@Operator(spec="[min: 0, max: 1, inMin: NULL, inMax: NULL]")
	public static final class DScale extends AbstractOperator.Statefull {
		private static final String IN_MAX = "inMax";
		private static final String IN_MIN = "inMin";
		
		double inMin = Double.MAX_VALUE;
		double inMax = Double.MIN_VALUE;		
		
		public DScale(OperatorData od, Specializer spec) {
			super(od);
			
			if (spec.containsKey(IN_MAX) && spec.get(IN_MAX) != null) {
				inMax = Converter.toDouble(spec.get(IN_MAX));
			} 
			
			if (spec.containsKey(IN_MIN) && spec.get(IN_MIN) != null) {
				inMin = Converter.toDouble(spec.get(IN_MIN));
			}
		}

		public DScale(OperatorData od) {super(od);}
		
		@Facet(memUse="WRITER", prototype="(double value)", counterpart="query")
		public double map(double dv, double outMin, double outMax) {
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
			
			return query(dv, outMax, outMin);   
		}

		@Facet(memUse="READER", prototype="(double value)")
		public double query(double v, double outMin, double outMax) {
			double span = outMax-outMin;
			double percent = (v-inMin)/inMax;
			double value = span*percent + outMin;
			
			return value;
		}
		
		
		@Override
		public DScale duplicate() {return new DScale(operatorData);}
	}
	
	
	/**Perform a linear interpolation between zero and one, returning the Percent, mIn and maX (thus PIX).*/
	@Operator
	public static final class PIX extends AbstractOperator.Statefull {
		private static final class PIXTuple implements PrototypedTuple {
			private static final TuplePrototype PROTOTYPE = new TuplePrototype(new String[]{"p","min","max"}, new Class[]{Number.class, Number.class, Number.class});
			private final Number[] values;

			protected PIXTuple(Number percent, Number min, Number max){values=new Number[]{percent, min, max};}
			
			@Override
			public Object get(String name) throws InvalidNameException {return Tuples.namedDereference(name, this);}
			
			@Override
			public Object get(int idx) throws TupleBoundsException {return values[idx];}
			
			@Override
			public TuplePrototype prototype() {return PROTOTYPE;}
			
			@Override
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
		
		@Facet(memUse="WRITER", prototype="(Number p, Number min, Number max)", counterpart="query")
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
	
	@Override
	public StencilOperator optimize(StencilOperator op, Context context) {
		if (context != null && op instanceof Count) {
			if (context.maxArgCount() == 0) {
				OperatorData od = ModuleDataParser.operatorData(Counter.class, Projection.class.getSimpleName());
				return new Counter(od);
			}
		} if (context != null && op instanceof Rank) {
			if (context.maxArgCount() == 1) {
				OperatorData od = ModuleDataParser.operatorData(RankSingle.class, Projection.class.getSimpleName());
				return new RankSingle(od);
			}
		}
		return op;
	}
}
