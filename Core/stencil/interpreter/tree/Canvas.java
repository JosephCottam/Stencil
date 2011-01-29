package stencil.interpreter.tree;

public class Canvas {
	private final Specializer spec;
	private final Guide[] guides;
	public Canvas(Specializer spec, Guide[] guides) {
		this.spec = spec;
		this.guides = guides;
	}
	
	public Specializer specializer() {return spec;}
	public Guide[] guides() {return guides;}
}
