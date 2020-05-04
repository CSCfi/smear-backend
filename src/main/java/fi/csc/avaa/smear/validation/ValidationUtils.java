package fi.csc.avaa.smear.validation;

import javax.validation.ConstraintValidatorContext;

public abstract class ValidationUtils {

    public static boolean constraintViolation(ConstraintValidatorContext ctx, String msg) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
        return false;
    }
}
