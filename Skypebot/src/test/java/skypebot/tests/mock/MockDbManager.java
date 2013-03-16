package skypebot.tests.mock;

import skypebot.db.*;
import skypebot.db.schema.*;
import java.sql.SQLException;
import com.skype.SkypeException;
import skypebot.db.schema.Schema;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 5:10 PM
 */
public class MockDbManager implements IDbManager {

    public void setProvider(IDbProvider provider){
        return;
    }

    public void constructSchema() throws SQLException {
        return;
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

        if(table.equals( new Schema().getNounTable() ) ){
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
}
