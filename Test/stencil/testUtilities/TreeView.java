package stencil.testUtilities;

import javax.swing.*;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.*;

import stencil.adapters.java2D.Adapter;
import stencil.interpreter.tree.Freezer;
import stencil.parser.ParseStencil;
import stencil.parser.string.*;
import stencil.parser.tree.StencilTree;
import stencil.testUtilities.treeView.ANTLRNode;
import stencil.util.FileUtils;
import static stencil.unittests.parser.string.TestParseStencil.ancestryCheck;

public class TreeView {
	//Arguments: <StencilProperties> [-header] <SourceFile>
	//	If header is indicated, will treat source file as if it has a stencil explore header
	//  If header is not indicated, all lines in the source file will loaded
	//  Stencil Properties file must always be indicated
	 public static void main(String args[]) throws Exception {
		 ParseStencil.abortOnValidationException = false;
		 
		String file = args[1];
		String text = FileUtils.readFile(file);
		//Init modules...
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(args[0]));
		ANTLRNode.showClass= true;
		
		Tree tree;
		JFrame f;
		
		if (file == null) {throw  new IllegalArgumentException("Must provide file name.");}
		

		stencil.Configure.loadProperties(props);
		
		tree = stencil.parser.ParseStencil.programTree(text, Adapter.ADAPTER);
		f = new stencil.testUtilities.treeView.TreeFrame(tree, new StencilParser(null));
		f.setSize(500, 800);
		f.setVisible(true);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {ancestryCheck(tree);}
		catch (Exception e) {
			System.err.println("Error checking ancestory:");
			System.err.println(e.getMessage());
		}
		
		if (tree instanceof StencilTree && tree.getType()==StencilParser.PROGRAM) {
			try {Freezer.program((StencilTree) tree);}
			catch (Exception e) {
				System.err.println("Error freezing program");
				e.printStackTrace();
			}
		}
	}
}
