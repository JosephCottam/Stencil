package stencil.parser.string.util;


import stencil.tuple.prototype.TupleFieldDef;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.samplers.LayerSampler;
import stencil.interpreter.tree.Freezer;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.util.OperatorData;
import stencil.parser.tree.*;
import stencil.parser.tree.util.Environment;
import stencil.parser.tree.util.MultiPartName;
import stencil.parser.tree.util.Path;
import stencil.tuple.prototype.SimplePrototype;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import static stencil.tuple.prototype.TuplePrototypes.EMPTY_PROTOTYPE;
import static stencil.parser.string.StencilParser.*;

/**The environment proxy is used during compilation to mimic the shape of, 
 * but not the content of, runtime environment.
 * 
 * It carries prototype information instead of values; as such, it also
 * contains some compilation-specified prototype related utilities.
 * 
 * @author jcottam
 *
 */
public final class EnvironmentProxy {
	private static final TuplePrototype NO_PROTOTYPE = new SimplePrototype();
	
	public static final class FrameException extends RuntimeException {
		private static String briefMessage(String frame, TuplePrototype contents, FrameException prior) {
			StringBuilder b = new StringBuilder();
			b.append(String.format("\tSearched in frame %1$s (fields: %2$s).\n", frame, Arrays.deepToString(TuplePrototypes.getNames(contents))));
			if (prior != null) {
				b.append(briefMessage(prior.frame, prior.contents, prior.prior));
			}
			return b.toString();
		}
		private String frame;
		private TuplePrototype contents;

		private FrameException prior;
		public FrameException(int idx) {super("Frame reference out of bounds " + idx);}

		public FrameException(String message) {super(message);}

		public FrameException(String name, String frame, TuplePrototype contents) {
			this(name, frame, contents, null);
		}

		public FrameException(String name, String frame, TuplePrototype contents, FrameException prior) {
			super("Could not find field '" + name + "'.\n" + briefMessage(frame, contents, prior));
			this.frame = frame;
			this.contents= contents;
			this.prior = prior;
		}
	}

	/**Utility object for calculating, passing and referring 
	 * to potential ancestors of a tree.
	 */
	private static final class AncestryPackage {
		final StencilTree c;
		final StencilTree operatorFacet;
		final StencilTree g;
		final StencilTree l;
		final StencilTree program;
		final CommonTree focus;
		final boolean inRuleList;
		final boolean inGuideGenerator;
		final boolean inDynamicRule;
		
		public AncestryPackage(StencilTree t) {
			c = t.getAncestor(CONSUMES);
			operatorFacet = t.getAncestor(OPERATOR_FACET);
			g = t.getAncestor(GUIDE);
			l = t.getAncestor(LAYER);
			
			StencilTree r= (StencilTree) t.getAncestor(RULE);
			inRuleList = (r==null) ? false : r.getParent().getType() == LIST_RULES || StencilTree.typeName(r.getParent().getType()).startsWith("RULES");	//HACK: The startWith call depends on an incidental property of my naming convention...it could change and there would be no warning
			inDynamicRule = (r == null || c == null) ? false : r.getAncestor(DYNAMIC_RULE) != null;
			inGuideGenerator = t.getAncestor(GUIDE_GENERATOR) != null;
			
			program = t.getAncestor(PROGRAM);
			focus = t;
		}
	}
	
