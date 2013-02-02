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
        logger.trace( "Executing query: " + sql );
        return s.executeQuery( sql.toString() );

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
        logger.trace( "Executing query: " + sql );
        return s.executeQuery( sql.toString() );
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
        logger.trace( "Executing query: " + sql );
        return s.executeQuery( sql.toString() );

    }

    @Override
    public void insertInto(
        String tableName,
        String[] fieldsToAdd
    ) throws SQLException {
        Statement s = conn.createStatement();
        ISqlString sql = CreateSqlInsertString(
            tableName,
            fieldsToAdd
        );
        logger.trace( "Executing query: " + sql );
        s.executeUpdate( sql.toString() );

    }

    private ISqlString CreateSqlInsertString(
        String tableName,
        String[] fieldsToAdd
    ) throws SQLException {
        String sql = "INSERT INTO " + tableName;
        sql += " VALUES( ";
        if( fieldsToAdd.length == 0 ) {
            throw new SQLException( "Can't insert no fields into table: " + tableName );
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
        logger.trace( "Executing query: " + sqlConstructorString.toString() );
        s.execute( sqlConstructorString.toString() );
    }

    @Override
    public void createIndex( ISqlString sqlIndexCreationString ) throws SQLException {
        Statement s = conn.createStatement();
        logger.trace( "Executing query: " + sqlIndexCreationString );
        s.execute( sqlIndexCreationString.toString() );
    }
}

