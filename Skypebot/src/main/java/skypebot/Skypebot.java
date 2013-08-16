package skypebot;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import skypebot.db.DbManager;
import skypebot.db.IDbProvider;
import skypebot.db.SqliteDb;
import skypebot.db.schema.Schema;
import skypebot.handlers.*;
import skypebot.handlers.addVariableHandlers.*;
import skypebot.variables.VariableExpander;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Brad Glasbergen
 * @since 2013
 */
public class Skypebot {


    private static Logger logger = Logger.getLogger( Skypebot.class.getCanonicalName() );

    public static void main( String[] args ) throws SkypeException {

        DOMConfigurator.configure( "log4j.xml" );
        final DbManager dbManager = createDbManager();
        final IHandler[] handlersInOrder = initializeIHandlersOrDie( dbManager );
        attachToSkype(
            handlersInOrder,
            dbManager
        );


    }

    private static void attachToSkype(
        final IHandler[] handlersInOrder,
        final DbManager dbManager
    ) throws SkypeException {
        logger.debug( "Attaching to skype..." );
        Skype.setDaemon( false );
        Skype.addChatMessageListener(
            new ChatMessageAdapter() {
                public void chatMessageReceived( ChatMessage messageReceived ) throws SkypeException {
                    logger.debug( "Message Received: " + messageReceived.getContent() );
                    if( messageReceived.getType().equals( ChatMessage.Type.SAID ) ) {
                        for( IHandler h : handlersInOrder ) {
                            if( h.canHandle( messageReceived ) ) {
                                h.setManager( dbManager );
                                h.handle( messageReceived );
                                break;
                            }
                        }

                    }
                }
            }
        );
    }

    private static String sanitize( String messageToSanitize ) {

        messageToSanitize = messageToSanitize.replaceAll(
            "\"",
            ""
        );
        messageToSanitize = messageToSanitize.replaceAll(
            "-",
            ""
        );
        return messageToSanitize;

    }

    private static DbManager createDbManager() {
        Schema s = new Schema();
        return configureDBManager( s );
    }

    private static IHandler[] initializeIHandlersOrDie( DbManager m ) {
        logger.debug( "Adding handlers..." );
        final IHandler[] handlersInOrder = CreateHandlers( m );
        if( handlersInOrder.length == 0 ) {
            logger.error( "No handlers were created... terminating" );
            System.exit( 1 );
        }
        return handlersInOrder;
    }

    private static IHandler[] CreateHandlers( DbManager m ) {
        return new IHandler[]{
            new AddVideoHandler(),
            new GetVideoHandler(),
            new AddHandler(),
            new AddItemHandler(),
            new AddNounHandler(),
            new AddNounsHandler(),
            new AddAdjectiveHandler(),
            new AddVerbedHandler(),
            new AddVerbingHandler(),
            new AddVerbHandler(),
            new GetInventoryHandler(),
            new DropItemHandler(),
            new StatsHandler(),

            getResponseHandler( m )
        };
    }

    private static ResponseHandler getResponseHandler( DbManager m ) {
        try {
            return new ResponseHandler( new VariableExpander( m ) );
        } catch( IllegalAccessException e ) {
            logger.error( "Could not access IVariable implementation!" );
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        } catch( InstantiationException e ) {
            logger.error( "IVariable implementation could not be instantiated!" );
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        } catch( NoSuchMethodException e ) {
            logger.error( "IVariable implementation has no constructor!" );
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        } catch( InvocationTargetException e ) {
            logger.error( "IVariable target is invalid!" );
            logger.error( e.getMessage() );
            logger.error( e.getStackTrace() );
        }
        System.exit( 1 );
        return null;
    }

    private static DbManager configureDBManager( Schema s ) {
        final DbManager dbManager = new DbManager( s );
        IDbProvider dbProvider;
        try {
            dbProvider = new SqliteDb( "responses.db" );
            dbManager.setProvider( dbProvider );
            logger.debug( "Checking Schema..." );
            dbManager.constructSchema();
        } catch( SQLException e ) {
            //Could not open db, dump the stack
            e.printStackTrace();
            logger.error( "Could not construct the schema..." );
            logger.error( e );
        }
        logger.debug( "Successfully constructed the schema" );
        return dbManager;
    }

}
