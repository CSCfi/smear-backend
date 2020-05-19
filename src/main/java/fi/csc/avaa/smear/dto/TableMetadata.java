package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TableMetadata {

    private Long id;
    private Long stationId;
    private String identifier;
    private String name;
    private String title;
    private String spatialCoverage;
    private Long period;
    private LocalDateTime timestamp;
}
