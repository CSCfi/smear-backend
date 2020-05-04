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
                search.variableIds,
                search.variables,
                search.categories,
                search.sources,
                search.tableIds
        )
                .allMatch(List::isEmpty);
        boolean hasTableVariableParameters = search.tablevariables.isEmpty();
        return (hasSearchParameters && !hasTableVariableParameters)
                || (!hasSearchParameters && hasTableVariableParameters);
    }
}
