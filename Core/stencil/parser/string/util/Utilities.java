package stencil.parser.string.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.Token;

import stencil.interpreter.tree.Freezer;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;
import stencil.module.Module;
import stencil.module.ModuleCache;
import stencil.module.OperatorInstanceException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.wrappers.SyntheticOperator;
import stencil.module.util.OperatorData;
import stencil.parser.ParserConstants;
import stencil.parser.tree.OperatorProxy;
import stencil.parser.tree.Path;
import stencil.parser.tree.StencilTree;
import static stencil.parser.string.StencilParser.STATE_QUERY;
import static stencil.parser.ParserConstants.STATE_ID_FACET;

import static stencil.parser.string.StencilParser.*;


public class Utilities {
	/**Name that should be used with genSym whenever creating an auotmatic label for a yield/map/fold operator.**/
	public static final String FRAME_SYM_PREFIX = "Frame";

	public static final class OperatorNotFoundException extends IllegalArgumentException {
		public OperatorNotFoundException(String name) {super("Operator not found: " + name);}
		public OperatorNotFoundException(String name, Throwable cause) {super("Operator not found: " + name, cause);}
	}
	
	private static int gsCounter = 0;
	
	/**Generate a guaranteed unusued name.**/
	public static String genSym(String name) {
		if (gsCounter <0) {throw new Error("Exceed gensym guranteed namespace capacity.");}
		return "#" + name + "_" + gsCounter++;
	}
	
	/**Could the passed name have been generated from the given root?
	 * This does not guarantee it was so generated, only testing the possibility that it was.
	 * @param root -- The possible root
	 * @param name -- The name being test against the root
	 * **/
	public static boolean isGenSymRoot(String root, String name) {
		return name.substring(1).startsWith(root);
	}
	
	/**Takes a tree and creates a suitable query for it.
	 * Query constructed per:
	 *    1) Gather invokeables
	 *    2) Retain only invokeables with operators that (a) are not functions and (b) have a StateQuery facet
	 *    3) Create a STATE_QUERY node with invokeables pointing to retained state query facets
	 */
	public static Tree stateQueryList(TreeAdaptor adaptor, StencilTree tree) {
		List<StencilTree> ops =  tree.findAllDescendants(OP_NAME);
		Collection<MultiPartName> targets = new HashSet();
		
		
		for (StencilTree op: ops) {
			StencilOperator target = findOperator(op);
			if (target != null) {
				if (target.getOperatorData().hasFacet(STATE_ID_FACET)) {
					MultiPartName n = Freezer.multiName(op);
					targets.add(n.modFacet(STATE_ID_FACET));
				}
			}
		}

		Tree rv = (Tree) adaptor.create(STATE_QUERY, "STATE_QUERY");
		for (MultiPartName target: targets) {
			adaptor.addChild(rv, unfreezeName(target, adaptor));					
		}		
		return rv;
	}
	
	/**Given a multi-part name, make the corresponding AST.**/
	public static final StencilTree unfreezeName(MultiPartName name, TreeAdaptor adaptor) {
		Object root = adaptor.create(OP_NAME, "");
		adaptor.addChild(root, adaptor.create(ID, name.prefix()));
		adaptor.addChild(root, adaptor.create(ID, name.name()));
		adaptor.addChild(root, adaptor.create(ID, name.facet()));
		return (StencilTree) root;
	}
	

	public static final String counterpartFacet(StencilTree opName) {
		MultiPartName name = Freezer.multiName(opName);
		StencilTree opList = operatorsList(opName);
		StencilTree opNode = findOperatorNode(opList, name.name());
		
		
		if (opName.findDescendant(DEFAULT_FACET) != null) {
			name = name.modFacet(defaultFacet(opName));
		} else if (opName.findDescendant(COUNTERPART_FACET) != null){
			name = name.modFacet(opName.findDescendant(COUNTERPART_FACET).getChild(0).getText());
		}
		
		if (opNode.is(OPERATOR_PROXY)) {
			OperatorProxy proxy = (OperatorProxy) opNode;
			return proxy.getOperatorData().getFacet(name.facet()).counterpart();
		} else if (opNode.is(OPERATOR)) {
			return SyntheticOperator.COUNTERPART_FACET;
		} else {
			throw new IllegalArgumentException("Cannot find counterpart for " + opName + ": Operator position not found.");
		}
	}
	
