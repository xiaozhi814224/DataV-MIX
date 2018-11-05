package com.haier.fintech.modules.report.dydatasource.annotation;

import java.lang.annotation.*;

/**
 * @Description 作用于类、接口或者方法上
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    String name() default "";
}