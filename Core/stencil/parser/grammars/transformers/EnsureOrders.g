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
	private Order newOrder;
	private void addName(String name) {
		adaptor.addChild(newOrder.getChild(0), adaptor.create(ID, name));
	}
	
	public Program ensureOrder(Program p) {
		//Prep new-order node
		newOrder = (Order) adaptor.create(ORDER, "Order");
		adaptor.addChild(newOrder, adaptor.create(LIST, "Streams"));
		
		//Gather Streams
		fptr down =	new fptr() {public Object rule() throws RecognitionException { return gatherStreams(); }};
   	    fptr up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
   	    downup(p, down, up);

		//Ensure Orders
		down =	new fptr() {public Object rule() throws RecognitionException { return fixOrder(); }};
   	    up = new fptr() {public Object rule() throws RecognitionException { return bottomup(); }};
		return (Program) downup(p, down, up);
	}
		

}

gatherStreams
	: ^(e=STREAM .*) {addName($e.text);};
	
fixOrder
	: ORDER -> {newOrder}
	| ^(ORDER orderRef+) -> ^(ORDER orderRef+);
	
orderRef:  ^(LIST ID+);
	