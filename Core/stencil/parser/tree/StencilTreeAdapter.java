package stencil.parser.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class StencilTreeAdapter extends CommonTreeAdaptor {

	@Override
	public Object create(Token token) {
		Object t;

		if (token == null) {return new StencilTree(null);}


		switch(token.getType()){
		//Default for PRE
		case 48	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: CLOSE_GROUP");
		//Default for AS
		case 25	: t = new MapEntry(token); break;		//Token MAP_ENTRY
		case 31	: t = new External(token); break;		//Token EXTERNAL
		case 13	: t = new List(token); break;		//Token LIST
		case 8	: t = new CallGroup(token); break;		//Token CALL_GROUP
		case 34	: t = new Glyph(token); break;		//Token GLYPH
		case 14	: t = new StencilNumber(token); break;		//Token NUMBER
		case 46	: t = new Facet(token); break;		//Token FACET
		//Default for NAMESPACE
		//Default for GATE
		case 44	: t = new View(token); break;		//Token VIEW
		case 20	: t = new Rule(token); break;		//Token RULE
		case 19	: t = new Pack(token); break;		//Token PACK
		case 23	: t = new TuplePrototype(token); break;		//Token TUPLE_PROTOTYPE
		case 35	: t = new Import(token); break;		//Token IMPORT
		case 18	: t = new Program(token); break;		//Token PROGRAM
		case 24	: t = new TupleRef(token); break;		//Token TUPLE_REF
		case 39	: t = new Order(token); break;		//Token ORDER
		//Default for POST
		case 17	: t = new Predicate(token); break;		//Token PREDICATE
		//Default for BASIC
		case 36	: t = new Local(token); break;		//Token LOCAL
		case 10	: t = new Function(token); break;		//Token FUNCTION
		//Default for GUIDE_FEED
		case 11	: t = new Guide(token); break;		//Token GUIDE
		//Default for GROUP
		case 37	: t = new Layer(token); break;		//Token LAYER
		case 38	: t = new Legend(token); break;		//Token LEGEND
		case 57	: t = new Yields(token); break;		//Token YIELDS
		//Default for TAG
		case 63	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: JOIN");
		//Default for BASE
		case 28	: t = new Canvas(token); break;		//Token CANVAS
		case 65	: t = new Id(token); break;		//Token ID
		//Default for FROM
		case 43	: t = new Stream(token); break;		//Token STREAM
		case 52	: t = new Range(token); break;		//Token RANGE
		case 21	: t = new Sigil(token); break;		//Token SIGIL
		case 69	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: DIGITS");
		case 62	: t = new Split(token); break;		//Token SPLIT
		case 32	: t = new Filter(token); break;		//Token FILTER
		//Default for TAGGED_ID
		//Default for CODE_BLOCK
		case 71	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: WS");
		case 68	: t = new StencilString(token); break;		//Token STRING
		case 9	: t = new CallChain(token); break;		//Token CALL_CHAIN
		//Default for NAMESPLIT
		case 12	: t = new LegendRule(token); break;		//Token LEGEND_RULE
		case 72	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: COMMENT");
		case 22	: t = new Specializer(token); break;		//Token SPECIALIZER
		case 50	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: CLOSE_ARG");
		//Default for GUIDE_YIELD
		//Default for STATIC
		case 55	: t = new Define(token); break;		//Token DEFINE
		case 51	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: SEPARATOR");
		case 41	: t = new Return(token); break;		//Token RETURN
		case 70	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: ESCAPE_SEQUENCE");
		case 49	: throw new IllegalArgumentException("Attempted to create tree-node for token on error list: ARG");
		case 7	: t = new Consumes(token); break;		//Token CONSUMES
		//Default for DYNAMIC
		case 40	: t = new Python(token); break;		//Token PYTHON
		//Default for COLOR
		//Default for FEED
		case 4	: t = new Annotation(token); break;		//Token ANNOTATION
		//Default for DEFAULT
		case 5	: t = new BooleanOp(token); break;		//Token BOOLEAN_OP
		case 26	: t = new All(token); break;		//Token ALL

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