package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 10/13/13
 * Time: 11:22 PM
 */
public class PopItemVariable implements IVariable {
    private static Logger logger = Logger.getLogger( PopItemVariable.class.getCanonicalName() );
    private IDbManager manager;

    public PopItemVariable( IDbManager m ) {
        manager = m;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$popitem" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        String item = "defaultItem";
        try {
            item = manager.getSingleFromDb(
                manager.getSchema().getItemTable(),
                "item"
            );
            if( item == null ) {
                item = "himself";
            }
            manager.deleteRowFromTable(
                manager.getSchema().getItemTable(),
                new String[]{ "item" },
                new String[]{ item }
            );
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        }
        return message.replaceFirst(
            "\\$popitem",
            item
        );
    }
}
