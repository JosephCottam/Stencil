package stencil.testUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StringUtils {
	public static final String STENCIL_CACHE_DIRECTORY = "./TestData/RegressionImages";
	
	public static String stripSpaces(String source) {return source.replaceAll("\\s", "");}

	/**Search out occurrences of 'element' in the 'source text.
	 * Adapted from: http://timvalenta.wordpress.com/2009/01/06/java-count-substring/
	 * 
	 */
	public static int countOccurances(String source, String element)
	{
	    int count = 0;
	    for (int fromIndex = 0; fromIndex > -1; count++)
	        fromIndex = source.indexOf(element, fromIndex + ((count > 0) ? 1 : 0));
	    return count - 1;
	}

	/**@return Map from filename to program itself.  Works recursively through directory structure.*/
	public static Map<String,String> allPrograms(String root) throws Exception {
		Collection<String> files = allFiles(root, ".stencil");
		HashMap<String,String> programs = new HashMap();
		for (String file: files) {
			File f= new File(file);
			if (f.isDirectory()) {continue;}//Skip directories
			programs.put(f.getName(), getContents(f.getCanonicalPath(), true));
		}
		return programs;
	}
	
	/**@return List of files that have an extension, works recursively.*/
	public static Collection<String> allFiles(String root, final String extension) throws Exception {
		File source = new File(root);
		
		File[] files = source.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(extension) || pathname.isDirectory();
			}
		});
		
		Collection<String> matchedFiles = new ArrayList();
		if (files == null) {return matchedFiles;}
		
		for (File f:files) {
			if (f.isHidden()) {continue;}
			if (f.isDirectory()) {matchedFiles.addAll(allFiles(f.getCanonicalPath(), extension));}
			if (f.getName().startsWith("._")) {continue;}
			matchedFiles.add(f.getCanonicalPath());
		}
		return matchedFiles; 		
	}

	
	public static String getContents(String fileName) throws Exception {return getContents(fileName, true);}
	public static String getContents(String fileName, boolean header) throws Exception {
	 File aFile = new File(fileName);
	 //...checks on aFile are elided
	    StringBuilder contents = new StringBuilder();
	
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      try {
	        String line = null; //not declared within while loop
	        /*
	        * readLine is a bit quirky :
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        if (header) {
	        	line = input.readLine();
		        while (!line.equals("")) {
		        	line = input.readLine();
		        }
	        }
	
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	
	    return contents.toString();
	  }
}
