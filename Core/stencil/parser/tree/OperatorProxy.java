package stencil.parser.tree;

import org.antlr.runtime.Token;

import stencil.module.operator.StencilOperator;
import stencil.module.util.OperatorData;

public class OperatorProxy extends StencilTree {
	private StencilOperator operator;
	private OperatorData operatorData;
	
	public OperatorProxy(Token t) {super(t);}
	
	public String getName() {return token.getText();}
	
	public void setOperator(String group, StencilOperator operator, OperatorData operatorData) {
		this.operator = operator;
		if (!operatorData.name().equals(getName())) {
			this.operatorData = new OperatorData(operatorData).module(group).name(getName());
		} else {
			this.operatorData =operatorData; 
		}
	}
	public StencilOperator getOperator() {return operator;}
	
	public OperatorData getOperatorData() {return operatorData;}
	
	public String toString() {
		String result = super.toString();
		if (operator == null) {result = result + " -Op";}
		if (operatorData == null) {result = result + " -OpData";}
		return result;
	}

	public OperatorProxy dupNode() {
		OperatorProxy n = (OperatorProxy) super.dupNode();
		n.operator = this.operator;
		n.operatorData = this.operatorData;
		return n;
	}
}
