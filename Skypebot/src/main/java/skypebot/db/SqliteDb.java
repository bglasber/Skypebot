package skypebot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class SqliteDb implements IDbProvider {

	private String dbName;
	private Connection conn;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	
	public SqliteDb(String dbName) throws SQLException {
		this.dbName = dbName;
		this.open();

	}

	@Override
	public void open() throws SQLException {
		try{
			Class.forName("org.sqlite.JDBC");
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
	}

	@Override
	public void close() throws SQLException {
		conn.close();
	}

	@Override
	public ResultSet getResultQuery(String tableName, String[] fieldsToGet) throws SQLException {
		Statement s = conn.createStatement();
		String sql = constructSelectStatementWithFields(tableName, fieldsToGet);
		logger.trace("Executing query: " + sql);
		return s.executeQuery(sql);
		
	}

	private String constructSelectStatementWithFields(String tableName,
			String[] fieldsToGet) throws SQLException {
		String sql = "SELECT ";
		if(fieldsToGet.length > 0){
			for(String field: fieldsToGet){
				sql += field + ", ";
			}
			sql = sql.substring(0, sql.length() - 2) + " ";
		}
		else {
			sql += "* ";
		}
		sql += "FROM " + tableName;
		return sql;
	}

	@Override
	public ResultSet getResultLookup(String tableName,
			String[] fieldsToGet, String fieldToCheck, String fieldValue) throws SQLException {
		Statement s = conn.createStatement();
		String sql = constructSelectStatementWithFields(tableName, fieldsToGet);
		sql += " WHERE " + fieldToCheck + " = \"" + fieldValue + "\"";
		logger.trace("Executing query: " + sql);
		return s.executeQuery(sql);
		
	}

	@Override
	public void insertInto(String tableName, String[] fieldsToAdd) throws SQLException {
		Statement s = conn.createStatement();
		String sql = "INSERT INTO " + tableName;
		sql += " VALUES( ";
		if(fieldsToAdd.length == 0){
			throw new SQLException("Can't insert no fields into table: " + tableName);
		}
		for(String field : fieldsToAdd){
			sql += "\"" + field + "\", ";
		}
		sql = sql.substring(0, sql.length() - 2);
		sql += ")";
		logger.trace("Executing query: " + sql);
		s.executeUpdate(sql);
		
	}

	@Override
	public void createTable(String sqlConstructorString) throws SQLException {
		Statement s = conn.createStatement();
		logger.trace("Executing query: " + sqlConstructorString);
		s.execute(sqlConstructorString);
	}

	@Override
	public void createIndex(String sqlIndexCreationString) throws SQLException {
		Statement s = conn.createStatement();
		logger.trace("Executing query: " + sqlIndexCreationString);
		s.execute(sqlIndexCreationString);
	}
}
	
