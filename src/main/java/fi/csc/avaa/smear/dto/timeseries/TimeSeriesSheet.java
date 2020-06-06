package fi.csc.avaa.smear.dto.timeseries;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import javax.json.bind.annotation.JsonbDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_WITH_MILLIS;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class TimeSeriesSheet {

    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime startTime;
    @JsonbDateFormat(value = ISO8601_DATETIME_WITH_MILLIS)
    private LocalDateTime endTime;
    private Integer recordCount;
    private String aggregation;
    private Integer aggregationInterval;
    private Set<String> columns;
    private List<Map<String, Object>> data;
}
