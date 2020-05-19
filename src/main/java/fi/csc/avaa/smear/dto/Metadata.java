package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    private LocalDateTime timestamp;
}
