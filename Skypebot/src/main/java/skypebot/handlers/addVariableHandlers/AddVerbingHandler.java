package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 7:52 PM
 */
public class AddVerbingHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, verbing+" );
        } catch( SkypeException e ) {
            return true;
        }
    }

    @Override
    public void setManager( IDbManager m ) {
        manager = m;
    }

    @Override
    public void handle( ChatMessage m ) {
        try {
            String verbingToAdd = m.getContent().replace(
                "bucket, verbing+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getVerbingTable(),
                new String[]{ verbingToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert verb into verbing table!" );
            }
            else {
                m.getChat().send( "Success - inserted " + verbingToAdd );
                logger.info( "Successfully inserted " + verbingToAdd + " into verbing table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
            return;
        }
    }
}
