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
 
/* Verifies that python blocks contain valid python code.
 * Corrects python block indentation for blocks that have are
 * indented on their first non-blank line. 
 */
tree grammar PythonValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /** Validates that ranges make sense.**/
   

  package stencil.parser.validators;
  
  import stencil.parser.tree.*;
  import static stencil.parser.ParserConstants.INIT_BLOCK_TAG;
    
  import org.python.core.*;
}

@members {
  private static final class PythonValidationException extends ValidationException {
  	public PythonValidationException(PythonFacet facet, String message) {
  		super("Error parsing \%1\$s.\%2\$s. \%3\$s.", ((Python) facet.getParent()).getEnvironment(), facet.getName(), message);
  	}
  	
    public PythonValidationException(PythonFacet facet, Exception e) {
      super(e, "Error parsing \%1\$s.\%2\$s.", ((Python) facet.getParent()).getEnvironment(), facet.getName());
    }
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
    stripIndent(facet);
    
    if (facet.getName().equals(INIT_BLOCK_TAG)) {
    	if (facet.getArguments().size() != 0) {throw new PythonValidationException(facet, INIT_BLOCK_TAG + " facet with arguments not permitted.");}
    }
    
    if (facet.getBody().equals("")) {return;}   
    try {
      Py.compile(new java.io.ByteArrayInputStream(facet.getBody().getBytes()), null, CompileMode.exec);      
    } catch (Exception e) {throw new PythonValidationException(facet, e);}
    

  }
}

topdown: r=PYTHON_FACET {validate((PythonFacet) r);};