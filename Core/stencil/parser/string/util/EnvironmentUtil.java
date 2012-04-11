package stencil.parser.string.util;


import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.adapters.java2D.LayerTypeRegistry;
import stencil.display.DisplayLayer;
import stencil.interpreter.Environment;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.samplers.LayerSampler;
import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.module.operator.StencilOperator;
import stencil.parser.tree.*;
import java.util.List;
import java.util.ArrayList;

import static stencil.tuple.prototype.TuplePrototypes.EMPTY_PROTOTYPE;
import static stencil.parser.string.StencilParser.*;
import static stencil.parser.ParserConstants.INPUT_FIELD;

/**The environment proxy is used during compilation to mimic the shape of, 
 * but not the content of, runtime environment.
 * 
 * It carries prototype information instead of values; as such, it also
 * contains some compilation-specified prototype related utilities.
 * 
 * @author jcottam
 *
 */
public final class EnvironmentUtil {
	private static final TuplePrototype NO_PROTOTYPE = new TuplePrototype();

	/**Utility object for calculating, passing and referring 
	 * to potential ancestors of a tree.
	 */
	private static final class AncestrySummary {
		final StencilTree consumes;
		final StencilTree operatorFacet;
		final StencilTree guide;
		final StencilTree layer;
		final StencilTree program;
		final boolean inRuleList;
		final boolean inGuideGenerator;
		final boolean inDynamicRule;

		public AncestrySummary(StencilTree t) {
			consumes = t.getAncestor(CONSUMES);
			operatorFacet = t.getAncestor(OPERATOR_FACET);
			guide = t.getAncestor(GUIDE);
			layer = t.getAncestor(LAYER);

			StencilTree r= t.getAncestor(RULE);
			inDynamicRule = (r == null || consumes == null) ? false : r.getAncestor(DYNAMIC_RULE) != null;
			inGuideGenerator = t.getAncestor(LIST_GUIDE_GENERATORS) != null;

			//HACK: The startWith call depends on an incidental property of my naming convention...it could change and there would be no warning
			inRuleList = (r==null) ? false : 
				r.getParent().getType() == LIST_RULES 
				|| StencilTree.typeName(r.getParent().getType()).startsWith("RULES");	

			program = t.getAncestor(PROGRAM);
		}
	}

	/**Get the  tuple prototype of the stream-like entity for the given node.
	 * Stream-like entities include the implicit guide stream, the input stream to an operator, etc.
	 * If no stream-like entity exists, an empty prototype is returned.
	 */
	public static TuplePrototype streamFor(StencilTree node) {
		AncestrySummary anc = new AncestrySummary(node);		

		if (anc.consumes == null && anc.layer != null && anc.guide == null) {return NO_PROTOTYPE;}//This is the characteristic of the defaults block

		if (anc.consumes != null) {
			if (!anc.inDynamicRule) {
				StencilTree stream = findStream(anc.consumes.getText(), anc.program.find(LIST_STREAM_DECLS));
				return Freezer.prototype(stream.find(TUPLE_PROTOTYPE));
			} else {
				return EMPTY_PROTOTYPE;	//Any sub-ref will be different if in a dynamic rule, but direct refs are numeralized in reducer creation
			}
		}
		if (anc.operatorFacet != null) {
			return Freezer.prototype(anc.operatorFacet.find(YIELDS).getChild(0));
		}

		if (anc.guide != null) {

			//Get the generic streams when there is no more detailed info 
			if (anc.guide.findAllDescendants(MONITOR_OPERATOR).size() ==0) {
				if (anc.guide.getAncestor(GUIDE_DIRECT) != null) {
					return new TuplePrototype(INPUT_FIELD, "OUTPUT");
				} else {
					return layerPrototype(anc.layer);
				}
			}

			if (anc.inRuleList && (((Const) (anc.guide.findDescendant(LIST_GUIDE_SAMPLERS).getChild(0))).getValue() instanceof LayerSampler)) {
				LayerSampler sampler = (LayerSampler) ((Const) (anc.guide.findDescendant(LIST_GUIDE_SAMPLERS).getChild(0))).getValue();
				return sampler.prototype();
			} else if (anc.inGuideGenerator) {
				return SampleOperator.Util.prototype(anc.guide.find(LIST_GUIDE_SAMPLERS).getChildCount());
			} else if (anc.inRuleList) {
				List<StencilTree> tts = anc.guide.find(LIST_GUIDE_GENERATORS).findAllDescendants(TARGET_TUPLE);
				return asPrototype(tts);
			}
		}
		throw new RuntimeException(String.format("Could not calculate stream proxy for %1$s at %2$s", node.toString(), Path.toString(node)));
	}

