package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 3/10/13
 * Time: 6:56 PM
 */
public class AddNounHandler implements IHandler {
    private Logger logger = Logger.getLogger( "AddHandler" );
    private IDbManager manager;

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, noun+" );
        } catch( SkypeException e ) {
            //something weird happened, drop the message
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
            String nounToAdd = m.getContent().replace(
                "bucket, noun+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getNounTable(),
                new String[]{ nounToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert noun into noun table!" );
            }
            else {
                m.getChat().send( "Success - inserted " + nounToAdd );
                logger.info( "Successfully inserted " + nounToAdd + " into noun table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
        }

    }
}
