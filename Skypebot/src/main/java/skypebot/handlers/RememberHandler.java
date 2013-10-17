package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
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
            String[] fields = m.getContent().replace(
                "bucket, remember ([^ ]+) \"([^\"])\" ([0-9]+)",
                "$1@$2@$3"
            ).split( "@" );
            String alias = manager.getSingleFromDbThatEquals(
                manager.getSchema().getAliasTable(),
                "realId",
                "alias",
                fields[ 0 ]
            );
            String message = Engine.messageList.GetMessageToQuote(
                alias,
                fields[ 1 ],
                Integer.parseInt( fields[ 2 ] )
            );

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
