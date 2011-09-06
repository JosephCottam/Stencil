package stencil.unittests.adapters.examples;

public class VSM_FillGrid extends ImageTest {
	public VSM_FillGrid(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/VSM/",
				resultSpace("VSM"),
				"VSM.stencil",
				  "BFS VertexList",
				  "fillGrid.bfs fillGrid.adj",
				  "fillGrid.txt",
				  "fillGrid.png", configs));
	}
}
