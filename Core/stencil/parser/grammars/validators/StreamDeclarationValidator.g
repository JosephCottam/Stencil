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
 
/* Verifies that each range is properly formed.*/
tree grammar StreamDeclarationValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /** Validates that all stream declarations include the 
    * standard source-indicator field in the prototype,
    * that all field names in the declaration are unique
    * and that no field name is the same as the stream name.
   **/
   

  package stencil.parser.string.validators;
  
  import stencil.parser.tree.Stream;
  import stencil.parser.string.StencilParser;
  import stencil.parser.tree.TuplePrototype;
  import stencil.parser.tree.TupleFieldDef;

  import java.util.HashSet;
  import java.util.Set;

  import stencil.parser.string.ValidationException;
  import static java.lang.String.format;
  import static stencil.parser.ParserConstants.SOURCE_FIELD;
}

@members {
  public void uniqueFieldNames(Stream e, TuplePrototype prototype) {
    String field = null;
    Set<String> fields = new HashSet<String>();
    
    for (TupleFieldDef def: prototype) {
      field = def.getFieldName();
      if (!fields.add(field)) {break;}
      else {field = null;}
    }  

    String stream = e.getName();  
    if (field != null) {
      throw new ValidationException(format("Duplicate field name in stream declaration \%1\$s: \%2\$s.", stream, field));
    }
    
    if (fields.contains(stream)) {
      throw new ValidationException(format("Field with same name as containing stream: \%1\$s", stream));
    }
  }
  
}

topdown: ^(e=EXTERNAL_STREAM ^(p=TUPLE_PROTOTYPE .*)) 
         {
            uniqueFieldNames((Stream) e, (TuplePrototype) p);
         };
