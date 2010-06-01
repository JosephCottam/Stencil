package stencil.modules.layouts;

import java.awt.geom.Point2D;

import stencil.operator.module.util.OperatorData;
import stencil.parser.tree.Specializer;
import stencil.types.Converter;
import stencil.util.collections.Tree;


//TODO: add a fit-to-specific-width option
/**Based on the prefuse radial layout routines.*/
public class RadialTree extends Layout{
	public static final String NAME = "RadialLayout";
	
	private class LayoutTree extends Tree<LayoutTree> {
		private double r, theta1, theta2;
		private double x,y;
		
		double avgDescendents;   //Descendents per layer
		double totalDescendents; //Total number of descedents in all subtrees
		
		public LayoutTree(Object id) {super(id, null);}
		public LayoutTree(Object id, LayoutTree parent) {super(id, parent);}
		
		public String toString() {return toString(0);}
		
		private String toString(int nest) {
			String pad = new String();
			for (int i=0; i<nest; i++) {pad = pad + "  ";}
			
			StringBuilder children = new StringBuilder();
			for (LayoutTree child: children()) {
				children.append(child.toString(nest+1));
			}
			
			String self = String.format("%1$s (%2$.3f, %3$.3f, %4$.3f, (%5$.3f, %6$.3f))\n", id, r, theta1, theta2, x, y);
			
			return pad + self + children;
		}
		
	    private void setPosition() {
			double center = (theta2-theta1)/2f + theta1;
	    	x = origin.getX() + r*Math.cos(center);
	    	y = origin.getY() + r*Math.sin(center);
	    }
		
		public void setR(double r) {
			this.r = r;
			setPosition();
		}
		
		public void setTheta2(double theta2) {
			this.theta2=theta2;
			setPosition();
		}
		
		public void setTheta1(double theta1) {
			this.theta1=theta1;
			setPosition();			
		}
		
	}
	
    private static class Allocation {
    	double theta1, theta2;
    	public Allocation(double t1, double t2) {theta1=t1; theta2=t2;}
    	public boolean isEmpty() {return (size())<=0;}
    	public double size() {return theta2-theta1;}
    }
	
	private static final Allocation ROOT_ALLOCATION = new Allocation(0,Math.PI*2);

	
	/**Radius increment specializer field.*/
	private static final String RADIUS_INC = "gap";
	
	/**how quickly does the radius change as go out.
	 * 1 is no drop-off
	 * less than 1 is a fish-eye
	 * Greater than 1 is inverse fish-eye.
	 **/
	private static final String FALL_OFF = "fallOff";
	
	
	/**How to divide up the space?
	 * Shallow -- The next level entirely determines the next allocation
	 * Total   -- The total number of children determines allocation
	 * Avg     -- The avg children-per-level determines allocation
	 */
	private static final String RULE = "rule";
	private static final String SIMPLE_RULE = "SIMPLE";
	private static final String TOTAL_RULE = "TOTAL";
	private static final String AVG_RULE = "AVG";
	
	public static final int DEFAULT_RADIUS = 10;

    protected double radiusInc;		//How much space is there between levels?
    protected double fallOff;		//How quickly does the radius increment change?
    protected String rule;
    
    protected boolean layoutStale = true;
    
    protected LayoutTree root;		//Tree to layout
    
    /**
     * Creates a new RadialTreeLayout. Automatic scaling of the radius
     * values to fit the layout bounds is enabled by default.
     * @param group the data group to process. This should resolve to
     * either a Graph or Tree instance.
     */
    public RadialTree(OperatorData opData, Specializer spec) {
    	super(opData, spec);
        radiusInc = DEFAULT_RADIUS;
        
        radiusInc = Converter.toDouble(spec.get(RADIUS_INC));
        fallOff = Math.pow(Math.sqrt(2), Converter.toDouble(spec.get(FALL_OFF)));
        rule = Converter.toString(spec.get(RULE)).toUpperCase();
    }
            
    /**What division does the child get, given it is the idx-th child in a parent's allocation?**/
    private static Allocation division(String rule, LayoutTree child, Allocation parentAllocation) {
    	LayoutTree parent = child.getParent();
    	if (parentAllocation.isEmpty()) {return parentAllocation;}	 //Once empty it will ever be empty.
    	
    	int idx = child.getIdx();
    	if (rule.equals(SIMPLE_RULE)) {
	    	double span = parentAllocation.size()/parent.children().size();  //radians per-child
	    	double start = parentAllocation.theta1 + span*idx;				 //theta1 for this child
	    	return new Allocation(start, start+span);
    	} else {
    		double percent, start, span;
    		
    		if (rule.equals(TOTAL_RULE)) {percent = child.totalDescendents/parent.totalDescendents;}
    		else if (rule.equals(AVG_RULE)) {percent = child.totalDescendents/parent.avgDescendents;}
    		else {throw new RuntimeException("Unrecognized radial layout allocation rule: " + rule);}
    		span = parentAllocation.size()*percent;
    		
    		if (idx ==0) {start = parent.theta1;}
    		else {start = parent.children().get(idx-1).theta2;}
    		
    		return new Allocation(start, start + span);
    	} 
    }
    

