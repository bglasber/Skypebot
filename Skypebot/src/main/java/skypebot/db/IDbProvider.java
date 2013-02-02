package skypebot.db;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDbProvider {

    public void open() throws SQLException;

    public void close() throws SQLException;

    public ResultSet getResultQuery(
        String tableName,
        String[] fieldsToGet
    ) throws SQLException;

    public ResultSet getResultsByContains(
        String tableName,
        String[] fieldsToGet,
        String fieldToCheck,
        String fieldValue
    ) throws SQLException;

    public ResultSet getResultsByEquality(
        String tableName,
        String[] fieldsToGet,
        String fieldToCheck,
        String fieldValue
    ) throws SQLException;

    public void createTable( ISqlString string ) throws SQLException;

    public void createIndex( ISqlString string ) throws SQLException;

    void insertInto(
        String tableName,
        String[] fieldsToAdd
    ) throws SQLException;
}
