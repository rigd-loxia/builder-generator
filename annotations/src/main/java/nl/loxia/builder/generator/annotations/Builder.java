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

    /**
     * By disabling the copy of method, builders can be generated for classes where the constructor arguments and setters do not
     * have their own getter methods.<BR>
     * <BR>
     * Compiler arguments override the default value, manually set values override the compiler arguments. For example compiler
     * argument set to false, manually overriding this to true will enable copyOf behavior again.
     *
     * @return whether or not the copy of methods should be generated. The default is true unless overridden using compiler
     *         arguments.
     */
    boolean copyOf() default true;

    /**
     * If you do not want a prefix, you can set this to an empty value. what would originally result in {@code withField(...)} will
     * then become {@code field(...)}. <BR>
     * <BR>
     * Compiler arguments override the default value, manually set values override the compiler arguments. For example compiler
     * argument set to 'set', manually overriding this to 'with' will result in the default behavior again.
     *
     * @return the method prefix to be used. If not set by the user then 'with' will be used.
     */
    String methodPrefix() default "with";

    /**
     * By disabling validation, the builder does not validate if all the fields of a constructor are present.
     *
     * @return whether or not the check for the presence of constructor arguments should take place.
     */
    boolean validation() default true;

}
