package stencil.modules.animate;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;

@Module
@Description("Provide animation constructs.")
public class Animation extends BasicModule {

	
	public static class Sigmoid extends AbstractOperator.Statefull {
		protected Sigmoid(OperatorData opData) {super(opData);}
		
	}
	
}
