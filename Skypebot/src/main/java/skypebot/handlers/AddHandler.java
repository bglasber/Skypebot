package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

public class AddHandler implements IHandler {


    private IDbManager dbManager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            boolean willHandle = m.getContent().matches( "bucket, add '[^']*' '[^']*'" );
            if( willHandle ) {
                logger.debug( "AddHandler will handle message..." );
            }
            return willHandle;
        } catch( SkypeException e ) {
            //Something weird happened, just drop the message
            return false;
        }
    }

    @Override
    public void setManager( IDbManager m ) {
        dbManager = m;
    }

    @Override
    public void handle( ChatMessage m ) {
        //We assume that we 'canHandle' it
        try {
            String[] splitMessage = getFieldsToInsert( m );
            boolean wasSuccessful = dbManager.insertFieldsIntoTable(
                dbManager.getSchema().getResponseTable(),
                splitMessage
            );
            if( wasSuccessful ) {
                m.getChat().send( "Inserted " + splitMessage[ 0 ] + " -> " + splitMessage[ 1 ] );
                logger.info( "Inserted" + splitMessage[ 0 ] + " -> " + splitMessage[ 1 ] );
            }
        } catch( SkypeException e ) {
            //just drop it
            logger.error( "AddHandler could not handle message - could not get message content" );
        }

    }

    private String[] getFieldsToInsert( ChatMessage m ) throws SkypeException {
        String message = m.getContent();
        return message.replaceAll(
            "^.*'([^']*)' '([^']*)'$",
            "$1@$2"
        ).split( "@" );
    }

}
