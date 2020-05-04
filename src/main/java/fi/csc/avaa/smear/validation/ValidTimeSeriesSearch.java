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
@Constraint(validatedBy = TimeSeriesSearchValidator.class)
@Documented
public @interface ValidTimeSeriesSearch {

    String message() default "Either one or more tablevariables (tablevariable=HYY_META.Pamb0) " +
            "or a single table + variables combination (table=HYY_META&variable=Pamb0) " +
            "must be provided (but not both)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
