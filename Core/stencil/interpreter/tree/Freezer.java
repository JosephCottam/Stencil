package stencil.interpreter.tree;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.interpreter.TupleStore;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.MonitorOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.operator.wrappers.LayerOperator;
import stencil.parser.string.CompletePrototypeTypes;
import stencil.parser.string.StencilParser;
import stencil.parser.string.util.Utilities;
import stencil.parser.tree.OperatorProxy;
import stencil.parser.tree.Const;
import stencil.parser.tree.StencilTree;
import stencil.tuple.prototype.TuplePrototype;
import stencil.types.Converter;

import static stencil.parser.string.StencilParser.*;


//TODO: Remove asserts...they off by default so they don't help much.  Keep the test, drop the assert.
public final class Freezer {
	public static Object VALUE_ALL = new String("ALL");	//New strings because these need to be distinct from other "random" usages of the word, but still have these contents

	/**Indicates when extending that the original or default value should be used, not the one present in the update values.
	 * This is used to enable consumes blocks with different results prototypes in the same layer.
	 */
	public static final Object NO_UPDATE = new Object() {@Override
	public String toString() {return "**VALUE: NO UPDATE**";}};
	
	/**A value to indicate that the default value should be used instead.**/
	public static final Object VALUE_DEFAULT = new Object() {@Override
	public String toString() {return "**VALUE: DEFAULT**";}};
	
	private Freezer() {}

	public static boolean verifyType(StencilTree tree, int... types) {
		if (tree == null) {return true;}  //Null is always a tree of "that" type
		for (int type: types) {
			if (tree.is(type)) {return true;}
		}
		throw new Error("Found " + StencilTree.typeName(tree.getType()));
	}

	
	public static Object freezeValue(StencilTree tree) {
		try {
			switch (tree.getType()) {
			case ALL: return VALUE_ALL;
			case CONST: return constant(tree);
			case DEFAULT: return VALUE_DEFAULT;
			case ID: return tree.getText();
			case NUMBER: return number(tree);
			case NULL: return null;
			case STRING: return string(tree);
			case TUPLE_REF: return tupleRef(tree);
			case OP_AS_ARG: return multiName(tree.find(OP_NAME));
			case TRUE: return true;
			case FALSE: return false; 
			case StencilParser.NO_UPDATE: return NO_UPDATE;
			default: throw new RuntimeException("Unknown value freeze requested " + StencilTree.typeName(tree.getType()));
			}
		} catch (FreezeException fe) {throw fe;}
		catch (Exception e) {throw new FreezeException(tree, e);}
	}
	
	public static MultiPartName multiName(StencilTree name) {
		assert verifyType(name, OP_NAME, OPERATOR_BASE);
		String pre = name.getChild(0).is(DEFAULT) ? "" : name.getChild(0).toString();
		String base = name.getChild(1).toString();
		String facet = (name.getChildCount() < 3 || name.getChild(2).is(DEFAULT))
							? null 
							: name.getChild(2).toString();
		return new MultiPartName(pre, base, facet);
	}
	
	public static StreamDef streamDef(StencilTree streamDef) {
		assert verifyType(streamDef, STREAM_DEF);
		final Consumes[] blocks = Freezer.groups(streamDef.find(StencilParser.LIST_CONSUMES));
		final TuplePrototype proto = Freezer.prototype(streamDef.find(TUPLE_PROTOTYPE));

		return new StreamDef(streamDef.getText(), blocks, proto);
	}
	
	public static StreamDec streamDec(StencilTree streamDec) {
		assert verifyType(streamDec, STREAM);
		final Specializer spec = Freezer.specializer(streamDec.find(SPECIALIZER));
		final String type = streamDec.find(ID).getText();
		final String name = streamDec.getText();
		final TuplePrototype proto = Freezer.prototype(streamDec.find(TUPLE_PROTOTYPE));
		return new StreamDec(name, proto, type, spec);
	}
	
