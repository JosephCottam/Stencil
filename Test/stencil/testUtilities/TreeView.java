package stencil.testUtilities;

import javax.swing.*;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.*;

import stencil.adapters.java2D.Adapter;
import stencil.parser.string.*;
import stencil.testUtilities.treeView.ANTLRNode;
import static stencil.unittests.parser.string.TestParseStencil.ancestryCheck;

public class TreeView {
	public static String HEADER_FLAG = "-header";
	
	public static String test = "LEGEND Justify(max, current) -> (just)\n" +
//								"(current = 0) => just : CENTER\n" +
//								"(current = divide(max, 2) -> (div)) => just : CENTER\n" +
//								"(current < divide(max, 2) -> (div)) => just : LEFT\n" +
								"(current > divide(max, 2) -> (div)) => just : RIGHT\n" +
								"ALL => just: CENTER";

	//Arguments: <StencilProperties> [-header] <SourceFile>
	//	If header is indicated, will treat source file as if it has a stencil explore header
	//  If header is not indicated, all lines in the source file will loaded
	//  Stencil Properties file must always be indicated
	 public static void main(String args[]) throws Exception {
		 ParseStencil.abortOnValidationException = false;
		 
		String text = TreeView.test;
		String file = null;
		//Init modules...
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(args[0]));
		ANTLRNode.showClass= true;
		
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
		

		stencil.Configure.loadProperties(props);
		tree = stencil.parser.string.ParseStencil.parse(text, Adapter.INSTANCE);
//			tree = pieceWise(text, Adapter.INSTANCE);
		f = new stencil.testUtilities.treeView.TreeFrame(tree, new StencilParser(null));
		f.setVisible(true);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {ancestryCheck(tree);}
		catch (Exception e) {
			System.err.println("Error checking ancestory:");
			System.err.println(e.getMessage());
		}

	}
}
