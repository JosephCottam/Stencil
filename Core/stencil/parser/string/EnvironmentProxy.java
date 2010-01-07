package stencil.parser.string;

import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.prototype.TuplePrototypes;
import stencil.operator.module.Module;
import stencil.operator.module.ModuleCache;
import stencil.operator.module.OperatorData;
import stencil.parser.ParserConstants;
import stencil.parser.tree.*;
import stencil.tuple.prototype.SimplePrototype;
import stencil.util.MultiPartName;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.CommonTree;

/**The environment proxy is used during compilation to mimic the shape of, 
 * but not the content of, runtime environment.
 * 
 * It carries prototype information instead of values; as such, it also
 * contains some compilation-specified prototype related utilities.
 * 
 * @author jcottam
 *
 */
public interface EnvironmentProxy {
	public static enum TARGETS {NONE, PREFILTER, LOCAL, BOTH}
	
	public static final class FrameException extends RuntimeException {
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

		private static String briefMessage(String frame, TuplePrototype contents, FrameException prior) {
			StringBuilder b = new StringBuilder();
			b.append(String.format("\tSearched in frame %1$s (fields: %2$s).\n", frame, Arrays.deepToString(TuplePrototypes.getNames(contents))));
			if (prior != null) {
				b.append(briefMessage(prior.frame, prior.contents, prior.prior));
			}
			return b.toString();
		}
	}

