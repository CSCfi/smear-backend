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
@Constraint(validatedBy = VariableMetadataSearchValidator.class)
@Documented
public @interface ValidVariableMetadataSearch {

    String message() default "Either search parameters or table + variable combination must be provided (not both)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
