package stencil.types.gradient;

import java.awt.Color;
import java.util.regex.Pattern;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.types.Converter;

@Description("Create gradients")
@Module
public class GradientUtils extends BasicModule {

	@Operator
	public static class Gradient extends AbstractOperator {
		public Gradient(OperatorData opData) {super(opData);}
		
		/**Gradient format is <Color> -> <Color> : <len>,cycle, abs
		 * The cycle/abs may appear in either order.
		 * Len may be omitted, but if it appears it must be first after the colon. 
		 * If all are missing, the colon may be omitted.
		 * White space is ignored.
		 *  
		 * @param arg
		 * @return
		 */
		private static final Pattern SPLIT_PATTERN = Pattern.compile("(\\s*->\\s*)|(\\s*:\\s*)");
		
		@Facet(memUse="FUNCTION", prototype="(self, start, end, length, absolute, cyclic)", alias={"map","query"})
		public GradientTuple query(String arg) {
			String[] args = SPLIT_PATTERN.split(arg);
			Color one = (Color) Converter.convert(args[0], Color.class);
			Color two = (Color) Converter.convert(args[1], Color.class);
			boolean abs = arg.contains("abs");
			boolean cyclic = arg.contains("cycle");
			double len = 0;
			
			if (args.length >2) {
				try {len = Converter.toDouble(args[2]);}
				catch (NumberFormatException e) {}//ignored...if the number doesn't parse just ignore it
			}
			
			return new GradientTuple(one, two, len, abs, cyclic);
		}
	}
}
