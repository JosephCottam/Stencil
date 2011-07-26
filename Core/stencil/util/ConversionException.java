package stencil.util;

/**Indicates that values could not be properly convereted*/
public class ConversionException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	

	public ConversionException(Class sourceClass, Class desiredClass) {this(sourceClass, desiredClass, null);}
	public ConversionException(Class sourceClass, Class desiredClass, Exception e) {
		super("Could not convert from " + sourceClass + " to " + desiredClass + ".", e);
	}

	public ConversionException(Object v, Class desiredClass) {this(v, desiredClass, null);}
	public ConversionException(Object v, Class desiredClass, Exception e) {
		super(String.format("Could not convert '%1$s' from %2$s to %3$s.", v, v.getClass(), desiredClass), e);
	}



}
