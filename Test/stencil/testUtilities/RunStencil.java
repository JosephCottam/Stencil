package stencil.testUtilities;

import stencil.parser.ParseStencil;
import static stencil.explore.Application.*;

/**Run the current stencil.*/
public class RunStencil {
	//Arguments: <StencilProperties> [-header] <SourceFile>
	//	If header is indicated, will treat source file as if it has a stencil explore header
	//  If header is not indicated, all lines in the source file will loaded
	//  Stencil Properties file must always be indicated
	 public static void main(String args[]) throws Exception {
		ParseStencil.abortOnValidationException = false;

		stencil.explore.PropertyManager.exploreConfig = "./TestData/Explore.properties";
		stencil.explore.PropertyManager.stencilConfig = "./TestData/Stencil.properties";

		String command = "./ " + OPEN_FLAG + " ./TestData/StencilExplore.stencil " + "-txt test.txt";
		
		stencil.explore.ui.Batch.batchInvoke(command);
	}
}
