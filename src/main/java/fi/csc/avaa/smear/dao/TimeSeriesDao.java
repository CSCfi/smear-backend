package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.parameter.TimeSeriesParameters;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TimeSeriesDao extends SmearDao {

    @Inject
    MySQLPool client;

    public Uni<Map<String, String>> getTimeSeries(TimeSeriesParameters parameters) {
        return client
                .query("SHOW DATABASES")
                .map(rowSet -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("foo", "bar");
                    return map;
                });
    }
}
