package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

public class Tag {

    public Long id;
    public String vocabulary;
    public String tag;
    public String displayKeyword;

    public static Tag from(Row row) {
        Tag tag = new Tag();
        tag.id = row.getLong("tagID");
        tag.vocabulary = row.getString("vocabulary");
        tag.tag = row.getString("tag");
        tag.displayKeyword = row.getString("displaykeyword");
        return tag;
    }
}
