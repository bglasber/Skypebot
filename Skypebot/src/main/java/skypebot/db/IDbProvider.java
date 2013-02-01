package skypebot.db;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDbProvider {

	public void open() throws SQLException;
	
	public void close() throws SQLException;
	
	public ResultSet getResultQuery(String tableName, String[] fieldsToGet) throws SQLException;
	
	public ResultSet getResultLookup(String tableName, String[] fieldsToGet, String fieldToCheck, String FieldValue) throws SQLException;

	public void createTable(String string) throws SQLException;

	public void createIndex(String string) throws SQLException;

	void insertInto(String tableName, String[] fieldsToAdd) throws SQLException;
}
