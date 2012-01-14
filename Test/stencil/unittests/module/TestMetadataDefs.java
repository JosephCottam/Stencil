package stencil.unittests.module;

import java.util.*;
import java.io.File;

import stencil.module.util.*;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.Module;
import stencil.testUtilities.StringUtils;
import junit.framework.TestCase;


//TODO:Update to load the stencil meta-data and investigate all (and only) module files listed there
public class TestMetadataDefs  extends TestCase {
	public static final String METADATA_PATH = "./bin/stencil/";
	
	public void testParseAll() throws Exception {
		ArrayList errors = new ArrayList();
		Collection<String> files= StringUtils.allFiles(METADATA_PATH, ".class");
		assertTrue("No metadata files found.", files.size() >0);
		int prefixLength = new java.io.File(".").getCanonicalPath().length() + 5; //five is for "/bin/"
		
		for (String fileName: files) {
			try {
				String truncated = fileName.substring(prefixLength, fileName.length()-6); // -6 to remove ".class" 
				String className = truncated.replace("/", ".");
				
				try {
					Class c = Class.forName(className);
					if (c.getAnnotation(Module.class) == null) {continue;}
				} catch (Exception e) {continue;}
				
				ModuleDataParser.moduleData(className);
			}
			catch (Throwable e) {
				e.printStackTrace();
				File f = new File(fileName);
				errors.add(f.getCanonicalFile());
			}			
		}		
		
		if (errors.size()>0) {
			fail("Could not parse " + errors.size() + " files: " + java.util.Arrays.deepToString(errors.toArray()));
		}		
	}
	
	
	public void testDetails() throws Exception {
		ModuleData md = ModuleDataParser.moduleData("stencil.modules.Numerics");
		
		OperatorData od = md.getOperator("Add1"); 
		assertNotNull("Could not find Add1", od);
		assertNotNull(od.getFacet("map"));
		assertNotNull(od.getFacet("query"));
		assertEquals(MemoryUse.FUNCTION, od.getFacet("map").memUse());
		assertEquals(MemoryUse.FUNCTION, od.getFacet("query").memUse());
		assertEquals(od.getFacet("query").target(), od.getFacet("map").target());
	}
}
