package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_WITH_MILLIS;

@Getter
@Builder
public class Event {

    private Long id;
    private String eventType;
    private String description;
    private String responsiblePerson;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime timestamp;
}
