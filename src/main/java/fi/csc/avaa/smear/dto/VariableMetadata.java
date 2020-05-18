package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VariableMetadata {

    private Long id;
    private Long tableId;
    private String name;
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
    private LocalDateTime timestamp;
}
