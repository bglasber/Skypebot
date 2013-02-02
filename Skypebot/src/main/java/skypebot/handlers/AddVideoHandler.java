package skypebot.handlers;

import java.sql.SQLException;
import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class AddVideoHandler implements IHandler {

	private DbManager dbManager;
	public static final String ANSI_BLUE = "\u001B[34m";

	private String[] videoSites = {
		"http://www.youtube.com/watch", "http://youtu.be.com/",
		"http://www.metacafe.com/watch",
		"http://www.cracked.com/video"
	};
	
	private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());


	
	@Override
	public boolean canHandle(ChatMessage m) {
		try {
			boolean willHandle = scanForVideoUrl(m.getContent());
			if(willHandle){
				logger.debug("AddVideoHandler will handle message");
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
		try {
			String chatText = m.getContent();
			List<String> urls = generateVideoUrlList(chatText);
			urls = removeDuplicates(urls);
			logger.debug("Got Urls to insert: ");
			logger.debug(urls);
			addVideos(urls,m.getSenderDisplayName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private List<String> generateVideoUrlList(String message) {
		List<String> urls = new ArrayList<String>();
		int urlIndex;
		int pointer = 0;
		String url;

		while(true){
			urlIndex = message.indexOf("http://", pointer);

			if(urlIndex == -1) {
				break;
			}

			url = getUrl(message.substring(urlIndex));
			pointer = urlIndex + url.length();

			for (String s : videoSites) {
				if (url.contains(s)) {
					urls.add(url);
					break;
				}
			}
		}

		return urls;
	}



	// rebuilds a list of urls, removing duplicate entries
	private List<String> removeDuplicates(List<String> l) {
		List<String> newList = new ArrayList<String>();
		boolean isDuplicate;

		for(int x=0; x<l.size(); x++) {
			isDuplicate = false;

			for(int y=x+1; y<l.size(); y++) {
				if (l.get(x).equals(l.get(y))) {
					isDuplicate = true;
				}
			}

			if(!isDuplicate){
				newList.add(l.get(x));
			}
		}

		return newList;
	}


	
	// extracts a url from given text (assumes url starts at the beginning of the string)
	private String getUrl(String subMessage) {
		int x = 0;
		
		while(true){
			try {
				if (subMessage.substring(x, x+1).equals(" "))
					break;
				x++;
			} catch (StringIndexOutOfBoundsException e) {
				break;
			}
		}
		return subMessage.substring(0, x);
	}



	private boolean scanForVideoUrl(String message) {
		for (String s : videoSites) {
			if (message.contains(s)) {
				return true;
			}
		}

		return false;
	}



	//  DATABASE INTERFACING

	// adds urls to the database
	private void addVideos(List<String> urls, String user) {
		try {
			for (String link : urls) {
				
				boolean wasSuccessful = dbManager.insertFieldsIntoTable(
						dbManager.getSchema().getVideosTable(),
						new String[]{ user, link });
				if (!wasSuccessful) {
					logger.error("Error occurred while adding video link to db.");
				} else {
					logger.info("Added Videos: " + urls.toString());
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

}