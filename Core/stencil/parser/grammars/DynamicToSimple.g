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

/** Ensures that each dynamic binding also has a simple
 * binding in the result list.  If a simple binding is 
 * explicitly provided, the dynamic binding is simply removed
 * from the simple-binding results.  If a simple binding is
 * not provided, the dynamic marker is removed.
 **/
tree grammar DynamicToSimple;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
}

@header{
  package stencil.parser.string;
	
  import org.antlr.runtime.tree.*;
  import stencil.parser.tree.Rule;
  import stencil.tuple.prototype.TuplePrototypes;
  import java.util.Arrays;
}


@members {
  private boolean hasStaticBinding(String name, List<Rule> rules) {
     for (Rule rule: rules) {
        if (rule.isDynamic()) {continue;}
        List<String> names = Arrays.asList(TuplePrototypes.getNames(rule.getTarget().getPrototype()));
        if (names.contains(name)) {return true;}
     }
     return false;
  }

  /**Is there an alternative binding for the attributs
   * set by this dynamic rule elsewhere in the parent
   * rule list?
   */
  private boolean altBindings(Rule rule) {
     List<Rule> rules = (List) rule.getParent();
     List<String> names = new ArrayList(Arrays.asList(TuplePrototypes.getNames(rule.getTarget().getPrototype())));

     for (String name: names) {
        if (hasStaticBinding(name, rules)) {
           names.remove(name);
        } else {
           break;
        }
     }
     return names.size() ==0;
  }
}

topdown: ^(CONSUMES f=. pf=. l=. r=result rest+=.*);
      
result: ^(LIST rule*);
rule
  : ^(r=RULE t=. cc=. b=.) 
        -> {$b.getType() == DEFINE}? ^(RULE $t $cc $b)
        -> {altBindings((Rule) $r)}?
        -> ^(RULE $t $cc DEFINE[":"]);
