tree grammar UpdateGuides;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
}

@header{
    /** Operators over a properly formed-AST and ensures that all guides are up-to date.
    */ 
	package stencil.interpreter;
	
	import java.util.Arrays;
	import java.util.ArrayList;
	import java.lang.Iterable;
	
	import stencil.parser.tree.*;	
	import stencil.util.MultiPartName;
	import stencil.display.*;
	import stencil.module.*;
	import stencil.tuple.prototype.*;
	import stencil.tuple.Tuple;
	import stencil.tuple.Tuples;
	import stencil.interpreter.guide.SampleSeed; 
	import stencil.interpreter.guide.SeedOperator;
	import stencil.interpreter.guide.samplers.LayerSampler;
	
	
}

@members{
	private StencilPanel panel; //Panel to take elements from
	
	public void updateGuides(StencilPanel panel) {
		this.panel = panel;
				
		downup(panel.getProgram().getCanvasDef().getGuides());
	}
	
	private void apply(Guide g) {
		Specializer details = g.getSpecializer();
		SeedOperator seedOp = g.getSeedOperator();
		String layerName = g.getSelector().getPath().get(0).getName();
		String attribute = g.getSelector().getAttribute();
		List<Tuple> sample, projection, pairs, results;
		
		if (seedOp instanceof LayerSampler.SeedOperator) {
            sample = g.getSampleOperator().sample(null, details);
            projection = sample;
		} else {
			SampleSeed seed = seedOp.getSeed();
		
			try {
				sample = g.getSampleOperator().sample(seed, details);
				projection = Interpreter.processAll(sample, g.getGenerator());
			} catch (Exception e) {throw new RuntimeException("Error creating guide sample.", e);}
		}
				
		try {results = Interpreter.processAll(projection, g.getRules());}
		catch (Exception e) {throw new RuntimeException("Error formatting guide results.", e);}
		

    //TODO: Remove null check when scheduling is improved.
		DisplayGuide guide = panel.getCanvas().getComponent().getGuide(g.getSelector());
		if (guide != null) {guide.setElements(results);}
	}

}

topdown: ^(g=GUIDE .*) {apply((Guide) g);};