	public static final String defaultFacet(StencilTree opName) {
		MultiPartName name = Freezer.multiName(opName);
		StencilTree opList = operatorsList(opName);
		StencilTree opNode = findOperatorNode(opList, name.name());
		if (opNode.is(OPERATOR_PROXY)) {
			OperatorProxy proxy = (OperatorProxy) opNode;
			return proxy.getOperatorData().defaultFacet().name();
		} else if (opNode.is(OPERATOR)) {
			return SyntheticOperator.DEFAULT_FACET;
		} else {
			throw new IllegalArgumentException("Cannot find counterpart for " + opName + ": Operator position not found.");
		}
	}
	
	
	public static final String defaultFacet(ModuleCache modules, StencilTree opName) {
		MultiPartName name = Freezer.multiName(opName);
		Module m = modules.findModuleForOperator(name);
		OperatorData od = m.getOperatorData(name.name(), ParserConstants.EMPTY_SPECIALIZER);
		return od.defaultFacet().name();
	}
	
	public static final String counterpart(ModuleCache modules, StencilTree opName) {
		MultiPartName name = Freezer.multiName(opName);
		Module m = modules.findModuleForOperator(name);
		OperatorData od = m.getOperatorData(name.name(), ParserConstants.EMPTY_SPECIALIZER);
		String facet = name.facet();
		if (facet.equals("DEFAULT_FACET")) {
			facet = od.defaultFacet().name();
		}
		return od.getFacet(facet).counterpart();
	}

	
	/**@param name Name of operator to add
	 * @param op   Actual operator instance
	 * @param list Tree fragment to add to (will be added as a direct child)
	 * @param adaptor Tree adaptor to use
	 * @param originToken Original program token to associate with this proxy (for error reporting).
	 */
	public static final void addToOperators(String group, String name, StencilOperator operator, 
											StencilTree list, TreeAdaptor adaptor, Token originToken) {
		
		OperatorProxy tree = (OperatorProxy) adaptor.create(OPERATOR_PROXY, originToken, name);
		tree.setOperator(group, operator, operator.getOperatorData());
		adaptor.addChild(list, tree);
	}
	
	/**Locate a stencil operator in the operators list, 
	 * 	given a reference to it from the same program tree.
	 * The returned value IS NOT guaranteed to be the final instance,
	 * 	just an instance of the operator that reflects the current program state.
	 */
	public static final StencilOperator findOperator(StencilTree reference) {
		if (!reference.is(OP_NAME)) { 
			reference = reference.find(OP_NAME);
			if (reference == null) {throw new IllegalArgumentException("Could not discern operator reference in " + Path.toString(reference));}
		}
		MultiPartName name=Freezer.multiName(reference);
		return findOperator(reference, name.name());
	}
	
	/**Find the operator with the given name in the operators list that is part of the same
	 * tree as the tree member.
	 */
	public static final StencilOperator findOperator(StencilTree treeMember, String name) {
		StencilTree op = findOperatorNode(operatorsList(treeMember), name);
		if (op.is(OPERATOR)) {
			return new SyntheticOperator(ParserConstants.STAND_IN_GROUP, op);
		} else {
			return ((OperatorProxy) op).getOperator();
		}
	}
	
	
	/**Get the tree-node associated with a given operator.**/
	public static final StencilTree findOperatorNode(StencilTree list, String name) {
		for (StencilTree op:list) {
			if (op.getText().equals(name)) {return op;}
		}
		throw new OperatorNotFoundException(name);
	}
	
	/**Given a node that belongs to a full program tree, 
	 * find the operators list in that program tree. 
	 */
	public static final StencilTree operatorsList(StencilTree treeMember) {
        return treeMember.getAncestor(PROGRAM).findDescendant(LIST_OPERATORS);
	}
	
	/**Add an operator of the given base type to the tree.
	 * Will return the unique name generated for the operator instance.
	 * An operator instance or reference will be created.
	 * 
	 * This method is used to add operators to a tree after unique naming 
	 * 	and the first operator instantiation run are completed.  After running
	 *  this method, the newly added operator will have the same status as operators
	 *  used directly by the programmer. 
	 * 
	 * @param base  What operator should this new operator be an instance of?
	 * @param inTree A node in the tree that will contain the operator (the operators list will automatically be found)
	 * 				This will also be used to get input information for any generated tokens (used in error reporting),
	 * 				so it is best if it is related to the position of the operator to be generated.
	 * @return Name  the new operator instance recieves
	 */
 	public static final String addOperator(String base, Specializer spec, ModuleCache modules, StencilTree inTree, TreeAdaptor adaptor) {
 		StencilTree opList = operatorsList(inTree);
 		String newName = genSym(base);
 		
 		MultiPartName name;
 		StencilOperator op;
 		name= MultiPartName.parse(base);

 		try {op = modules.instance(name, null, spec);}
 		catch (OperatorInstanceException e) {throw new IllegalArgumentException("Error creating operator: " + base, e);}
 		
 		OperatorProxy proxy = (OperatorProxy) adaptor.create(OPERATOR_PROXY, inTree.token,newName);
 		proxy.setOperator(ParserConstants.STAND_IN_GROUP, op, op.getOperatorData());

 		adaptor.addChild(opList, proxy);
 		
 		return newName; 		
	}
}
