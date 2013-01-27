package skypebot.db.schema;

public class Table {
	
	private String tableName;
	private String[] tableFields;
	private TableType tableType;
	private String tableIndex;

	public Table(String name, String[] fields, String index, TableType type){
		tableName = name;
		tableFields = fields;
		tableType = type;
		tableIndex = index;
	}
	
	public String getTableName(){
		return tableName;
	}
	
	public TableType getTableType(){
		return tableType;
	}
	
	public String[] getTableFields(){
		return tableFields;
	}
	
	public String getTableIndex(){
		return tableIndex;
	}

}
