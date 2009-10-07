/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.unittests.adapters;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import stencil.unittests.adapters.examples.*;

public abstract class Examples extends TestCase {
	protected String[] configs;
	
	protected Examples() {this(null);}
	protected Examples(String[] configs) {setConfigs(configs);}
	
	protected void setConfigs(String... configs) {this.configs = configs;}
	
	//Run a test.  Preferentially throws the txt error, if there is one.
	
	private void testOne(ImageTest imageTest) throws Throwable {
		Throwable pngFail = null;
		Throwable txtFail = null;
		
		imageTest.setUp();
		
		try {imageTest.testPNG();}
		catch (AssertionFailedError e1) {pngFail = e1;}
		catch (Throwable e) {
			pngFail = e;
			System.err.println("Error creating test PNG:");
			e.printStackTrace();
		}
		
		try {imageTest.testTXT();} 
		catch (AssertionFailedError e1) {txtFail = e1;}
		catch (Throwable e) {
			txtFail = e;
			System.err.println("Error creating test TXT:");
			e.printStackTrace();
		}
		
		if (pngFail == null && txtFail == null) {return;} //passed!
		if (pngFail == null && txtFail != null) {throw txtFail;}
		if (pngFail != null && txtFail == null) {throw pngFail;}
		
		if (txtFail != null) {throw new Exception("Error creating txt and png (txt error enclosed)", txtFail);}
		throw new Exception("Error creating png", pngFail);		
	}


	public void testAutoGuide_Axis() throws Throwable {
		testOne(new AutoGuide_Axis(configs));
	}

	public void testAutoGuide_Sidebar() throws Throwable {
		testOne(new AutoGuide_Sidebar(configs));
	}

	public void testNodeLink() throws Throwable {
		testOne(new NodeLink(configs));
	}

	public void testPoverty() throws Throwable {
		testOne(new Poverty(configs));
	}

	public void testRegistration() throws Throwable {
		testOne(new Registration(configs));
	}
	
	public void testRotation() throws Throwable {
		testOne(new Rotation(configs));
	}
	
	public void testSeeTest() throws Throwable {
		testOne(new SeeTest(configs));
	}
	
	public void testSimpleLines() throws Throwable {
		testOne(new SimpleLines(configs));
	}
	
	public void testSourceForge() throws Throwable {
		testOne(new Sourceforge(configs));
	}

	public void testStocks() throws Throwable {
		testOne(new Stocks(configs));
	}
	
	public void testTweetCount() throws Throwable {
		testOne(new TweetCount(configs));
	}
	
	public void testVSM() throws Throwable {
		testOne(new VSM(configs));
	}

	public void testVSM2() throws Throwable {
		testOne(new VSM2(configs));
	}
}
