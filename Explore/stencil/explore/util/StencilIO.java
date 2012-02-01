package stencil.explore.util;

import static stencil.explore.model.Model.NEW_LINE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

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

		try {
			StringBuilder lines = new StringBuilder();
			while (input.ready()) {
				lines.append(input.readLine());
				lines.append(NEW_LINE);
			}

			stencil = lines.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error restoring prior state.", e);
		}
		
		try {model.setStencil(stencil);}
		catch (StencilRunner.AbnormalTerminiationException e) {
			//Ignores error on load and just moves on!
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
			output.write(model.getStencil());
			output.close();
		} catch (Exception e ){
			throw new RuntimeException(e);
		}
	}
}
