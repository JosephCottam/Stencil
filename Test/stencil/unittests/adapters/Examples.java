package stencil.unittests.adapters;

import junit.framework.TestCase;
import stencil.unittests.adapters.examples.*;

public abstract class Examples extends TestCase {
	protected String[] configs;
	
	protected Examples() {this(null);}
	protected Examples(String[] configs) {setConfigs(configs);}
	
	protected void setConfigs(String... configs) {this.configs = configs;}
	
	public void testAutoGuide_AxisImage() throws Throwable {new AutoGuide_Axis(configs).testPNG();}
	public void testAutoGuide_LegendImage() throws Throwable {new AutoGuide_Legend(configs).testPNG();}
	public void testNodeLinkImage() throws Throwable {new NodeLink(configs).testPNG();}
	public void testRegistrationImage() throws Throwable {new Registration(configs).testPNG();}
	public void testRotationImage() throws Throwable {new Rotation(configs).testPNG();}
	public void testSeeTestImage() throws Throwable {new SeeTest(configs).testPNG();}
	public void testSimpleLinesImage() throws Throwable {new SimpleLines(configs).testPNG();}
	public void testSourceForgeImage() throws Throwable {new Sourceforge(configs).testPNG();}
	public void testStocksImage() throws Throwable {new Stocks(configs).testPNG();}
	public void testAutoguide_TweetCountImage() throws Throwable {new AutoGuide_TweetCount(configs).testPNG();}
	public void testFillGridImage() throws Throwable {new VSM_FillGrid(configs).testPNG();}
	public void testVSM2Image() throws Throwable {new VSM(configs).testPNG();}
	public void testEpiCImage() throws Throwable {new EpiC(configs).testPNG();}
	public void testFlowersImage() throws Throwable {new AutoGuide_Flowers(configs).testPNG();}
	public void testFlowersCrossImage() throws Throwable {new AutoGuide_FlowersCross(configs).testPNG();}
	public void testFlowersMultiImage() throws Throwable {new AutoGuide_FlowersMulti(configs).testPNG();}
	public void testFlowersFlexImage() throws Throwable {new AutoGuide_FlowersFlex(configs).testPNG();}
	public void testBarleyImage() throws Throwable {new AutoGuide_Barley(configs).testPNG();}
	public void testRoseImage() throws Throwable {new AutoGuide_Rose(configs).testPNG();}
	public void testTextArcImage() throws Throwable {new TextArc(configs).testPNG();}	
	public void testArcsImage() throws Throwable {new Arcs(configs).testPNG();}	
	public void testArrowheadsImage() throws Throwable {new Arrowheads(configs).testPNG();}
	public void testJungTreeImage() throws Throwable {new JungTree(configs).testPNG();}	
	public void testRadialTreeImage() throws Throwable {new RadialTree(configs).testPNG();}
	public void testUMDTreeMapImage() throws Throwable {new UMDTreeMap(configs).testPNG();}
	public void testSquareShellsImage() throws Throwable {new SquareShells(configs).testPNG();}
}


