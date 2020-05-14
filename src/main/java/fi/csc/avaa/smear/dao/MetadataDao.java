package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Metadata;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetadataDao {

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "metadata-cache")
    public Metadata getMetadata() {
        return create
                .select()
                .from("Metadata")
                .fetchOne(Metadata::from);
    }
}
