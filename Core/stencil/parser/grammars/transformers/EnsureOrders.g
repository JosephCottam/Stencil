tree grammar EnsureOrders;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
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
  public static Program apply (Tree t) {
     return (Program) apply(t, new Object(){}.getClass().getEnclosingClass());
  }
    
  public Program downup(Object t) {
    newOrder = (Order) adaptor.create(ORDER, "Order");
    adaptor.addChild(newOrder, adaptor.create(LIST, "Streams"));
    downup(t, this, "gatherStreams");
    return (Program) downup(t, this, "fixOrder");     
  }

	private Order newOrder;
	private void addName(String name) {
		adaptor.addChild(newOrder.getChild(0), adaptor.create(ID, name));
	}
}

gatherStreams
	: ^(e=STREAM .*) {addName($e.text);};
	
fixOrder
	: ORDER -> {newOrder}
	| ^(ORDER orderRef+) -> ^(ORDER orderRef+);
	
orderRef:  ^(LIST ID+);
	