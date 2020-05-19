package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class TagTable extends CustomTable<TagRecord> {

    public static final TagTable TAG = new TagTable();

    public final TableField<TagRecord, Long> ID = createField(name("tagID"), BIGINT);
    public final TableField<TagRecord, String> VOCABULARY = createField(name("vocabulary"), VARCHAR);
    public final TableField<TagRecord, String> NAME = createField(name("tag"), VARCHAR);
    public final TableField<TagRecord, String> DISPLAY_KEYWORD = createField(name("displaykeyword"), VARCHAR);

    protected TagTable() {
        super(name("Tags"));
    }

    @Override
    public Class<? extends TagRecord> getRecordType() {
        return TagRecord.class;
    }
}
