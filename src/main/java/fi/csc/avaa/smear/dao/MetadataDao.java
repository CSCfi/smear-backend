package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Metadata;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class MetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, Metadata> recordToMetadata = record ->
            Metadata.builder()
                    .title(record.get(field("title"), String.class))
                    .rightsCategory(record.get(field("rightsCategory"), String.class))
                    .accessRights(record.get(field("access_rights"), String.class))
                    .project(record.get(field("project"), String.class))
                    .maintainingOrganisation(record.get(field("maintaining_organisation"), String.class))
                    .contact(record.get(field("contact"), String.class))
                    .ref(record.get(field("ref"), String.class))
                    .creator(record.get(field("creator"), String.class))
                    .discipline(record.get(field("discipline"), String.class))
                    .timestamp(record.get(field("timestamp"), LocalDateTime.class))
                    .build();

    @CacheResult(cacheName = "metadata-cache")
    public Metadata getMetadata() {
        return create
                .select()
                .from("Metadata")
                .fetchOne(recordToMetadata);
    }
}
