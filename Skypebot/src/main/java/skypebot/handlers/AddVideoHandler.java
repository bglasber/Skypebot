package skypebot.handlers;

import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

import java.util.List;
import java.util.ArrayList;

public class AddVideoHandler implements IHandler {

	private DbManager dbManager;
	public static final String ANSI_BLUE = "\u001B[34m";

	private String[] videoSites = {
		"http://www.youtube.com/watch", "http://youtu.be.com/",
		"http://www.metacafe.com/watch",
		"http://www.cracked.com/video"
	};


	
	@Override
	public boolean canHandle(ChatMessage m) {
		try {
			return scanForVideoUrl(m.getContent());
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
			addVideos(urls,m.getSenderDisplayName());
			tester(urls);
		} catch (Exception e) {
			System.err.println(e);
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
			Map<String, String> fieldsToInsert = new HashMap<String, String>();
			for (String link : urls) {
				fieldsToInsert.put("username",user);
				fieldsToInsert.put("url",link);
				boolean wasSuccessful = dbManager.insertFieldsIntoTable(dbManager.getSchema().getVideosTable(), fieldsToInsert);
				if (!wasSuccessful) {
					System.err.println("Error occurred while adding video link to db.");
				} else {
					System.out.println("Added Videos: " + urls.toString());
				}
			}
		} catch (SqlJetException e){
			e.printStackTrace();
		}
	}


	private void tester(List<String> l) {
		System.err.println(l.toString());
	}
}