package fi.csc.avaa.smear.config;

import io.quarkus.runtime.Startup;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.inject.Produces;

@Startup
public class DSLContextConfiguration {

    private final DSLContext dslContext = DSL.using(SQLDialect.MYSQL);

    @Produces
    public DSLContext dslContext() {
        return dslContext;
    }
}
