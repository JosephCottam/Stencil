package stencil.module.util.ann;

import java.lang.annotation.*;


/**Indicate that a class should be treated as a stream source.
 * Must also provide a constructor that takes a name, a prototype and a specializer.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Stream {
	String name() default "";	//Type name to be used in the Stencil system
	String spec() default "";	//Default specializer
}
