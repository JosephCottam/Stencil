package stencil.module.util.ann;

import java.lang.annotation.*;

/**May be applied to a class with class-methods as facets or static method in conjunction with a facet tag.*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Operator {
	String name() default "";		//If different from the name of the class
	String spec() default "[]";		//Must conform to specializer grammar AND be include only literal values
	String[] tags() default {};	//List of tags.  Tags are typically used to communicate properties of a particular implementation
}
