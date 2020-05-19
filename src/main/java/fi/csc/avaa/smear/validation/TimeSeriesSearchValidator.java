package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLE_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLE_HYY_TREE;
import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

@Dependent
public class TimeSeriesSearchValidator implements ConstraintValidator<ValidTimeSeriesSearch, TimeSeriesSearch> {

    @Inject
    TableMetadataDao tableMetadataDao;

    private static final String INVALID_TABLE_AND_VARIABLE = "Either one or more tablevariables " +
            "(tablevariable=HYY_META.Pamb0) or a single table + variables combination (table=HYY_META&variable=Pamb0) " +
            "must be provided (but not both)";
    private static final String INVALID_AGGREGATION_TYPE = "Invalid aggregation type";
    private static final String INVALID_QUALITY = "Invalid quality";
    private static final String INVALID_TABLES = "Invalid table(s): %s";
    private static final String HYY_AGGREGATION_NOT_SUPPORTED = "MEDIAN or CIRCULAR aggregation not supported " +
            "for tables HYY_SLOW and HYY_TREE";
    private static final String HYY_TREE_CUV_NO_REQUIRED = "One or more cuv_no parameters required when " +
            "querying HYY_TREE";

    @Override
    public boolean isValid(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        return validateTableAndVariableParams(search, ctx)
                && validateTableNames(search, ctx)
                && validateAggregationParams(search, ctx)
                && validateQualityParam(search, ctx)
                && validateCuvNo(search, ctx);
    }

    private boolean validateTableAndVariableParams(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.getTable() == null || search.getTable().isEmpty()) {
            if (search.getTableVariables().isEmpty()) {
                return constraintViolation(ctx, INVALID_TABLE_AND_VARIABLE);
            }
        } else {
            if (!search.getTableVariables().isEmpty()) {
                return constraintViolation(ctx, INVALID_TABLE_AND_VARIABLE);
            }
            if (search.getVariables().isEmpty()) {
                return constraintViolation(ctx, INVALID_TABLE_AND_VARIABLE);
            }
        }
        return true;
    }

    private Boolean validateTableNames(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        List<String> validTables = tableMetadataDao.findTableNames();
        List<String> invalidTables = search.getTableToVariables().keySet()
                .stream()
                .filter(tableName -> !validTables.contains(tableName))
                .collect(Collectors.toList());
        if (!invalidTables.isEmpty()) {
            return constraintViolation(ctx,
                    String.format(INVALID_TABLES, String.join(", ", invalidTables)));
        }
        return true;
    }

    private boolean validateAggregationParams(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.getAggregationStr() != null) {
            if (!Aggregation.getQueryParams().contains(search.getAggregationStr())) {
                return constraintViolation(ctx, INVALID_AGGREGATION_TYPE);
            }
            if (search.getAggregation().isGroupedManually()
                    && (search.getTableToVariables().containsKey(TABLE_HYY_SLOW)
                    || search.getTableToVariables().containsKey(TABLE_HYY_TREE))) {
                return constraintViolation(ctx, HYY_AGGREGATION_NOT_SUPPORTED);
            }
        }
        return true;
    }

    private boolean validateQualityParam(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.getQualityStr() != null) {
            if (!Quality.getQueryParams().contains(search.getQualityStr())) {
                return constraintViolation(ctx, INVALID_QUALITY);
            }
        }
        return true;
    }

    private boolean validateCuvNo(TimeSeriesSearch search, ConstraintValidatorContext ctx) {
        if (search.getTableToVariables().containsKey(TABLE_HYY_TREE)) {
            if (search.getCuvNos() == null || search.getCuvNos().isEmpty()) {
                return constraintViolation(ctx, HYY_TREE_CUV_NO_REQUIRED);
            }
        }
        return true;
    }
}
