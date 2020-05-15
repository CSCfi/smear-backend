package fi.csc.avaa.smear.config;

import io.quarkus.runtime.Startup;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;

@Startup
public class DSLContextConfiguration {

    @Inject
    DataSource smearDataSource;

    private DSLContext dslContext;

    @Produces
    public DSLContext dslContext() {
        if (dslContext == null) {
            dslContext = DSL.using(smearDataSource, SQLDialect.MYSQL);
        }
        return dslContext;
    }
}
