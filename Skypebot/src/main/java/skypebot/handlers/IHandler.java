package skypebot.handlers;

import com.skype.ChatMessage;
import skypebot.db.DbManager;

public interface IHandler {

    public boolean canHandle( ChatMessage m );

    public void setManager( DbManager m );

    public void handle( ChatMessage m );

}