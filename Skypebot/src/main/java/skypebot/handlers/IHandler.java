package skypebot.handlers;

import com.skype.ChatMessage;
import skypebot.db.DbManager;
import skypebot.db.IDbManager;

public interface IHandler {

    public boolean canHandle( ChatMessage m );

    public void setManager( IDbManager m );

    public void handle( ChatMessage m );

}