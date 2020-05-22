package fi.csc.avaa.smear.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = TimeSeriesQueryParametersValidator.class)
@Documented
public @interface ValidTimeSeriesQueryParameters {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
