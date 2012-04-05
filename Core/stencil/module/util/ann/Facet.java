package stencil.module.util.ann;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Facet {
	String memUse() default "FUNCTION";		//Text representations of the values found in FacetData.MemoryUse (case insensitive)
	String prototype() default "(value)";	//Must conform to tuple prototype grammar
	String[] alias() default {};			//Array of names; if none are supplied, will be just the method name; if any are supplied, then only the supplied names can be used
	String counterpart() default "";		//What is the facet's counterpart?  If none are supplied, it will be the method's first alias.	
}
