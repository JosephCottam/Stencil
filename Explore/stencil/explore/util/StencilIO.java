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
package stencil.explore.util;

import static stencil.explore.model.Model.NEW_LINE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import stencil.explore.model.sources.*;
import stencil.explore.model.Model;

/**Utilities for loading/saving stencil explore files (Stencil+Sources information).*/
public abstract class StencilIO {
	
	/**Loads the contents of the specified file in to the
	 * passed model.*/
	public static void load(String filename, Model model) {
		BufferedReader input;
		try {input = new BufferedReader(new FileReader(filename));}
		catch(Exception e) {throw new RuntimeException("Error Restoring prior state.", e);}
		load(input, model);
	}

	/**Creates an editor using the passed buffer as the data source.*/
	public static void load(BufferedReader input, Model model) {
		String stencil = "";
		ListModel<StreamSource> sources = new ListModel<StreamSource>();

		try {
			String line =  input.readLine();

			//Restore list of sources
			StreamSource source = null;
			while (line != null && !line.equals("")) {
				if (line.startsWith("STREAM")) {
					if (source != null) {sources.add(source);}
					String type  = line.substring(line.indexOf(":") +2).trim();
					source = getByType("temp", type);
					source = source.restore(input);
					SourceCache.put(source);
				}
				line = input.readLine();
			}
			if (source !=null ) {sources.add(source);}

			//Restore stencil (one rule at a time)
			StringBuilder rule = new StringBuilder();
			while (input.ready()) {
				rule.append(input.readLine());
				rule.append(NEW_LINE);
			}


			stencil = rule.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error restoring prior state.", e);
		}
		
		try {model.setStencil(stencil);}
		catch (StencilRunner.AbnormalTerminiationException e) {
			//Ignores error on load and just moves on!
		} finally {
			model.clearSources(false);
			model.setSources(sources);
		}
	}

	
	/**Saves the current editor to the specified file.*/
	public static void save(String filename, Model model) {
		if (filename == null) {System.err.println("Could not save state, no location specified."); return;}
		
		BufferedWriter output;
		try {output= new BufferedWriter(new FileWriter(filename));}
		catch (Exception e) {throw new RuntimeException(e);}
		save(output, model);
	}

	/**Saves the current editor to the specified buffer.*/
	public static void save(BufferedWriter output, Model model) {
		try {
			for (StreamSource source:model.getSources()) {
				output.write(source.toString());
			}
			output.write(NEW_LINE);
			output.write(model.getStencil());
			output.close();
		} catch (Exception e ){
			throw new RuntimeException(e);
		}
	}
	
	
	/**Given a name and a type, returns a source element.**/
	public static StreamSource getByType(String name, String type) {
		assert type != null : "Type cannot be null.";

		if (type.equals(FileSource.NAME)) {return new FileSource(name);}
		else if (type.equals(DBSource.NAME)) {return new DBSource(name);}
		else if (type.equals(MouseSource.NAME)) {return new MouseSource(name);}
		else if (type.equals(TwitterSource.NAME)) {return new TwitterSource(name);}
		else if (type.equals(WindowStateSource.NAME)) {return new WindowStateSource(name);}
		else if (type.equals(RandomSource.NAME)) {return new RandomSource(name);}
		else if (type.equals(SequenceSource.NAME)) {return new SequenceSource(name);}
		
		throw new RuntimeException("Could not find source mapping for " + type);
	}
}
