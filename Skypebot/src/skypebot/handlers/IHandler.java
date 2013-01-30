package skypebot.handlers;

import skypebot.db.DbManager;

import com.skype.ChatMessage;

public interface IHandler {
	
	public boolean canHandle(ChatMessage m);
	public void setManager(DbManager m);
	public void handle(ChatMessage m);

}