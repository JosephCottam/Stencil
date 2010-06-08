package stencil.parser.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class StencilTreeAdapter extends CommonTreeAdaptor {

	@Override
	public Object create(Token token) {
		Object t;

		if (token == null) {return new StencilTree(null);}


		switch(token.getType()){
		case 12	: t = new Function(token); break;		//Token FUNCTION
		case 56	: t = new Layer(token); break;		//Token LAYER
		//Default for LT
		//Default for CONST
		//Default for GUIDE_SUMMARIZATION
		//Default for GUIDE_DIRECT
		case 71	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: SEPARATOR");
		case 41	: t = new TuplePrototype(token); break;		//Token TUPLE_PROTOTYPE
		case 24	: t = new OperatorTemplate(token); break;		//Token OPERATOR_TEMPLATE
		case 43	: t = new TupleRef(token); break;		//Token TUPLE_REF
		case 48	: t = new Canvas(token); break;		//Token CANVAS
		//Default for TYPE
		case 55	: t = new Import(token); break;		//Token IMPORT
		case 8	: t = new CallChain(token); break;		//Token CALL_CHAIN
		case 69	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: ARG");
		case 70	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: CLOSE_ARG");
		//Default for BASE
		//Default for GUIDE_YIELD
		case 57	: t = new Last(token); break;		//Token LAST
		//Default for EQ
		case 100	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: COMMENT");
		case 65	: t = new Stream(token); break;		//Token STREAM
		//Default for GATE
		case 22	: t = new OperatorProxy(token); break;		//Token OPERATOR_PROXY
		case 19	: t = new MapEntry(token); break;		//Token MAP_ENTRY
		case 35	: t = new Rule(token); break;		//Token RULE
		case 66	: t = new View(token); break;		//Token VIEW
		case 68	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: CLOSE_GROUP");
		case 62	: t = new Prefilter(token); break;		//Token PREFILTER
		case 59	: t = new Null(token); break;		//Token NULL
		case 20	: t = new StencilNumber(token); break;		//Token NUMBER
		//Default for OPERATOR_INSTANCE
		case 58	: t = new Local(token); break;		//Token LOCAL
		case 18	: t = new List(token); break;		//Token LIST
		//Default for NAMESPACE
		case 7	: t = new Consumes(token); break;		//Token CONSUMES
		case 87	: t = new Yields(token); break;		//Token YIELDS
		//Default for TUPLE_VALUE
		//Default for ANIMATED_DYNAMIC
		case 37	: t = new StateQuery(token); break;		//Token STATE_QUERY
		//Default for GROUP
		case 99	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: WS");
		case 25	: t = new OperatorRule(token); break;		//Token OPERATOR_RULE
		//Default for FILTER
		//Default for TAGGED_ID
		//Default for GT
		//Default for FROM
		//Default for DEFAULT_VALUE
		case 32	: t = new Pack(token); break;		//Token PACK
		//Default for DYNAMIC
		//Default for FOLD
		//Default for FACET
		//Default for RE
		case 9	: t = new CanvasDef(token); break;		//Token CANVAS_DEF
		case 61	: t = new Order(token); break;		//Token ORDER
		//Default for ANNOTATION
		case 96	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: DIGITS");
		//Default for GTE
		case 33	: t = new PythonFacet(token); break;		//Token PYTHON_FACET
		//Default for PRE
		case 23	: t = new OperatorReference(token); break;		//Token OPERATOR_REFERENCE
		case 92	: t = new Id(token); break;		//Token ID
		//Default for GUIDE_GENERATOR
		//Default for DEFINE
		//Default for DIRECT_YIELD
		//Default for LTE
		case 30	: t = new Predicate(token); break;		//Token PREDICATE
		case 98	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: ESCAPE_SEQUENCE");
		//Default for AS
		case 39	: t = new Selector(token); break;		//Token SELECTOR
		//Default for ISLAND_BLOCK
		case 54	: t = new Guide(token); break;		//Token GUIDE
		case 42	: t = new TupleFieldDef(token); break;		//Token TUPLE_FIELD_DEF
		case 63	: t = new Python(token); break;		//Token PYTHON
		case 45	: t = new All(token); break;		//Token ALL
		//Default for ANIMATED
		//Default for NRE
		//Default for NESTED_BLOCK
		case 4	: t = new AstInvokeable(token); break;		//Token AST_INVOKEABLE
		case 60	: t = new Operator(token); break;		//Token OPERATOR
		//Default for DEFAULT
		case 34	: t = new Result(token); break;		//Token RESULT
		//Default for TAG
		//Default for NEQ
		case 26	: t = new OperatorBase(token); break;		//Token OPERATOR_BASE
		//Default for TEMPLATE
		case 11	: t = new DynamicRule(token); break;		//Token DYNAMIC_RULE
		//Default for MAP
		case 38	: t = new Specializer(token); break;		//Token SPECIALIZER
		//Default for POST
		case 31	: t = new Program(token); break;		//Token PROGRAM
		//Default for GLYPH_TYPE
		//Default for SIGIL_ARGS
		case 27	: t = new OperatorFacet(token); break;		//Token OPERATOR_FACET
		//Default for BASIC
		case 95	: t = new StencilString(token); break;		//Token STRING
		case 40	: t = new StreamDef(token); break;		//Token STREAM_DEF

		default : t = new StencilTree(token); break;
		}
		return t;
	}

	@Override
	public Object dupNode(Object t) {
		// TODO Auto-generated method stub
		return super.dupNode(t);
	}
}