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
tree grammar NumeralizeTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
  
}

@header{
  /** Converts all tuple references to numeric references.
   **/
   
  package stencil.parser.string;
  
  import stencil.parser.ParserConstants;  
  import stencil.parser.tree.*;
  import stencil.operator.module.*;
  import stencil.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototype;
}

@members {
  private ModuleCache modules;
  
  public NumeralizeTupleRefs(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
  }

  
  

  private static final class EnvironmentProxy {
     final EnvironmentProxy parent;
     final TuplePrototype prototype;
     final String label;
     
     public EnvironmentProxy(External root) {this(root.getName(), root.getPrototype(), null);}
     private EnvironmentProxy(String label, TuplePrototype prototype) {this(label, prototype, null);}
     private EnvironmentProxy(String label, TuplePrototype prototype, EnvironmentProxy parent) {
        this.label = label;
        this.prototype = prototype;
        this.parent = parent;
     }
     
     private int search(String name, int offset) {
       if (label != null && this.label.equals(name)) {return offset;}
       else {return parent.search(name, offset+1);}
     }

    //Convert the name to a numeric ref (can be either a frame or tuple ref)
     public int getFrameIndex(String name) {
      int idx = prototype.indexOf(name);
      if (idx <0) {idx = parent.search(name, 0);}
      return idx;
     }
     
     public TuplePrototype get(int idx) {
      if (idx == 0) {return prototype;}
      else {return parent.get(idx--);}
     }
     
     public EnvironmentProxy push(String label, TuplePrototype prototype) {
        return new EnvironmentProxy(label, prototype, this);
     }    
  }

     public EnvironmentProxy extend(EnvironmentProxy env, CommonTree pass, CommonTree callTree) {
      String label = ((Pass) pass).getName();
      Function call = (Function) callTree;
      
      MultiPartName name= new MultiPartName(call.getName());
      Module m;
      try{
        m = modules.findModuleForOperator(name.prefixedName()).module;
      } catch (Exception e) {
        throw new RuntimeException("Error getting module information for operator " + name, e);
      }
    
      try {
        OperatorData od = m.getOperatorData(name.getName(), call.getSpecializer());
        TuplePrototype prototype = od.getFacetData(name.getFacet()).getPrototype();
        return env.push(label, prototype);
      } catch (Exception e) {throw new RuntimeException("Error getting operator data for " + name, e);} 
     } 

  private EnvironmentProxy initialEnv(CommonTree t) {
    Consumes consumes = (Consumes) t;
    Program p = (Program) consumes.getAncestor(StencilParser.PROGRAM);
    External ex = External.find(consumes.getStream(), p.getExternals());
    return new EnvironmentProxy(ParserConstants.CANVAS_FRAME, stencil.display.CanvasTuple.PROTOTYPE)
        .push(ParserConstants.VIEW_FRAME, stencil.display.ViewTuple.PROTOTYPE)
        .push(ex.getName(), ex.getPrototype())
        .push(ParserConstants.LOCAL_FRAME, null);                   //TOOD: need to calculate the local tuple!
  }
  
  
  private TupleRef resolve(TupleRef ref, String prototype) {
    throw new RuntimeException("Tuple sub-ref numeralization not complete.");
  }
  
  
  private TupleRef resolve(TupleRef ref, TuplePrototype prototype) {
    TupleRef newRef = (TupleRef) adaptor.dupNode(ref);
    
    int idx;
    if (!ref.isNumericRef()) {
      String name = ((Id) ref.getValue()).getName();
      idx = prototype.indexOf(name);
      StencilNumber num = (StencilNumber) adaptor.create(StencilParser.NUMBER, Integer.toString(idx));
      newRef = (TupleRef) adaptor.create(StencilParser.TUPLE_REF, (Token) null);
      adaptor.addChild(newRef, num);
    } else {
      idx = ((StencilNumber) ref.getValue()).getNumber().intValue();
    }
    
    if (ref.hasSubRef()) {
      adaptor.addChild(newRef, resolve(ref, prototype.get(idx).getFieldName()));
    }
    return newRef;
  }
  
  private TupleRef resolve(CommonTree r, EnvironmentProxy env) {
    TupleRef ref = (TupleRef) r;
    System.out.println("Identified for resolution: " + ref.toStringTree());
    int idx;
    
    if (!ref.isNumericRef()) {
      String name = ((Id) ref.getValue()).getName();
      idx = env.getFrameIndex(name);
    } else {
      idx = ((StencilNumber) ref.getValue()).getNumber().intValue();      
    }
    
    StencilNumber num = (StencilNumber) adaptor.create(StencilParser.NUMBER, Integer.toString(idx));
    TupleRef newRef = (TupleRef) adaptor.create(StencilParser.TUPLE_REF, (Token) null);
    adaptor.addChild(newRef, num);
    
    if (ref.hasSubRef()) {
      adaptor.addChild(newRef, resolve(ref, env.get(idx)));
    }
    
    return newRef;
  }

}

topdown: consumes;

consumes: ^(c=CONSUMES list[initialEnv($c)]+);

list[EnvironmentProxy env]:  ^(l=LIST rule[env]*);

rule[EnvironmentProxy env] 
  : ^(r=RULE target=. action[env] binding=.);
  
action[EnvironmentProxy env]
  : ^(CALL_CHAIN callTarget[env]);

callTarget[EnvironmentProxy env] 
  : ^(f=FUNCTION . ^(LIST (tupleRef[env] | .)*) c=. callTarget[extend(env, $c, $f)])
  | ^(PACK tupleRef[env]+);
          
tupleRef[EnvironmentProxy env]
  : ^(t=TUPLE_REF .+) -> {resolve($t, env)};
 