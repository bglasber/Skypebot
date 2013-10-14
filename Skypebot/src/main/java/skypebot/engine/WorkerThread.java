package skypebot.engine;

import com.skype.ChatMessage;
import skypebot.db.DbManager;
import skypebot.db.IDbManager;
import skypebot.handlers.IHandler;

/**
 * User: brad
 * Date: 10/13/13
 * Time: 10:15 PM
 */
public class WorkerThread implements Runnable {

    private static IHandler[] handlers;
    private static IDbManager dbManager;
    private ChatMessage messageReceived;

    public WorkerThread( ChatMessage messageToHandle ) {
        this.messageReceived = messageToHandle;
    }

    @Override
    public void run() {
        for( IHandler h : handlers ) {
            if( h.canHandle( messageReceived ) ) {
                h.setManager( dbManager );
                h.handle( messageReceived );
                break;
            }
        }
    }

    public static void SetHandlers( IHandler[] handlers ) {
        handlers = handlers;
    }

    public static void SetDbManager( IDbManager dbManager ) {
        WorkerThread.dbManager = dbManager;
    }
}
