package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.constants.AggregationInterval;
import fi.csc.avaa.smear.constants.AggregationType;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

public class TimeSeriesSearchValidator implements ConstraintValidator<ValidTimeSeriesSearch, TimeSeriesSearch> {

    // TODO: move to .properties file, add valid parameters to aggregation params
    private final String tableAndVariableMsg = "Either one or more tablevariables (tablevariable=HYY_META.Pamb0) " +
            "or a single table + variables combination (table=HYY_META&variable=Pamb0) must be provided (but not both)";
    private final String unknownAggregationType = "Invalid aggregation type";
    private final String aggregationIntervalRequired = "Aggregation interval required if aggregation type is provided";
    private final String unknownAggregationInterval = "Invalid aggregation interval";

    @Override
    public boolean isValid(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        return validateTableAndVariableParams(search, ctx)
                && validateAggregationParams(search, ctx);
    }

    private boolean validateTableAndVariableParams(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.table == null || search.table.isEmpty()) {
            if (search.tablevariables.isEmpty()) {
                return constraintViolation(ctx, tableAndVariableMsg);
            }
        } else {
            if (!search.tablevariables.isEmpty()) {
                return constraintViolation(ctx, tableAndVariableMsg);
            }
            if (search.variables.isEmpty()) {
                return constraintViolation(ctx, tableAndVariableMsg);
            }
        }
        return true;
    }

    private boolean validateAggregationParams(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.aggregationTypeStr != null) {
            if (!AggregationType.getQueryParams().contains(search.aggregationTypeStr)) {
                return constraintViolation(ctx, unknownAggregationType);
            }
            if (search.aggregationIntervalStr == null) {
                return constraintViolation(ctx, aggregationIntervalRequired);
            }
            if (!AggregationInterval.getQueryParams().contains(search.aggregationIntervalStr)) {
                return constraintViolation(ctx, unknownAggregationInterval);
            }
        }
        return true;
    }
}
