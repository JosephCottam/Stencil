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
tree grammar FrameTupleRefs;
options {
  tokenVocab = Stencil;
  ASTLabelType = CommonTree;  
  output = AST;
  filter = true;
  superClass = TreeRewriteSequence;
}

@header{
  /** Makes all tuple refs qualified by their frame offsets.
   *  References in the current frame are offset by [0], others
   *  must be reference back to an earlier frame.
   **/
   
  package stencil.parser.string;
  
  import java.util.Arrays;
  
  import stencil.parser.ParserConstants;
  import stencil.parser.tree.*;
  import stencil.operator.module.*;
  import stencil.util.MultiPartName;
  import stencil.tuple.prototype.TuplePrototype;
  import stencil.tuple.prototype.SimplePrototype;
  import stencil.tuple.prototype.TuplePrototypes;
  
}

@members {
  public static final class FrameException extends RuntimeException {
    private String frame;
    private TuplePrototype contents;
    private FrameException prior;
    
    public FrameException(String name, String frame, TuplePrototype contents) {
      this(name, frame, contents, null);
    }
    
    public FrameException(String name, String frame, TuplePrototype contents, FrameException prior) {
      super("Could not find field '" + name + "'.\n" + briefMessage(frame, contents, prior));
      this.frame = frame;
      this.contents= contents;
      this.prior = prior;
    }
    
    private static String briefMessage(String frame, TuplePrototype contents, FrameException prior) {
      StringBuilder b = new StringBuilder();
      b.append(String.format("\tSearched in frame \%1\$s (fields: \%2\$s).\n", frame, Arrays.deepToString(TuplePrototypes.getNames(contents).toArray())));
      if (prior != null) {
        b.append(briefMessage(prior.frame, prior.contents, prior.prior));
      }
      return b.toString();
    }
  }

 private static final class EnvironmentProxy {
     final EnvironmentProxy parent;
     final TuplePrototype names;
     final String label;
     
     public EnvironmentProxy(String label, TuplePrototype names) {this(label, names, null);}
     private EnvironmentProxy(String label, TuplePrototype names, EnvironmentProxy parent) {
        this.label = label;
        this.names = names;
        this.parent = parent;
     }

     public int frameRefFor(String name) {return frameRefFor(name, 0);}
     private int frameRefFor(String name, int offset) {
      if (names.contains(name)) {return offset;}
      if (label.equals(name)) {return offset;}
      if (parent == null) {
        throw new FrameException(name, label, names);
      }
      try {return parent.frameRefFor(name, offset+1);}
      catch (FrameException e) {
        throw new FrameException(name, label, names, e);
      }
     }
     
     public boolean isFrameRef(String name) {
      return (label!= null && label.equals(name)) 
              || (parent != null && parent.isFrameRef(name));
     }

     public EnvironmentProxy push(String label, TuplePrototype t) {
        return new EnvironmentProxy(label, t, this);
     }    
  }

  private ModuleCache modules;
  
  public FrameTupleRefs(TreeNodeStream input, ModuleCache modules) {
    super(input, new RecognizerSharedState());
    assert modules != null : "ModuleCache must not be null.";
    this.modules = modules;
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
  
  protected EnvironmentProxy initialEnv(CommonTree t) {
    Consumes c = (Consumes) t;
    Program p = (Program) c.getAncestor(StencilParser.PROGRAM);
    External ex = External.find(c.getStream(), p.getExternals());
    return new EnvironmentProxy(ParserConstants.CANVAS_FRAME, stencil.display.CanvasTuple.PROTOTYPE)
              .push(ParserConstants.VIEW_FRAME, stencil.display.ViewTuple.PROTOTYPE)
              .push(ex.getName(), ex.getPrototype())
              .push(ParserConstants.LOCAL_FRAME, new SimplePrototype());                   //TOOD: need to calculate the local tuple!
  }
  
  protected TupleRef frame(CommonTree t, EnvironmentProxy env) {
    TupleRef ref = (TupleRef) t;
    
    if (!ref.isNumericRef()) {
      String name = ((Id) ref.getValue()).getName();
      if (env.isFrameRef(name)) {return ref;}

      int frameIdx = env.frameRefFor(name);
      TupleRef newRef = (TupleRef) adaptor.create(StencilParser.TUPLE_REF, "<autogen>");
      StencilNumber frame = (StencilNumber) adaptor.create(StencilParser.NUMBER, Integer.toString(frameIdx));
      adaptor.addChild(newRef, frame);
      adaptor.addChild(newRef, adaptor.dupTree(ref));
      return newRef;
    }
    return ref;
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
  : ^(t=TUPLE_REF .+) -> {frame($t, env)};