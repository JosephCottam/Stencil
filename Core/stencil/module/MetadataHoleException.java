package stencil.module;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.OperatorData;

/**Indicate that an meta-data object has an incomplete field, when
 * all fields are expected to be complete.
 */
public class MetadataHoleException extends RuntimeException {
	private OperatorData operatorData;
	
	public MetadataHoleException(OperatorData operatorData, Specializer spec) {
		super(makeMessage(operatorData) + " with specializer " + spec.toString());
		this.operatorData = operatorData;
	}

	public MetadataHoleException(String module, String opName, String detailMessage) {
		super(makeMessage(module, opName) + ":" +  detailMessage);
	}

	public MetadataHoleException(OperatorData operatorData, String detailMessage) {
		this(operatorData.module(), operatorData.name(), detailMessage);
		this.operatorData = operatorData;
	}
	
	public MetadataHoleException(String message) {
		super(message);
	}
	
	
	public OperatorData getOperatorData() {return operatorData;}
	
	private static String makeMessage(String module, String op) {return "Metadata missing from  " + module + "." + op;}
	private static String makeMessage(OperatorData od) {return makeMessage(od.module(), od.name());}
}