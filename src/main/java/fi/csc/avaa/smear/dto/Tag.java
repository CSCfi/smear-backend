package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import static org.jooq.impl.DSL.field;

public class Tag {

    public Long id;
    public String vocabulary;
    public String tag;
    public String displayKeyword;

    public static Tag from(Record record) {
        Tag tag = new Tag();
        tag.id = record.get(field("tagID"), Long.class);
        tag.vocabulary = record.get(field("vocabulary"), String.class);
        tag.tag = record.get(field("tag"), String.class);
        tag.displayKeyword = record.get(field("displaykeyword"), String.class);
        return tag;
    }
}
