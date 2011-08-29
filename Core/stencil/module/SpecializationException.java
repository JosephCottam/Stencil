package stencil.module;

import stencil.module.util.ModuleData;
import stencil.interpreter.tree.Specializer;

/**Indicates that trying to specialize a method from a package failed.
 * The error message indicates the method name and the attempted specialization
 * and may include further details in nested exceptions.
 */
public class SpecializationException extends RuntimeException {
	public SpecializationException(ModuleData module, String operator, Specializer specializer) {this(module.getName(), operator, specializer, "", null);}

	public SpecializationException(String module, String operator, Specializer specializer) {this(module, operator, specializer, "", null);}
	public SpecializationException(String module, String operator, Specializer specializer, String message) {this(module, operator, specializer, message, null);}

	/**
	 * @param name Name of method trying to specialize
	 * @param specializer Specializer employed
	 * @param e Originating exception
	 */
	public SpecializationException(String module, String name, Specializer specializer, Exception e) {this(module, name, specializer, "", e);}
	public SpecializationException(String module, String name, Specializer specializer, String message, Exception e) {
		super(String.format("Cannot create operator %1$s from %2$s with specializer %3$s: %4$s", name, module, specializer.toString(), message), e);
	}
}
