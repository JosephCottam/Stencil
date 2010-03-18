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
tree grammar RangeValidator;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  filter = true;
}

@header {
  /** Validates that ranges make sense.**/
   

  package stencil.parser.string.validators;
  
  import static stencil.parser.ParserConstants.RANGE_END_INT;
  import stencil.parser.tree.Specializer;
  import stencil.parser.tree.Range;
  import stencil.parser.tree.StencilTree;
  import stencil.parser.string.ValidationException;
}

@members {
  private static final class RangeValidationException extends ValidationException {
    public RangeValidationException(Range range) {
         super("Invalid range:" + range.rangeString());
    }    
  }

   private void validate(Range range) {
      int start = range.getStart();
      boolean relativeStart = range.relativeStart();
      int end = range.getEnd();
      boolean relativeEnd = range.relativeEnd();
   
      if ((end == RANGE_END_INT)  //If the end is range-end, any start value can be used
          || (relativeStart == relativeEnd && relativeStart && start > end)
          || (relativeStart == relativeEnd && !relativeStart && start < end)
          || (relativeEnd && !relativeStart)) {return;}
          
      throw new RangeValidationException(range);
   }
}

topdown: ^(r=RANGE .*) {validate((Range) r);};