package skypebot.handlers.addVariableHandlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 6:26 PM
 */
public class AddNounsHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( "AddHandler" );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, nouns+" );
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
            String nounToAdd = m.getContent().replace(
                "bucket, nouns+ ",
                ""
            );
            boolean wasSuccessful = manager.insertFieldsIntoTable(
                manager.getSchema().getNounsTable(),
                new String[]{ nounToAdd }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not insert noun into nouns table!" );
            }
            else {
                m.getChat().send( "Success - inserted " + nounToAdd );
                logger.info( "Successfully inserted " + nounToAdd + " into nouns table" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
        }
    }
}
