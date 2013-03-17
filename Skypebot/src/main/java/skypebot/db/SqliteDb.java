package skypebot.db;

import org.apache.log4j.Logger;

import java.sql.*;

public class SqliteDb implements IDbProvider {

    private String dbName;
    private Connection conn;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public SqliteDb( String dbName ) throws SQLException {
        this.dbName = dbName;
        this.open();

    }

    @Override
    public void open() throws SQLException {
        try {
            Class.forName( "org.sqlite.JDBC" );
        } catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        conn = DriverManager.getConnection( "jdbc:sqlite:" + dbName );
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    @Override
    public ResultSet getResultQuery(
        String tableName,
        String[] fieldsToGet
    ) throws SQLException {
        Statement s = conn.createStatement();
        SqlConditionString sql = constructSelectStatementWithFields(
            tableName,
            fieldsToGet
        );
        logger.trace( "Executing query: " + sql.getString() );
        return s.executeQuery( sql.getString() );

    }

    private SqlConditionString constructSelectStatementWithFields(
        String tableName,
        String[] fieldsToGet
    ) throws SQLException {
        String sql = "SELECT ";
        if( fieldsToGet.length > 0 ) {
            for( String field : fieldsToGet ) {
                sql += field + ", ";
            }
            sql = sql.substring(
                0,
                sql.length() - 2
            ) + " ";
        }
        else {
            sql += "* ";
        }
        sql += "FROM " + tableName;
        return new SqlConditionString( sql );
    }

    @Override
    public ResultSet getResultsByEquality(
        String tableName,
        String[] fieldsToGet,
        String fieldToCheck,
        String fieldValue
    ) throws SQLException {
        Statement s = conn.createStatement();
        SqlConditionString sql = constructSelectStatementWithFields(
            tableName,
            fieldsToGet
        );
        sql.addEqualsCondition(
            fieldToCheck,
            fieldValue
        );
        logger.trace( "Executing query: " + sql.getString() );
        return s.executeQuery( sql.getString() );
    }

    public ResultSet getResultsByContains(
        String tableName,
        String[] fieldsToGet,
        String fieldToCheck,
        String fieldValue
    ) throws SQLException {
        Statement s = conn.createStatement();
        SqlConditionString sql = constructSelectStatementWithFields(
            tableName,
            fieldsToGet
        );
        sql.addContainsCondition(
            fieldToCheck,
            fieldValue
        );
        logger.trace( "Executing query: " + sql.getString() );
        return s.executeQuery( sql.getString() );

    }

    @Override
    public void insertInto(
        String tableName,
        String[] fieldsToAdd
    ) throws SQLException {
        Statement s = conn.createStatement();
        ISqlString sql = createSqlInsertString(
            tableName,
            fieldsToAdd
        );
        logger.trace( "Executing query: " + sql.getString() );
        s.executeUpdate( sql.getString() );

    }

    @Override
    public long getNumberOfEntries( String tableName ) throws SQLException {
        Statement s = conn.createStatement();
        ISqlString sql = createSqlCountString( tableName );

        ResultSet rowsReturned = s.executeQuery( sql.getString() );
        if( rowsReturned.next() ) {
            return rowsReturned.getLong( 1 );
        }
        return 0;
    }

    @Override
    public boolean deleteRowFromTable(
        String tableName,
        String fieldToCheck,
        String fieldValueExpected
    ) throws SQLException {
        Statement s = conn.createStatement();
        ISqlString sql = createSqlDeleteString(
            tableName,
            fieldToCheck,
            fieldValueExpected
        );
        return s.execute( sql.getString() );
    }

    private ISqlString createSqlInsertString(
        String tableName,
        String[] fieldsToAdd
    ) throws SQLException {
        String sql = "INSERT INTO " + tableName;
        sql += " VALUES( ";
        if( fieldsToAdd.length == 0 ) {
            throw new SQLException( "Can't insert fields into table: " + tableName );
        }
        for( String field : fieldsToAdd ) {
            sql += "\"" + field + "\", ";
        }
        sql = sql.substring(
            0,
            sql.length() - 2
        );
        sql += ")";
        return new SqlInsertString( sql );
    }

    @Override
    public void createTable( ISqlString sqlConstructorString ) throws SQLException {
        Statement s = conn.createStatement();
        logger.trace( "Executing query: " + sqlConstructorString.getString() );
        s.execute( sqlConstructorString.getString() );
    }

    @Override
    public void createIndex( ISqlString sqlIndexCreationString ) throws SQLException {
        Statement s = conn.createStatement();
        logger.trace( "Executing query: " + sqlIndexCreationString.getString() );
        s.execute( sqlIndexCreationString.getString() );
    }

    public ISqlString createSqlCountString( String tableName ) {
        return new SqlCountString( "SELECT COUNT(*) FROM " + tableName );
    }

    public ISqlString createSqlDeleteString(
        String tableName,
        String fieldToCheck,
        String uniqueIdentifier
    ) {
        return new SqlDeleteString( "DELETE FROM " + tableName + " WHERE " + fieldToCheck + " = " + uniqueIdentifier );
    }
}

