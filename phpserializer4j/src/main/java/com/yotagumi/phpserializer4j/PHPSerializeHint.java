package com.yotagumi.phpserializer4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PHPSerializeHint
 * 
 * Additional information for serialize.
 * 
 * @author hiranasu
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PHPSerializeHint {

    /**
     * The property name.
     * 
     * @return
     */
    public String name() default "";

}
