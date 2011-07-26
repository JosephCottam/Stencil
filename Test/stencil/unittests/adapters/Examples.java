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

	public void testAutoGuide_LegendText() throws Throwable {new AutoGuide_Legend(configs).testTXT();}
	public void testAutoGuide_LegendImage() throws Throwable {new AutoGuide_Legend(configs).testPNG();}

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
	
	public void testAutoguide_TweetCountText() throws Throwable {new AutoGuide_TweetCount(configs).testTXT();}
	public void testAutoguide_TweetCountImage() throws Throwable {new AutoGuide_TweetCount(configs).testPNG();}
	
	public void testFillGridText() throws Throwable {new VSM_FillGrid(configs).testTXT();}
	public void testFillGridImage() throws Throwable {new VSM_FillGrid(configs).testPNG();}

	public void testVSM2Text() throws Throwable {new VSM(configs).testTXT();}
	public void testVSM2Image() throws Throwable {new VSM(configs).testPNG();}

	public void testFlowersImage() throws Throwable {new AutoGuide_Flowers(configs).testPNG();}
	public void testFlowersText() throws Throwable {new AutoGuide_Flowers(configs).testTXT();}

	public void testFlowersCrossImage() throws Throwable {new AutoGuide_FlowersCross(configs).testPNG();}
	public void testFlowersCrossText() throws Throwable {new AutoGuide_FlowersCross(configs).testTXT();}

	public void testFlowersMultiImage() throws Throwable {new AutoGuide_FlowersMulti(configs).testPNG();}
	public void testFlowersMultiText() throws Throwable {new AutoGuide_FlowersMulti(configs).testTXT();}

	public void testFlowersFlexImage() throws Throwable {new AutoGuide_FlowersFlex(configs).testPNG();}
	public void testFlowersFlexText() throws Throwable {new AutoGuide_FlowersFlex(configs).testTXT();}

	
	public void testBarleyImage() throws Throwable {new AutoGuide_Barley(configs).testPNG();}
	public void testBarleyText() throws Throwable {new AutoGuide_Barley(configs).testTXT();}

	public void testRoseImage() throws Throwable {new AutoGuide_Rose(configs).testPNG();}
	public void testRoseText() throws Throwable {new AutoGuide_Rose(configs).testTXT();}

	public void testTextArcImage() throws Throwable {new TextArc(configs).testPNG();}
	public void testTextArcText() throws Throwable {new TextArc(configs).testTXT();}
	
	public void testArcsText() throws Throwable {new Arcs(configs).testTXT();}
	public void testArcsImage() throws Throwable {new Arcs(configs).testPNG();}
	
	public void testArrowheadsText() throws Throwable {new Arrowheads(configs).testTXT();}
	public void testArrowheadsImage() throws Throwable {new Arrowheads(configs).testPNG();}
	
	public void testJungTreeText() throws Throwable {new JungTree(configs).testTXT();}
	public void testJungTreeImage() throws Throwable {new JungTree(configs).testPNG();}
	
	public void testRadialTreeText() throws Throwable {new RadialTree(configs).testTXT();}
	public void testRadialTreeImage() throws Throwable {new RadialTree(configs).testPNG();}
	
	public void testUMDTreeMapText() throws Throwable {new UMDTreeMap(configs).testTXT();}
	public void testUMDTreeMapImage() throws Throwable {new UMDTreeMap(configs).testPNG();}

	public void testSquareShellsText() throws Throwable {new SquareShells(configs).testTXT();}
	public void testSquareShellsImage() throws Throwable {new SquareShells(configs).testPNG();}

	
}


