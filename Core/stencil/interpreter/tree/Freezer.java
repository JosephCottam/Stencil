package stencil.interpreter.tree;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.MonitorOperator;
import stencil.module.operator.util.Invokeable;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.AstInvokeable;
import stencil.parser.tree.Const;
import stencil.parser.tree.StencilTree;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

import static stencil.parser.string.StencilParser.*;

public final class Freezer {
	public static Object VALUE_ALL = new String("ALL");	//New strings because these need to be distinct from other "random" usages of the word, but still have these contents
	public static Object VALUE_LAST = new String("LAST");
	
	private Freezer() {}

	public static boolean verifyType(StencilTree tree, int... types) {
		for (int type: types) {
			if (tree.getType() == type) {return true;}
		}
		throw new Error("Found " + StencilTree.typeName(tree.getType()));
	}

	
	public static Object freeze(StencilTree tree) {
		try {
			switch (tree.getType()) {
			case ALL: return VALUE_ALL;
			case AST_INVOKEABLE: return invokeable(tree);
			case CONST: return constant(tree);
			case CONSUMES: return consumes(tree);
			case ID: return tree.getText();
			case LIST_ARGS: return valueList(tree);
			case LAST: return VALUE_LAST;
			case NUMBER: return number(tree);
			case NULL: return null;
			case OPERATOR_FACET: return operatorFacet("**UNKNOWN**", (StencilTree) tree);
			case RULE: return rule(tree);
			case SPECIALIZER: return specializer(tree);
			case STATE_QUERY: return stateQuery(tree);
			case STRING: return string(tree);
			case TUPLE_PROTOTYPE: return prototype(tree);
			case TUPLE_REF: return tupleRef(tree);
			case OP_AS_ARG: return tree.getText();
			default: throw new RuntimeException("Cannot generically freeze type " + StencilTree.typeName(tree.getType()));
			}
		} catch (FreezeException fe) {throw fe;}
		catch (Exception e) {throw new FreezeException(tree, e);}
	}
	
	public static StreamDef streamDef(StencilTree streamDef) {
		assert verifyType(streamDef, STREAM_DEF);
		final Consumes[] blocks = Freezer.groups(streamDef.find(StencilParser.LIST_CONSUMES));
		final TuplePrototype proto = Freezer.prototype(streamDef.find(TUPLE_PROTOTYPE));

		return new StreamDef(streamDef.getText(), blocks, proto);
	}
	
	public static Program program(StencilTree program) {
		assert verifyType(program, PROGRAM);
		Order order = order(program.find(ORDER));
		Layer[] layers = layers(program.find(LIST_LAYERS));
		StreamDef[] streams = streams(program.find(LIST_STREAM_DEFS));
		Specializer canvasSpec = specializer(program.find(CANVAS_DEF).find(SPECIALIZER));
		
		List<StencilTree> dynamicRules = program.findAllDescendants(DYNAMIC_RULE);
		final DynamicRule[] dynamics = new DynamicRule[dynamicRules.size()];
		for (int i=0; i< dynamics.length; i++) {dynamics[i] = dynamicRule(dynamicRules.get(i));}
		
		List<StencilTree> guideDefs = program.findAllDescendants(GUIDE);
		final Guide[] guides = new Guide[guideDefs.size()];
		for (int i=0; i< guides.length;i++) {guides[i]=guide(guideDefs.get(i));}
		
		return new Program(canvasSpec, layers, streams, order, dynamics, guides);
	}
	
	public static Guide guide(StencilTree guide) {
		assert verifyType(guide, GUIDE);
		final String id= guide.find(SELECTOR).getText();
		final String type = guide.find(ID).getText();
		final MonitorOperator[] monitorOps = typedArray(guide.find(LIST_GUIDE_MONITORS), MonitorOperator.class, "monitorOperator"); 
		final SampleOperator[] sampleOps = typedArray(guide.find(LIST_GUIDE_SAMPLERS), SampleOperator.class, "sampleOperator");
		final StateQuery query = stateQuery(guide.find(STATE_QUERY));
		final Rule[] generators = typedArray(guide.findDescendant(LIST_GUIDE_GENERATORS), Rule.class, "guideGenerator");
		final Specializer spec = specializer(guide.find(SPECIALIZER));
		final Rule rules = ruleFromList(guide.find(LIST_RULES));
		
		return new Guide(id, type, rules, monitorOps, sampleOps, query, generators, spec);
	}

