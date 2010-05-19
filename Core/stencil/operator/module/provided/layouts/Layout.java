package stencil.operator.module.provided.layouts;

import stencil.operator.module.util.OperatorData;
import stencil.operator.util.BasicProject;

abstract class Layout extends BasicProject {
	protected static final String X = "X";
	protected static final String Y = "Y";

	protected Layout(OperatorData opData) {super(opData);}

}
