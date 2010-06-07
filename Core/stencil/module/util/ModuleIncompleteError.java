package stencil.module.util;

public class ModuleIncompleteError extends Error {
	public ModuleIncompleteError(String name) {super("Operator " + name + " found in meta-data but no instance produced.");}
}
