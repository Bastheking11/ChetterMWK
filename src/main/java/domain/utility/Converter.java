package domain.utility;

public @interface Converter {

    boolean ignore() default false;
    boolean nullable() default false;
    Class<?> converter() default void.class;
    String name() default "";

}
