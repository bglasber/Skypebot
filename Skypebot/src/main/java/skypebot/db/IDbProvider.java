package skypebot.db;

import org.tmatesoft.sqljet.core.SqlJetException;
import java.util.List;
import java.util.Map;

public interface IDbProvider {

	public void open() throws SqlJetException;
	
	public void close() throws SqlJetException;
	
	public List<DbResult> getResultQuery(String tableName, String[] fieldsToGet) throws SqlJetException;
	
	public List<DbResult> getResultLookup(String tableName, String[] fieldsToGet, String fieldToCheck, String FieldValue)
		throws SqlJetException;
	
	public void insertInto(String tableName, Map<String,Object> fieldsToAdd) throws SqlJetException;

	public void createTable(String string);

	public void createIndex(String string);
}
