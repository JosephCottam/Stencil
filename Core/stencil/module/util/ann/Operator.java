package stencil.module.util.ann;

import java.lang.annotation.*;

/**May be applied to a class with class-methods as facets or static method in conjunction with a facet tag.*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Operator {
	/**What is this operator's name?  Will default to the class/method name if omitted.**/
	String name() default "";
	
	/**What is the default specializer?  Must conform to the specializer grammar and only include literal values.**/
	String spec() default "[]";	
	
	/**Which facet should be used by default.  In general, this facet should have a counterpart.*/
	String defaultFacet() default "";
}