	private static StencilTree findStream(String name, Iterable<StencilTree> externals) {
		for (StencilTree s: externals) {if (s.getText().equals(name)) {return s;}}
		throw new RuntimeException("Could not find stream of name " + name + ".");
	}


	/**Given a set of rules, what will the prototype of the results be?*/
	public static TuplePrototype calcPrototype(Iterable<StencilTree> rules) {
		List<StencilTree> tts = new ArrayList();
		for (StencilTree r: rules) {
			StencilTree target = r.find(TARGET, RESULT, VIEW, CANVAS, LOCAL, PREFILTER);
			StencilTree tt = target.find(TARGET_TUPLE);
			tts.add(tt);
		}
		return asPrototype(tts);
	}

	private static TuplePrototype asPrototype(List<StencilTree> tts) {
		final TuplePrototype[] prototypes = new TuplePrototype[tts.size()];
		for(int i=0; i< prototypes.length; i++) {
			prototypes[i] = Freezer.targetTuple(tts.get(i)).asPrototype();
		}
		return TuplePrototypes.append(prototypes);
	}


	/**Given a tuple reference, what is the prototype of the frame referred to?
	 * Can resolve numeric frame refs or named frame refs.
	 * */
	public static TuplePrototype framePrototypeFor(StencilTree tupleRef) {
		StencilTree frameRef = tupleRef.getChild(0);
		int frameNum =-1;

		if (frameRef.getType() == NUMBER) {
			frameNum = Integer.parseInt(frameRef.getText());				
		} else {
			StencilTree func = frameRef.getAncestor(FUNCTION);
			while (func != null) {
				StencilTree yield = func.find(DIRECT_YIELD);
				if (yield.getText().equals(frameRef.getText())) {
					frameNum=Environment.DEFAULT_SIZE+countPriorFuncs(yield);
					break;
				} else {
					func = func.getAncestor(FUNCTION);
				}
			}
		}
		if (frameNum <0) {throw new RuntimeException("Error getting frame prototype for " + Path.toString(tupleRef));}
		return framePrototypeFor(tupleRef, frameNum);
	}

	public static TuplePrototype framePrototypeFor(StencilTree ref, int frame) {

		if (frame >= Environment.DEFAULT_SIZE) {
			//Walk the chain back, get the result prototype and then numeralize
			int myFrame = EnvironmentUtil.countPriorFuncs(ref) + Environment.DEFAULT_SIZE;
			int backup =  myFrame - frame;
			StencilTree func = ref.getAncestor(FUNCTION);
			while(backup>0) {func = func.getAncestor(FUNCTION); backup--;}
			return facetPrototype(func);
		} else if (frame == Environment.STREAM_FRAME) {
			return EnvironmentUtil.streamFor(ref);
		} else if (frame == Environment.LOCAL_FRAME) {
			StencilTree locals = ref.getAncestor(CONSUMES).find(RULES_LOCAL);
			return EnvironmentUtil.calcPrototype(locals);
		} else if (frame == Environment.PREFILTER_FRAME) {
			StencilTree groupRoot= ref.getAncestor(CONSUMES);
			if (groupRoot == null) {groupRoot = ref.getAncestor(OPERATOR_FACET);}
			StencilTree prefilter = groupRoot.find(RULES_PREFILTER);
			return EnvironmentUtil.calcPrototype(prefilter);
		} else if (frame == Environment.GLOBAL_FRAME) {
			return new GlobalsTuple(ref.getAncestor(PROGRAM).find(LIST_GLOBALS)).prototype();
		} else {
			throw new Error("Could not frame :" + ref.toStringTree());
		}
	}


	/**Given a function references in a tree, what is the prototype of the associated function?
	 * Assumes the function as the AST_INVOKEABLE set.
	 * **/
	public static TuplePrototype facetPrototype(StencilTree func) {
		assert func.getType() == FUNCTION;
		
		StencilTree opName = func.find(OP_NAME);
		MultiPartName name = Freezer.multiName(opName);
		StencilOperator op = Utilities.findOperator(opName);
		return op.getOperatorData().getFacet(name.facet()).prototype();
	}

	/**How many function calls up from here to the root of a call chain?**/
	public static int countPriorFuncs(StencilTree chainNode) {
		int count=-1;
		while (chainNode != null) {chainNode = chainNode.getAncestor(FUNCTION); count++;}
		return count-1;
	}
	
	public static TuplePrototype layerPrototype(StencilTree layerDef) {
		assert layerDef.is(LAYER);
        Specializer spec = Freezer.specializer(layerDef.find(SPECIALIZER));
        TuplePrototype proto = LayerTypeRegistry.makeTable(layerDef.getText(), (String) spec.get(DisplayLayer.TYPE_KEY)).prototype();
        return proto;
	}

}
