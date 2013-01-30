package skypebot.handlers;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;
import skypebot.db.schema.Table;

import com.skype.ChatMessage;
import com.skype.SkypeException;

public class ResponseHandler implements IHandler {

	private DbManager dbManager;
	@Override
	public boolean canHandle(ChatMessage m) {
		//The response handler handles everything that comes in if it doesn't match anything else.
		return true;
	}

	@Override
	public void handle(ChatMessage m) {
		// Get Response From DB
		Table table = dbManager.getSchema().getResponseTable();
		try {
			String response = dbManager.getSingleFromDb(table, "response", m.getContent());
			if(response != null) {
				m.getChat().send(response);
			}
		} catch (SqlJetException | SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void setManager(DbManager m){
		dbManager = m;
	}

}
