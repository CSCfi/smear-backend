package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TimeSeries {

    private final Set<String> allColnames = new TreeSet<>();
    private final Map<String, Map<String, Double>> timeSeries = new TreeMap<>();

    public void add(Row row, Collection<String> colnames) {
        String samptime = row.getLocalDateTime("samptime").toString();
        if (!timeSeries.containsKey(samptime)) {
            timeSeries.put(samptime, new TreeMap<>());
        }
        allColnames.addAll(colnames);
        colnames.forEach(colname ->
                timeSeries.get(samptime)
                        .put(colname, row.getDouble(colname)));
    }

    public Map<String, Map<String, Double>> get() {
        return timeSeries;
    }
}
