package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:40 PM
 */
public class VerbingVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$verbing" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String verbing = dbManager.getSingleFromDb(
                dbManager.getSchema().getVerbingTable(),
                "verbing"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + verbing );
            return message.replaceFirst(
                "\\$verbing",
                verbing
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }
}