	private static TuplePrototype calcLocalProxy(AncestryPackage anc) {
		if (anc.c != null) {return calcPrototype(anc.c.find(RULES_LOCAL));}
		return NO_PROTOTYPE;
	}
	private static TuplePrototype calcPrefilterProxy(AncestryPackage anc) {
		TuplePrototype p = null;
		if (anc.c != null) {p=calcPrototype(anc.c.find(RULES_PREFILTER));}
		if (anc.operatorFacet != null) {p=calcPrototype(anc.operatorFacet.find(RULES_PREFILTER));}

		if (p == null || p.size() ==0) {return NO_PROTOTYPE;}
		return p;
	}

	
	public static TuplePrototype calcPrototype(Iterable<StencilTree> rules) {
		List<TupleFieldDef> defs = new ArrayList();
		for (StencilTree r: rules) {
		    StencilTree target = r.find(TARGET, RESULT, VIEW, CANVAS, LOCAL, PREFILTER);
		    TuplePrototype<TupleFieldDef> targetPrototype = Freezer.prototype(target.find(TUPLE_PROTOTYPE));

			for (TupleFieldDef def: targetPrototype.fields()) {
				defs.add(def);
			}
		}
		return new SimplePrototype(TuplePrototypes.getNames(defs), TuplePrototypes.getTypes(defs));
	}
	private static TuplePrototype calcStreamProxy(AncestryPackage anc, ModuleCache modules) {
		if (anc.c == null && anc.l!= null && anc.g == null) {return NO_PROTOTYPE;}//This is the characteristic of the defaults block
		if (anc.c != null) {
			if (!anc.inDynamicRule) {
				StencilTree stream = findStream(anc.c.getText(), anc.program.find(LIST_STREAM_DECLS));
				return Freezer.prototype(stream.find(TUPLE_PROTOTYPE));
			} else {
				return EMPTY_PROTOTYPE;	//Any sub-ref will be different if in a dynamic rule, but direct refs are numeralized in reducer creation
			}
		}
		if (anc.operatorFacet != null) {
			return Freezer.prototype(anc.operatorFacet.find(YIELDS).getChild(0));
		}
		
		//HACK: does not handle multiple monitor operators or generators
		if (anc.g != null) {
			//If there is no MONITOR_OPERATOR, then there is not yet enough info to generate the prototype
			if (anc.g.findAllDescendants(MONITOR_OPERATOR) == null) {
				throw new RuntimeException("Monitor and Sample operators must be set before framing guides.");
			}

			if (anc.inRuleList && (((Const) (anc.g.findAllDescendants(SAMPLE_OPERATOR).get(0).getChild(0))).getValue() instanceof LayerSampler)) {
				SampleOperator sampler = (SampleOperator) ((Const) (anc.g.findDescendant(SAMPLE_OPERATOR).getChild(0))).getValue();
				return ((LayerSampler) sampler).getDisplayLayer().getPrototype();
			} else if (anc.inGuideGenerator) {
				return SampleOperator.Util.prototype(anc.g.find(LIST_GUIDE_SAMPLERS).getChildCount());
			} else if (anc.inRuleList) {
				List<StencilTree> protos = anc.g.find(LIST_GUIDE_GENERATORS).findAllDescendants(TUPLE_PROTOTYPE);
				if (protos.size() ==1) {return Freezer.prototype(protos.get(0));}
				else {
					final TuplePrototype[] prototypes = new TuplePrototype[protos.size()];
					for(int i=0; i< prototypes.length; i++) {prototypes[i] = Freezer.prototype(protos.get(i));}
					return TuplePrototypes.append(prototypes);
				}
			}
		}
		
		throw new RuntimeException(String.format("Could not calculate stream proxy for %1$s at %2$s", anc.focus.toString(), Path.toString((StencilTree) anc.focus)));
	}
	
	
	private static StencilTree findStream(String name, Iterable<StencilTree> externals) {
		for (StencilTree s: externals) {if (s.getText().equals(name)) {return s;}}
		throw new RuntimeException("Could not find stream of name " + name + ".");
	}

	public static EnvironmentProxy extend(EnvironmentProxy env, Tree pass, StencilTree call, ModuleCache modules) {
		String label = pass.getText();
		TuplePrototype prototype = getPrototype(call, modules);
		EnvironmentProxy p = env.push(label, prototype);
		return p;
	}

	/**Create a new Proxy environment from a default environment.
	 * The proxy environment will have the default labels on the initial
	 * frames and empty labels on subsequent frames.
	 * @param streamName  Name applied to the stream frame.
	 * @return
	 */
	public static EnvironmentProxy fromDefault(Environment env, String streamName) {
		EnvironmentProxy  p = null; //root frame has null parent, so this is safe...for now
		for (int i=0; i<Environment.DEFAULT_SIZE; i++) {
			String name;
			if (i==Environment.STREAM_FRAME) {name= streamName;}
			else {name = Environment.DEFAULT_FRAME_NAMES[i];}
			p = new EnvironmentProxy (name, env.get(i).getPrototype(), p);
		}

		for (int i=Environment.DEFAULT_SIZE; i<env.size();i++) {
			p = new EnvironmentProxy ("", env.get(i).getPrototype(),p);
		}
		return p;
	}

	private static TuplePrototype getPrototype(StencilTree call, ModuleCache modules) {
		MultiPartName name= new MultiPartName(call.getText());
		Module m;
		try{
			m = modules.findModuleForOperator(name.getPrefix(), name.getName());
		} catch (Exception e) {
			throw new RuntimeException("Error getting module information for operator " + name, e);
		}

		try {
			OperatorData od = m.getOperatorData(name.getName(), Freezer.specializer(call.find(SPECIALIZER)));
			return od.getFacet(name.getFacet()).getPrototype();
		} catch (Exception e) {throw new RuntimeException("Error getting operator data for " + name, e);}       
	}

