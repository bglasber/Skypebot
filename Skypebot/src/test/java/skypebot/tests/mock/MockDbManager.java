package skypebot.tests.mock;

import com.skype.SkypeException;
import skypebot.db.schema.Schema;
import skypebot.db.schema.Table;
import skypebot.db.IDbManager;
import skypebot.db.IDbProvider;

import java.sql.SQLException;
import java.util.List;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 5:10 PM
 */
public class MockDbManager implements IDbManager {

    public void setProvider( IDbProvider provider ) {
    }

    public void constructSchema() throws SQLException {
    }

    public Schema getSchema() {
        return new Schema();
    }

    public String getSingleFromDbThatContains(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException {
        return null;
    }

    @Override
    public String[] getMultipleFieldsFromDbThatContains(
        Table table,
        String fieldNameToCheck,
        String[] fieldsToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException {
        return new String[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getSingleFromDbThatEquals(
        Table table,
        String fieldNameToCheck,
        String fieldNameToReturn,
        String messageToMatch
    ) throws SQLException, SkypeException {
        return null;
    }

    public String getSingleFromDb(
        Table table,
        String fieldNameToReturn
    ) throws SQLException {

        if( table.equals( new Schema().getNounTable() ) ) {
            return "nounForTest";
        }
        return null;
    }

    public boolean insertFieldsIntoTable(
        Table table,
        String[] fieldsToInsert
    ) {
        return true;
    }

    @Override
    public long getDbCount( Table table ) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteRowFromTable(
        Table table,
        String[] fieldsToCheck,
        String[] fieldValuesExpected
    ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String deleteRandomRowFromTable(
        Table table,
        String fieldToCheck,
        String fieldToReturn
    ) {
        return "emptyString";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getEntireTable(
        Table table,
        String fieldToReturn
    ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
