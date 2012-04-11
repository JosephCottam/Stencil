package stencil.module.util.ann;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Facet {
	/**Text representations of the values found in FacetData.MemoryUse (case insensitive)**/
	String memUse() default "FUNCTION";		
	
	/**Must conform to tuple prototype grammar**/
	String prototype() default "(Object value)";	
	
	/**Array of names; if none are supplied, will be just the method name; 
	 * if any are supplied, then only the supplied names can be used**/
	String[] alias() default {};
	
	/**What is the operators counterpart?  
	 * If no counterpart is supplied, it will be the method's first alias.
	 **/
	String counterpart() default "";	

}
