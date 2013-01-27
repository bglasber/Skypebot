package skypebot.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class SqliteDb implements IDbProvider {

	private String dbName;
	private SqlJetDb db;
	
	public SqliteDb(String dbName) throws SqlJetException {
		this.dbName = dbName;
		this.open();
	}
	
	public void open() throws SqlJetException{
		db = SqlJetDb.open(new File(dbName), true);
	}
	
	public void close() throws SqlJetException {
		db.close();
	}
	
	public List<DbResult> getResultQuery(String tableName, String[] fieldsToGet) throws SqlJetException{
		ISqlJetTable table = db.getTable(tableName);
		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
		ISqlJetCursor cursor = table.open();
		List<DbResult> results = getResults(fieldsToGet, cursor);
		db.commit();
		return results;
	}

	private List<DbResult> getResults(String[] fieldsToGet, ISqlJetCursor cursor)
			throws SqlJetException {
		List<DbResult> results = new ArrayList<DbResult>();
		try {
			if(!cursor.eof()){
				do {
					DbResult result = new DbResult();
					for(String field : fieldsToGet){
						result.put(field, cursor.getString(field));
					}
					results.add(result);
				}
				while(cursor.next());
			}
		}
		finally {
			cursor.close();
		}
		return results;
	}
	
	public List<DbResult> getResultLookup(String tableName, String[] fieldsToGet, String fieldToCheck, String fieldValue)
		throws SqlJetException {
		
		ISqlJetTable table = db.getTable(tableName);
		db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
		ISqlJetCursor cursor = table.lookup(fieldToCheck, fieldValue);
		System.out.println(fieldValue);
		List<DbResult> results = getResults(fieldsToGet, cursor);
		db.commit();
		return results;
	}

	@Override
	public void insertInto(String tableName, Map<String, Object> fieldsToAdd) throws SqlJetException {
		ISqlJetTable table = db.getTable(tableName);
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		table.insertByFieldNames(fieldsToAdd);
		db.commit();
		
	}
	
}
