package stencil.unittests.operator.module;

import java.util.*;
import java.io.File;

import stencil.operator.module.util.*;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;

public class TestMetadataDefs  extends TestCase {
	public static final String METADATA_PATH = "./Core/stencil/operator/module/provided/";
	
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
