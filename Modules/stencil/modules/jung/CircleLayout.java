package stencil.modules.jung;

import java.util.Comparator;

import edu.uci.ics.jung.graph.DelegateForest;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.Operator;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;

@Operator(spec="[range: ALL, split: 0, width: 500, height: 500, radius: 100, sort: \"SEQ\"]")
public final class CircleLayout extends GraphOperator.SizedOperator {
	private static final String RADIUS = "radius";
	private static final String SORTER_KEY ="sort";

	public static final String SEQUENCE_VALUE = "SEQ";	
	private final Comparator SEQUENCE = new Comparator() {
		public int compare(Object o1, Object o2) {
			if (o1.equals(o2)) {return 0;}
			for (Object v: graph.getVertices()) {
				if (v.equals(o1)) {return -1;}
				if (v.equals(o2)) {return 1;}
			}
			return 0;
		}
	};
	
	
	public static final String LEXI_VALUE = "LEX";
	private static final Comparator LEXICOGRAPHIC = new Comparator() {
		public int compare(Object o1, Object o2) {return o1.toString().compareTo(o2.toString());}
	};
	
	
	
	private final double radius;
	private final Comparator sorter;
	
	public CircleLayout(OperatorData opData, Specializer spec) {
		super(opData, spec);
		radius = Converter.toDouble(spec.get(RADIUS));
		resetLayout();
		
		String sortBy = spec.get(SORTER_KEY).getText().toUpperCase();
		if (sortBy.equals(SEQUENCE_VALUE)) {sorter = SEQUENCE;}
		else if (sortBy.equals(LEXI_VALUE)) {sorter = LEXICOGRAPHIC;}
		else {throw new IllegalArgumentException("Unknown sort specified: " + sortBy);}
		
	}

	protected void resetLayout() {
		edu.uci.ics.jung.algorithms.layout.CircleLayout cLayout = new edu.uci.ics.jung.algorithms.layout.CircleLayout(new DelegateForest(graph));
		cLayout.setSize(size);
		cLayout.setRadius(radius);
		cLayout.setVertexOrder(sorter);
		cLayout.reset();
		setLayout(cLayout);
	}
}
