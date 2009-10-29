package stencil.unittests.adapters.examples;

public class VSM extends ImageTest {
	public VSM(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/VSM/",
				  "VSM.stencil",
				  "BFS VertexList",
				  "er_100_0.005.bfs er_100_0.005.adj",
				  "VSM1.txt",
				  "VSM1.png", configs));
	}
}
