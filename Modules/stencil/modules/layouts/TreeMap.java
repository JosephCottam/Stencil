package stencil.modules.layouts;

import java.awt.geom.Rectangle2D;

import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import stencil.util.collections.Tree;
import edu.umd.cs.treemap.*;

@Operator(spec="[range: ALL, split: 0, style: \"square\", X:0, Y: 0, W:100, H:100]")
public class TreeMap extends Layout {
	public static final String NAME = "TreeMap";

	public static final String STYLE_KEY = "style";
	public static final String WIDTH_KEY = "W";
	public static final String HEIGHT_KEY = "H";
	
	private LayoutTree root;
	private TreeModel layout;
	private MapLayout algorithm;
	private double width, height;
	
	/**Mutable tree for storing the tree structure.
	 * Can translate to the UMD data structures for computing layout.
	 * @author jcottam
	 *
	 */
	private class LayoutTree extends Tree<LayoutTree> {
		private static final double DEFAULT_WEIGHT=1;
		
		double weight =DEFAULT_WEIGHT;
		
		public LayoutTree(Object id) {super(id, null);}		
		public LayoutTree(Object id, LayoutTree parent) {this(id, parent, DEFAULT_WEIGHT);}
		public LayoutTree(Object id, LayoutTree parent, double weight) {
			super(id, parent);
			this.weight = weight;
		}
		
		public TreeModel getLayoutTree() {
			Mappable m = new MapItem(weight, getIdx());
			TreeModel node= new TreeModel(m);
			node.getMapItem().setSize(weight);
			for (LayoutTree child: children()) {
				node.addChild(child.getLayoutTree());
			}
			return node;
		}
	}	
	
	public TreeMap(OperatorData opData, Specializer spec) {
		super(opData, spec);
		
		String style = spec.get(STYLE_KEY).getText().toUpperCase();
		if (style.equals("SQUARE")) {algorithm = new SquarifiedLayout();}
		else if (style.equals("SLICE")) {algorithm = new SliceLayout();}
		else if (style.equals("BINARY")) {algorithm = new BinaryTreeLayout();}
		else if (style.equals("STRIP")) {algorithm = new StripTreemap();}
		else {throw new IllegalArgumentException("Unrecognized style: " + spec.get(STYLE_KEY).getText());}

	
		width = Converter.toDouble(spec.get(WIDTH_KEY));
		height = Converter.toDouble(spec.get(HEIGHT_KEY));
	}
	
	@Facet(memUse="READER", prototype="(X,Y,W,H)")
	public Rectangle2D query(final Object id) {
		Mappable layoutNode = layoutFor(id); 
		Rect r = layoutNode.getBounds();
		Rectangle2D result = new Rectangle2D.Double(r.x, r.y, r.w, r.h);
		return result;
	}
	
	@Facet(memUse="WRITER", prototype="(X,Y,W,H)")
	public Rectangle2D map(final Object id, final Object parent, double value) {
		add(id, parent, value);
		return query(id);
	}

	@Facet(memUse="WRITER", prototype="(X,Y,W,H)")
	public boolean add(final Object id, final Object parentID, double value) {
		LayoutTree parent = Tree.findNode(root, parentID);
		stateID++;
    	
		layout=null;
		if (parent == null) {
			root = new LayoutTree(id);
			return true;
		} else {
			parent.children().add(new LayoutTree(id, parent));
			return false;
		}
	}

	
	/**Generate a layout for the stored tree.
	 * Will populate the layout array.
	 */
	private void layout() {
		layout = root.getLayoutTree();
		layout.layout(algorithm,new Rect(origin.getX(), origin.getY(), width, height));
	}
	
	/**What is the layout info for the given id?
	 */
	private Mappable layoutFor(final Object id) {
		if (layout == null) {layout();}
		Tree node = Tree.findNode(root, id);
		Integer[] path = node.getPath();
		
		TreeModel candidate = layout;
		
		for (int i:path) {candidate = candidate.getChild(i);}
		return candidate.getMapItem();
	}
	
	public TreeMap viewPoint() {
		return (TreeMap) super.viewPoint();
	}
}
