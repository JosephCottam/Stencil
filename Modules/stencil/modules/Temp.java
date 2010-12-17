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
	


	public static class Oscillate extends AbstractOperator {
		public static enum STYLE {CIRCLE, SINE, SINE2}
		
		public static final String STATES_KEY = "states";	//How many states should it oscillate through (may either be an int or a coma-separated list)
		public static final String STYLE_KEY = "style";	//circle (A B C A B C A B C)  or sine (A B C B A B C B A) or sine2 (A B C C B A A B C C B A)
		
		private final Object[] states;
		private final STYLE style;
		
		private int idx=0;
		private boolean goingUp = true;
		
		public Oscillate(OperatorData opData, Specializer spec) {
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
			
			
		public synchronized Object query() {
			stateID--;
			return map();
		}
		
		public synchronized Object map(){
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
			StencilOperator op = super.instance(name, specializer);
			
			Split split = new Split(specializer.get(SPLIT));
			return SplitHelper.makeOperator(split, op);
		} catch (Exception e) {throw new Error("Error retriving " + name + " method.",e);}
	}
}
