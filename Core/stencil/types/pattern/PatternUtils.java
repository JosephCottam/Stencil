package stencil.types.pattern;

import java.awt.Color;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Description;
import stencil.module.util.ann.Facet;
import stencil.module.util.ann.Module;
import stencil.module.util.ann.Operator;

@Description("Create pattern fills")
@Module
public class PatternUtils extends BasicModule  {

	@Operator
	public static class PatternFill extends AbstractOperator {
		public PatternFill(OperatorData opData) {super(opData);}
		
		@Facet(memUse="FUNCTION", prototype=PatternTuple.PROTOTYPE_STRING, alias={"map","query"})
		@Description("Create a new fill pattern.  Arguments are: Pattern name, foreground, background, weight and scale.")
		public PatternTuple query(String name, Color fore, Color back, int scale, double weight) {
			return new PatternTuple(name, fore,back, scale, weight);
		}
	}
}
