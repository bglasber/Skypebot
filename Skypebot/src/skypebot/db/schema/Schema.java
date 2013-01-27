package skypebot.db.schema;

public class Schema {
	
	public static class Tables {
		
		public static Table ResponseTable =
				new Table("responses", new String[]{
						  "query", "response"},
						  "responsesIndex",
						  TableType.RESPONSE);
		
		public static Table NounTable =
				new Table("noun", new String[]{
						"noun"},
						"nounIndex",
						TableType.NOUN);
		
		
				
	}
	
	public Table getResponseTable(){
		return Tables.ResponseTable;
	}
	
	public Table getNounTable(){
		return Tables.NounTable;
	}

}