	public static Rule guideGenerator(StencilTree generator) {
		assert verifyType(generator, GUIDE_GENERATOR);
		return rule(generator.find(RULE));
	}
	
	public static MonitorOperator monitorOperator(StencilTree root) {
		assert verifyType(root, MONITOR_OPERATOR);
		return (MonitorOperator) ((Const) root.find(CONST)).getValue();
	}
	
	public static SampleOperator sampleOperator(StencilTree root) {
		assert verifyType(root, SAMPLE_OPERATOR);
		return (SampleOperator) ((Const) root.find(CONST)).getValue();
	}
	
	
	
	public static Layer[] layers(StencilTree root) {
		assert verifyType(root, LIST_LAYERS);
		return typedArray(root, Layer.class, "layer");
	}
	
	public static StreamDef[] streams(StencilTree root) {
		assert verifyType(root, LIST_STREAM_DEFS);
		return typedArray(root, StreamDef.class, "streamDef");
	}
	
	private static <T> T[] typedArray(StencilTree root, Class<T> type, String freezerName) throws FreezeException {
		try {
			Method freezer = Freezer.class.getMethod(freezerName, StencilTree.class);

			Object value = Array.newInstance(type, root.getChildCount());
			for (int i=0;i < root.getChildCount(); i++) {
				try {Array.set(value, i, freezer.invoke(null, root.getChild(i)));}
				catch (Exception e) {throw new FreezeException(root.getChild(i), e);}
			}
			return (T[]) value;
		} catch (Exception e) {throw new FreezeException(root, e);}
	}
	
	public static Layer layer(StencilTree layer) {
		assert verifyType(layer, LAYER);
		String name = layer.getText();
		Consumes[] groups = Freezer.groups(layer.find(StencilParser.LIST_CONSUMES));
		DisplayLayer impl = (DisplayLayer) ((Const) layer.find(CONST)).getValue();
		return new Layer(impl, name, groups);
	}
	
	public static Order order(StencilTree order) {
		assert verifyType(order, ORDER);
		String[][] clauses = new String[order.getChildCount()][];
		for (int clause=0; clause<order.getChildCount(); clause++) {
			StencilTree list = order.getChild(clause);
			assert verifyType(list, LIST_STREAMS);
			clauses[clause] = new String[list.getChildCount()];
			for (int stream=0; stream< list.getChildCount(); stream++) {
				clauses[clause][stream] = list.getChild(stream).getText();
			}
		}		
		return new Order(clauses);
	}
	
	public static CallChain chain(StencilTree chain) {
		assert verifyType(chain, CALL_CHAIN);
		ArrayList<Invokeable> invokeables = new ArrayList();
		ArrayList<Object[]> args = new ArrayList();
		for (StencilTree func: chain.findAllDescendants(FUNCTION)) {
			invokeables.add(invokeable(func.find(AST_INVOKEABLE)));
			args.add(valueList(func.find(LIST_ARGS)));
		}
		Invokeable[] invs = invokeables.toArray(new Invokeable[invokeables.size()]);
		Object[][] argss = args.toArray(new Object[args.size()][]);
		Object[] pack = valueList(chain.findDescendant(PACK));		
		
		return new CallChain(invs, argss, pack);
	}
	
	public static Consumes[] groups(StencilTree source) {
		assert verifyType(source, LIST_CONSUMES);
		Consumes[] consumes = new Consumes[source.getChildCount()];
		for (int i=0; i<consumes.length; i++) {
			consumes[i] = Freezer.consumes(source.getChild(i));
		}
		return consumes;
	}
	
	public static Consumes consumes(StencilTree consumes) {
		assert verifyType(consumes, CONSUMES);
		
		String stream = consumes.getText();
		Predicate[] filters = predicateList(consumes.find(LIST_FILTERS));
		Rule prefilter = ruleFromList(consumes.find(RULES_PREFILTER));
		Rule local = ruleFromList(consumes.find(RULES_LOCAL));
		Rule results = ruleFromList(consumes.find(RULES_RESULT));
		Rule view = ruleFromList(consumes.find(RULES_VIEW));
		Rule canvas = ruleFromList(consumes.find(RULES_CANVAS));
		DynamicRule[] dynamics = dynamicRuleList(consumes.find(RULES_DYNAMIC));
		Object[] reducer = valueList(consumes.find(DYNAMIC_REDUCER).find(PACK));
		return new Consumes(consumes.getChildIndex(), stream, filters, prefilter, local, results, view, canvas, dynamics, reducer);
		
	}
	
