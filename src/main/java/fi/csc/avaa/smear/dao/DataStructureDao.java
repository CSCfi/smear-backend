package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.datastructure.CategoryNode;
import fi.csc.avaa.smear.dto.datastructure.StationNode;
import fi.csc.avaa.smear.dto.datastructure.VariableNode;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.Result;

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
    private static final Comparator<CategoryNode> categoryNodeComparator = (node1, node2) -> {
        if (node1.getName().equals(CATEGORY_OTHER)) {
            return 1;
        } else if (node2.getName().equals(CATEGORY_OTHER)) {
            return -1;
        } else {
            return node1.getName().compareTo(node2.getName());
        }
    };

    public List<StationNode> fetchDataStructure() {
        Result<Record6<Long, String, String, String, String, String>> result =
                create
                        .select(
                                STATION.ID,
                                STATION.NAME,
                                coalesce(VARIABLE_METADATA.CATEGORY, CATEGORY_OTHER)
                                        .as(VARIABLE_METADATA.CATEGORY),
                                VARIABLE_METADATA.TITLE,
                                TABLE_METADATA.NAME,
                                VARIABLE_METADATA.NAME
                        )
                        .from(VARIABLE_METADATA)
                        .join(TABLE_METADATA).on(VARIABLE_METADATA.TABLE_ID.eq(TABLE_METADATA.ID))
                        .join(STATION).on(TABLE_METADATA.STATION_ID.eq(STATION.ID))
                        .where(VARIABLE_IS_PUBLIC)
                        .fetch();
        return toStationNodes(result);
    }

    private static List<StationNode> toStationNodes(
            Result<Record6<Long, String, String, String, String, String>> result) {
        List<StationNode> stationNodes = result
                .stream()
                .map(DataStructureDao::toStationNode)
                .distinct()
                .sorted(Comparator.comparing(StationNode::getId))
                .collect(Collectors.toList());
        stationNodes.forEach(stationNode -> {
            List<CategoryNode> categoryNodes = toCategoryNodes(stationNode.getId(), result);
            categoryNodes.forEach(categoryNode -> {
                List<VariableNode> variableNodes =
                        toVariableNodes(stationNode.getId(), categoryNode.getName(), result);
                categoryNode.setVariables(variableNodes);
            });
            stationNode.setCategories(categoryNodes);
        });
        return stationNodes;
    }

    private static StationNode toStationNode(Record6<Long, String, String, String, String, String> record) {
        return StationNode.builder()
                .id(record.get(STATION.ID))
                .name(record.get(STATION.NAME))
                .build();
    }

    private static List<CategoryNode> toCategoryNodes(Long stationId,
                                                      Result<Record6<Long, String, String, String, String, String>> result) {
        return result
                .stream()
                .filter(record -> record.get(STATION.ID).equals(stationId))
                .map(DataStructureDao::toCategoryNode)
                .distinct()
                .sorted(categoryNodeComparator)
                .collect(Collectors.toList());
    }

    private static CategoryNode toCategoryNode(Record6<Long, String, String, String, String, String> record) {
        String categoryName = record.get(VARIABLE_METADATA.CATEGORY);
        return CategoryNode.builder()
                .id(categoryName + record.get(STATION.ID))
                .name(categoryName)
                .build();
    }

    private static List<VariableNode> toVariableNodes(
            Long stationId, String categoryName,
            Result<Record6<Long, String, String, String, String, String>> result) {
        return result
                .stream()
                .filter(record ->
                        record.get(STATION.ID).equals(stationId)
                                && record.get(VARIABLE_METADATA.CATEGORY).equals(categoryName))
                .map(DataStructureDao::toVariableNode)
                .sorted(Comparator.comparing(VariableNode::getTitle))
                .collect(Collectors.toList());
    }

    private static VariableNode toVariableNode(Record6<Long, String, String, String, String, String> record) {
        return VariableNode.builder()
                .tablevariable(record.get(TABLE_METADATA.NAME) + "." + record.get(VARIABLE_METADATA.NAME))
                .title(record.get(VARIABLE_METADATA.TITLE))
                .build();
    }
}
