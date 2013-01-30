package skypebot.db;

import java.util.HashMap;
import java.util.Map;

public class DbResult {
	
	private Map<String, String> fields;
	
	public DbResult(){
		 fields  = new HashMap<String, String>();
	}
	
	public void put(String fieldName, String stringVal){
		fields.put(fieldName, stringVal);
	}
	
	public String get(String fieldName){
		return fields.get(fieldName);
	}
	
	


}
