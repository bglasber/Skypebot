package skypebot.db.schema;

public class Table {

    private String tableName;
    private String[] tableFields;
    private TableType tableType;
    private String tableIndex;
    private String indexField;

    public Table(
        String name,
        String[] fields,
        String index,
        String indexField,
        TableType type
    ) {
        tableName = name;
        tableFields = fields;
        tableType = type;
        tableIndex = index;
        this.indexField = indexField;
    }

    public String getTableName() {
        return tableName;
    }

    public TableType getTableType() {
        return tableType;
    }

    public String[] getTableFields() {
        return tableFields;
    }

    public String getTableIndex() {
        return tableIndex;
    }

    public String getIndexField() {
        return indexField;
    }

}
