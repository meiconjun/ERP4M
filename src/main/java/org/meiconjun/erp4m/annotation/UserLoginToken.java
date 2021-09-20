package org.meiconjun.erp4m.annotation;

import java.lang.annotation.*;

/**
 * @author Lch
 * @Title:
 * @Package
 * @Description: 登陆验证
 * @date 2021/9/16 22:11
 */
@Target({ElementType.METHOD, ElementType.TYPE})// 可注解在接口、类、枚举、注解和方法上
@Retention(RetentionPolicy.RUNTIME)// 这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用
@Inherited// 可被子类继承
public @interface UserLoginToken {
    boolean required() default true;
}
