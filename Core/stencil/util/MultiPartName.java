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
package stencil.util;
import static stencil.parser.ParserConstants.NAME_SEPARATOR_PATTERN;
import static stencil.parser.ParserConstants.NAME_SPACE;

/**Divides a name up into its constituent parts.*/
//final because it is immutable
public final class MultiPartName {	
	private String pre;
	private String name;
	private String suffix;
	
	public MultiPartName(String name) {
		String[] parts = name.split(NAME_SEPARATOR_PATTERN);
		String[] parts2 = parts[0].split(NAME_SPACE);

		if (parts.length == 1) {suffix = "";}
		else {suffix = parts[1];}
		
		if (parts2.length ==1) {
			pre = "";
			this.name = parts2[0];
		} else {
			pre = parts2[0];
			this.name=parts2[1];
		}

	}
	
	public MultiPartName(String pre, String name, String suffix) {
		this.pre = pre;
		this.name = name;
		this.suffix = suffix;
	}
	
	
	/**What was the prefix on the name?*/
	public String getPrefix() {return pre;}
	
	/**What was the root name?*/
	public String getName() {return name;}
	
	/**What was the facet (same as suffix, but used in operator names).*/
	public String getFacet() {return suffix;}
	
	/**What was the suffix?*/
	public String getSuffix() {return suffix;}
	
	/**Create a new multi-part name, changing the prefix.  If the prefix is the
	 * same as the current prefix, the current object is returned.
	 */
	public MultiPartName modPrefix(String newPrefix) { 
		if (this.pre == null && newPrefix == null || (this.pre != null && this.pre.equals(newPrefix))) {return this;}

		return new MultiPartName(newPrefix, name, suffix);
	}
	
	/**Create a new multi-part name, changing the suffix.  If the suffix is the
	 * same as the current suffix, the current object is returned.
	 */	
	public MultiPartName modSuffix(String newSuffix) {
		if (this.suffix == null && newSuffix == null || (this.suffix != null && this.suffix.equals(newSuffix))) {return this;}
		return new MultiPartName(pre, name, newSuffix);
	}
	
	/**Return the name plus the prefix (no suffix).*/
	public String prefixedName() {
		if (pre.equals("")) {return name;}
		return pre + NAME_SPACE + name;
	}

	/**Return the whole name (prefix, name, suffix), appropriately delimited.*/
	public String toString() {return prefixedName() + "." + suffix;}
}