	public static DynamicRule[] dynamicRuleList(StencilTree dynamics) {
		assert verifyType(dynamics, RULES_DYNAMIC);
		DynamicRule[] results = new DynamicRule[dynamics.getChildCount()];
		for (int i=0; i< results.length; i++) {
			results[i] = dynamicRule(dynamics.getChild(i));
		}
		return results;
	}
	
	public static DynamicRule dynamicRule(StencilTree dynamic) {
		assert verifyType(dynamic, DYNAMIC_RULE);
		Rule rule = Freezer.rule(dynamic.find(RULE));
		StateQuery stateQuery = Freezer.stateQuery(dynamic.find(STATE_QUERY));
		int groupID = dynamic.getAncestor(CONSUMES).getChildIndex();
		String layer = dynamic.getAncestor(LAYER).getText();
		return new DynamicRule(layer, groupID, rule, stateQuery);
	}
	
	public static OperatorFacet operatorFacet(String opName, StencilTree facet) {
		assert verifyType(facet, OPERATOR_FACET);
		
		String name = facet.getText();
		//TODO: Add an "arguments" and "results" tree node to simplify finding these values pre-freeze
		TuplePrototype arguments = prototype(facet.find(YIELDS).getChild(0));
		TuplePrototype results = prototype(facet.find(YIELDS).getChild(1));
		Rule prefilters = ruleFromList(facet.find(RULES_PREFILTER));
		OperatorRule[] opRules = opRuleList(opName, facet.find(RULES_OPERATOR));
		return new OperatorFacet(name, arguments, results, prefilters, opRules);
	}
	
	public static StateQuery stateQuery(StencilTree stateQuery) {
		assert verifyType(stateQuery, STATE_QUERY);
		final Invokeable[] invs = new Invokeable[stateQuery.getChildCount()];
		for (int i=0; i< stateQuery.getChildCount(); i++) {
			StencilTree child = stateQuery.getChild(i);
			assert verifyType(child, AST_INVOKEABLE);
			invs[i] = ((AstInvokeable) child).getInvokeable();
		}
		return new StateQuery(invs);
	}
	
	public static TupleRef tupleRef(StencilTree tupleRef) {
		assert verifyType(tupleRef, TUPLE_REF);
		final int[] steps = new int[tupleRef.getChildCount()];
		for (int i=0; i< steps.length; i++) {
			steps[i] = number(tupleRef.getChild(i)).intValue();
		}
		return new TupleRef(steps);
	}
	
	public static TuplePrototype prototype(StencilTree prototype) {
		assert verifyType(prototype, TUPLE_PROTOTYPE);
		final String[] names = new String[prototype.getChildCount()];
		final Class[] types = new Class[names.length];
		for (int i=0; i< names.length; i++) {
			StencilTree field = prototype.getChild(i);
			assert verifyType(field, TUPLE_FIELD_DEF);
			names[i] = field.getChild(0).getText();
			types[i] = Object.class;

			//TODO: add pass to fill in full type names for common types
			//try {types[i] = Class.forName(field.getChild(1).getText());}
			//catch (ClassNotFoundException e) {throw new IllegalArgumentException("Could not freeze prototype (unknown type): " + prototype.toStringTree());}
		}
		return new SimplePrototype(names, types);
	}
	
	
	public static Object[] valueList(StencilTree list) {
		assert verifyType(list, LIST_ARGS, PACK);

		final Object[] rv = new Object[list.getChildCount()];
		for (int i=0; i<rv.length; i++) {
			rv[i] = freeze(list.getChild(i));
		}
		return rv;
	}
	
	public static Rule ruleFromList(StencilTree rulesRoot) {
		assert verifyType(rulesRoot, LIST_RULES, RULES_CANVAS, RULES_DEFAULTS, RULES_DYNAMIC, RULES_FILTER, RULES_LOCAL, RULES_OPERATOR, RULES_PREDICATES, RULES_PREFILTER, RULES_RESULT, RULES_VIEW);
		if (rulesRoot.getChildCount() ==0) {return Rule.EMPTY_RULE;}
		if (rulesRoot.getChildCount() >1) {throw new IllegalArgumentException("Can only freeze rules lists of length 1, given list of size " + rulesRoot.getChildCount());}
		return rule(rulesRoot.getChild(0));
	}
		
