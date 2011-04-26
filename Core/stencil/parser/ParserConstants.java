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

import stencil.display.DisplayLayer;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.parser.tree.AstInvokeable;
import stencil.parser.tree.StencilTree;

public abstract class ParserConstants {
	public static final Class<AstInvokeable> INVOKEABLE = AstInvokeable.class;
	
	/**Used to represent a default value that should be automatically derived.
	 * Not all places can use a default value, but those that can should use this value to 
	 * indicate that default should be used.*/
	public static final String SIMPLE_DEFAULT = "DEFAULT";
	
	//Common fields
	public static final String IDENTIFIER_FIELD  = "ID";
	public static final String SEQUENCE_FIELD = "SEQ";
	public static final String INPUT_FIELD = "Input";
	public static final String X_FIELD = "X";
	public static final String Y_FIELD = "Y";
	public static final String TEXT_FIELD = "TEXT";
	
	public static final String GUIDE_LABEL = "guideLabel";
	public static final String GUIDE_ELEMENT_TAG = "element";
	public static final String POSITIONAL_ARG = "#POSITIONAL";
	public static final String SEPARATOR = ",";
	public static final String INITIATOR = "(";
	public static final String TERMINATOR = ")";
	public static final String SIGIL = "@";


	//Block tags
	public static final String MAP_FACET = StencilOperator.MAP_FACET;
	public static final String QUERY_FACET = StencilOperator.QUERY_FACET;
	public static final String STATE_ID_FACET = StencilOperator.STATE_ID_FACET;
	public static final String CUSTOM_PARSER_FACET = "argumentParser";
	public static final String DEFAULT_JAVA_SUPER = "stencil.module.operator.util.AbstractOperator";
	
	public static final int RANGE_START_INT = 1;
	public static final int RANGE_END_INT = 0;
	public static final String RANGE_START = Integer.toString(RANGE_START_INT);
	public static final String RANGE_END = Integer.toString(RANGE_END_INT);
	public static final String FINAL_VALUE = "n";
	public static final String ALL = "ALL";
	public static final String LAST = "LAST";

	public static final String NAME_SPACE = "::";
	public static final String NAME_SEPARATOR  = ".";
	public static final String NAME_SEPARATOR_PATTERN  = "\\.";
	public static final String DEFAULT_GLYPH_TYPE = "SHAPE";
	
	public static final String GLOBALS_FRAME = "global";
	public static final String VIEW_FRAME = "view";
	public static final String CANVAS_FRAME = "canvas";
	public static final String LOCAL_FRAME = "local";
	public static final String PREFILTER_FRAME = "prefilter";
	public static final String STREAM_FRAME = "stream";
	
	public static final String SOURCE_FIELD = "Source";
	public static final String VALUES_FIELD = "Values";
	
	public static final String BIND_OPERATOR = ":";
	
	public static final String TRUE_STRING = "TRUE";
	public static final String FALSE_STRING = "FALSE";
	
	
	public static final StencilTree DEFAULT_CANVAS_SPECIALIZER;
	public static final StencilTree DEFAULT_LAYER_SPECIALIZER;
	
	/**Common safe specializer:
	 *    * Memory is preserved (range: ALL)
	 *    * No split is to be performed (split: 0)
	 */
	public static final Specializer EMPTY_SPECIALIZER;
		
	static {
		try {
			EMPTY_SPECIALIZER = ParseStencil.specializer("[]");
			DEFAULT_CANVAS_SPECIALIZER = ParseStencil.specializerTree("[BACKGROUND_COLOR: \"white\"]");
			DEFAULT_LAYER_SPECIALIZER =  ParseStencil.specializerTree(String.format("[%1$s: \"SHAPE\"]", DisplayLayer.TYPE_KEY));
		} catch (Throwable e) {throw new Error("Error creating reference specializer.", e);}
	}

}
