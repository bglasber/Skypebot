package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:35 PM
 */
public class VerbVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public VerbVariable( IDbManager manager ) {
        dbManager = manager;
    }

    @Override
    public boolean isContainedInString( String message ) {
        //Its technically bad sentence structure to end a sentence with a verb so...
        return message.contains( "$verb " );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String verb = dbManager.getSingleFromDb(
                dbManager.getSchema().getVerbTable(),
                "verb"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + verb );
            return message.replaceFirst(
                "\\$verb",
                verb
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }
}
