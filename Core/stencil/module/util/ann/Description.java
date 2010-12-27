package stencil.module.util.ann;

import java.lang.annotation.*;

/**Description that can be pulled out by the meta-data system.
 * Contents should be a brief description (one or two sentences).
 * @author jcottam
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {
	String value() default "";
}
