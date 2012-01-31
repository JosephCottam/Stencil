package stencil.unittests.parser.string;

import junit.framework.TestCase;
import static stencil.parser.string.util.Utilities.genSym;
import static stencil.parser.string.util.Utilities.isGenSymRoot;

public class TestParserUtils extends TestCase {
	public void testGenSymAndRoot() {
		String[] vs = {"One","Two","#Skip","___$TRy$$___"};
		for (String s: vs) {
			String sym = genSym(s);
			assertFalse("GenSym did not modify root.", sym.equals(s));
			assertTrue("GenSymRoot did not detect potential root/sym pair", isGenSymRoot(s, sym));
		}

		for (String s: vs) {
			assertFalse("GEnSymRoot reported incorrect root/sym pairing", isGenSymRoot(s,s));
		}		
	}
}
