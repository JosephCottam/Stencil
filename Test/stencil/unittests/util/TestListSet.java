package stencil.unittests.util;

import stencil.util.collections.ListSet;
import junit.framework.*; 

public class TestListSet extends TestCase {
	public void testAdd() {
		ListSet<String> l = new ListSet<String>();
		l.add("Test");
		l.add("Test2");
		
		Assert.assertTrue(l.contains("Test"));
		try {
			l.add("Test");
			Assert.fail("Able to add the same element twice (no index).");
		} catch (IllegalArgumentException e1) {/*Exception expected.*/}

		try {
			l.add(3, "Test");
			Assert.fail("Able to add the same element twice (with index).");
		} catch (IllegalArgumentException e1) {/*Exception expected.*/}

		try {
			l.add(null);
			Assert.fail("Able to add the null element.");
		} catch (IllegalArgumentException e1) {/*Exception expected.*/}		
	}
	
	public void testSet(){
		ListSet<String> l = new ListSet<String>();
		l.add(0,"Test1");
		l.add(1,"Test2");
		l.add(2,"Test3");
		l.add(3,"Test4");
		l.add(4,"Test5");
		
		try {
			l.set(1,"Test1");
			Assert.fail("Able to add the same element twice via 'set'");
		} catch (IllegalArgumentException e) {/*Exception expected.*/}
		
		try {
			l.set(1, "Test13");
		} catch (IllegalArgumentException e) {
			Assert.fail("Unamble to insert unique item via 'set'.");
		}		
	}
	
	public void testMove() {
		ListSet<String> l = new ListSet<String>();
		l.add(0,"Test1");
		l.add(1,"Test2");
		l.add(2,"Test3");
		l.add(3,"Test4");
		l.add(4,"Test5");
		
		try {
			l.move(3, l.get(0));
		} catch (IllegalArgumentException e) {
			Assert.fail("Unamble to insert unique item via 'set'.");
		}			
		Assert.assertEquals("Test1", l.get(3));
	}
}
