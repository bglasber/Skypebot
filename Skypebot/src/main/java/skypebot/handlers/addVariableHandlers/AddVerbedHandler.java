package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 10:23 PM
 */
public class AddVerbedHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, verbed+" );
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
                "bucket, verbed+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getVerbedTable(),
                new String[]{ verbToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert verb into verbed table!" );
            }
            else {
                m.getChat().send( "Success inserted " + verbToAdd );
                logger.info( "Successfully inserted " + verbToAdd + " into verbed table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
        }
    }
}
