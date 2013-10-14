package skypebot.engine;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.DbManager;
import skypebot.db.IDbProvider;
import skypebot.db.SqliteDb;
import skypebot.db.schema.Schema;
import skypebot.handlers.*;
import skypebot.handlers.addVariableHandlers.*;
import skypebot.variables.VariableExpander;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: brad
 * Date: 10/13/13
 * Time: 10:20 PM
 */
public class Engine {
    private static IHandler[] handlers;
    private static ExecutorService executor;
    private static Logger logger = Logger.getLogger( Engine.class.getCanonicalName() );
    private static DbManager dbManager;

    public Engine() {
        dbManager = createDbManager();
        handlers = CreateHandlers( dbManager );
        WorkerThread.SetHandlers( handlers );
        executor = Executors.newFixedThreadPool( getNumberOfCores() );
    }

    public boolean Launch() {
        Skype.setDaemon( false );
        try {
            Skype.addChatMessageListener(
                new ChatMessageAdapter() {
                    public void chatMessageReceived( ChatMessage messageReceived ) throws SkypeException {
                        logger.debug( "Message Received: " + messageReceived.getContent() );
                        if( messageReceived.getType().equals( ChatMessage.Type.SAID ) ) {
                            Runnable worker = new WorkerThread( messageReceived );
                            executor.execute( worker );
                        }
                    }
                }
            );
        } catch( SkypeException e ) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    private static DbManager createDbManager() {
        Schema s = new Schema();
        return configureDBManager( s );
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

    private int getNumberOfCores() {
        logger.info( "Checking System Info..." );
        logger.info(
            System.getProperty(
                "os.name",
                "unknown"
            ) + " " +
                System.getProperty(
                    "os.arch",
                    "unknown"
                ) + ", " +
                System.getProperty(
                    "os.version",
                    "unknown"
                )
        );
        int cores = Runtime.getRuntime().availableProcessors();
        logger.info( "Processors: " + cores );
        return cores;
    }

}
