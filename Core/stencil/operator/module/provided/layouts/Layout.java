package stencil.operator.module.provided.layouts;

import java.awt.geom.Point2D;

import stencil.operator.module.util.OperatorData;
import stencil.operator.util.BasicProject;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

abstract class Layout extends BasicProject {
	protected static final String X = "X";
	protected static final String Y = "Y";
	
	protected Point2D origin;

	protected Layout(OperatorData opData, Specializer spec) {
		super(opData);
		
        final double x = Converter.toDouble(spec.get(X));
        final double y = Converter.toDouble(spec.get(Y));
        origin = new Point2D.Double(x,y);
	}

}
