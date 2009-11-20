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
package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Mouse;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.TupleStream;
import stencil.util.streams.ui.MouseStream;

public class MouseSource extends StreamSource {
	public static final String NAME = "Mouse";

	public MouseSource(String name) {super(name);}
	public SourceEditor getEditor() {return new Mouse(name);}
	public boolean isReady() {return true;}
	public TupleStream getStream(Model context) {
		return new stencil.util.streams.ui.MouseStream(context.getStencilPanel());
	}
	
	public MouseSource name(String name) {
		if(this.name.equals(name)) {return this;}
		return new MouseSource(name);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("FREQUENCY: ");
		b.append(MouseStream.frequency);
		b.append("\n");
		return b.toString();
	}

	public MouseSource restore(BufferedReader input) throws IOException {
		String line =input.readLine();
		MouseSource result = this;
		while (line != null && !line.trim().equals("")) {
			input.mark(100);
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("FREQUENCY:")) {
				int freq = Integer.parseInt(line.split(": ")[1]);
				MouseStream.frequency = freq;
				break;
			}
			line = input.readLine();
		}
		input.reset();
		return result;
	}

	/**Constructs a header from the system mouse description and the default separator.**/
	public String header() {
		StringBuilder b = new StringBuilder();

		for (MouseStream.Names n: MouseStream.Names.values()) {
			b.append(n.name());
			b.append(StreamSource.DEFAULT_SEPARATOR);
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}

}
