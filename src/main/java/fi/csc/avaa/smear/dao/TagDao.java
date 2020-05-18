package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Tag;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class TagDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, Tag> recordToTag = record ->
            Tag.builder()
                    .id(record.get(field("tagID"), Long.class))
                    .vocabulary(record.get(field("vocabulary"), String.class))
                    .tag(record.get(field("tag"), String.class))
                    .displayKeyword(record.get(field("displaykeyword"), String.class))
                    .build();

    public List<Tag> findByVariableIds(List<String> variableIds) {
        return create
                .select()
                .from("Tags")
                .join("variableTags")
                .on(field("Tags.tagID").eq(field("variableTags.TagID")))
                .where(field("variableTags.variableID").in(variableIds))
                .fetch(recordToTag);
    }

    public List<Tag> findById(Integer id) {
        return create
                .select()
                .from("Tags")
                .where(field("tagID").eq(id))
                .fetch(recordToTag);
    }
}
