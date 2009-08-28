package stencil.unittests.legend.module;

import java.util.*;
import java.io.File;

import stencil.legend.module.util.*;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;

public class TestMetadataDefs  extends TestCase {
	//TODO: Have it load the properties file and parse all the modules listed there
	public static final String METADATA_PATH = "../Stencil/src/stencil/legend/module/provided/";
	
	public void testParseAll() throws Exception {
		ArrayList errors = new ArrayList();
		
		Collection<String> files= StringUtils.allFiles(METADATA_PATH, ".xml");
		assertTrue("No metadata files found.", files.size() >0);

		for (String fileName: files) {
			try {
				ModuleDataParser.parse(fileName);
			}
			catch (Throwable e) {
				e.printStackTrace();
				File f = new File(fileName);
				errors.add(f.getName());
			}			
		}

		if (errors.size()>0) {
			fail("Could not parse " + errors.size() + " files: " + java.util.Arrays.deepToString(errors.toArray()));
		}		
	}
}
