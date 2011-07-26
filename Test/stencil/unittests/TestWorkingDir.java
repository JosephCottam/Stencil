package stencil.unittests;

import stencil.WorkingDir;
import java.io.*;

public class TestWorkingDir extends junit.framework.TestCase {
	public void testSet() throws Exception {
		File prior = WorkingDir.get();
		String path = "/NotThere/";
		WorkingDir.set(path);
		File post = WorkingDir.get();
		assertNotNull(post);
		assertFalse("Files same after update.", post.equals(prior));
		assertEquals("Update not performed.", new File(path), post);
		
		prior = post;
		
		path = System.getProperty("user.dir");
		WorkingDir.set(path);
		post = WorkingDir.get();
		assertNotNull(post);
		assertFalse("Files same after update.", post.equals(prior));
		assertEquals("Update not performed.", new File(path), post);
		
		WorkingDir.set((String) null);
		assertNotNull(WorkingDir.get());

		WorkingDir.set((File) null);
		assertNotNull(WorkingDir.get());

		
		path = "/Some/Path/To/Somewhere";
		WorkingDir.set(path);
		assertNotNull(WorkingDir.get());
		assertEquals("Update not performed.", new File(path), WorkingDir.get());
	}
	
	public void testResolve() throws Exception {
		String root = "/TestRoot/Root2/";
		WorkingDir.set(root);
		assertEquals("Test not properly intialized.", new File(root), WorkingDir.get());
		
		String[][] tests = new String[][]{
				{"/TestRoot/Root2/level1/",	root + "level1/"},
				{"./level1/",				root + "level1/"},
				{"../",						"/TestRoot/"},
				{"/Not/TestRoot/",			"/Not/TestRoot/"},
				{"/NotTestRoot/level1/",	"/NotTestRoot/level1/"},
				{"level1",					root + "level1"}
		};
		
		for (String[] testCase: tests) {
			String test = testCase[0];
			String expect = testCase[1];
			String resolved =WorkingDir.resolve(test);
			assertEquals("Error testing: " + test, new File(expect).getCanonicalPath(), new File(resolved).getCanonicalPath());
		}
	}
	
	public void testRelativize() throws Exception {
		String root = "/Root/Root2/";
		WorkingDir.set(root);
		assertEquals("Test not properly intialized.", new File(root), WorkingDir.get());
		
		
		String[][] tests = new String[][] {
				{root + "more", 				"more"},
				{root + "more/and/more/",	"more/and/more"},
				{"/Not/Root",				"/Not/Root"}
		};
		
		for (String[] testCase: tests) {
			String test = testCase[0];
			String expect = testCase[1];
			String result = WorkingDir.relativize(test);
			assertEquals("Testing " + test, expect, result);
		}
		
	}

}
