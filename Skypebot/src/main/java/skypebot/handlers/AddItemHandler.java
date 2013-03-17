package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.db.schema.Table;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 12:19 PM
 */
public class AddItemHandler implements IHandler {

    private IDbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "gives bucket" );
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
        String itemToAdd = getItemToAdd( m );
        String deletedItem = null;
        Table itemTable = manager.getSchema().getItemTable();
        if( manager.getDbCount( itemTable ) >= 15 ) {
            deletedItem = manager.deleteRandomRowFromTable(
                itemTable,
                "item",
                "item"
            );
        }
        if( itemToAdd != null ) {
            manager.insertFieldsIntoTable(
                manager.getSchema().getItemTable(),
                new String[]{ itemToAdd }
            );
            try {
                if( deletedItem != null ) {
                    m.getChat().send( "/me drops " + deletedItem + " and picks up " + itemToAdd );
                }
                else {
                    m.getChat().send( "/me picks up " + itemToAdd );
                }
            } catch( SkypeException e ) {
                logger.error( e.getMessage() );
                logger.error( e.getStackTrace() );
            }
        }

    }

    private String getItemToAdd( ChatMessage m ) {
        try {
            return m.getContent().replaceAll(
                "^.*gives bucket (.*)$",
                "$1"
            );

        } catch( SkypeException e ) {
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        }
        return null;
    }
}
