package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

public class Metadata {

    public String title;
    public String rightsCategory;
    public String accessRights;
    public String project;
    public String maintainingOrganisation;
    public String contact;
    public String ref;
    public String creator;
    public String discipline;
    public LocalDateTime timestamp;

    public static Metadata from(Record record) {
        Metadata metadata = new Metadata();
        metadata.title = record.get(field("title"), String.class);
        metadata.rightsCategory = record.get(field("rightsCategory"), String.class);
        metadata.accessRights = record.get(field("access_rights"), String.class);
        metadata.project = record.get(field("project"), String.class);
        metadata.maintainingOrganisation = record.get(field("maintaining_organisation"), String.class);
        metadata.contact = record.get(field("contact"), String.class);
        metadata.ref = record.get(field("ref"), String.class);
        metadata.creator = record.get(field("creator"), String.class);
        metadata.discipline = record.get(field("discipline"), String.class);
        metadata.timestamp = record.get(field("timestamp"), LocalDateTime.class);
        return metadata;
    }
}
