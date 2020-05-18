package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.VariableMetadataSearch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class VariableMetadataSearchValidator
        implements ConstraintValidator<ValidVariableMetadataSearch, VariableMetadataSearch> {

    @Override
    public boolean isValid(VariableMetadataSearch search, ConstraintValidatorContext ctx) {
        boolean hasSearchParameters = Stream.of(
                search.getVariableIds(),
                search.getVariables(),
                search.getCategories(),
                search.getSources(),
                search.getTableIds()
        ).allMatch(List::isEmpty);

        boolean hasTableVariableParameters = search.getTablevariables().isEmpty();

        return (hasSearchParameters && !hasTableVariableParameters)
                || (!hasSearchParameters && hasTableVariableParameters);
    }
}
