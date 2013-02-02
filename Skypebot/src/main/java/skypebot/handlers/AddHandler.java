package skypebot.handlers;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

public class AddHandler implements IHandler {

	
	private DbManager dbManager;
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName()); 
	@Override
	public boolean canHandle(ChatMessage m) {
		try {
			boolean willHandle = m.getContent().matches("bucket, add '[^']*' '[^']*'");
			if(willHandle){
				logger.debug("AddHandler will handle message...");
			}
			return willHandle;
		} catch (SkypeException e) {
			//Something weird happened, just drop the message
			return false;
		}
	}

	@Override
	public void setManager(DbManager m) {
		dbManager = m;
	}

	@Override
	public void handle(ChatMessage m) {
		//We assume that 'canHandle' it
		try {
			String message = m.getContent();
			String[] splitMessage = message.replaceAll("^.*'([^']*)' '([^']*)'$", "$1@$2").split("@");
			boolean wasSuccessful = dbManager.insertFieldsIntoTable(
					dbManager.getSchema().getResponseTable(), 
					splitMessage);
			if(wasSuccessful){
				m.getChat().send("Inserted " + splitMessage[0] + " -> " + splitMessage[1]);
				logger.info("Inserted"  + splitMessage[0] + " -> " + splitMessage[1]);
			}
		} catch (SkypeException e) {
			//just drop it
			logger.error("AddHandler could not handle message - could not get message content");
			return;
		}
		catch (SQLException e){
			logger.error("Could not insert message into the table.");
			logger.error(e);
			e.printStackTrace();
		}
		
	}

}
