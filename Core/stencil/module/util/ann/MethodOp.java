package stencil.module.util.ann;

import java.lang.annotation.*;

/**May be applied to a class or static method.*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodOp {
	String name() default "";		//If different from the name of the class
	String spec() default "[]";		//Must conform to specializer grammar AND be include only literal values
}
