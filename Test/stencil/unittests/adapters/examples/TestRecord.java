package stencil.unittests.adapters.examples;

import static stencil.explore.Application.OPEN_FLAG;
import static stencil.explore.Application.SOURCE_FLAG;

import java.io.File;

public final class TestRecord {
	static final String TEST_PREFIX = "TEST_";
	static final String DELTA_PREFIX = "DELTA_";

	final String[] configs;
	final String inputDir; 	//Test input info (stencil, data, ref image, etc)
	final String outputDir;	//Where should the results go?	ABSOLUATE PATH!!!
	final String stencil; //Stencil name
	final String[] names; //Stream names to use with the sources
	final String[] inputs;//Stream source inputs
	final String TXT;     //Text file to output
	final String PNG;     //PNG file to output

	public TestRecord(String inputDir, String outputDir, String stencil, String sourceNames, String inputs, String TXT, String PNG, String[] configs) {
		this.configs = configs;
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		this.stencil = stencil;
		this.names = sourceNames == null? new String[0] : sourceNames.split(" ");
		this.inputs = inputs == null? new String[0] :inputs.split(" ");
		this.TXT = TXT;
		this.PNG = PNG;
	}

	public String getTextCommand() {
		StringBuilder b = new StringBuilder();
		b.append(getProfileCommand());

		b.append("-txt " + getTestTXT());
		return b.toString();
	}
	
	public String getImageCommand() {
		StringBuilder b = new StringBuilder();
		b.append(getProfileCommand());

		b.append("-png2 1000 -1 " + getTestPNG());
		return b.toString();			
	}
			
	public String getProfileCommand() {
		StringBuilder b = new StringBuilder();

		
		b.append(inputDir);

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

	public String getTestTXT() {return outputDir + File.separator + TEST_PREFIX + TXT;}
	public String getTestPNG() {return outputDir + File.separator + TEST_PREFIX + PNG;}
	public String getDeltaPNG() {return outputDir + File.separator + DELTA_PREFIX + PNG;}

	public String getTXT() {return inputDir + TXT;}
	public String getPNG() {return inputDir + PNG;}
}