	public static Program program(StencilTree program) {
		assert verifyType(program, PROGRAM);
		Order order = order(program.find(ORDER));
		ViewOrCanvas view = viewOrCanvas(program.find(VIEW));
		ViewOrCanvas canvas = viewOrCanvas(program.find(CANVAS));
		Layer[] layers = layers(program.find(LIST_LAYERS));
		StreamDef[] streamDefs = streamDefs(program.find(LIST_STREAM_DEFS));
		StreamDec[] streamDecs = streamDecs(program.find(LIST_STREAM_DECLS));
		
		List<StencilTree> dynamicRules = program.findAllDescendants(DYNAMIC_RULE);
		final DynamicRule[] dynamics = new DynamicRule[dynamicRules.size()];
		for (int i=0; i< dynamics.length; i++) {dynamics[i] = dynamicRule(dynamicRules.get(i));}
		
		List<StencilTree> guideDefs = program.findAllDescendants(GUIDE);
		final Guide[] guides = new Guide[guideDefs.size()];
		for (int i=0; i< guides.length;i++) {guides[i]=guide(guideDefs.get(i));}
		
		Object[] operators = operators(program);
		
		return new Program(view, canvas, layers, streamDecs, streamDefs, order, dynamics, guides, operators);
	}
	
	private static Object[] operators(StencilTree program) {
		assert verifyType(program, PROGRAM);
		
		StencilTree opTrees = program.find(LIST_OPERATORS);
		Object[] ops = new Object[opTrees.getChildCount()];
		for (int i=0; i< ops.length; i++) {
			StencilTree op = opTrees.getChild(i);
			if (op.is(OPERATOR_PROXY)) {
				ops[i] = ((OperatorProxy) op).getOperator();
			} else {
				ops[i] = null;	//TODO: Get the SyntheticOperator into the tree, this ops[] can be the basis for the operators tuple and reworking the higher-order ops 
			}
		}
		return ops;
	}
	
	public static Guide guide(StencilTree guide) {
		assert verifyType(guide, GUIDE);
		final String id= guide.getText();
		final String type = guide.find(ID).getText();
		final Specializer selectors = selectors(guide.find(LIST_SELECTORS));
		final MonitorOperator[] monitorOps = typedArray(guide.find(LIST_GUIDE_MONITORS), MonitorOperator.class, "monitorOperator"); 
		final SampleOperator[] sampleOps = typedArray(guide.find(LIST_GUIDE_SAMPLERS), SampleOperator.class, "sampleOperator");
		final StateQuery query = stateQuery(guide.find(STATE_QUERY));
		final Rule[] generators = typedArray(guide.findDescendant(LIST_GUIDE_GENERATORS), Rule.class, "guideGenerator");
		final Specializer spec = specializer(guide.find(SPECIALIZER));
		final Rule rules = ruleFromList(guide.find(LIST_RULES));
		
		return new Guide(id, type, selectors, rules, monitorOps, sampleOps, query, generators, spec);
	}
	public static Specializer selectors(StencilTree selectors) {
		String[] keys = new String[selectors.getChildCount()];
		String[] values = new String[selectors.getChildCount()];
		
		for (int i=0; i< selectors.getChildCount(); i++) {
			keys[i] = selectors.getChild(i).getText();
			values[i] = selectors.getChild(i).find(SAMPLE_TYPE).getText();
		}
		return new Specializer(keys, values);
	}

	public static Rule guideGenerator(StencilTree generator) {
		assert verifyType(generator, GUIDE_GENERATOR);
		return rule(generator.find(RULE));
	}
	
	public static MonitorOperator monitorOperator(StencilTree root) {
		assert verifyType(root, MONITOR_OPERATOR);
		return (MonitorOperator) Utilities.findOperator(root, root.getText());
	}
	
	public static SampleOperator sampleOperator(StencilTree root) {
		assert verifyType(root, CONST);
		assert verifyType(root.getParent(), LIST_GUIDE_SAMPLERS);
		return (SampleOperator) ((Const) root).getValue();
	}
	
	
	
	public static Layer[] layers(StencilTree root) {
		assert verifyType(root, LIST_LAYERS);
		return typedArray(root, Layer.class, "layer");
	}
	
