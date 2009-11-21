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
import stencil.operator.module.OperatorData.OpType;
import stencil.operator.module.util.*;
import stencil.operator.util.BasicProject;
import stencil.parser.tree.*;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
public class Temp extends BasicModule {
	/**Keeps sorted lists of elements, reporting back
	 * the position in the list on lookup.
	 */
	public static class Rank extends BasicProject {
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

		public Tuple map(Object... values) {return rank(true, values);} 
		public Tuple query(Object... values) {return rank(false, values);}
	
		public String getName() {return NAME;}

		/**What is the rank of the values passed.
		 * -1 indicates no rank (not seen before and not added)
		 * @param add Should this set of values be added to the set if it is not already there?
		 * @param values What set of values needs ranking?
		 */
		private Tuple rank(boolean add, Object... values) {
			int rank;
			if (add && !set.contains(values)) {
				set.add(values);
				rank = set.headSet(values).size();
			}else if (set.contains(values)) {
				rank = set.headSet(values).size();
			}else {
				rank =-1;
			}
			
			return PrototypedTuple.singleton(rank);
		}
		
		public Rank duplicate() {return new Rank();}
	}

	/**Maps one element to a set of other elements*/
	public static class Mapping extends BasicProject {
		public static final String NAME = "Mapping";
		
		protected Map<Object, Object[]> map = new HashMap();
		String[] names;
		boolean caseSensitive=true;
		
		public Mapping(String...names) {this.names = names;}

		public String getName() {return NAME;}

		public Tuple put(Object... values) {
			Object key = values[0];
			Object[] objects = new Object[values.length-1];
			System.arraycopy(values, 1, objects, 0, values.length-1);
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}
			
			if (objects.length ==0) {
				objects = map.get(key);
			} else if (objects.length== names.length){
				map.put(key, objects);
			} else {
				throw new IllegalArgumentException("Objects to store list must match the prototype names list length.");
			}
			return new PrototypedTuple(names, objects);
		}

		public Tuple map(Object... args) {return query(args);}
		public Tuple query(Object... args) {
			if (args.length > 1) {throw new IllegalArgumentException("Can only query with single-argument to map.");}
			Object key = args[0];
			if (!caseSensitive && key instanceof String) {key = ((String) key).toUpperCase();}
			
			Object[] results = map.get(key);
			if (results == null) {return null;}			
			return new PrototypedTuple(names, results);
		}
		
		public static OperatorData getLegendData(String moduleName, String name, Specializer specializer) throws SpecializationException{
			String[] fields;
			try {fields = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(moduleName, name, specializer, e);}
			
			return Modules.basicLegendData(moduleName, name, OpType.PROJECT, fields);
		}

		public static StencilOperator instance(String moduleName, String name, Specializer specializer) throws SpecializationException, NoSuchMethodException {
			if (!specializer.getRange().isFullRange() || specializer.getSplit().hasSplitField()) {
				throw new SpecializationException(moduleName, name, specializer);
			}

			String[] names;
			try {names = getNames(specializer);}
			catch (Exception e) {throw new SpecializationException(moduleName, name, specializer, e);}			
			Mapping m = new Mapping(names);
			
			if (specializer.getMap().containsKey("CaseInsensitive")) {m.caseSensitive = false;}
			
			return m;
		}

		/**Convert a specializer into a list of strings.  
		 * All specializer arguments must have atom.isName == true.
		 * 
		 * @throws IllegalArgumentException A non-name appears in the specializer list.
		 **/
		private static String[] getNames(Specializer specializer) throws SpecializationException {
			String[] fields = new String[specializer.getArgs().size()];

			for (int i = 0; i< specializer.getArgs().size(); i++) {
				Atom atom = specializer.getArgs().get(i);
				if (!atom.isString()) {throw new IllegalArgumentException(String.format("Non-string in position %1$d.  Found '%2$s' instead.", i, atom.toStringTree()));}
				fields[i] = atom.getText();
			}
			return fields;
		}
		
		public Mapping duplicate() {return new Mapping(names);}
	}

	
	
	public Temp(ModuleData md) {super(md);}

	public OperatorData getOperatorData(String name, Specializer specializer)
		throws SpecializationException {

		if(name.equals("Mapping")) {
			return Mapping.getLegendData(getModuleData().getName(), name, specializer);
		}

		return super.getOperatorData(name, specializer);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		try {
			if (name.equals("Mapping")) {
				return Mapping.instance(getModuleData().getName(), name, specializer);
			}
			return super.instance(name, specializer);
		} catch (Exception e) {throw new Error("Error retriving " + name + " method.",e);}
	}
}
