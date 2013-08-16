package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;


/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:31 PM
 */
public class AdjectiveVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public AdjectiveVariable( IDbManager manager ) {
        dbManager = manager;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$adjective" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        try {
            String adjective = dbManager.getSingleFromDb(
                dbManager.getSchema().getAdjectiveTable(),
                "adjective"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + adjective );
            return message.replaceFirst(
                "\\$adjective",
                adjective
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }
}
