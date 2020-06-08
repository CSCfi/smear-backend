package fi.csc.avaa.smear.dto;

import java.util.List;

public final class VariableMetadataFormatter {

    public static String toCsv(List<VariableMetadata> variableMetadataList) {
        return toPlainText(variableMetadataList, ",");
    }

    public static String toTsv(List<VariableMetadata> variableMetadataList) {
        return toPlainText(variableMetadataList, "\t");
    }

    private static String toPlainText(List<VariableMetadata> result, String delimiter) {
        String header = String.join(delimiter, "id", "tableId", "name", "description",
                "unit", "title", "source", "periodStart", "periodEnd", "coverage", "rights", "mandatory",
                "derivative", "timestamp", "category\n");
        StringBuilder builder = new StringBuilder(header);
        result.forEach(variableMetadata -> builder.append(toRow(delimiter, variableMetadata)));
        return builder.toString();
    }

    private static String toRow(String delimiter, VariableMetadata variableMetadata) {
        return String.join(delimiter,
                variableMetadata.getTableName(),
                variableMetadata.getName(),
                variableMetadata.getDescription(),
                variableMetadata.getUnit(),
                variableMetadata.getTitle(),
                variableMetadata.getSource(),
                variableMetadata.getPeriodStart(),
                variableMetadata.getPeriodEnd(),
                variableMetadata.getCoverage().toString(),
                variableMetadata.getRights(),
                variableMetadata.getMandatory().toString(),
                variableMetadata.getDerivative().toString(),
                variableMetadata.getTimestamp().toString(),
                variableMetadata.getCategory())
                + "\n";
    }
}
