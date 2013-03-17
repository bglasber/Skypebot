package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.sql.SQLException;

public class NounVariable implements IVariable {

    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public NounVariable( IDbManager manager ) {
        dbManager = manager;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$noun" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        //Do Db stuff to expand the variable

        try {
            String noun = dbManager.getSingleFromDb(
                dbManager.getSchema().getNounTable(),
                "noun"
            );
            logger.trace( "trying to replace \"" + message + "\" with " + noun );
            return message.replaceFirst(
                "\\$noun",
                noun
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

}
