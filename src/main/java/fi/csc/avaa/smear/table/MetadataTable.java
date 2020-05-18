package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;
import org.jooq.impl.SQLDataType;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.name;

public class MetadataTable extends CustomTable<MetadataRecord> {

    public static final MetadataTable METADATA = new MetadataTable();

    public final TableField<MetadataRecord, String> TITLE = createField(name("title"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> RIGHTS_CATEGORY = createField(name("rightsCategory"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> ACCESS_RIGHTS = createField(name("access_rights"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> PROJECT = createField(name("project"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> MAINTAINING_ORGANISATION = createField(name("maintaining_organisation"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> CONTACT = createField(name("contact"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> REF = createField(name("ref"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> CREATOR = createField(name("creator"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, String> DISCIPLINE = createField(name("discipline"), SQLDataType.VARCHAR);
    public final TableField<MetadataRecord, LocalDateTime> TIMESTAMP = createField(name("timestamp"), SQLDataType.LOCALDATETIME);

    protected MetadataTable() {
        super(name("Metadata"));
    }

    @Override
    public Class<? extends MetadataRecord> getRecordType() {
        return MetadataRecord.class;
    }
}
