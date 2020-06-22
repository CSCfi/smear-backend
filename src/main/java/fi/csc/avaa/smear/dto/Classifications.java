package fi.csc.avaa.smear.dto;

import fi.csc.avaa.smear.dto.datastructure.StationNode;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class Classifications {

    private final List<Map<String, ? extends Serializable>> aggregations = Aggregation.valuesAsMaps();
    private final List<Map<String, ? extends Serializable>> qualities = Quality.valuesAsMaps();
    private final List<StationNode> variableClassification;
}
