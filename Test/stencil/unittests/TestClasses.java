package stencil.unittests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import stencil.testUtilities.StringUtils;

public class TestClasses extends StencilTestCase {
	public void testInit() throws Exception {
		ArrayList errors = new ArrayList();
		Collection<String> files= StringUtils.allFiles(".", ".class");
		assertTrue("No class files found.", files.size() >0);

		int prefixLength = new java.io.File(".").getCanonicalPath().length() + 5; //five is for "/bin/"
		
		for (String fileName: files) {
			if (fileName.contains("$")) {continue;}
			try {
				String truncated = fileName.substring(prefixLength, fileName.length()-6); // -6 to remove ".class" 
				String className = truncated.replace("/", ".");
				
				Class.forName(className);	//Get the class performs init
			} catch (Throwable e) {
				e.printStackTrace();
				File f = new File(fileName);
				errors.add(f.getCanonicalFile());
			}			
			
			if (errors.size()>0) {
				fail("Could not  init " + errors.size() + " classes: " + java.util.Arrays.deepToString(errors.toArray()));
			}
		}
	}
}
