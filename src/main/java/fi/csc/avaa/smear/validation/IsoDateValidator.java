package fi.csc.avaa.smear.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class IsoDateValidator implements ConstraintValidator<ValidIsoDate, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext ctx) {
        if (s == null) {
            return true;
        }
        try {
            LocalDateTime.parse(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
