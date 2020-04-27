package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.VariableMetadataSearch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class VariableMetadataValidator
        implements ConstraintValidator<ValidVariableMetadataSearch, VariableMetadataSearch> {

    @Override
    public boolean isValid(VariableMetadataSearch search, ConstraintValidatorContext ctx) {
        return !Stream.of(
                search.variables,
                search.tablevariables,
                search.categories,
                search.sources,
                search.tableIds
        )
                .allMatch(List::isEmpty);
    }
}
