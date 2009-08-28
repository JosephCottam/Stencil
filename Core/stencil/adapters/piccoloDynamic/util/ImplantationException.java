package stencil.adapters.piccoloDynamic.util;

public class ImplantationException extends RuntimeException {
	public ImplantationException(String implantation) {
		super(String.format("Error creating implantation of type %1$s.", implantation));
	}

	public ImplantationException(String implantation, String id) {
		super(String.format("Error creating implantation of type %1$s (ID of %2$s).", implantation, id));
	}

	public ImplantationException(String implantation, String id, String addendum) {
		super(String.format("Error creating implantation of type %1$s (ID of %2$s): %3$s.", implantation, id,addendum));
	}

}
