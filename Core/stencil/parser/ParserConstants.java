package stencil.parser;

import stencil.display.CanvasTuple;
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
	public static final String VISIBLE_FIELD  = "VISIBLE";
	public static final String BOUNDS_FIELD  = "BOUNDS";
	public static final String RENDER_ORDER_FIELD  = "Z";
	public static final String INPUT_FIELD = "Input";
	public static final String X_FIELD = "X";
	public static final String Y_FIELD = "Y";
	public static final String TEXT_FIELD = "TEXT";
	
	public static final String GUIDE_LABEL = "guideLabel";
	public static final String GUIDE_ELEMENT_TAG = "ele";
	public static final String POSITIONAL_ARG = "#POSITIONAL";
	public static final String SEPARATOR = ",";
	public static final String INITIATOR = "(";
	public static final String TERMINATOR = ")";
	public static final String SIGIL = "@";
	
	public static final String RENDER_STREAM = "#Render";


	/**Field a used to store data for dynamic bindings.
	 * Must be included in any schema that supports dynamic binding. 
	 */
	public static final String DYNAMIC_STORE_FIELD = "#DYNAMIC_STORE";
	

	//Block tags
	public static final String MAP_FACET = StencilOperator.MAP_FACET;
	public static final String QUERY_FACET = StencilOperator.QUERY_FACET;
	public static final String STATE_ID_FACET = StencilOperator.STATE_ID_FACET;
	public static final String CUSTOM_PARSER_FACET = "argumentParser";		//TODO: Eliminate CUSTOM_PARSER_FACET, just use Map
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
	public static final String STREAM_FRAME = "Stream";
	
	public static final String SOURCE_FIELD = "Source";
	public static final String VALUES_FIELD = "Values";
	
	public static final String BIND_OPERATOR = ":";
	
	public static final String TRUE_STRING = "TRUE";
	public static final String FALSE_STRING = "FALSE";
	
	
	public static final StencilTree DEFAULT_CANVAS_SPECIALIZER;
	public static final StencilTree DEFAULT_VIEW_SPECIALIZER;
	public static final StencilTree DEFAULT_LAYER_SPECIALIZER;
	
	/**Common safe specializer:
	 *    * Memory is preserved (range: ALL)
	 *    * No split is to be performed (split: 0)
	 */
	public static final StencilTree EMPTY_SPECIALIZER_TREE;
	public static final Specializer EMPTY_SPECIALIZER;
		
	static {
		try {
			EMPTY_SPECIALIZER_TREE = ParseStencil.specializerTree("[]");
			EMPTY_SPECIALIZER = ParseStencil.specializer("[]");
			DEFAULT_VIEW_SPECIALIZER = ParseStencil.specializerTree("[]");
			DEFAULT_CANVAS_SPECIALIZER = ParseStencil.specializerTree("[" + CanvasTuple.BACKGROUND_COLOR +": \"white\"]");
			DEFAULT_LAYER_SPECIALIZER =  ParseStencil.specializerTree(String.format("[%1$s: \"SHAPE\"]", DisplayLayer.TYPE_KEY));
		} catch (Throwable e) {throw new Error("Error creating reference specializer.", e);}
	}
}
