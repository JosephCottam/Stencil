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
 
/* Ensures that stencil native and python operators are defined in the 
 * ad-hoc module.  Does NOT modify the AST, just populates the ad-hoc module.
 */
tree grammar AdHocOperators;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;
	superClass = TreeRewriteSequence;
	filter = true;
	output = AST;
}

@header {
	package stencil.parser.string;

	import stencil.adapters.Adapter;
	import stencil.display.DisplayLayer;
  import stencil.operator.wrappers.EncapsulationGenerator;
  import stencil.operator.*;
  import stencil.operator.module.*;
  import stencil.operator.module.util.*;    
  import stencil.operator.wrappers.*;
  import stencil.parser.tree.*;
}

@members {
	protected MutableModule adHoc;
	protected Adapter adapter;
	protected ModuleCache modules;
	protected final EncapsulationGenerator encGenerator = new EncapsulationGenerator();
	
	public AdHocOperators(TreeNodeStream input, ModuleCache modules, Adapter adapter) {
		super(input, new RecognizerSharedState());
		assert modules != null : "Module cache must not be null.";
		assert adapter != null : "Adapter must not be null.";
		
		this.modules = modules;
		this.adHoc = modules.getAdHoc();
		this.adapter = adapter;				
	}
	
	public Program transform(Program p) {
	   p = simpleOps(p);
	   p = proxyOperators(p);
	   return p;
	}

  private Program simpleOps(Program p) {
    return (Program) downup(p, this, "simple");
  }
	
	

	protected void makeOperator(Operator op) {
		StencilOperator operator = new SyntheticOperator(adHoc.getModuleData().getName(), op);		
		adHoc.addOperator(operator);
	}
	
	protected void makePython(Python p) {
		encGenerator.generate(p, adHoc);
	}
	
	protected void makeLayer(Layer l) {
		DisplayLayer dl =adapter.makeLayer(l); 
		l.setDisplayLayer(dl);
		
		LayerOperator operator = new LayerOperator(adHoc.getName(), dl);
		adHoc.addOperator(operator, operator.getOperatorData());
	}
	
	
	//--------------- Proxy operator fixed-point ---------------------
	boolean changed = true;
	public Program proxyOperators(Program p) {
	   while (changed) {
	      changed = false;
	      p = runOnce(p);
	   }
	   return p;
	}
	
	private Program runOnce(Program p) {
	  return (Program) downup(p, this, "proxies");
	}
	
  private Tree transferProxy(OperatorReference ref) {
    OperatorProxy proxy = makeProxy(ref);
     
    if (adHoc.getModuleData().getOperatorNames().contains(proxy.getName())) {return ref;}
    adHoc.addOperator(proxy.getName(), proxy.getOperator(), proxy.getOperatorData());
    changed = true; 
    return proxy;
  } 
  
  private OperatorProxy makeProxy(OperatorReference ref) {
      String name = ref.getName();
      StencilOperator op = findBase(ref);
       
      OperatorProxy p = (OperatorProxy) adaptor.create(OPERATOR_PROXY, name);
      p.setOperator(op, op.getOperatorData());
      return p;
  }
  
  private StencilOperator findBase(OperatorReference ref) {
      OperatorBase base = (OperatorBase) ref.getFirstChildWithType(OPERATOR_BASE);
      Specializer spec = (Specializer) ref.getFirstChildWithType(SPECIALIZER);
      String baseName = base.getName();
      String name = ref.getName();  
  
      Module module; 
      try {module = modules.findModuleForOperator(baseName).module;}
      catch (Exception e) {return null;}
          
      StencilOperator op;
      OperatorData od;
      try {
        op = module.instance(baseName, spec);
      } catch (Exception e) {throw new RuntimeException(String.format("Error instantiating \%1\$s as base for \%2\$s", baseName, name), e);}
      return op;  
  }
  
	 
}
 
simple
	: ^(r=OPERATOR .*) {makeOperator((Operator) $r);}
	| ^(r=PYTHON .*) {makePython((Python) $r);}
	| ^(r=LAYER .*) {makeLayer((Layer) $r);}
  ;
  
proxies
 : ^(r=OPERATOR_REFERENCE .*) {findBase((OperatorReference) $r) != null}? -> {transferProxy((OperatorReference) $r)}
 ;
	