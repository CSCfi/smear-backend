package fi.csc.avaa.smear;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SmearService {

    @Inject
    MySQLPool client;

    public Uni<String> getFoo() {
        Uni<RowSet<Row>> uni = client.query("SELECT * FROM test");
        return uni.map(rowSet -> {
            List<String> foo = new ArrayList<>();
            rowSet.forEach(row -> foo.add(row.getString("foo")));
            return foo.get(0);
        });
    }
}