	/**Environment that cannot be used for an environment operations.
	 * This environment is used when processing defaults blocks.
	 * Any referencing of the environment is an error in a defaults block,
	 * since they are environment independent.
	 */
	public static final EnvironmentProxy ERROR_ENV = new EnvironmentProxy() {
		public int currentIndex() {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
		public int frameRefFor(String name) {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
		public TuplePrototype get(int idx) {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
		public int getFrameIndex(String name) {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
		public boolean isFrameRef(String name) {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
		public EnvironmentProxy push(String label, TuplePrototype t) {throw new UnsupportedOperationException("Trying to employ an environment in an illegal context.");}
	};
	
	public EnvironmentProxy push(String label, TuplePrototype t);
	public boolean isFrameRef(String name);
	public int frameRefFor(String name);
	public int currentIndex();
	public int getFrameIndex(String name);
	public TuplePrototype get(int idx);
	
	public static class Proxy implements EnvironmentProxy {
		final Proxy parent;
		final TuplePrototype prototype;
		final String label;
	
		public Proxy(String label, TuplePrototype prototype) {this(label, prototype, null);}
		private Proxy(String label, TuplePrototype prototype, Proxy parent) {
			this.label = label;
			this.prototype = prototype;
			this.parent = parent;
		}
	
		//Convert the name to a numeric ref (can be either a frame or tuple ref)
		public int getFrameIndex(String name) {
			if (label != null && label.equals(name)) {return this.currentIndex();}
			else if (parent == null) {throw new FrameException("Could not find frame named: " + name);}
			else {return parent.getFrameIndex(name);}
		}
	
		public TuplePrototype get(int idx) {
			if (currentIndex() == idx) {return prototype;}
			else if (parent == null) {throw new FrameException(idx);}
			else {return parent.get(idx);}
		}
	
		public int frameRefFor(String name) {
			if (prototype.contains(name)) {return currentIndex();}
			if (label != null && label.equals(name)) {return currentIndex();}
			if (parent == null) {
				throw new FrameException(name, label, prototype);
			}
			try {return parent.frameRefFor(name);}
			catch (FrameException e) {
				throw new FrameException(name, label, prototype, e);
			}
		}
	
		public boolean isFrameRef(String name) {
			return (label!= null && label.equals(name)) 
			|| (parent != null && parent.isFrameRef(name));
		}
	
		public EnvironmentProxy push(String label, TuplePrototype t) {
			return new Proxy(label, t, this);
		}    
	
		public int currentIndex() {
			int index =0;
			Proxy prior=parent;
			while (prior != null) {
				prior = prior.parent;
				index++;
			}
			return index;
		}
	
	
		/**Given a list of rules, what is the eventual prototype?*/
		public static TuplePrototype calcPrototype(List<Rule> rules) {
			List<TupleFieldDef> defs = new ArrayList();
			for (Rule r: rules) {
				for (TupleFieldDef def: r.getTarget().getPrototype()) {
					defs.add(def);
				}
			}
			return new SimplePrototype(TuplePrototypes.getNames(defs), TuplePrototypes.getTypes(defs));
		}
	
	
		public static EnvironmentProxy extend(EnvironmentProxy env, CommonTree pass, CommonTree callTree, ModuleCache modules) {
			String label = ((Pass) pass).getName();
			Function call = (Function) callTree;
			TuplePrototype prototype = getPrototype(call, modules);
			return env.push(label, prototype);
		} 
	
		private static TuplePrototype getPrototype(Function call, ModuleCache modules) {
			MultiPartName name= new MultiPartName(call.getName());
			Module m;
			try{
				m = modules.findModuleForOperator(name.prefixedName()).module;
			} catch (Exception e) {
				throw new RuntimeException("Error getting module information for operator " + name, e);
			}
	
			try {
				OperatorData od = m.getOperatorData(name.getName(), call.getSpecializer());
				return od.getFacetData(name.getFacet()).getPrototype();
			} catch (Exception e) {throw new RuntimeException("Error getting operator data for " + name, e);}       
		}
	
	
		@SuppressWarnings("null")
		public static EnvironmentProxy initialEnv(CommonTree t, ModuleCache modules) {
			Consumes c = (Consumes) t.getAncestor(StencilParser.CONSUMES);
			Operator o = (Operator) t.getAncestor(StencilParser.OPERATOR);
			Guide g = (Guide) t.getAncestor(StencilParser.GUIDE);
			if (c != null) {return initialEnv(c, additionalTargets(t));}
			if (o != null) {return initialEnv(o, additionalTargets(t));}
			if (g != null) {return initialEnv(g, modules);}
			
			//Check for special cases where the environment doesn't matter...
			Layer l= (Layer) t.getAncestor(StencilParser.LAYER);
			if (l != null && c == null) {return ERROR_ENV;}
			
			throw new RuntimeException("Found rule with unknown initial environment: " + t.toStringTree());
		}
		
		private static EnvironmentProxy initialEnv(Consumes c, TARGETS targets) {
			TuplePrototype prefilter = null;
			TuplePrototype local =null;
			if (targets.ordinal() >= TARGETS.PREFILTER.ordinal()) {prefilter = calcPrototype(c.getPrefilterRules());}
			if (targets.ordinal() >= TARGETS.LOCAL.ordinal()) {local = calcPrototype(c.getLocalRules());}
	
			Program p = (Program) c.getAncestor(StencilParser.PROGRAM);
			External ex = External.find(c.getStream(), p.getExternals());
			return makeInitialEnv(ex.getName(), ex.getPrototype(), prefilter, local);
		}
	
		private static EnvironmentProxy initialEnv(Operator o, TARGETS targets) {
			TuplePrototype prefilter = null;
			if (targets.ordinal() >= TARGETS.PREFILTER.ordinal()) {prefilter = calcPrototype(o.getPrefilterRules());}
	
			return makeInitialEnv(o.getName(), o.getYields().getInput(), prefilter, null);
		}
	
		private static EnvironmentProxy initialEnv(Guide g, ModuleCache modules) {
			Function call = (Function) g.getGenerator().getStart();
			TuplePrototype prototype = getPrototype(call, modules);
	
			return makeInitialEnv(g.getLayer(), prototype, null, null);
		}
	
		private static EnvironmentProxy makeInitialEnv(String name, TuplePrototype prototype, TuplePrototype prefilter, TuplePrototype local) {
			EnvironmentProxy proxy = new Proxy(ParserConstants.CANVAS_FRAME, stencil.display.CanvasTuple.PROTOTYPE)
			.push(ParserConstants.VIEW_FRAME, stencil.display.ViewTuple.PROTOTYPE)
			.push(name, prototype);
	
			if (prefilter != null) {proxy = proxy.push(ParserConstants.PREFILTER_FRAME, prefilter);}
			if (local != null) {proxy = proxy.push(ParserConstants.LOCAL_FRAME, local);}
			return proxy;
		}
	
		protected static TARGETS additionalTargets(CommonTree t) {
			if (t instanceof Predicate) {return TARGETS.PREFILTER;}
	
			Target target = ((Rule) t.getAncestor(StencilParser.RULE)).getTarget();
	
			if (target instanceof Prefilter) {return TARGETS.NONE;}
			if (target instanceof Local) {return TARGETS.PREFILTER;}
			return TARGETS.BOTH;
		}
	}


}
