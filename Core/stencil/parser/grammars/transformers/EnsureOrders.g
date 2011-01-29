tree grammar EnsureOrders;
options {
	tokenVocab = Stencil;
	ASTLabelType = StencilTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header {
	/** Ensure that there is a stream-order list.  */
	
	package stencil.parser.string;
	
	import stencil.parser.tree.*;
}

@members{
  public static StencilTree apply (Tree t) {return (StencilTree) TreeRewriteSequence.apply(t);}
    
  public StencilTree downup(Object t) {
    newOrder = (StencilTree) adaptor.create(ORDER, StencilTree.typeName(ORDER));
    adaptor.addChild(newOrder, adaptor.create(LIST_STREAMS, StencilTree.typeName(LIST_STREAMS)));
    downup(t, this, "gatherStreams");
    return (StencilTree) downup(t, this, "fixOrder");     
  }

	private StencilTree newOrder;
	private void addName(String name) {
		adaptor.addChild(newOrder.getChild(0), adaptor.create(ID, name));
	}
}

gatherStreams
	: ^(e=STREAM .*) {addName($e.text);};
	
fixOrder
	: ORDER -> {newOrder}
	| ^(ORDER orderRef+) -> ^(ORDER orderRef+);
	
orderRef:  ^(LIST_STREAMS ID+);
	