package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;


/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:45 PM
 */
public class AddVerbHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );


    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, verbs+" );
        } catch( SkypeException e ) {
            return false;
        }
    }

    @Override
    public void setManager( IDbManager m ) {
        manager = m;
    }

    @Override
    public void handle( ChatMessage m ) {
        try {
            String verbToAdd = m.getContent().replace(
                "bucket, verbs+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getVerbTable(),
                new String[]{ verbToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert verb into verb table!" );
            }
            else {
                m.getChat().send( "Success - inserted " + verbToAdd );
                logger.info( "Successfully inserted " + verbToAdd + " into verb table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
        }

    }
}
