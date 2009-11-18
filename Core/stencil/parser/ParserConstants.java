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
package stencil.parser;

import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;

public abstract class ParserConstants {
	/**Used to represent a default value that should be automatically derived.
	 * Not all places can use a default value, but those that can should use this value to 
	 * indicate that default should be used.*/
	public static final String SIMPLE_DEFAULT = "DEFAULT";
	
	public static final String DEFAULT_RESTRICTION = "ANY";
	public static final String DEFAULT_FIELD_TYPE_LABEL="NOMINAL";
	public static final String DEFAULT_LEGEND_BASE="Null";

	public static final String MATCH_ALL = ".*";
	public static final String NULL_PATTERN= "[null]";
	
	public static final String SEPARATOR = ",";
	public static final String INITIATOR = "(";
	public static final String TERMINATOR = ")";
	public static final String SIGIL = "@";

	public static final String GLYPH_ID_FIELD  = "ID";

	public static final String REGULAR_EXPRESSION = "=~";
	public static final String NEGATED_REGULAR_EXPRESSION = "!~";
	public static final String GT = ">";
	public static final String GTE = ">=";
	public static final String LT = "<";
	public static final String LTE = "<=";
	public static final String EQ = "=";
	public static final String NOT_EQ = "!=";


	public static final String OPAQUE_DOUBLE = "1.0";
	public static final String OPAQUE_INT	= "255";

	//Block tags (for Python and corresponding to Legend items)
	public static final String MAIN_BLOCK_TAG = "Map";
	public static final String INIT_BLOCK_TAG = "Init";
	public static final String QUERY_BLOCK_TAG = "Query";
	public static final String ITERATE_BLOCK_TAG = "Iterate";
	public static final String GUIDE_BLOCK_TAG = "Guide";
	public static final String DO_GUIDE_BLOCK_TAG = "DoGuide";
	
	public static final int RANGE_START_INT = 1;
	public static final int RANGE_END_INT = 0;
	public static final String RANGE_START = Integer.toString(RANGE_START_INT);
	public static final String RANGE_END = Integer.toString(RANGE_END_INT);
	public static final String NEW_VALUE = "new";
	public static final String FINAL_VALUE = "n";

	public static final String NAME_SPACE = "::";
	public static final String NAME_SEPARATOR  = ".";
	public static final String NAME_SEPARATOR_PATTERN  = "\\.";
	public static final String DEFAULT_GLYPH_TYPE = "SHAPE";
	
	public static final String VIEW_PREFIX = "view";
	public static final String CANVAS_PREFIX = "canvas";
	public static final String LOCAL_PREFIX = "local";

	
	public static final String BIND_OPERATOR = ":";
	
	/**Specializer to indicate that only the current value 
	 * is significant and no other special instructions are given.
	 * This is a common default specializer.
	 */
	public static final Specializer SIMPLE_SPECIALIZER;
		
	static {
		try {
			SIMPLE_SPECIALIZER = ParseStencil.parseSpecializer(String.format("[%1$s .. %1$s]",FINAL_VALUE));
			if (!SIMPLE_SPECIALIZER.isSimple()) {throw new Error("Error creating simple specializer: does not return isSimple()==true");}			
		} catch (Throwable e) {throw new Error("Error creating reference specializer.", e);}
	}

}
