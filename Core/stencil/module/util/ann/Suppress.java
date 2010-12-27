package stencil.module.util.ann;

import java.lang.annotation.*;

/**Should a given meta-data definition be skipped unless explicitly requested?
 * This allows, for example, a module to contain alternative definitions of an operator,
 * but only auto-load one.  The alternatives can then be selected based on specializer or context.
 * @author jcottam
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Suppress {
	
}
