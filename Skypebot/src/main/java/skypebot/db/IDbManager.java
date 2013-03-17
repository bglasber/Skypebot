package skypebot.db;

import com.skype.SkypeException;
import skypebot.db.schema.Schema;
import skypebot.db.schema.Table;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 5:11 PM
 */
public interface IDbManager {

    public void setProvider( IDbProvider provider );

    public void constructSchema() throws SQLException;

    public Schema getSchema();

    public String getSingleFromDbThatContains(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException;

    public String getSingleFromDbThatEquals(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException;

    public String getSingleFromDb(
        Table table,
        String fieldNameToReturn
    ) throws SQLException;

    public boolean insertFieldsIntoTable(
        Table table,
        String[] fieldsToInsert
    );

    public long getDbCount(
        Table table
    );

    public boolean deleteRowFromTable(
        Table table,
        String fieldToCheck,
        String fieldValueExpected
    );

    public String deleteRandomRowFromTable(
        Table table,
        String fieldToCheck,
        String fieldToReturn
    );
}
