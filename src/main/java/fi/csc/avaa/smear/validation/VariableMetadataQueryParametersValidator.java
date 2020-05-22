package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.VariableMetadataQueryParameters;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class VariableMetadataQueryParametersValidator
        implements ConstraintValidator<ValidVariableMetadataQueryParameters, VariableMetadataQueryParameters> {

    @Override
    public boolean isValid(VariableMetadataQueryParameters params, ConstraintValidatorContext ctx) {
        boolean hasSearchParameters = Stream.of(
                params.getVariable(),
                params.getCategory(),
                params.getSource(),
                params.getTable()
        ).allMatch(List::isEmpty);

        boolean hasTableVariableParameters = params.getTablevariable().isEmpty();

        return (hasSearchParameters && !hasTableVariableParameters)
                || (!hasSearchParameters && hasTableVariableParameters);
    }
}
