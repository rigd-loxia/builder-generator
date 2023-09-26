package nl.loxia.builder.generator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for marking classes for which a Builder needs to be generated. Placing this annotation on an `interface` or
 * `enum` is not supported. `Records` are untested.<BR>
 * <BR>
 * The generated class will have the same name as the class on which the annotation is placed but suffixed with `Builder`.
 *
 * @author Ben Zegveld
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Builder {

}