	public static EnvironmentProxy initialEnv(StencilTree t, ModuleCache modules) {
		AncestryPackage ancestry = new AncestryPackage(t);
		TuplePrototype[] prototypes = new TuplePrototype[Environment.DEFAULT_SIZE];
		prototypes[Environment.CANVAS_FRAME] = stencil.display.CanvasTuple.PROTOTYPE;
		prototypes[Environment.VIEW_FRAME] = stencil.display.ViewTuple.PROTOTYPE;
		prototypes[Environment.GLOBAL_FRAME] = new GlobalsTuple(ancestry.program.find(LIST_GLOBALS)).getPrototype();
		prototypes[Environment.STREAM_FRAME] = calcStreamProxy(ancestry, modules);
		prototypes[Environment.PREFILTER_FRAME] = calcPrefilterProxy(ancestry);
		prototypes[Environment.LOCAL_FRAME] = calcLocalProxy(ancestry);

		//Construct the actual proxy environment
		EnvironmentProxy proxy = new EnvironmentProxy(Environment.DEFAULT_FRAME_NAMES[0], prototypes[0]);
		for (int i=1; i< prototypes.length; i++) {
			proxy = proxy.push(Environment.DEFAULT_FRAME_NAMES[i], prototypes[i]);
		}
		return proxy;
	}

	final EnvironmentProxy parent;    

	private final TuplePrototype prototype;

	private final String label;

	public EnvironmentProxy (String label, TuplePrototype prototype) {this(label, prototype, null);}

	public String getLabel() {
		if (prototype == NO_PROTOTYPE) {return parent.getLabel();}
		return label;
	}
	
	EnvironmentProxy (String label, TuplePrototype prototype, EnvironmentProxy parent) {
		this.label = label;
		this.prototype = prototype;
		this.parent = parent;
	}
	
	public int currentIndex() {
		int index =0;
		EnvironmentProxy  prior=parent;
		while (prior != null) {
			prior = prior.parent;
			index++;
		}
		return index;
	} 

	public boolean canFrame(String name) {
		try {
			frameRefFor(name);
			return true;
		}
		catch (FrameException e) {return false;}
	}
	
	/**Value returned for frame labels (not value names)*/
	public static final String IS_LABEL = "***IS A LABEL*** (Just though you should know).";
	
	/**Returns the name of the frame that the value appears in OR 
	 * IS_LABEL if the name passed is for a frame and not a value.
	 * 
	 * @param name
	 * @return
	 */
	public String frameNameFor(String name) {
		if (prototype != null && prototype.contains(name)) {return label;}
		if (label != null && label.equals(name)) {
			return IS_LABEL;
		}
		if (parent == null) {throw new FrameException(name, label, prototype);}
		try {return parent.frameNameFor(name);}
		catch (FrameException e) {
			throw new FrameException(name, label, prototype, e);
		}
	}
	
	public int frameRefFor(String name) {
		if (prototype != null && prototype.contains(name)) {return currentIndex();}
		if (label != null && label.equals(name)) {return currentIndex();}
		if (parent == null) {
			throw new FrameException(name, label, prototype);
		}
		try {return parent.frameRefFor(name);}
		catch (FrameException e) {
			throw new FrameException(name, label, prototype, e);
		}
	}

	public TuplePrototype get(int idx) {
		if (currentIndex() == idx) {return prototype;}
		else if (parent == null) {throw new FrameException(idx);}
		else {return parent.get(idx);}
	}


	//Convert the name to a numeric ref (can be either a frame or tuple ref)
	public int getFrameIndex(String name) {
		if (label != null && label.equals(name)) {return this.currentIndex();}
		else if (parent == null) {throw new FrameException("Could not find frame named: " + name);}
		else {return parent.getFrameIndex(name);}
	}

	public boolean isFrameRef(String name) {
		return (label!= null && label.equals(name)) 
		|| (parent != null && parent.isFrameRef(name));
	}

	public EnvironmentProxy push(String label, TuplePrototype t) {
		return new EnvironmentProxy (label, t, this);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		if(parent != null) {
			b.append(parent.toString());
		}
		
		b.append(label);
		b.append(": ");
		b.append(Arrays.deepToString(TuplePrototypes.getNames(prototype)));
		b.append("\n");
		return b.toString();
	}
	
}