    public void layout() {
    	if (root == null) {return;}
    	
    	countChildren(root);
    	avgChildren(root);
    	
    	layout(root, 0, ROOT_ALLOCATION, radiusInc, fallOff, rule);
    	layoutStale = false;
    }

    private final static double calcRadius(final int level, final double radiusInc, final double fallOff) {
    	if (fallOff == 1) {return level*radiusInc;}
    	else {return radiusInc * (1 - Math.pow(fallOff, level))/(1-fallOff);}
    }

    private static void layout(LayoutTree root, int level, Allocation space, double radiusInc, double fallOff, String rule) {
    	root.setR(calcRadius(level, radiusInc, fallOff));
    	root.setTheta1(space.theta1);
    	root.setTheta2(space.theta2);
    	for (LayoutTree child: root.children()) {
    		layout(child, level+1, division(rule, child, space), radiusInc, fallOff, rule);
    	}
    }
    
    private static int countChildren(LayoutTree root) {
    	if (root.children().size()==0) {
    		root.totalDescendents=0;
    		return 0;
    	} else {
    		int total = root.children().size();
    		for (LayoutTree child: root.children()) {
    			total += countChildren(child);
    		}
    		root.totalDescendents = total;
    		return total;
    	}	
    }

    //Method "countChildren" MUST be run before this method to get correct results
    private static int avgChildren(LayoutTree root) {
    	if (root.children().size()==0) {
    		root.avgDescendents =0;
    		return 0;
    	} else {
    		int childLevels = 0;
    		for (LayoutTree child: root.children()) {
    			childLevels = Math.max(childLevels, avgChildren(child));
    		}
    		root.avgDescendents=root.totalDescendents/childLevels;
    		return childLevels +1;
    	}
    }
    
    /**What is the point-representation of the allocation for the given node?
     * This returns the center-point of the allocation, on the radius line.
     * 
     * TODO: Make a point2D tuple and return a Point2D from here.
     * */
    public Point2D query(Object id) {
    	if (layoutStale) {layout();}
    	
    	LayoutTree node = Tree.findNode(root, id);
    	if (node != null) {
    		return new Point2D.Double(node.x, node.y);
    	} else {
    		return null;
    	}
    }

    /**Add a node and report where it was placed.*/
    public Point2D map(Object id, Object parentID) {
    	add(id, parentID);
    	return query(id);
    }
    
    /**Add a new node to the tree in the specified position.
     * 
     * @param id 	   Identity of new node
     * @param parentID Identity of parent
     * @return         Did adding this node change anything?
     */
    public boolean add(Object id, Object parentID) {
    	LayoutTree node = Tree.findNode(root, id);
    	LayoutTree parent = Tree.findNode(root, parentID);
    	if (node!= null && node.getParent() == parent) {return false;}
    	
    	stateID++;
    	layoutStale = true;
    	    	
    	if (parent == null) {
    		parent = new LayoutTree(parentID);

    		if (root == null || root.getID() != null) {//Create a synthetic root when needed
        		LayoutTree newRoot = new LayoutTree(null);
        		if (root != null) {newRoot.addChild(root);}
        		root = newRoot;
    		}    		
    		root.addChild(parent);
    	}
    	
    	if (node == null && !parentID.equals(id)) { //If parentID == id, then the node will have been created when creating the parent
    		node = new LayoutTree(id, parent);
    	} else if (node != null) {
    		parent.addChild(node);
    	}
    	
    	
    	if (root.getID() == null && root.children().size() == 1) {//Remove a synthetic root when needed
    		parent.setParent(parent);
    		root = parent;
    	}
    	
    	return true;

    }
    
    /**What is the slice-representation of the allocation for a given node?
     * This returns the radius line and the start/end angles (in radians).
     */
    public double[] slice(Object id) {
    	if (layoutStale) {layout();}
    	
    	LayoutTree node = Tree.findNode(root, id);
    	if (node != null) {
    		return new double[]{node.r, node.theta1, node.theta2};
    	}
    	return null;
    }
}
