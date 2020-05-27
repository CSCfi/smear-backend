package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.VariableMetadataQueryParameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

public class VariableMetadataQueryParametersValidator
        implements ConstraintValidator<ValidVariableMetadataQueryParameters, VariableMetadataQueryParameters> {

    private static final String PARAMETER_VIOLATION_MSG =
            "Either search parameters or table + variable combination must be provided (not both)";

    @Override
    public boolean isValid(VariableMetadataQueryParameters params, ConstraintValidatorContext ctx) {
        boolean hasSearchParameters = Stream.of(
                params.getVariable(),
                params.getCategory(),
                params.getSource(),
                params.getTable()
        ).allMatch(List::isEmpty);

        boolean hasTableVariableParameters = params.getTablevariable().isEmpty();

        if ((hasSearchParameters && hasTableVariableParameters)
                || (!hasSearchParameters && !hasTableVariableParameters)) {
            return constraintViolation(ctx, PARAMETER_VIOLATION_MSG);
        } else {
            return true;
        }
    }
}
