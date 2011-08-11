package stencil.unittests.adapters.examples;

/**Tests the equivalence of the flex guide to explicitly chosen
 * guides if the explicit statement indicates the same sample type as the 
 * flex guide automatically chooses.
 * @author jcottam
 *
 */
public class AutoGuide_FlowersFlex extends ImageTest {
	public AutoGuide_FlowersFlex(String[] configs) {
		super(new TestRecord("./TestData/RegressionImages/Flowers/",
				  "AndersonFlowers-Flex.stencil",
				  null,
				  null,
				  "Andersonflowers.txt",
				  "Andersonflowers.png", configs));
	}
}
