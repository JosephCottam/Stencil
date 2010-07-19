package stencil.modules.layouts;

import java.awt.geom.Point2D;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

public abstract class Layout extends AbstractOperator {
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
