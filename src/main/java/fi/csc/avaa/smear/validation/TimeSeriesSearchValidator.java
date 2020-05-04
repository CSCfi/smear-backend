package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.parameter.TimeSeriesSearch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeSeriesSearchValidator implements ConstraintValidator<ValidTimeSeriesSearch, TimeSeriesSearch> {

    @Override
    public boolean isValid(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.table == null || search.table.isEmpty()) {
            return !search.tablevariables.isEmpty();
        } else {
            if (!search.tablevariables.isEmpty()) {
                return false;
            }
            return !search.variables.isEmpty();
        }
    }
}