	public static StreamDef[] streamDefs(StencilTree root) {
		assert verifyType(root, LIST_STREAM_DEFS);
		return typedArray(root, StreamDef.class, "streamDef");
	}
	
	public static StreamDec[] streamDecs(StencilTree root) {
		assert verifyType(root, LIST_STREAM_DECLS);
		return typedArray(root, StreamDec.class, "streamDec");
	}

	
	private static <T> T[] typedArray(StencilTree root, Class<T> type, String freezerName) throws FreezeException {return typedArray(root, root.getChildCount(), type, freezerName);}
	private static <T> T[] typedArray(Iterable<StencilTree> root, int size, Class<T> type, String freezerName) throws FreezeException {
		try {
			Method freezer = Freezer.class.getMethod(freezerName, StencilTree.class);

			Object value = Array.newInstance(type, size);
			int i=0;
			for (StencilTree child: root) {
				try {Array.set(value, i, freezer.invoke(null, child));}
				catch (Exception e) {throw new FreezeException(child, e);}
				i++;
			}
			return (T[]) value;
		} 
		catch (FreezeException fe) {throw fe;}
		catch (Exception e) {
			if (root instanceof StencilTree) {throw new FreezeException((StencilTree) root, e);}
			else {throw new FreezeException(e);}			
		}
	}
	
	//TODO: Merge ViewOrCanvas with Layer
	public static ViewOrCanvas viewOrCanvas(StencilTree source) {
		if (source == null) {return null;}
		assert verifyType(source, VIEW, CANVAS);	
		
		String name = source.getText();
		Specializer spec = specializer(source.find(SPECIALIZER));
		Consumes[] groups = groups(source.find(StencilParser.LIST_CONSUMES));
		TupleStore impl = (TupleStore) ((Const) source.find(CONST)).getValue();
		return new ViewOrCanvas(impl, name, spec, groups);
	}
	
	public static Layer layer(StencilTree layer) {
		assert verifyType(layer, LAYER);
		String name = layer.getText();
		Specializer spec = specializer(layer.find(SPECIALIZER));
		Consumes[] groups = groups(layer.find(StencilParser.LIST_CONSUMES));
		LayerOperator op = (LayerOperator) Utilities.findOperator(layer, layer.getText());
		DisplayLayer impl = op.layer();			

		return new Layer(impl, name, spec, groups);
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
			invokeables.add(invokeable(func.find(OP_NAME)));
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
		DynamicRule[] dynamics = dynamicRuleList(consumes.find(RULES_DYNAMIC));
		return new Consumes(consumes.getChildIndex(), stream, filters, prefilter, local, results, dynamics);
		
	}
	
	public static DynamicRule[] dynamicRuleList(StencilTree dynamics) {
		if (dynamics == null) {return new DynamicRule[0];}
		verifyType(dynamics, RULES_DYNAMIC);
		
		DynamicRule[] results = new DynamicRule[dynamics.getChildCount()];
		for (int i=0; i< results.length; i++) {
			results[i] = dynamicRule(dynamics.getChild(i));
		}
		return results;
	}
	
