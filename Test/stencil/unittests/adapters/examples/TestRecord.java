package stencil.unittests.adapters.examples;

import static stencil.explore.Application.OPEN_FLAG;
import static stencil.explore.Application.SOURCE_FLAG;

public final class TestRecord {
	static final String TEST_PREFIX = "TEST_";
	static final String DELTA_PREFIX = "DELTA_";

	String[] configs;
	String prefix;  //Directory prefix
	String stencil; //Stencil name
	String[] names; //Stream names to use with the sources
	String[] inputs;//Stream source inputs
	String TXT;     //Text file to output
	String PNG;     //PNG file to output

	public TestRecord(String prefix, String stencil, String sourceNames, String inputs, String TXT, String PNG, String[] configs) {
		this.configs = configs;
		this.prefix = prefix;
		this.stencil = stencil;
		this.names = sourceNames == null? new String[0] : sourceNames.split(" ");
		this.inputs = inputs == null? new String[0] :inputs.split(" ");
		this.TXT = TXT;
		this.PNG = PNG;
	}

	public String getTextCommand() {
		StringBuilder b = new StringBuilder();
		b.append(getProfileCommand());

		b.append("-txt " + getBaseTestTXT());
		return b.toString();
	}
	
	public String getImageCommand() {
		StringBuilder b = new StringBuilder();
		b.append(getProfileCommand());

		b.append("-png2 1000 -1 " + getBaseTestPNG());
		return b.toString();			
	}
			
	public String getProfileCommand() {
		StringBuilder b = new StringBuilder();

		
		b.append(prefix);

		for (String file: configs) {
			b.append(" -settings ");
			b.append(file);
		}
		
		b.append(" ");
		b.append(OPEN_FLAG);
		b.append(" ");
		b.append(stencil);
		b.append(" ");

		for (int i=0; i< inputs.length; i++) {
			b.append(SOURCE_FLAG);
			b.append(" ");
			b.append(names[i]);
			b.append(" ");
			b.append(inputs[i]);
			b.append(" ");
		}
		return b.toString();
	}

	private String getBaseTestTXT() {return TEST_PREFIX + TXT;}
	private String getBaseTestPNG() {return TEST_PREFIX + PNG;}
	private String getBaseDelta() {return TEST_PREFIX + DELTA_PREFIX + PNG;}

	public String getTestTXT() {return prefix + getBaseTestTXT();}
	public String getTestPNG() {return prefix + getBaseTestPNG();}
	public String getDeltaPNG() {return prefix + getBaseDelta();}

	public String getTXT() {return prefix + TXT;}
	public String getPNG() {return prefix + PNG;}
}