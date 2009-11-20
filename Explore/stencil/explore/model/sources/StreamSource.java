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

import stencil.tuple.TupleStream;
import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.SourceEditor;

public abstract class StreamSource implements Comparable<StreamSource> {
	public static String DEFAULT_SEPARATOR = ",";

	protected final String name;

	protected StreamSource(String name) {
		this.name = name;
		SourceCache.put(this);
	}

	/**Get an editor panel to be used to set properties on this source?
	 * The panel returned should be linked into the source properly so no
	 * further wiring is required.
	 *
	 * */
	public abstract SourceEditor getEditor();

	/**Are all relevant values set?*/
	public abstract boolean isReady();

	/**What is the name of the stream in this source?
	 * Name is the only property shared between all source types.
	 * */
	public String name() {return name;}
	public abstract StreamSource name(String name);

	/**What type of stream is this?
	 * Looks for a static field called "NAME", if not
	 * found returns the class name.
	 *
	 * */
	public String getTypeName() {
		try {return (String) this.getClass().getField("NAME").get(null);}
		catch (Exception e) {return this.getClass().getName();}
	}

	public abstract String header();

	/**Create a tuple stream based on the information of the stream source.**/
	public abstract TupleStream getStream(Model context) throws Exception;

	/**Restore a stream source from its own 'toString' output.
	 * TODO: Generalize with a reflection lookup of field names or setters (then you can remove the individual class methods...maybe learn about standard Java serialization?)
	 * */
	public abstract StreamSource restore(BufferedReader input) throws IOException;

	/**Stream sources are compared according to their names.  A null stream
	 * source is considered less than an instantiated one.
	 */
	public int compareTo(StreamSource o) {
		if (o == null) {return 1;}
		if (this == o) {return 0;}
		
		return o.name().compareTo(this.name());
	}
}