	public static DynamicRule dynamicRule(StencilTree dynamic) {
		assert verifyType(dynamic, DYNAMIC_RULE);
		CallChain chain = Freezer.chain(dynamic.find(RULE).find(CALL_CHAIN));
		Target target = Freezer.target(dynamic.find(RULE).find(TARGET));
		StateQuery stateQuery = Freezer.stateQuery(dynamic.find(STATE_QUERY));
		int groupID = dynamic.getAncestor(CONSUMES).getChildIndex();
		String layer = dynamic.getAncestor(LAYER).getText();
		return new DynamicRule(layer, groupID, target, chain, stateQuery);
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
			invs[i] = invokeable(child);
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

	public static TupleField tupleField(StencilTree longName) {
		assert verifyType(longName, TUPLE_FIELD);
		String[] parts = new String[longName.getChildCount()];
		for (int j=0; j<parts.length; j++) {
			parts[j] = longName.getChild(j).getText();
		}
		return new TupleField(parts);
	}
	
	public static TargetTuple targetTuple(StencilTree tuple) {
		assert verifyType(tuple, TARGET_TUPLE);
		final TupleField[] names = new TupleField[tuple.getChildCount()];
		for (int i=0; i< names.length; i++) {
			names[i] = tupleField(tuple.getChild(i));
		}
		return new TargetTuple(names);
	}
	
	public static TuplePrototype prototype(StencilTree prototype) {
		assert verifyType(prototype, TUPLE_PROTOTYPE);
		final String[] names = new String[prototype.getChildCount()];
		final Class[] types = new Class[names.length];
		for (int i=0; i< names.length; i++) {
			StencilTree field = prototype.getChild(i);
			assert verifyType(field, TUPLE_FIELD_DEF);
			names[i] = field.getChild(0).getText();
			
			try {types[i] = CompletePrototypeTypes.getType(field.getChild(1).getText());}
			catch (ClassNotFoundException e) {throw new FreezeException(prototype, e);}
		}
		return new TuplePrototype(names, types);
	}
	
	public static Object[] valueList(StencilTree list) {
		assert verifyType(list, LIST_ARGS, PACK);

		final Object[] rv = new Object[list.getChildCount()];
		for (int i=0; i<rv.length; i++) {
			rv[i] = freezeValue(list.getChild(i));
		}
		return rv;
	}
	
	public static Rule ruleFromList(StencilTree rulesRoot) {
		assert verifyType(rulesRoot, LIST_RULES, RULES_DEFAULTS, RULES_DYNAMIC, RULES_FILTER, RULES_LOCAL, RULES_OPERATOR, RULES_PREDICATES, RULES_PREFILTER, RULES_RESULT);
		if (rulesRoot.getChildCount() ==0) {return Rule.EMPTY_RULE;}
		if (rulesRoot.getChildCount() >1) {throw new IllegalArgumentException("Can only freeze rules lists of length 1, given list of size " + rulesRoot.getChildCount());}
		return rule(rulesRoot.getChild(0));
	}
		
	public static Rule rule(StencilTree rule) {
		assert verifyType(rule, RULE);
	
		StringBuilder path = new StringBuilder();
		StencilTree root = rule;
		while (!root.is(PROGRAM)) {
			path.append(root.getText() + " <-- ");
			root = root.getParent();
		}
		path.append(root.getText());
	
		return  new Rule(path.toString(), chain(rule.find(CALL_CHAIN)), target(rule.find(TARGET)));
	}
	
	
	public static Target target(StencilTree target) {
		assert verifyType(target, TARGET); // RESULT, LOCAL, PREFILTER;
		try {
			TargetTuple tt = targetTuple(target.find(TARGET_TUPLE));
			return new Target(tt);
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
		Invokeable inv = Utilities.findOperator(predicate, predicate.getText()).getFacet("query");
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
	
	public static Invokeable invokeable(StencilTree opName) {
		assert verifyType(opName, OP_NAME);
		MultiPartName name = multiName(opName);
		try{return Utilities.findOperator(opName).getFacet(name.facet());}
		catch (Exception e) {throw new FreezeException(opName, e);}
	}
	
	public static Specializer specializer(StencilTree spec) {
		verifyType(spec, SPECIALIZER);
		
		List<StencilTree> entries = spec.findAll(MAP_ENTRY);
		
		String[] keys = new String[entries.size()];
		Object[] vals = new Object[entries.size()];
		
		try {
			for (int i=0; i< keys.length; i++) {
				StencilTree entry = entries.get(i);
				assert verifyType(entry, MAP_ENTRY);
				assert entry.getChildCount() == 1 : "Malformed MAP_ENTRY found" + entry.toStringTree();
				keys[i] = entry.getText();
				vals[i] = freezeValue(entry.getChild(0));
			}
			return new Specializer(keys, vals);
		} catch (Exception e) {throw new FreezeException(spec, e);}
	}
}
