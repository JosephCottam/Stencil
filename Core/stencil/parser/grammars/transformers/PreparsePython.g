tree grammar PreparsePython;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
  superClass = TreeFilterSequence;
}

@header {
/* Verifies that python blocks contain valid python code.
 * Corrects python block indentation for blocks that have are
 * indented on their first non-blank line. 
 */
   

  package stencil.parser.string;
  
  import stencil.parser.tree.*;
  import static stencil.parser.ParserConstants.INIT_FACET;
    
  import org.python.core.*;
}

@members {
  public static void apply (Tree t) {TreeFilterSequence.apply(t);}
  
  private static final class PythonValidationException extends ValidationException {
  	public PythonValidationException(PythonFacet facet, String message) {
  		super("Error parsing \%1\$s.\%2\$s. \%3\$s.", ((Python) facet.getParent()).getEnvironment(), facet.getName(), message);
  	}
  	
    public PythonValidationException(PythonFacet facet, Exception e) {
      super(e, "Error parsing \%1\$s.\%2\$s.", ((Python) facet.getParent()).getEnvironment(), facet.getName());
    }
  }  

  private void stripEnclosedBraces(PythonFacet facet) {
    String body = facet.getBody();
    body = body.substring(1, body.length()-1);
    facet.setBody(body);
  }

  private void stripIndent(PythonFacet facet) {
    String body = facet.getBody();
    
    String[] lines = body.split("\\n");
    StringBuilder newBody = new StringBuilder();
    int whiteCount =0;
    boolean removeIndent=false;
    boolean pastFirst=false;
    
    for (String line: lines) {
      if (line.trim().equals("")) {continue;}
      if (!pastFirst) {
      	pastFirst = true;
        while(Character.isWhitespace(line.charAt(whiteCount))) {whiteCount++;}
      }
      newBody.append(line.substring(whiteCount));
      newBody.append("\n");
    } 
    facet.setBody(newBody.toString().trim());
  }

  private void validate(PythonFacet facet) {
    stripEnclosedBraces(facet);
    stripIndent(facet);
    
    if (facet.getName().equals(INIT_FACET)) {
    	if (facet.getArguments().size() != 0) {throw new PythonValidationException(facet, INIT_FACET + " facet with arguments not permitted.");}
    }
    
    if (facet.getBody().equals("")) {return;}   
    try {
      Py.compile(new java.io.ByteArrayInputStream(facet.getBody().getBytes()), null, CompileMode.exec);      
    } catch (Exception e) {throw new PythonValidationException(facet, e);}
    

  }
}

topdown: r=PYTHON_FACET {validate((PythonFacet) r);};