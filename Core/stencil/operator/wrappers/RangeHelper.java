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
package stencil.operator.wrappers;

import stencil.operator.StencilOperator;
import stencil.parser.tree.Range;
import stencil.parser.tree.Value;
import stencil.tuple.Tuple;
import stencil.types.Converter;

import java.util.ArrayList;
import java.util.List;

/**Wraps a StencilLegend with range support.
 * Keeps a copy of every argument set seen within the range
 * and re-invokes the legend with an argument list equal to concatenating
 * all in-range arguments.  
 * 
 * Warnings:  
 * 1) If using for a range with a static start, the memory consumption is linear with the number of tuples seen
 * 2) Assumes that an operator instance is used either in Query or Map context (not both).
 * 
 */

public abstract class RangeHelper implements StencilOperator {
	private static final class RelativeHelper extends RangeHelper {
		public RelativeHelper(Range range, StencilOperator oprator) {
			super(range, oprator);
			
			if (range.getStart() < range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toStringTree());}
			
			values = new ArrayList(range.getStart());
		}
		 
		protected Object[] updateCache(Object... args) {
			//Rotate in the new values
			if (values.size() > range.getStart()) {values.remove(0);} //Range.start indicates the oldest value that needs to be remembered.  In an offset, this is the larger number
			values.add(args);

			//We can always start at 0, since that is the 'oldest' value, but we may
			//have an end that is not the end of the range we must remember
			//(e.g. the range arg was -10 to -5, just five items are returned but you need to remember 10)
			int endRange = values.size()-1 > range.getEnd() ? values.size()-1 - range.getEnd() : 0;
			Object[] formals = values.subList(0, endRange).toArray();
			return formals;
		}
		
	}
	
	private static final class AbsoluteHelper extends RangeHelper {
		private boolean trimmed = false;
		private int offsetCountdown;
		
		public AbsoluteHelper(Range range, StencilOperator operator) {
			super(range, operator);

			if (range.getStart() > range.getEnd()) {throw new IllegalArgumentException("Range ends before it starts: " + range.toStringTree());}

			values = new ArrayList(range.getEnd());
			offsetCountdown = range.getStart();
		}
		
		protected Object[] updateCache(Object... args) {
			if (offsetCountdown >0) {offsetCountdown--; return new Object[0];}
			
			if (values.size() < range.getEnd()) {
				values.add(args);
			} else if (!trimmed) {
				//A range with absolute indices on both start and end will eventually become a constant...
				//so we trim it to that constant value
				values = values.subList(0, range.getEnd()); //The endpoint is exclusive, but range is 1-based, so it all works out!
				trimmed = true;
			}
			return values.toArray();
		}
	}
	
	private static final class HybridHelper extends RangeHelper {
		int offsetCountdown;
			
		public HybridHelper(Range range, StencilOperator operator) {
			super(range, operator);
			
			if (range.relativeStart()) {throw new RuntimeException("Hybrid ranges must have absolute start points.  Recieved range " + range.toStringTree());}
			values = new ArrayList();
			offsetCountdown = range.getStart();
		}
		
		protected Object[] updateCache(Object... args) {
			if (offsetCountdown >0) {offsetCountdown--; return new Object[0];}
			values.add(args);			
			return values.subList(0, range.getEnd()).toArray();
		}
	}
	

	protected Range range;
	protected List values;	 		//One list, used by both Query and Map.  Invoking a single instance in both may end up arguments appended multiple times. 
	private StencilOperator operator; //Backing StencilLegend instance
	
	protected RangeHelper(Range range, StencilOperator operator) {
		this.operator = operator;
		this.range = range;
	}

	public Tuple map(Object...args) {
		Object[] formals = updateCache(args);
		//TODO: Should we do something special if formals.length ==0?
		return operator.map(formals);
	}

	/**Given an array of objects, will attempt to convert them all to the
	 * specified target class.  Will convert arrays of arrays to a
	 * single-level array as well.
	 *
	 * @param values
	 * @param target
	 * @return
	 */
	 public static <T> T[] flatten(Object[] values, T prototype) {
		ArrayList<T> v = new ArrayList<T>();

		for(Object o:values) {
			if (o.getClass().isArray()) {
				for (Object o2: (Object[]) o) {
					v.add((T)Converter.convert(o2, prototype.getClass()));
				}
			} else {
				v.add((T)Converter.convert(o, prototype.getClass()));
			}
		}
		return v.toArray((T[]) java.lang.reflect.Array.newInstance(prototype.getClass(), 0));
	}

	public String getName() {return operator.getName() + "(Ranged)";}

	public Tuple query(Object... args) {
		Object[] formals = updateCache(args);
		return operator.query(formals);
	}
	
	/**Updates the list storage according to the specified range.
	 * @return List of arguments to actually invoke the underlying legend with.
	 */
	protected abstract Object[] updateCache(Object... args);
	
	public StencilOperator duplicate() {
		StencilOperator op = operator.duplicate();
		return makeLegend(range, op);
	}
	
	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {throw new UnsupportedOperationException(String.format("Range cannot autoguide (wrapping %1$s).", operator.getName()));}
	public boolean refreshGuide() {throw new UnsupportedOperationException(String.format("Range cannot autoguide (wrapping %1$s).", operator.getName()));}
	
	
	public static StencilOperator makeLegend(Range range, StencilOperator operator) {
		if (range.isSimple()) {return operator;}
		if (range.relativeStart() && range.relativeEnd()) {return new RelativeHelper(range, operator);}
		if (!range.relativeStart() && !range.relativeEnd()) {return new AbsoluteHelper(range, operator);}
		if (!range.relativeStart() && range.relativeEnd()) {return new HybridHelper(range, operator);}
		
		throw new RuntimeException("Unsupported paramter combinitation in range: " + range.toStringTree());
		
		
	}
}
