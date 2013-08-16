package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:38 PM
 */
public class VerbedVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$verbed" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String verbed = dbManager.getSingleFromDb(
                dbManager.getSchema().getVerbedTable(),
                "verbed"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + verbed );
            return message.replaceFirst(
                "\\$verbed",
                verbed
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }
}
