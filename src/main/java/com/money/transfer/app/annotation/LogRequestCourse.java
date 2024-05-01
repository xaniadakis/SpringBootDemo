package com.money.transfer.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to tag methods and classes for centralized logging via AOP.
 * Enables logging at runtime by serving as a pointcut indicator for
 * intercepting method executions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface LogRequestCourse {
}
