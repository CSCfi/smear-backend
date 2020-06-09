package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.VariableMetadataQueryParameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

public class VariableMetadataQueryParametersValidator
        implements ConstraintValidator<ValidVariableMetadataQueryParameters, VariableMetadataQueryParameters> {

    private static final String PARAMETER_VIOLATION_MSG =
            "Table or variable parameters cannot be used with tablevariable parameters";

    @Override
    public boolean isValid(VariableMetadataQueryParameters params, ConstraintValidatorContext ctx) {
        boolean hasSeparateTableAndVariableParameters =
                !params.getTable().isEmpty() || !params.getVariable().isEmpty();
        boolean hasTableVariableParameters = !params.getTablevariable().isEmpty();

        if ((hasSeparateTableAndVariableParameters && hasTableVariableParameters)) {
            return constraintViolation(ctx, PARAMETER_VIOLATION_MSG);
        } else {
            return true;
        }
    }
}
