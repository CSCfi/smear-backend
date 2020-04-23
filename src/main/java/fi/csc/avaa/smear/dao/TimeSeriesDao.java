package fi.csc.avaa.smear.dao;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TimeSeriesDao extends SmearDao {

    @Inject
    MySQLPool client;

    public Uni<Map<String, String>> getTimeSeries(
            List<String> variables,
            String table,
            List<String> tablevariables,
            Date from,
            Date to,
            String quality,
            String averaging,
            String type,
            String cuv_no
    ) {
        return client
                .query("SELECT * FROM stations WHERE 0=1")
                .map(rowSet -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("foo", "bar");
                    return map;
                });
    }
}
