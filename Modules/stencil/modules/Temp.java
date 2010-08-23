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

import static stencil.parser.tree.Specializer.SPLIT;

import java.util.*;

import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Split;
import stencil.module.operator.wrappers.SplitHelper;
import stencil.module.util.*;
import stencil.parser.tree.*;
import stencil.types.Converter;

/**
 * A module of misc utilities that I haven't figured out where they really belong yet.
 */
public class Temp extends BasicModule {
	/**A list of stop words for text analysis.
	 * List taken from http://www.textfixer.com/resources/common-english-words.txt.
	 * 
	 * */
	public static class StopWords extends AbstractOperator {
		String[] words = new String[]{"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"};
		
		public StopWords(OperatorData opData) {super(opData);}		
		public boolean query(String word) {return (Arrays.binarySearch(words, word.toLowerCase()) >= 0);} 		
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
			
			if (spec.containsKey(IN_MAX)) {
				inMax = Converter.toDouble(spec.get(IN_MAX));
			} 
			
			if (spec.containsKey(IN_MIN)) {
				inMin = Converter.toDouble(spec.get(IN_MIN));
			}
			
			span = outMax-outMin;
			stateID =0;
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
	}
	
	
	/**Perform a linear interpolation.*/
	public static final class LinearInterp extends AbstractOperator {
		public LinearInterp(OperatorData opData) {super(opData);}
		
		public double map(double step, double steps, double min, double max) {
			return query(step, steps, min, max);
		}
		
		public double query(double step, double steps, double min, double max) {
			return ((step/steps) * (min-max)) + min; 
		}
	}
	


	/**Takes a range of values and creates a set of partitions of that range.
	 * Will report what partition any value falls in.  This is done with modulus
	 * if a value is outside the original range and with abs if outside in a negative manner.
	 * 
	 * @author jcottam
	 *
	 */
	public static final class Partition extends AbstractOperator {
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

	public static class Oscillate extends AbstractOperator {
		public static enum STYLE {CIRCLE, SINE, SINE2}
		
		public static final String STATES_KEY = "states";	//How many states should it oscillate through (may either be an int or a coma-separated list)
		public static final String STYLE_KEY = "style";	//circle (A B C A B C A B C)  or sine (A B C B A B C B A) or sine2 (A B C C B A A B C C B A)
		
		private final Object[] states;
		private final STYLE style;
		
		private int idx=0;
		private boolean goingUp;
		
		protected Oscillate(OperatorData opData, Specializer spec) {
			super(opData);
			style = STYLE.valueOf(Converter.toString(spec.get(STYLE_KEY)).toUpperCase());
		
			Object[] candidate;
			try {
				int stateCount = Converter.toInteger(spec.get(STATES_KEY));
				candidate = new Integer[stateCount];
				for (int i =0; i< candidate.length; i++) {candidate[i] =i;}
			}
			catch (NumberFormatException ex) {
				String states  = Converter.toString(spec.get(STATES_KEY));
				candidate = states.split("\\s*,\\s*");
			}
			states = candidate;
			if (states.length ==1) {throw new RuntimeException("Oscillate specified with only one state: " + Arrays.deepToString(states));}
		}
		
		protected Oscillate(OperatorData opData, Object[] states, STYLE style) {
			super(opData);
			this.states = states;
			this.style = style;
		}
			
			
		public Object query() {
			stateID--;
			return map();
		}
		
		public Object map(){
			Object rv = states[idx];
			stateID++;
			
			switch (style) {
			case CIRCLE : idx = (idx+1)%states.length; break;
			case SINE :
				if (goingUp) {
					idx = idx+1;
					if (idx == states.length) {
						idx = states.length-2;
						goingUp = false;
					} 
				} else {
					idx = idx -1;
					if (idx <0) {
						idx = 1;
						goingUp = true;
					}
				}
				break;
			case SINE2 :
				if (goingUp) {
					idx = idx+1;
					if (idx == states.length) {
						idx = states.length-1;
						goingUp = false;
					} 
				} else {
					idx = idx -1;
					if (idx <0) {
						idx = 0;
						goingUp = true;
					}
				}
				break;
			default : throw new Error("cycle style not covered: " + style.toString());
			}
			return rv;
		}
		
		public StencilOperator duplicate() {
			StencilOperator nop = new Oscillate(operatorData, states, style);
			return nop;
		}
		
	}
	
	public Temp(ModuleData md) {super(md);}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		try {
			StencilOperator op;
			if (name.equals("Scale")) {
				op = new Scale(getOperatorData(name, specializer), specializer);
			} else if (name.equals("Partition")) {
				op = new Partition(getOperatorData(name, specializer), specializer);
			} else if (name.equals("Oscillate")) {
				op = new Oscillate(getOperatorData(name, specializer), specializer);
			} else {
				op = super.instance(name, specializer);
			}
			
			Split split = new Split(specializer.get(SPLIT));
			return SplitHelper.makeOperator(split, op);
		} catch (Exception e) {throw new Error("Error retriving " + name + " method.",e);}
	}
}
