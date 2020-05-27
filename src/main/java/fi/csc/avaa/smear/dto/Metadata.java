package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_WITH_MILLIS;

@Getter
@Builder
public class Metadata {

    private String title;
    private String rightsCategory;
    private String accessRights;
    private String project;
    private String maintainingOrganisation;
    private String contact;
    private String ref;
    private String creator;
    private String discipline;
    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime timestamp;
}
