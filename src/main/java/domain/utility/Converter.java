package domain.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Converter {

    boolean ignore() default false;
    boolean nullable() default false;
    Class<?> converter() default void.class;
    String name() default "";

}
