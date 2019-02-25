package cn.jarvan.annotation;

import java.lang.annotation.*;

/**
 * <b><code>CellName</code></b>
 * <p>
 * Description.
 * <p>
 * <b>Creation Time:</b> 2019/2/25 16:37.
 *
 * @author liuruojing
 * @since auto-report-word 0.1.0
 */
@Documented //会生成在javadoc中
@Inherited //可以被继承
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CellName {
    public String value() default "";
}
