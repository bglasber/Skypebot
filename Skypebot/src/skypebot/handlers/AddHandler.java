package skypebot.handlers;

import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

public class AddHandler implements IHandler {

	
	private DbManager dbManager;
	@Override
	public boolean canHandle(ChatMessage m) {
		
		try {
			return m.getContent().matches("bucket, add '[^']*' '[^']*'");
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
			Map<String, String> fieldsToInsert = new HashMap<String, String>();
			fieldsToInsert.put("query", splitMessage[0]);
			fieldsToInsert.put("response", splitMessage[1]);
			boolean wasSucessful = dbManager.insertFieldsIntoTable(dbManager.getSchema().getResponseTable(), fieldsToInsert);
			if(wasSucessful){
				m.getChat().send("Inserted " + splitMessage[0] + " -> " + splitMessage[1]);
			}
		} catch (SkypeException e) {
			//just drop it
			return;
		}
		catch (SqlJetException e){
			e.printStackTrace();
		}
		
	}

}
