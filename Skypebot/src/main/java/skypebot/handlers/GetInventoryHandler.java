package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;

import java.util.List;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 2:23 PM
 */
public class GetInventoryHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().equals( "bucket, inv" );
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
        List<String> items = manager.getEntireTable(
            manager.getSchema().getItemTable(),
            "item"
        );
        try {
            for( String item : items ) {
                m.getChat().send( item );
            }
        } catch( SkypeException e ) {
            logger.error( e.getMessage() );
        }

    }
}
