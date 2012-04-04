package stencil.module.util.ann;

import java.lang.annotation.*;

/**
 * Module-level annotation to indicate that the stream types that the module includes.
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamTypes {
	Class[] classes();
}
