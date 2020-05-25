package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Tag;
import fi.csc.avaa.smear.table.TagRecord;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static fi.csc.avaa.smear.table.TagTable.TAG;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
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

    public List<Tag> findByVariableNames(List<String> variableNames) {
        return create
                .select()
                .from(TAG)
                .join(VARIABLE_TAG)
                .on(TAG.ID.eq(VARIABLE_TAG.TAG_ID))
                .join(VARIABLE_METADATA)
                .on(VARIABLE_TAG.VARIABLE_ID.eq(VARIABLE_METADATA.ID))
                .where(VARIABLE_METADATA.NAME.in(variableNames))
                .fetchInto(TAG)
                .map(recordToTag);
    }
}
