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

import stencil.module.util.BasicModule;
import stencil.module.util.ann.*;

@Module
@Description("String manipulation.")
public class StringUtil extends BasicModule {
	
	/**Print the passed tuple. Replaces names with new names.*/
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static Object[] print(Object... os) {
		for (int i=0;i<os.length;i++) {
			if(os[i] ==null) {System.out.print("null");}
			else{System.out.print(os[i].toString());}
			if (i!=os.length-1) {System.out.print(",");}
		}
		System.out.println();
		return os;
	}
	
	/**Converts a value to a string value.*/
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String toString(Object s) {return s.toString();}

	/**Given a delimiter and a list of strings, creates
	 * a new string where each element of the list is separated
	 * by the delimiter.
	 * 
	 * @param delimiter
	 * @param values
	 * @return
	 */
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String delimit(String delimiter, String... values) {
		StringBuilder b  = new StringBuilder();
		for (String v: values) {
			b.append(v);
			b.append(delimiter);
		}
		b.delete(b.length()-delimiter.length(), b.length());
		return b.toString();
	}

	@Operator
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	public static String[] split(String value, String pattern) {
		return value.split(pattern);
	}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String toUpper(String s) {return s.toUpperCase();}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String toLower(String s) {return s.toLowerCase();}	
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String concatenate(Object... os) {
		StringBuilder b = new StringBuilder();
		for (Object o:os) {b.append(o!= null ? o.toString() : o);}
		return b.toString();
	}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String format(String f, Object... vs) {
		return String.format(f, vs);
	}
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String substring(String string, int start, int end) {		
		if (end >= 0) {return string.substring(start, end);}
		else {return string.substring(start);}
	}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String replace(String value, String pattern, String with) {return value.replace(pattern, with);}

	/**Retains only "word" characters characters.*/
	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String strip(String value) {return value.replaceAll("\\W", "");}

	@Operator
	@Facet(memUse="FUNCTION", prototype="(String value)", alias={"map","query"})
	public static String trim(String string) {return string.trim();}
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(int index)", alias={"map","query"})
	public static int indexOf(String string, String target) {
		return string.indexOf(target);
	}
	
	@Operator
	@Facet(memUse="FUNCTION", prototype="(int length)", alias={"map","query"})
	public static int length(String string) {return string.length();}
}