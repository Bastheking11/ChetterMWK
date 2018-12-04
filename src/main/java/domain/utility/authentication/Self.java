package domain.utility.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
public @interface Self {
}
