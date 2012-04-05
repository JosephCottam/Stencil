package stencil.explore.util;

import java.io.BufferedWriter;
import java.io.FileWriter;

import stencil.explore.model.Model;
import stencil.util.FileUtils;

/**Utilities for loading/saving stencil explore files (Stencil+Sources information).*/
public abstract class StencilIO {


	/**Creates an editor using the passed filename as the data source.*/
	public static void load(String filename, Model model) {
		String stencil = "";

		try {stencil = FileUtils.readFile(filename);}
		catch (Exception e) {
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
