package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Metadata;
import fi.csc.avaa.smear.table.MetadataRecord;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static fi.csc.avaa.smear.table.MetadataTable.METADATA;

@ApplicationScoped
public class MetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<MetadataRecord, Metadata> recordToMetadata = record ->
            Metadata.builder()
                    .title(record.get(METADATA.TITLE))
                    .rightsCategory(record.get(METADATA.RIGHTS_CATEGORY))
                    .accessRights(record.get(METADATA.ACCESS_RIGHTS))
                    .project(record.get(METADATA.PROJECT))
                    .maintainingOrganisation(record.get(METADATA.MAINTAINING_ORGANISATION))
                    .contact(record.get(METADATA.CONTACT))
                    .ref(record.get(METADATA.REF))
                    .creator(record.get(METADATA.CREATOR))
                    .discipline(record.get(METADATA.DISCIPLINE))
                    .timestamp(record.get(METADATA.TIMESTAMP))
                    .build();

    @CacheResult(cacheName = "metadata-cache")
    public Metadata getMetadata() {
        return create
                .selectFrom(METADATA)
                .fetchOne(recordToMetadata);
    }
}
