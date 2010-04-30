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
package stencil.tuple;


import java.util.*;
import stencil.tuple.prototype.TuplePrototypes;

/**Utility methods for appending groups of tupless.*/
//final because it is a collection of utility methods and is not to be instantiated or overridden
public final class TupleAppender {
	private TupleAppender() {/*Utility class. Not instantiable.*/}


	/**Create a tuple as if the first tuple were followed by
	 * the second tuple.  If either is null or empty, then
	 * the other is returned (no copy is made).
	 * 
	 * If both are null or empty, an empty or null tuple is returned.
	 * Names found in the two tuples must be disjoint.  For copying values
	 * from one tuple to replace those of another use Transfer.
	 *
	 * TODO: Replace all calls to merge with calls to append
	 * TODO: Remove dependence on prototypes...or provide an alternative version for prototyped tuples only
	 *
	 * @param sourceName Where should the resulting tuple indicate it is from?
	 * @param source1
	 * @param source2
	 * @return
	 */
	public static Tuple append(Tuple source1, Tuple source2) {
		if (source1 == null || source1 == Tuples.EMPTY_TUPLE) {return source2;}
		if (source2 == null || source2 == Tuples.EMPTY_TUPLE) {return source1;}
		
		List<String> names = new ArrayList();
		List<Object> values = new ArrayList();
		
		for (String name: TuplePrototypes.getNames(source1)) {
			Object value = source1.get(name);
			names.add(name);
			values.add(value);
		}
		for (String name: TuplePrototypes.getNames(source2)) {
			Object value = source2.get(name);
			names.add(name);
			values.add(value);
		}
		return new PrototypedTuple(names, values);
	}
	
	
	public static Tuple append(final Tuple... sources) {
		for (int i=0; i< sources.length; i++) {
			if (sources[i]==null) {sources[i] = Tuples.EMPTY_TUPLE;}
		}

		Tuple result = sources[0];
		for (int i=1; i< sources.length; i++) {
			result = append(result, sources[i]);
		}
		return result;
	}
	
	/**Takes an array of tuples and produces a complex append of those tuples.
	 * Append proceeds in the following cases:
	 *   IF all sources are of length one
	 *   	THEN a single tuple is produced per normal append
	 *   
	 *   IF ONE source is length n > 1
	 *      THEN n tuples are produced with one value from the longer source
	 *           appended with all other source in the normal way
	 *           
	 *   IF MORE THAN ONE source is length n > 1
	 *     THEN n tuples are produced with one value from EACH of the
	 *          longer tuples and appended with the length 1 sources.
	 *          
	 *   IF MORE THAN ONE source is length > 1 BUT at least one is of a different length than the others
	 *      THEN an exception is produced.
	 *   
	 * @param source
	 * @return
	 */
	public static Tuple crossAppend(List<Tuple> sources) {
		int maxSize = Integer.MIN_VALUE;
		for (Tuple source: sources) {
			if ((source instanceof MapMergeTuple)
				&& (maxSize > 0 && source.size() != maxSize)) {
				throw new RuntimeException("Cannot cross-append tuples of inconsistent lengths greater than 1.  Data: " + Arrays.deepToString(sources.toArray()));
			}
			
			if (source instanceof MapMergeTuple) {maxSize = Math.max(maxSize, source.size());}
		}
		
		if (maxSize > 0) {return complexCross(sources, maxSize);}	
		else {return simpleCross(sources);}
	}
	
	public static Tuple simpleCross(List<Tuple> sources) {
		Tuple result = Tuples.EMPTY_TUPLE;
		for (Tuple source: sources) {
			result = append(result, source);
		}
		return result;		
	}
	
	
	public static MapMergeTuple complexCross(final List<Tuple> sources, int size) {
		final Tuple[] results = new Tuple[size];

		for (int i=0; i< results.length; i++) {
			List<Tuple> tempSources = new ArrayList(sources.size());
			
			for (int j=0; j<sources.size(); j++) {
				if (sources.get(j) instanceof MapMergeTuple) {
					tempSources.add((Tuple) sources.get(j).get(i));	//Take a single element from a longer value
				} else {
					tempSources.add(sources.get(j));
				}
			}
			results[i] = simpleCross(tempSources);
		}
		return new MapMergeTuple(results);
 	}
}




