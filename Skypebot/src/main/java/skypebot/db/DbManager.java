package skypebot.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import skypebot.db.schema.Schema;
import skypebot.db.schema.SchemaConstructorString;
import skypebot.db.schema.SchemaConstructorType;
import skypebot.db.schema.Table;

import com.skype.SkypeException;

public class DbManager {

	private IDbProvider provider;
	private Schema schema;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	public DbManager(Schema s){
		schema = s;
	}
	
	public void setProvider(IDbProvider provider){
		this.provider = provider;
	}
	
	public void constructSchema() throws SQLException{
		for(SchemaConstructorString schemaConstructor : schema.getSchemaConstructionStrings()){
			if(schemaConstructor.getType() == SchemaConstructorType.TABLECONSTRUCTOR){
				provider.createTable(schemaConstructor.getString());
			}
			else if(schemaConstructor.getType() == SchemaConstructorType.INDEXCONSTRUCTOR){
				provider.createIndex(schemaConstructor.getString());
				
			}
		}
	}
	public Schema getSchema(){
		return schema;
	}
	
	public String getSingleFromDb(Table table, String fieldNameToCheck, String fieldNameToReturn, String messageToMatch) throws SQLException, SkypeException{
		
		ResultSet resultSet = provider.getResultLookup(table.getTableName(), table.getTableFields(), fieldNameToCheck, 
				messageToMatch);
		
		List<String> resultList = new ArrayList<String>();
		while(resultSet.next()){
			logger.debug("fieldNameToReturn: " + fieldNameToReturn);
			resultList.add(resultSet.getString(fieldNameToReturn));
		}
		logger.debug("Got results from db query: ");
		logger.debug(resultList);
		return getRandomResult(fieldNameToReturn, resultList);
	}

	public String getSingleFromDb(Table table, String fieldNameToReturn) throws SQLException {
		ResultSet resultSet = provider.getResultQuery(table.getTableName(), new String[]{ fieldNameToReturn });
		List<String> resultList = new ArrayList<String>();
		while(resultSet.next()){
			resultList.add(resultSet.getString(fieldNameToReturn));
		}
		return getRandomResult(fieldNameToReturn, resultList);
	}
	private String getRandomResult(String fieldNameToReturn,
			List<String> resultList) {
		try {
			String result =  resultList.get(
					(int)(Math.random() * (resultList.size())));
			logger.debug("Got random result: " + result);
			return result;
		}
		catch(IndexOutOfBoundsException e){
			//No valid responses.
			return null;
		}
	}
	
	
	public boolean insertFieldsIntoTable(Table table, String[] fieldsToInsert) throws SQLException{
		String tableName = table.getTableName();
		try {
			provider.insertInto(tableName, fieldsToInsert);
			logger.debug("Successfully inserted fields into: " + tableName);
			return true;
		}
		catch(SQLException e){
			logger.error("Failed to insert fields into table: " + tableName);
			logger.error(e);
			return false;
		}
		
	}
}
