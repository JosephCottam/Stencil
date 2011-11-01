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
		else if (type.equals(WindowStateSource.NAME)) {return new WindowStateSource(name);}
		else if (type.equals(RandomSource.NAME)) {return new RandomSource(name);}
		else if (type.equals(SequenceSource.NAME)) {return new SequenceSource(name);}
		else if (type.equals(BinarySource.NAME)) {return new BinarySource(name);}
		else if (type.equals(TwitterSource.NAME)) {return new TwitterSource(name);}
		
		throw new RuntimeException("Could not find source mapping for " + type);
	}
}
