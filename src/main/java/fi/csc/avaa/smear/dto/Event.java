package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Event {

    private Long id;
    private String eventType;
    private String description;
    private String responsiblePerson;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime timestamp;
}
