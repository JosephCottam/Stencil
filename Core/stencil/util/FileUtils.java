package stencil.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public final class FileUtils {
    public static final String NEW_LINE = System.getProperty("line.separator");
	
	private FileUtils() {}
	
	public static String readFile(String filename) throws Exception{
		return readFile(new File(filename));
	}
	
	public static String readFile(File f) throws Exception {
		try {return readFile(new BufferedReader(new FileReader(f)));}
		catch(Exception e) {throw new RuntimeException("Error Restoring prior state.", e);}
		
	}
	
	public static String readFile(BufferedReader input) throws Exception {
		StringBuilder lines = new StringBuilder();

		while (input.ready()) {
			lines.append(input.readLine());
			lines.append(NEW_LINE);
		}
		return lines.toString();
	}

}
