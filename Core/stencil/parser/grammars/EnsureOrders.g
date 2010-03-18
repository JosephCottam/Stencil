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
 
/* Ensure that there is a stream-order list. 
 */
tree grammar EnsureOrders;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	output = AST;
	filter = true;
	superClass = TreeRewriteSequence;
}

@header {
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
	: ^(e=EXTERNAL .*) {addName($e.text);};
	
fixOrder
	: ORDER -> {newOrder}
	| ^(ORDER orderRef+) -> ^(ORDER orderRef+);
	
orderRef:  ^(LIST ID+);
	