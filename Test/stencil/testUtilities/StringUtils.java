package stencil.testUtilities;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import stencil.util.FileUtils;

public class StringUtils {
	public static final String STENCIL_CACHE_DIRECTORY = "./TestData/RegressionImages";
	

	/**Strip line-leading space and empty lines.**/
	public static String stripSpaces(String source) {
		String[] lines = source.split("\\n");
		StringBuilder b = new StringBuilder();
		for (String line: lines) {
			line = line.trim();
			line = line.replace("\\s+", " ");
			if (!line.equals("")) {b.append(line);}
		}
		return b.toString();
	}

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
			programs.put(f.getName(), FileUtils.readFile(f.getCanonicalPath()));
		}
		return programs;
	}
	
	/**@return List of files that have an extension, works recursively.*/
	public static Collection<String> allFiles(String root, final String extension) throws Exception {
		File source = new File(root);
		
		File[] files = source.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(extension) || pathname.isDirectory();
			}
		});
		
		Collection<String> matchedFiles = new ArrayList();
		if (files == null) {return matchedFiles;}
		
		for (File f:files) {
			if (f.isHidden()) {continue;}
			if (f.isDirectory()) {matchedFiles.addAll(allFiles(f.getCanonicalPath(), extension)); continue;}
			if (f.getName().startsWith("._")) {continue;}
			matchedFiles.add(f.getCanonicalPath());
		}
		return matchedFiles; 		
	}
}
