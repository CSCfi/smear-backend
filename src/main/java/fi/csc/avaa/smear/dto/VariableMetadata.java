package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_WITH_MILLIS;

@Getter
@Builder
public class VariableMetadata {

    private String name;
    private String table;
    private String description;
    private String type;
    private String unit;
    private String title;
    private String source;
    private String periodStart;
    private String periodEnd;
    private Integer coverage;
    private String rights;
    private String category;
    private Boolean mandatory;
    private Boolean derivative;
    private Integer uiSortOrder;
    private String uiAvgType;
    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime timestamp;
}
