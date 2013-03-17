package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 12:26 PM
 */
public class ItemVariable implements IVariable {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public ItemVariable( IDbManager m ) {
        manager = m;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$item" );
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
        } catch( SQLException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        }
        return message.replaceFirst(
            "\\$item",
            item
        );
    }
}
