package stencil.module.util.ann;

import java.lang.annotation.*;

/**Indicate that a class is an instantiable module.
 * Though modules must implement the Modules interface, 
 * this annotations indicates that a module should be instantiated (e.g., the author asserts that it is "ready").
 * It also provides a means of indicating a module name,
 * since Stencil has different identifier restrictions that Java.
 * 
 * @author jcottam
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Module {
	String name() default "";
}
