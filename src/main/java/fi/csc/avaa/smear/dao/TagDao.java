package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Tag;
import fi.csc.avaa.smear.table.TagRecord;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static fi.csc.avaa.smear.table.TagTable.TAG;
import static fi.csc.avaa.smear.table.VariableTagTable.VARIABLE_TAG;

@ApplicationScoped
public class TagDao {

    @Inject
    DSLContext create;

    private final RecordMapper<TagRecord, Tag> recordToTag = record ->
            Tag.builder()
                    .id(record.get(TAG.ID))
                    .vocabulary(record.get(TAG.VOCABULARY))
                    .name(record.get(TAG.NAME))
                    .displayKeyword(record.get(TAG.DISPLAY_KEYWORD))
                    .build();

    public List<Tag> findByVariableIds(List<String> variableIds) {
        return create
                .select()
                .from(TAG)
                .join(VARIABLE_TAG)
                .on(TAG.ID.eq(VARIABLE_TAG.TAG_ID))
                .where(VARIABLE_TAG.VARIABLE_ID.in(variableIds))
                .fetchInto(TAG)
                .map(recordToTag);
    }

    public List<Tag> findById(Long id) {
        return create
                .selectFrom(TAG)
                .where(TAG.ID.eq(id))
                .fetch(recordToTag);
    }
}
