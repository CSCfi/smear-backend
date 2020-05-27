package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_WITH_MILLIS;

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
    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime timestamp;
}