	public static Rule rule(StencilTree rule) {
		assert verifyType(rule, RULE);
	
		StringBuilder path = new StringBuilder();
		StencilTree root = rule;
		while (root.getType() != PROGRAM) {
			path.append(root.getText() + " <-- ");
			root = root.getParent();
		}
		path.append(root.getText());
	
		return  new Rule(path.toString(), chain(rule.find(CALL_CHAIN)), target(rule.find(TARGET)));
	}
	
	
	public static Target target(StencilTree target) {
		assert verifyType(target, TARGET, VIEW, CANVAS, RESULT, LOCAL, PREFILTER);
		try {
			TuplePrototype proto = prototype(target.find(TUPLE_PROTOTYPE));
			return new Target(proto);
		} catch (Exception e) {throw new FreezeException(target, e);}
	}
	
	public static OperatorRule[] opRuleList(String opName, StencilTree rulesRoot) {
		try {
			OperatorRule[] rules = new OperatorRule[rulesRoot.getChildCount()];
			for (int i=0; i< rulesRoot.getChildCount(); i++) {
				rules[i] = operatorRule(opName, rulesRoot.getChild(i));
			}
			return rules;
		} catch (Exception e) {throw new FreezeException(rulesRoot, e);}
	}
	
	public static OperatorRule operatorRule(String opName, StencilTree opRule) {
		assert verifyType(opRule, OPERATOR_RULE);
		Predicate[] preds = predicateList(opRule.find(LIST_PREDICATES));
		Rule rules = ruleFromList(opRule.find(LIST_RULES));
		try {return new OperatorRule(opName, preds, rules);}
		catch (Exception e) {throw new FreezeException(opRule, e);}
	}
	
	public static Predicate[] predicateList(StencilTree predList) {
		assert verifyType(predList, LIST_FILTERS, LIST_PREDICATES);
		Predicate[] preds = new Predicate[predList.getChildCount()];
		for (int i=0; i<preds.length; i++) {
			preds[i] = predicate(predList.getChild(i));
		}
		return preds;
	}
	public static Predicate predicate(StencilTree predicate) {
		assert verifyType(predicate, PREDICATE);
		Invokeable inv = invokeable(predicate.find(AST_INVOKEABLE));
		Object[] args = valueList(predicate.find(LIST_ARGS));
		return new Predicate(inv, args);
	}
	
	public static Object constant(StencilTree constant) {
		assert verifyType(constant, CONST);
		try {return ((Const) constant).getValue();}
		catch (Exception e) {throw new FreezeException(constant, e);}
	}
	
	public static String string(StencilTree tree) {
		assert verifyType(tree, STRING);
		try {return tree.getText();}
		catch (Exception e) {throw new FreezeException(tree, e);}
	}

	public static Number number(StencilTree num) {
		assert verifyType(num, NUMBER);
		try{return Converter.toNumber(num.getText());}
		catch (Exception e) {throw new FreezeException(num, e);}
	}
	
	public static Invokeable invokeable(StencilTree inv) {
		assert verifyType(inv, AST_INVOKEABLE);
		try{return ((AstInvokeable) inv).getInvokeable();}
		catch (Exception e) {throw new FreezeException(inv, e);}
	}
	
	public static Specializer specializer(StencilTree spec) {
		assert verifyType(spec, SPECIALIZER);
		assert spec instanceof StencilTree;
		String[] keys = new String[spec.getChildCount()];
		Object[] vals = new Object[spec.getChildCount()];
		
		try {
			for (int i=0; i< keys.length; i++) {
				StencilTree entry = (StencilTree) spec.getChild(i);
				assert verifyType(entry, MAP_ENTRY);
				assert entry.getChildCount() == 1 : "Malformed MAP_ENTRY found" + entry.toStringTree();
				keys[i] = entry.getText();
				vals[i] = freeze(entry.getChild(0));
			}
			if (spec instanceof StencilTree) {
				return new Specializer(keys, vals, (StencilTree) spec);
			} else {
				return new Specializer(keys, vals);
			}
		} catch (Exception e) {throw new FreezeException(spec, e);}
	}
}
