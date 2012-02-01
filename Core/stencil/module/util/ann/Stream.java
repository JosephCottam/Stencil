package stencil.module.util.ann;

import java.lang.annotation.*;


/**Indicate that a class should be treated as a stream source.
 * Must also provide a two-argument constructor that takes a Specializer.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Stream {
	String name() default "";	//Type name to be used in the Stencil system
	String spec() default "";	//Default specializer
	
	/**Specializer key that will be used to pass the tuple prototype to the constructor.*/
	public static String PROTOTYPE_KEY = "#proto";
	
	/**Specializer key that will be used to pass the stream name.*/
	public static String NAME_KEY = "#name";
}
