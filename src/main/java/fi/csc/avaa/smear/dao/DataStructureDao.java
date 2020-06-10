package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.datastructure.CategoryNode;
import fi.csc.avaa.smear.dto.datastructure.StationNode;
import fi.csc.avaa.smear.dto.datastructure.VariableNode;
import lombok.Builder;
import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.Conditions.VARIABLE_IS_PUBLIC;
import static fi.csc.avaa.smear.table.StationTable.STATION;
import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
import static org.jooq.impl.DSL.coalesce;

@ApplicationScoped
public class DataStructureDao {

    @Inject
    DSLContext create;

    private static final String CATEGORY_OTHER = "Other";

    private static final RecordMapper<Record, DataStructureRow> recordToRow = record ->
            DataStructureRow.builder()
                    .stationId(record.get(STATION.ID))
                    .stationName(record.get(STATION.NAME))
                    .category(record.get(VARIABLE_METADATA.CATEGORY))
                    .variableTitle(record.get(VARIABLE_METADATA.TITLE))
                    .variableSortOrder(record.get((VARIABLE_METADATA.UI_SORT_ORDER)))
                    .tableName(record.get(TABLE_METADATA.NAME))
                    .variablename(record.get(VARIABLE_METADATA.NAME))
                    .build();

    public List<StationNode> fetchDataStructure() {
        List<DataStructureRow> rows = create
                .select(
                        STATION.ID,
                        STATION.NAME,
                        coalesce(VARIABLE_METADATA.CATEGORY, CATEGORY_OTHER)
                                .as(VARIABLE_METADATA.CATEGORY),
                        VARIABLE_METADATA.TITLE,
                        VARIABLE_METADATA.UI_SORT_ORDER,
                        TABLE_METADATA.NAME,
                        VARIABLE_METADATA.NAME
                )
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA).on(VARIABLE_METADATA.TABLE_ID.eq(TABLE_METADATA.ID))
                .join(STATION).on(TABLE_METADATA.STATION_ID.eq(STATION.ID))
                .where(VARIABLE_IS_PUBLIC)
                .fetch(recordToRow);
        return toStationNodes(rows);
    }

    private static List<StationNode> toStationNodes(List<DataStructureRow> rows) {
        List<StationNode> stationNodes = rows
                .stream()
                .map(DataStructureDao::toStationNode)
                .distinct()
                .sorted(Comparator.comparing(StationNode::getId))
                .collect(Collectors.toList());
        stationNodes.forEach(stationNode -> {
            List<CategoryNode> categoryNodes = toCategoryNodes(stationNode.getId(), rows);
            categoryNodes.forEach(categoryNode -> {
                List<VariableNode> variableNodes =
                        toVariableNodes(stationNode.getId(), categoryNode.getName(), rows);
                categoryNode.setVariables(variableNodes);
            });
            stationNode.setCategories(categoryNodes);
        });
        return stationNodes;
    }

    private static StationNode toStationNode(DataStructureRow row) {
        return StationNode.builder()
                .id(row.getStationId())
                .name(row.getStationName())
                .build();
    }

    private static List<CategoryNode> toCategoryNodes(Long stationId, List<DataStructureRow> rows) {
        return rows
                .stream()
                .filter(row -> row.getStationId().equals(stationId))
                .map(DataStructureDao::toCategoryNode)
                .distinct()
                .sorted(categoryNodeComparator)
                .collect(Collectors.toList());
    }

    private static CategoryNode toCategoryNode(DataStructureRow row) {
        String categoryName = row.getCategory();
        return CategoryNode.builder()
                .id(categoryName + row.getStationId())
                .name(categoryName)
                .build();
    }

    private static List<VariableNode> toVariableNodes(Long stationId, String categoryName,
                                                      List<DataStructureRow> rows) {
        return rows
                .stream()
                .filter(row -> row.getStationId().equals(stationId) && row.getCategory().equals(categoryName))
                .map(DataStructureDao::toVariableNode)
                .sorted(variableNodeComparator)
                .collect(Collectors.toList());
    }

    private static VariableNode toVariableNode(DataStructureRow row) {
        return VariableNode.builder()
                .tablevariable(row.getTableName() + "." + row.getVariablename())
                .title(row.getVariableTitle())
                .sortOrder(row.getVariableSortOrder())
                .build();
    }

    private static final Comparator<CategoryNode> categoryNodeComparator = (node1, node2) -> {
        if (node1.getName().equals(CATEGORY_OTHER)) {
            return 1;
        } else if (node2.getName().equals(CATEGORY_OTHER)) {
            return -1;
        } else {
            return node1.getName().compareTo(node2.getName());
        }
    };

    private static final Comparator<VariableNode> variableNodeComparator = (node1, node2) -> {
        if (node1.getSortOrder() == null) {
            return 1;
        } else if (node2.getSortOrder() == null) {
            return -1;
        } else {
            return node1.getSortOrder().compareTo(node2.getSortOrder());
        }
    };

    @Getter
    @Builder
    private static class DataStructureRow {

        private Long stationId;
        private String stationName;
        private String category;
        private String variableTitle;
        private Integer variableSortOrder;
        private String tableName;
        private String variablename;
    }
}
