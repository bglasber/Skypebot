package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 11:10 PM
 */
public class DropItemHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, drop" );
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
            String item = m.getContent().replace(
                "bucket, drop ",
                ""
            );
            boolean wasSuccessful = manager.deleteRowFromTable(
                manager.getSchema().getItemTable(),
                new String[]{ "item" },
                new String[]{ item }
            );
            if( !wasSuccessful ) {
                logger.error( "Could not delete '" + item + "'!" );
            }
            else {
                m.getChat().send( "/me drops " + item );
                logger.info( "Dropped '" + item + "'" );
            }

        } catch( SkypeException e ) {
            //Just drop the message
            return;
        }
    }
}
