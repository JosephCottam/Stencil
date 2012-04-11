package stencil.testUtilities;

import javax.swing.*;

import java.io.FileInputStream;
import java.util.Properties;

import org.antlr.runtime.tree.*;

import stencil.parser.ParseStencil;
import stencil.parser.string.*;
import stencil.testUtilities.treeView.ANTLRNode;
import stencil.util.FileUtils;

public class PrettyPrint {
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
		
		tree = stencil.parser.ParseStencil.checkParse(text);
		try {
			System.out.println(stencil.parser.string.PrettyPrinter.format(tree));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		f = new stencil.testUtilities.treeView.TreeFrame(tree, new StencilParser(null));
		f.setSize(500, 800);
		f.setVisible(true);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
}
