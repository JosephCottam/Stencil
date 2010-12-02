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

import junit.framework.TestCase;
import stencil.unittests.adapters.examples.*;

public abstract class Examples extends TestCase {
	protected String[] configs;
	
	protected Examples() {this(null);}
	protected Examples(String[] configs) {setConfigs(configs);}
	
	protected void setConfigs(String... configs) {this.configs = configs;}
	
	public void testAutoGuide_AxisImage() throws Throwable {new AutoGuide_Axis(configs).testPNG();}
	public void testAutoGuide_AxisText() throws Throwable {new AutoGuide_Axis(configs).testTXT();}

	public void testAutoGuide_SidebarText() throws Throwable {new AutoGuide_Legend(configs).testTXT();}
	public void testAutoGuide_SidebarImage() throws Throwable {new AutoGuide_Legend(configs).testPNG();}

	public void testNodeLinkText() throws Throwable {new NodeLink(configs).testTXT();}
	public void testNodeLinkImage() throws Throwable {new NodeLink(configs).testPNG();}

	public void testRegistrationText() throws Throwable {new Registration(configs).testTXT();}
	public void testRegistrationImage() throws Throwable {new Registration(configs).testPNG();}
	
	public void testRotationText() throws Throwable {new Rotation(configs).testTXT();}
	public void testRotationImage() throws Throwable {new Rotation(configs).testPNG();}
	
	public void testSeeTestText() throws Throwable {new SeeTest(configs).testTXT();}
	public void testSeeTestImage() throws Throwable {new SeeTest(configs).testPNG();}
	
	public void testSimpleLinesText() throws Throwable {new SimpleLines(configs).testTXT();}
	public void testSimpleLinesImage() throws Throwable {new SimpleLines(configs).testPNG();}
	
	public void testSourceForgeText() throws Throwable {new Sourceforge(configs).testTXT();}
	public void testSourceForgeImage() throws Throwable {new Sourceforge(configs).testPNG();}

	public void testStocksText() throws Throwable {new Stocks(configs).testTXT();}
	public void testStocksImage() throws Throwable {new Stocks(configs).testPNG();}
	
	public void testTweetCountText() throws Throwable {new TweetCount(configs).testTXT();}
	public void testTweetCountImage() throws Throwable {new TweetCount(configs).testPNG();}
	
	public void testFillGridText() throws Throwable {new VSM_FillGrid(configs).testTXT();}
	public void testFillGridImage() throws Throwable {new VSM_FillGrid(configs).testPNG();}

	public void testVSM2Text() throws Throwable {new VSM(configs).testTXT();}
	public void testVSM2Image() throws Throwable {new VSM(configs).testPNG();}

	public void testFlowersImage() throws Throwable {new AutoGuide_Flowers(configs).testPNG();}
	public void testFlowersText() throws Throwable {new AutoGuide_Flowers(configs).testTXT();}

	public void testBarleyImage() throws Throwable {new AutoGuide_Barley(configs).testPNG();}
	public void testBarleyText() throws Throwable {new AutoGuide_Barley(configs).testTXT();}

	public void testRoseImage() throws Throwable {new AutoGuide_Rose(configs).testPNG();}
	public void testRoseText() throws Throwable {new AutoGuide_Rose(configs).testTXT();}
}


