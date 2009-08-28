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
package stencil.explore.ui.applet;


import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;


public class StencilItem {
	public String stencil;
	public String name;

	public StencilItem(String name, String stencil) {
		this.name = name;
		this.stencil = stencil;
		correctIndent();
	}

	//TODO: Write a real pretty-printer...
	private void correctIndent() {
		BufferedReader reader = new BufferedReader(new StringReader(stencil));
		int indentSize = 0;
		int emptyLines = 0;
		StringBuilder b = new StringBuilder();

		try {
			reader.mark(stencil.length());
			while(true) {
				char c = (char) reader.read();
				if (c == '\n') {indentSize=0; emptyLines++;}
				if (!Character.isWhitespace(c)) {break;}
				indentSize++;
			}
			reader = new BufferedReader(new StringReader(stencil));

			for (int i=emptyLines; i>0; i--) {reader.readLine();}

			if (indentSize !=0) {
				indentSize--;
				while (true) {
					String line = reader.readLine();
					if (line ==null) {break;}
					if (line.length() > indentSize) {b.append(line.substring(indentSize)); b.append("\n");}
					else {b.append(line.trim()); b.append("\n");}
				}
			}
		} catch (IOException e) {throw new RuntimeException("Error correcting indentation on stencil.", e);}
		stencil = b.toString();
	}
	
	public String toString() {return name;}
}
