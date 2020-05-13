package fi.csc.avaa.smear.dto;

import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public final class VariableMetadataTable {

    public static String csv(List<VariableMetadata> variableMetadataList) {
        return toTable(variableMetadataList, ",");
    }

    public static String tsv(List<VariableMetadata> variableMetadataList) {
        return toTable(variableMetadataList, "\t");
    }

    private static String toTable(List<VariableMetadata> result, String delimiter) {
        StringBuilder builder = new StringBuilder("id,tableId,name,description,unit,title,source," +
                "periodStart,periodEnd,coverage,rights,mandatory,derivative,timestamp,category\n");
        result.forEach(variableMetadata -> builder.append(toRow(delimiter, variableMetadata)));
        return builder.toString();
    }

    private static String toRow(String delimiter, VariableMetadata variableMetadata) {
        String periodStart = ISO_DATE_TIME.format(variableMetadata.periodStart);
        String periodEnd = variableMetadata.periodEnd != null
                ? ISO_DATE_TIME.format(variableMetadata.periodStart)
                : null;
        return String.join(delimiter,
                variableMetadata.id.toString(),
                variableMetadata.tableId.toString(),
                variableMetadata.name,
                variableMetadata.description,
                variableMetadata.unit,
                variableMetadata.title,
                variableMetadata.source,
                periodStart,
                periodEnd,
                variableMetadata.coverage.toString(),
                variableMetadata.rights,
                variableMetadata.mandatory.toString(),
                variableMetadata.derivative.toString(),
                variableMetadata.timestamp.toString(),
                variableMetadata.category)
                + "\n";
    }
}
