package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.engine.Engine;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 10/16/13
 * Time: 11:09 PM
 */
public class RememberHandler implements IHandler {

    private IDbManager manager;
    private static Logger logger = Logger.getLogger( RememberHandler.class.getCanonicalName() );

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            //e.g. bucket, quote bean "find this message" 3
            return m.getContent().matches( "bucket, remember [^ ]+ \"[^\"]+\" [0-9]+" );
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
            logger.debug( "RememberHandler will be handling the message" );
            String[] fields = m.getContent().replace(
                "bucket, remember ([^ ]+) \"([^\"])\" ([0-9]+)",
                "$1@$2@$3"
            ).split( "@" );

            logger.trace( "Unaliasing: " + fields[ 0 ] );
            String realName = manager.getSingleFromDbThatEquals(
                manager.getSchema().getAliasTable(),
                "alias",
                "realId",
                fields[ 0 ]
            );
            logger.debug( "Found Real Name: " + realName );
            String message = Engine.messageList.GetMessageToQuote(
                realName,
                fields[ 1 ],
                Integer.parseInt( fields[ 2 ] )
            );
            logger.debug( "Got message: " + message );

            manager.insertFieldsIntoTable(
                manager.getSchema().getQuotesTable(),
                new String[]{
                    fields[ 0 ], //Fully qualified name
                    message
                }
            );
            m.getChat().send( "Okay, I'll remember that" );

        } catch( SkypeException e1 ) {
            return;
        } catch( SQLException e1 ) {
            return;
        }

    }
}
