package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.time.LocalDateTime;

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

    public static Metadata from(Row row) {
        Metadata metadata = new Metadata();
        metadata.title = row.getString("title");
        metadata.rightsCategory = row.getString("rightsCategory");
        metadata.accessRights = row.getString("access_rights");
        metadata.project = row.getString("project");
        metadata.maintainingOrganisation = row.getString("maintainingOrganisation");
        metadata.contact = row.getString("contact");
        metadata.ref = row.getString("ref");
        metadata.creator = row.getString("creator");
        metadata.discipline = row.getString("discipline");
        metadata.timestamp = row.getLocalDateTime("timestamp");
        return metadata;
    }
}
