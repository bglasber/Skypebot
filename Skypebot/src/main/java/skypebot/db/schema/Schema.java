package skypebot.db.schema;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Schema {
	
	
	public static class Tables {
		
		public static Table ResponseTable =
				new Table("responses", new String[]{
						  "query", "response"},
						  "responsesIndex",
						  "query",
						  TableType.RESPONSE);
		
		public static Table NounTable =
				new Table("noun", new String[]{
						"noun"},
						"nounIndex",
						"noun",
						TableType.NOUN);
		
				
	}
	
	public Table getResponseTable(){
		return Tables.ResponseTable;
	}
	
	public Table getNounTable(){
		return Tables.NounTable;
	}
	
	public List<SchemaConstructorString> getSchemaConstructionStrings() {
		
		Field[] fields = Schema.Tables.class.getFields();
		List<SchemaConstructorString> schemaStrings = new ArrayList<>();
		for(Field f : fields){
			Table t = null;
			try {
				t = (Table) f.get(t);
				String constructTableString = "CREATE TABLE " + t.getTableName() + " ( ";
				for(String tableField : t.getTableFields()){
					constructTableString += tableField + " TEXT, ";
				}
				//Get rid of the ', ' on the end
				constructTableString = constructTableString.substring(0, constructTableString.length() - 2); 
				constructTableString += ");";
				schemaStrings.add(new SchemaConstructorString(constructTableString, SchemaConstructorType.TABLECONSTRUCTOR));
				String constructIndexString = "CREATE INDEX " + t.getTableIndex() + " ON " + t.getTableName() + " ( ";
				constructIndexString += t.getIndexField() + " );";
				schemaStrings.add(new SchemaConstructorString(constructIndexString, SchemaConstructorType.INDEXCONSTRUCTOR));
			}
			catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}
		return schemaStrings;
	}

}
