package stencil.testUtilities;

import javax.swing.*;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import stencil.adapters.piccoloDynamic.Adapter;
import stencil.parser.string.*;
import stencil.parser.tree.Program;
import stencil.parser.tree.StencilTreeAdapter;
import stencil.rules.ModuleCache;
import stencil.legend.module.util.*;

public class TreeView {
	public static String HEADER_FLAG = "-header";

	public static String test = "LEGEND Justify(max, current) -> (just)\n" +
//								"(current = 0) => just : CENTER\n" +
//								"(current = divide(max, 2) -> (div)) => just : CENTER\n" +
//								"(current < divide(max, 2) -> (div)) => just : LEFT\n" +
								"(current > divide(max, 2) -> (div)) => just : RIGHT\n" +
								"ALL => just: CENTER";

	public static Tree pieceWise(String source, Adapter adapter) throws Exception {
		ANTLRStringStream input = new ANTLRStringStream(source);
		StencilTreeAdapter treeAdaptor = new StencilTreeAdapter();
		CommonTreeNodeStream treeTokens;
		
		StencilLexer lexer = new StencilLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		StencilParser parser = new StencilParser(tokens);
		parser.setTreeAdaptor(treeAdaptor);
		Program p= (Program) parser.program().getTree();

		//Ensure the proper order blocks
		treeTokens = new CommonTreeNodeStream(p);
		EnsureOrders orders = new EnsureOrders(treeTokens);
		orders.setTreeAdaptor(treeAdaptor);
		p = orders.ensureOrder(p);

		//Do module imports
		treeTokens = new CommonTreeNodeStream(p);
		Imports imports = new Imports(treeTokens);
		ModuleCache modules = imports.processImports(p);

		//Create ad-hoc operators
		AdHocOperators adHoc = new AdHocOperators(treeTokens, modules, adapter);
		adHoc.downup(p);
		
		//Add default specializers where required
		treeTokens = new CommonTreeNodeStream(p);
		Specializers specializers = new Specializers(treeTokens, modules);
		specializers.setTreeAdaptor(treeAdaptor);
		specializers.downup(p);

		treeTokens = new CommonTreeNodeStream(p);
		GuideSpecializers guideSpecailizers  = new GuideSpecializers(treeTokens, adapter);
		guideSpecailizers.setTreeAdaptor(treeAdaptor);
		guideSpecailizers.downup(p);


		//Ensure that autoguide requirements are met
		EnsureGuideOp ensure = new EnsureGuideOp(treeTokens,modules); 
		ensure.setTreeAdaptor(treeAdaptor);		
		p = (Program) ensure.transform(p);
		
		//Prime tree nodes with operators from the modules cache
		SetOperators set = new SetOperators(treeTokens, modules);
		set.downup(p);
		
		treeTokens = new CommonTreeNodeStream(p);
		AutoGuide ag = new AutoGuide(treeTokens, modules);
		ag.setTreeAdaptor(treeAdaptor);
		p = (Program) ag.transform(p);
		return p;
	}

	//Arguments: <StencilProperties> [-header] <SourceFile>
	//	If header is indicated, will treat source file as if it has a stencil explore header
	//  If header is not indicated, all lines in the source file will loaded
	//  Stencil Properties file must always be indicated
	 public static void main(String args[]) throws Exception {
		String text = TreeView.test;
		String file = null;
		//Init modules...
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(args[0]));
		
		
		if (args.length >0){
			if (args[1].equals(HEADER_FLAG)) {
				file =args[2];
				text = StringUtils.getContents(args[2], true);
			} else {
				file =args[1];
				text = StringUtils.getContents(args[1], false);
			}
		}
		
		Tree tree;
		JFrame f;
		if (file == null) {throw  new IllegalArgumentException("Must provide file name.");}
		
		if (file.endsWith(".xml")) {
			tree = (Tree) ModuleDataParser.parse(file);
			f = new stencil.testUtilities.TreeFrame(tree, new ModuleDataParser(null));
		} else {
			stencil.Configure.loadProperties(props);
//			tree = stencil.parser.string.ParseStencil.parse(text);
			tree = pieceWise(text, Adapter.INSTANCE);
			f = new stencil.testUtilities.TreeFrame(tree, new StencilParser(null));
		}
		f.setVisible(true);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
