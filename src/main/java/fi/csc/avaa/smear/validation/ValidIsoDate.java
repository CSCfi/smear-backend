package fi.csc.avaa.smear.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = IsoDateValidator.class)
@Documented
public @interface ValidIsoDate {

    String message() default "Not a valid ISO8601 date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
