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

import java.util.*;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.*;
import static stencil.operator.module.util.OperatorData.*;
import stencil.operator.util.BasicProject;
import stencil.parser.tree.*;
import stencil.types.Converter;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
public class Temp extends BasicModule {

	/**Projects one range of values into another.
	 * The target range is statically specified by the specializer.
	 * The source range is constructed based upon input.
	 * 
	 * This operator only works with numbers, but using Rank to convert
	 * arbitrary values to numbers can be used to scale arbitrary items. 
	 * @author jcottam
	 *
	 */
	public static final class Scale extends BasicProject {
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
			
			if (spec.containsKey(IN_MAX)) {
				inMax = Converter.toDouble(spec.get(IN_MAX));
			} 
			
			if (spec.containsKey(IN_MIN)) {
				inMin = Converter.toDouble(spec.get(IN_MIN));
			}
			
			span = outMax-outMin;
		}

		public double map(double dv) {
			inMin = Math.min(dv, inMin);
			inMax = Math.max(dv, inMax);
			return query(dv);   
		}

		public double query(double v) {
			double percent = (v-inMin)/inMax;
			double value = span*percent + outMin;
			return value;}
	}


	/**Takes a range of values and creates a set of partitions of that range.
	 * Will report what partition any value falls in.  This is done with modulus
	 * if a value is outside the original range and with abs if outside in a negative manner.
	 * 
	 * @author jcottam
	 *
	 */
	public static final class Partition extends BasicProject {
		private static final String MIN = "min";
		private static final String MAX = "max";
		private static final String BUCKETS = "n";
		
		private final double buckets;
		private final boolean autoMin;
		private final boolean autoMax;

		private double min;
		private double max;
		private double bucketSpan;
		
		public Partition(OperatorData od, Specializer spec) {
			super(od);

			buckets = Converter.toDouble(spec.get(BUCKETS));

			autoMin = !spec.containsKey(MAX);
			autoMax = !spec.containsKey(MIN);
			max = autoMax ? Converter.toDouble(spec.get(MAX)) : Double.MAX_VALUE;
			min = autoMin ? Converter.toDouble(spec.get(MIN)) : Double.MAX_VALUE; 
		}

		private void calcSpan(double value) {
			if (autoMax && value > max) {max = value;}
			if (autoMin && value < min) {min = value;}
			bucketSpan = (max-min)/buckets;
		}
		
		public double[] map(double dv) {return query(dv);}
		public double[] query(double dv) {
			calcSpan(dv);
			
			dv = dv-min;

			double bucket = (buckets)/((max-min)/dv);
			bucket = Math.abs(Math.floor(bucket));
			bucket = bucket%buckets;

			double start = bucket*bucketSpan;
			double end = (bucket +1)*bucketSpan;
			
			return new double[]{start,end,bucket};
		}
	}

	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	public static class Rank extends BasicProject {
		public Rank(OperatorData opData) {super(opData);}

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

		//TODO: Move to a concurrent set
		private SortedSet set = Collections.synchronizedSortedSet(new TreeSet(new CompoundCompare()));
		//		private TreeSet set = new TreeSet(new CompoundCompare());

		public int map(Object... values) {return rank(true, values);} 
		public int query(Object... values) {return rank(false, values);}

		public String getName() {return NAME;}

		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private int rank(boolean add, Object... values) {
			int rank;
			if (add && !set.contains(values)) {
				set.add(values);
				rank = set.headSet(values).size();
			}else if (set.contains(values)) {
				rank = set.headSet(values).size();
			}else {
				rank =-1;
			}

			return rank;
		}

		public Rank duplicate() {return new Rank(operatorData);}
	}

	/**Maps one element to a set of other elements*/
	public static class Mapping extends BasicProject {
		public static final String NAMES = "fields";
		public static final String NAME = "Mapping";
		public static final String PUT_FACET = "put";

		final protected Map<Object, Object[]> map = new HashMap();
		final String[] names;
		final boolean caseSensitive;
		final Object[] defaultValues;

		public Mapping(OperatorData opData, boolean caseSensitive, Object[] defaultValues, String...names) {
			super(opData);
			this.names = names;
			this.defaultValues = defaultValues;
			this.caseSensitive = caseSensitive;
		}

		public String getName() {return NAME;}

		public Object[] put(Object key, Object... values) {
			Object[] objects = new Object[values.length];
			System.arraycopy(values, 0, objects, 0, values.length);	//Copy is required for storage.
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			if (objects.length== names.length){ //TODO: Add compile-time call-site verification of argument lengths
				map.put(key, objects);
			} else {
				throw new IllegalArgumentException("Objects to store list must match the prototype names list length.");
			}
			return objects;
		}

		public Object[] map(Object key, Object... args) {return query(key, args);}
		public Object[] query(Object key, Object... args) {
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}

			Object[] results = map.get(key);
			if (results == null) {return defaultValues;}			
			return results;
		}

		private static OperatorData getOperatorData(OperatorData basic, Specializer specializer) throws SpecializationException{
			String module = basic.getModule();
			String name = basic.getName();
			String[] fields;
			
			try {fields = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(module, name, specializer, e);}

			OperatorData od = new OperatorData(basic);
			od.addFacet(new FacetData(PUT_FACET, TYPE_PROJECT, false,  fields));
			od.addFacet(new FacetData(MAP_FACET, TYPE_PROJECT, false, fields));
			od.addFacet(new FacetData(QUERY_FACET, TYPE_PROJECT, false, fields));
			return od;
		}

		public Mapping duplicate() {return new Mapping(operatorData, caseSensitive, defaultValues, names);}
		
		public static StencilOperator instance(OperatorData opData, Specializer specializer) throws SpecializationException, NoSuchMethodException {
			final String[] names;
			
			try {names = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(opData.getModule(), opData.getName(), specializer, e);}

			Object[] defaultValues = new Object[names.length];
			boolean noneSet = true;
			for (int i=0; i< names.length; i++) {
				String key = String.format("dv%1$s", i);
				if (specializer.containsKey(key)) {
					defaultValues[i] = specializer.get(key).getValue();
					noneSet = false;
				}	
			}
			if (noneSet) {defaultValues = null;}
			
			boolean caseSensitive = !specializer.getMap().containsKey("CaseInsensitive");
			
			Mapping m = new Mapping(getOperatorData(opData, specializer), caseSensitive, defaultValues, names);
			
			return m;
		}
		
		private static String[] getNames(Specializer spec) {
			return spec.get(NAMES).getText().split("\\s*,\\s*");
		}

	}



	public Temp(ModuleData md) {super(md);}

	public OperatorData getOperatorData(String name, Specializer specializer)
	throws SpecializationException {

		if(name.equals("Mapping")) {
			return Mapping.getOperatorData(getModuleData().getOperator(name), specializer);
		}

		return super.getOperatorData(name, specializer);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		try {
			if (name.equals("Mapping")) {
				return Mapping.instance(getOperatorData(name, specializer), specializer);
			} else if (name.equals("Scale")) {
				return new Scale(getOperatorData(name, specializer), specializer);
			} else if (name.equals("Partition")) {
				return new Partition(getOperatorData(name, specializer), specializer);
			}
			
			return super.instance(name, specializer);
		} catch (Exception e) {throw new Error("Error retriving " + name + " method.",e);}
	}
}
