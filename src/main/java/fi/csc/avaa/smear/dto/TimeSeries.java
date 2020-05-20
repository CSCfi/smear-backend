package fi.csc.avaa.smear.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class TimeSeries {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer recordCount;
    private String aggregation;
    private Integer aggregationInterval;
    private List<String> columns;
    private List<Map<String, Object>> data;
}
