package fi.csc.avaa.smear.table;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public final class TimeSeriesConstants {

    public static final String COLNAME_SAMPTIME = "samptime";
    public static final Field<LocalDateTime> SAMPTIME = field(COLNAME_SAMPTIME, LocalDateTime.class);

    public static final String TABLENAME_HYY_SLOW = "HYY_SLOW";
    public static final Table<Record> HYY_SLOW = table(TABLENAME_HYY_SLOW);
    public static final Field<LocalDateTime> START_TIME = field("start_time", LocalDateTime.class);
    public static final Field<String> VARIABLE = field("variable", String.class);
    public static final Field<Double> VALUE = field("value1", Double.class);

    public static final String TABLENAME_HYY_TREE = "HYY_TREE";
    public static final String COLNAME_CUV_NO = "cuv_no";
    public static final Field<Integer> CUV_NO = field(COLNAME_CUV_NO, Integer.class);
}
