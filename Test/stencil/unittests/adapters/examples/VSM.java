package stencil.unittests.adapters.examples;

public class VSM extends ImageTest {
	public VSM(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/VSM/",
				resultSpace("VSM"),
				  "VSM.stencil",
				  "BFS VertexList",
				  "ws_400_12_0.1.bfs ws_400_12_0.1.adj",
				  "VSM.png", configs));
	}
}
