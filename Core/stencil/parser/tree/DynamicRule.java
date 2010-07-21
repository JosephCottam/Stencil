package stencil.parser.tree;


import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import stencil.display.DisplayLayer;
import stencil.display.Glyph;
import stencil.display.LayerView;
import stencil.module.operator.StencilOperator;
import stencil.parser.string.StencilParser;
import stencil.parser.tree.util.Environment;
import stencil.tuple.Tuple;
import stencil.tuple.TupleAppender;
import stencil.tuple.instances.PrototypedTuple;
import stencil.types.Converter;

public class DynamicRule extends StencilTree {
	public DynamicRule(Token token) {super(token);}

	public Rule getAction() {return (Rule) getChild(0);}
	
	/**What group does this rule belong to?*/
	public Consumes getGroup() {
		Tree t = this.getAncestor(StencilParser.CONSUMES);
		if (t == null) {throw new RuntimeException("Rules not part of a layer do not belong to a group.");}
		return (Consumes) t;
	}

	public static final class Update {
		public String ID; 
		public Tuple update;
		public Update(String ID, Tuple update) {
			this.ID = ID;
			this.update = update;
		}
	}
	
	public java.util.List<Tuple> apply(DisplayLayer<Glyph> table, Map<String, Tuple> sourceData) {
		LayerView<Glyph> view = table.getView();
		
		final Rule rule = getAction();
		final CallChain chain = rule.getAction();
		
		Environment[] envs = new Environment[view.size()];
		int idx=0;
		for (Glyph glyph: view) {
			Tuple streamTuple = sourceData.get(glyph.getID());
			envs[idx] = Environment.getDefault(Canvas.global, View.global, streamTuple);
			envs[idx] = envs[idx].ensureCapacity(envs[idx].size() + chain.getDepth());
			idx++;
		}

		CallTarget action = chain.getStart();		
		//TODO: Extend to handle >> and >-; currently does -> for everything		
		while (!(action instanceof Pack)) {
			final Function func = (Function) action;
			
			final AstInvokeable inv = func.getTarget();
			final StencilOperator op = inv.getOperator();
			final java.util.List<Value> formals = func.getArguments();
			final Object[][] args = new Object[envs.length][formals.size()]; 
			
			for (int i=0; i< args.length; i++) {
				for (int j=0; j<formals.size(); j++) {
					args[i][j] = formals.get(j).getValue(envs[i]); 
				}				
			}
			
			final java.util.List result = op.vectorQuery(args);
			
			for (int i=0; i< envs.length; i++) {
				envs[i].extend(Converter.toTuple(result.get(i)));
			}
			
			action = func.getCall();
		}

		Pack pack = (Pack) action;
		java.util.List<Tuple> updates = new ArrayList(envs.length);
		int i=0;
		for (Glyph glyph: view) {
			Tuple result = rule.getTarget().finalize(pack.apply(envs[i++]));
			Tuple id = PrototypedTuple.singleton("ID", glyph.getID());
			updates.add(TupleAppender.append(id, result));
		}
		return updates;
	}	
		
	/**Should this dynamic rule be run now??*/
	public boolean requiresUpdate() {
		return ((StateQuery) getChild(1)).requiresUpdate();
	}
}
