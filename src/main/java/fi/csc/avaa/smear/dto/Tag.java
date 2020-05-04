package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

public class Tag {

    public Integer tagId;
    public String vocabulary;
    public String tag;
    public String displayKeyword;

    public static Tag from(Row row) {
        Tag tag = new Tag();
        tag.tagId = row.getInteger("tagID");
        tag.vocabulary = row.getString("vocabulary");
        tag.tag = row.getString("tag");
        tag.displayKeyword = row.getString("displaykeyword");
        return tag;
    }
}
