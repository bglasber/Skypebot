package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 5:44 PM
 */
public class NounsVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public NounsVariable( IDbManager manager ) {
        dbManager = manager;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$nouns" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String noun = dbManager.getSingleFromDb(
                dbManager.getSchema().getNounsTable(),
                "nouns"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + noun );
            return message.replaceFirst(
                "\\$nouns",
                noun
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }
}
