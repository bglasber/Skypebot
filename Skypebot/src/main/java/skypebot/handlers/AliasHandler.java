package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import skypebot.db.IDbManager;

/**
 * User: brad
 * Date: 10/16/13
 * Time: 7:42 PM
 */
public class AliasHandler implements IHandler {
    private IDbManager dbManager;

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().matches( "bucket, alias [A-Za-z0-9]+ .+" );
        } catch( SkypeException e ) {
            return false;
        }
    }

    @Override
    public void setManager( IDbManager m ) {
        dbManager = m;
    }

    @Override
    public void handle( ChatMessage m ) {
        String message;
        try {
            message = m.getContent();
        } catch( SkypeException e ) {
            return;
        }
        String[] fieldsToInsert = message.replaceAll(
            "bucket, alias ([^ ]+) (.+)",
            "$1@$2"
        ).split( "@" );
        if( dbManager.insertFieldsIntoTable(
            dbManager.getSchema().getAliasTable(),
            fieldsToInsert
        )
            ) {
            try {
                m.getChat().send( "Aliased " + fieldsToInsert[ 0 ] + " to " + fieldsToInsert[ 1 ] );
            } catch( SkypeException e ) {
                return;
            }
        }

    }
}
