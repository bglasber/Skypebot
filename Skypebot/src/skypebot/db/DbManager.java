package skypebot.db;

import java.util.List;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.schema.Schema;
import skypebot.db.schema.Table;

import com.skype.ChatMessage;
import com.skype.SkypeException;

public class DbManager {

	private IDbProvider provider;
	private Schema schema;
	public DbManager(Schema s){
		schema = s;
	}
	
	public void setProvider(IDbProvider provider){
		this.provider = provider;
	}
	
	public Schema getSchema(){
		return schema;
	}
	
	public String getSingleFromDb(Table table, String fieldNameToReturn, String m) throws SqlJetException, SkypeException{
		
		List<DbResult> resultList = provider.getResultLookup(table.getTableName(), table.getTableFields(), table.getTableIndex(), 
				m);
		return getRandomResult(fieldNameToReturn, resultList);
	}

	private String getRandomResult(String fieldNameToReturn,
			List<DbResult> resultList) {
		try {
			String result =  resultList.get(
					(int)(Math.random() * (resultList.size()))
					).get(fieldNameToReturn);
			return result;
		}
		catch(IndexOutOfBoundsException e){
			//No valid responses.
			return null;
		}
	}
	
	public String getSingleFromDb(Table table, String fieldNameToReturn) throws SqlJetException, SkypeException {
		List<DbResult> resultList = provider.getResultQuery(table.getTableName(), new String[]{ fieldNameToReturn });
		return getRandomResult(fieldNameToReturn, resultList);
	}
	
	public boolean insertFieldsIntoTable(Table table, Map<String, String> fieldsToInsert) throws SqlJetException{
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,Object> fields = (Map)fieldsToInsert;
		try {
			provider.insertInto(table.getTableName(), fields);
			return true;
		}
		catch(SqlJetException e){
			e.printStackTrace();
			return false;
		}
		
	}
}
