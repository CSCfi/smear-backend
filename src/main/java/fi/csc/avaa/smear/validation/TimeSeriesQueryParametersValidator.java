package fi.csc.avaa.smear.validation;

import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import fi.csc.avaa.smear.parameter.TimeSeriesQueryParameters;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.csc.avaa.smear.parameter.ParameterUtils.mapTablesToVariables;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_TREE;
import static fi.csc.avaa.smear.validation.ValidationUtils.constraintViolation;

@Dependent
public class TimeSeriesQueryParametersValidator
        implements ConstraintValidator<ValidTimeSeriesQueryParameters, TimeSeriesQueryParameters> {

    @Inject
    VariableMetadataDao variableMetadataDao;

    private static final String INVALID_AGGREGATION_TYPE = "Invalid aggregation type";
    private static final String INVALID_QUALITY = "Invalid quality";
    private static final String INVALID_TABLEVARIABLES = "Invalid table and variable combinations (not found): %s";
    private static final String HYY_AGGREGATION_NOT_SUPPORTED = "MEDIAN or CIRCULAR aggregation not supported " +
            "for tables HYY_SLOW and HYY_TREE";
    private static final String HYY_TREE_CUV_NO_REQUIRED = "One or more cuv_no parameters required when " +
            "querying HYY_TREE";

    @Override
    public boolean isValid(TimeSeriesQueryParameters params, ConstraintValidatorContext ctx) {
        Map<String, List<String>> tableToVariables = mapTablesToVariables(params.getTablevariable());
        Set<String> tables = tableToVariables.keySet();
        boolean tablevariablesValid = validateTablevariables(tableToVariables, ctx);
        boolean aggregationValid = validateAggregation(params.getAggregation(), tables, ctx);
        boolean qualityValid = validateQuality(params.getQuality(), ctx);
        return tablevariablesValid
                && aggregationValid
                && qualityValid;
    }

    private Boolean validateTablevariables(Map<String, List<String>> tableToVariables, ConstraintValidatorContext ctx) {
        List<String> invalidTablevariables = new ArrayList<>();
        tableToVariables.forEach((table, variables) ->
                variables.forEach(variable -> {
                    if (!variableMetadataDao.variableExists(table, variable)) {
                        invalidTablevariables.add(String.format("%s.%s", table, variable));
                    }
                }));
        if (!invalidTablevariables.isEmpty()) {
            return constraintViolation(ctx, "tablevariable",
                    String.format(INVALID_TABLEVARIABLES, String.join(", ", invalidTablevariables)));
        }
        return true;
    }

    private boolean validateAggregation(String aggregationParam, Set<String> tables,
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

    private boolean validateCuvNo(List<Integer> cuvNos, Set<String> tables, ConstraintValidatorContext ctx) {
        if (tables.contains(TABLENAME_HYY_TREE)) {
            if (cuvNos == null || cuvNos.isEmpty()) {
                return constraintViolation(ctx, "cuv_no", HYY_TREE_CUV_NO_REQUIRED);
            }
        }
        return true;
    }
}
