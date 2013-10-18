package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.engine.Engine;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            String re1 = ".*?";    // Non-greedy match on filler
            String re2 = "(?:[a-z][a-z]+)";    // Uninteresting: word
            String re3 = ".*?";    // Non-greedy match on filler
            String re4 = "(?:[a-z][a-z]+)";    // Uninteresting: word
            String re5 = ".*?";    // Non-greedy match on filler
            String re6 = "((?:[a-z][a-z]+))";    // Word 1
            String re7 = ".*?";    // Non-greedy match on filler
            String re8 = "((?:[a-z][a-z]+))";    // Word 2
            String re9 = ".*?";    // Non-greedy match on filler
            String re10 = "(\\d+)";    // Integer Number 1
            Pattern p = Pattern.compile(
                re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8 + re9 + re10,
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
            );
            Matcher mat = p.matcher( m.getContent() );
            mat.find();
            String name = mat.group( 1 );
            String text = mat.group( 2 );
            String num = mat.group( 3 );
            logger.debug( "(" + name.toString() + ")" + "(" + text.toString() + ")" + "(" + num.toString() + ")" + "\n" );


            logger.trace( "Unaliasing: " + name );
            String realName = manager.getSingleFromDbThatEquals(
                manager.getSchema().getAliasTable(),
                "alias",
                "realId",
                name
            );
            logger.debug( "Found Real Name: " + realName );
            String message = Engine.messageList.GetMessageToQuote(
                realName,
                text,
                Integer.parseInt( num )
            );

            logger.debug( "Got message: " + message );

            if( message == null ) {
                m.getChat().send( "Could not quote, no messages in my history match" );
            }
            manager.insertFieldsIntoTable(
                manager.getSchema().getQuotesTable(),
                new String[]{
                    realName, //Fully qualified name
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
