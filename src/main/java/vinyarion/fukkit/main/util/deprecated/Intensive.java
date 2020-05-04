/**
 * 
 */
package vinyarion.fukkit.main.util.deprecated;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE })
/**
 * @author Vinyarion
 * This should be used to indicate any potential sources of lag.
 */
public @interface Intensive { }
