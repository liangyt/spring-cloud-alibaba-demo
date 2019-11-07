package com.liangyt.readingandwritingseparation.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceRoutingWith {
    // routing key
    String value() default "";
}
