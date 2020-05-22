package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import fi.csc.avaa.smear.parameter.TimeSeriesQueryParameters;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_TREE;
import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

@Dependent
public class TimeSeriesQueryParametersValidator
        implements ConstraintValidator<ValidTimeSeriesQueryParameters, TimeSeriesQueryParameters> {

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
    public boolean isValid(TimeSeriesQueryParameters params, ConstraintValidatorContext ctx) {
        List<String> tables = getTables(params);
        boolean tableAndVariableValid = validateTableAndVariable(params, ctx);
        boolean tableNamesValid = validateTableNames(tables, ctx);
        boolean aggregationValid = validateAggregation(params.getAggregation(), tables, ctx);
        boolean qualityValid = validateQuality(params.getQuality(), ctx);
        boolean cuvNoValid = validateCuvNo(params.getCuv_no(), tables, ctx);
        return tableAndVariableValid
                && tableNamesValid
                && aggregationValid
                && qualityValid
                && cuvNoValid;
    }

    private boolean validateTableAndVariable(TimeSeriesQueryParameters params, ConstraintValidatorContext ctx) {
        boolean valid = true;
        if (params.getTable() == null || params.getTable().isEmpty()) {
            if (params.getTablevariable().isEmpty()) {
                valid = constraintViolation(ctx, "tablevariable", INVALID_TABLE_AND_VARIABLE);
            }
        } else {
            if (!params.getTablevariable().isEmpty()) {
                valid = constraintViolation(ctx, "tablevariable", INVALID_TABLE_AND_VARIABLE);
            }
            if (params.getVariable().isEmpty()) {
                valid = constraintViolation(ctx, "variable", INVALID_TABLE_AND_VARIABLE);
            }
        }
        return valid;
    }

    private Boolean validateTableNames(List<String> tables, ConstraintValidatorContext ctx) {
        List<String> validTables = tableMetadataDao.findTableNames();
        List<String> invalidTables = tables
                .stream()
                .filter(tableName -> !validTables.contains(tableName))
                .collect(Collectors.toList());
        if (!invalidTables.isEmpty()) {
            return constraintViolation(ctx, "tablevariable",
                    String.format(INVALID_TABLES, String.join(", ", invalidTables)));
        }
        return true;
    }

    private boolean validateAggregation(String aggregationParam, List<String> tables,
                                        ConstraintValidatorContext ctx) {
        boolean valid = true;
        if (aggregationParam != null) {
            if (!Aggregation.getQueryParams().contains(aggregationParam)) {
                valid = constraintViolation(ctx, "aggregation", INVALID_AGGREGATION_TYPE);
            } else {
                Aggregation aggregation = Aggregation.valueOf(aggregationParam);
                if (aggregation.isGroupedManually()
                        && (tables.contains(TABLENAME_HYY_SLOW) || tables.contains(TABLENAME_HYY_TREE))) {
                    valid = constraintViolation(ctx, "aggregation", HYY_AGGREGATION_NOT_SUPPORTED);
                }
            }
        }
        return valid;
    }

    private boolean validateQuality(String qualityParam, ConstraintValidatorContext ctx) {
        if (qualityParam != null) {
            if (!Quality.getQueryParams().contains(qualityParam)) {
                return constraintViolation(ctx, "quality", INVALID_QUALITY);
            }
        }
        return true;
    }

    private boolean validateCuvNo(List<Integer> cuvNos, List<String> tables, ConstraintValidatorContext ctx) {
        if (tables.contains(TABLENAME_HYY_TREE)) {
            if (cuvNos == null || cuvNos.isEmpty()) {
                return constraintViolation(ctx, "cuv_no", HYY_TREE_CUV_NO_REQUIRED);
            }
        }
        return true;
    }

    private List<String> getTables(TimeSeriesQueryParameters params) {
        List<String> tableParams = params.getTablevariable()
                .stream()
                .map(tablevariable -> tablevariable.split("\\.")[0])
                .collect(Collectors.toList());
        if (params.getTable() != null) {
            tableParams.add(params.getTable());
        }
        return tableParams;
    }
}
