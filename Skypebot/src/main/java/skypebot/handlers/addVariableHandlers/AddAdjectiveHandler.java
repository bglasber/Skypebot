package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 7:01 PM
 */
public class AddAdjectiveHandler implements IHandler {

    public IDbManager manager;
    public Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, adjective+" );
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
            String adjectiveToAdd = m.getContent().replace(
                "bucket, adjective+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getAdjectiveTable(),
                new String[]{ adjectiveToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert adjective into adjective table!" );
            }
            else {
                m.getChat().send( "Success - inserted " + adjectiveToAdd );
                logger.info( "Successfully inserted " + adjectiveToAdd + " into adjective table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
        }
    }
}
