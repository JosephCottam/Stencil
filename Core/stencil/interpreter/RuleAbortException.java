package stencil.interpreter;

import stencil.interpreter.tree.Rule;

/**Indicated that a rule aborted (non-normal termination).*/
public class RuleAbortException extends Exception {
	public RuleAbortException(Rule r) {
		this(r, null);
	}

	public RuleAbortException(Rule r, Exception e) {
		super(String.format("Rule %1$s aborted.", r.path()), e);
	}

}
