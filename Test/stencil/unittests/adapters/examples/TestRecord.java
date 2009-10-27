/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

		b.append("-png 200 " + getBaseTestPNG());
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