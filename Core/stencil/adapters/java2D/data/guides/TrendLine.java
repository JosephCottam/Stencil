package stencil.adapters.java2D.data.guides;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import stencil.adapters.java2D.data.Guide2D;
import stencil.parser.tree.Specializer;
import stencil.tuple.Tuple;

public class TrendLine  implements Guide2D {
	public static final String IMPLANTATION_NAME = "TREND_LINE";

	public TrendLine(Specializer spec) {
		
	}
	
	@Override
	public void setElements(List<Tuple> elements) {
		
		//First & final trendline (worst possible...)
		
		//Linear regression
		
		//LOESS
	}

	@Override
	public Rectangle2D getBoundsReference() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(Graphics2D g, AffineTransform viewTransform) {
		// TODO Auto-generated method stub
		
	}

}
