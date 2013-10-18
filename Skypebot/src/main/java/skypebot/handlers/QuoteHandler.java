package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import skypebot.db.IDbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 10/16/13
 * Time: 11:29 PM
 */
public class QuoteHandler implements IHandler {

    private IDbManager manager;

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().matches( "bucket, quote [^ ]+" );
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
            String message = m.getContent();
            message = message.replace(
                "bucket, quote ",
                ""
            );
            String unaliasedId = manager.getSingleFromDbThatEquals(
                manager.getSchema().getAliasTable(),
                "alias",
                "realId",
                message
            );
            if( unaliasedId == null ) {
                return;
            }
            String quote = manager.getSingleFromDbThatEquals(
                manager.getSchema().getQuotesTable(),
                "attributedAuthor",
                "quote",
                unaliasedId
            );

            m.getChat().send( quote );
        } catch( SkypeException e ) {
            return;
        } catch( SQLException e ) {
            return;
        }

    }
}
