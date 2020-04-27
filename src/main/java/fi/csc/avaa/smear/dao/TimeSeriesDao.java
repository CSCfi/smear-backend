package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    MySQLPool client;

    public Uni<Map<String, String>> find(TimeSeriesSearch parameters) {
        return client
                .query("SHOW DATABASES")
                .map(rowSet -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("foo", "bar");
                    return map;
                });
    }
}
