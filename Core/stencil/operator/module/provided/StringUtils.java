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

import stencil.operator.module.*;
import stencil.operator.module.util.BasicModule;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.types.Converter;

public class StringUtils extends BasicModule {
	/**Print the passed tuple. Replaces names with new names.*/
	public static Tuple print(Object... os) {
		String[] labels = new String[os.length];
		for (int i=0;i<os.length;i++) {
			labels[i] = "VALUE" + (i==0?"":i);
			if(os[i] ==null) {System.out.print("null");}
			else{System.out.print(os[i].toString());}
			if (i!=os.length-1) {System.out.print(",");}
		}
		System.out.println();
		return new PrototypedTuple(labels, os);
	}
	
	/**Converts a value to a string value.*/
	public static Tuple toString(Object s) {return PrototypedTuple.singleton(s.toString());}
	
	
	//TODO: Add range support to concatenate
	public static Tuple concatenate(Object... os) {
		StringBuilder b = new StringBuilder();
		for (Object o:os) {b.append(o.toString());}
		return PrototypedTuple.singleton(b.toString());
	}

	public static Tuple format(Object v, Object f) {
		String rv = String.format(f.toString(), v);
		return PrototypedTuple.singleton(rv);
	}
	
	public static Tuple substring(Object str, Object s, Object e) {
		String string = Converter.toString(str);
		int start = Converter.toInteger(s);
		int end = Converter.toInteger(e);
		
		if (end >= 0) {return PrototypedTuple.singleton(string.substring(start, end));}
		else {return PrototypedTuple.singleton(string.substring(start));}
	}
	
	public static Tuple trim(Object str) {
		String string = Converter.toString(str);
		return PrototypedTuple.singleton(string.trim());
	}

	public StringUtils(ModuleData md) {super(md);}
}