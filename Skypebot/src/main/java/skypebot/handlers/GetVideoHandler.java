package skypebot.handlers;

import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

public class GetVideoHandler implements IHandler {
	
	private DbManager dbManager;



	@Override
	public boolean canHandle(ChatMessage m) {
		try {
			if (m.getContent().toLowerCase().contains("bucket, video")) {
				return true;
			} else {
				return false;
			}
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
		getVideoUrl(m);
	}

	

	// grabs a random video link from the database
	public void getVideoUrl(ChatMessage m) {
		try {
			m.getChat().send(dbManager.getSingleFromDb(dbManager.getSchema().getVideosTable(), "url"));
		} catch (SkypeException e) {
			return;
		} catch (SqlJetException e){
			e.printStackTrace();
		}
	}
}
