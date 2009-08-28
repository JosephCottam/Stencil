package stencil.unittests.adapters.examples;

public class VSM2 extends ImageTest {
	public VSM2(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/VSM/",
				  "VSM.stencil",
				  "BFS VertexList",
				  "ws_400_12_0.1.bfs ws_400_12_0.1.adj",
				  "VSM2.txt",
				  "VSM2.png", configs));
	}
}
