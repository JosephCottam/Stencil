package stencil.testUtilities;

import stencil.unittests.adapters.examples.*;

public class Profile {
	private static ImageTest getTest(String... properties) {
		return new TextArc(properties);
	}
	
	
	private static void justLoad() throws Exception {
		stencil.explore.PropertyManager.exploreConfig = "./TestData/Explore.properties";
		stencil.explore.PropertyManager.stencilConfig = "./TestData/Stencil.properties";
		
		String profileProperties = Profile.class.getResource("Profile.properties").getFile();
		
		ImageTest test = getTest(profileProperties);
		TestRecord r = test.getTestRecord();
		
		stencil.explore.ui.Batch.batchInvoke(r.getProfileCommand());
	}

	private static void withRender() throws Exception {
		stencil.explore.PropertyManager.exploreConfig = "./TestData/Explore.properties";
		stencil.explore.PropertyManager.stencilConfig = "./TestData/Stencil.properties";

		ImageTest test = getTest();
		TestRecord r = test.getTestRecord();
		
		long start = System.currentTimeMillis();
		stencil.explore.ui.Batch.batchInvoke(r.getImageCommand());
		long end = System.currentTimeMillis();
		long elapse = end-start;
		System.out.printf("Full runtime: %1$s seconds (%2$s ms) ", elapse/1000, elapse);
	}

	
	public static void main(String[] args) throws Exception {
		if (args.length == 1 && args[0].equals("render")) {
			withRender();
		} else {
			justLoad();
		} 
	}

}
