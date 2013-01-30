package skypebot.handlers;

import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;

import com.skype.ChatMessage;
import com.skype.SkypeException;

import java.util.ArrayList;

public class VideoHandler implements IHandler {

	private DbManager dbManager;

	private String[] videoSites = {
		"http://www.youtube.com/watch", "http://youtu.be.com/",
		"http://www.metacafe.com/watch",
		"http://www.cracked.com/video"
	};


	@Override
	public boolean canHandle(ChatMessage m) {
		try {
			return scanForVideoUrl(m.getContent().toLowerCase());
		} catch(SkypeException e) {
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
			String chatText = m.getContent().toLowerCase();
			ArrayList urls = generateVideoUrlList(chatText);
			urls = removeDuplicates(urls);
			addVideos(urls);
		} catch(Exception e) {
			System.out.println("Exc");
		}
	}



	private ArrayList generateVideoUrlList(String message) {
		ArrayList urls = new ArrayList();
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



	// adds urls to the database
	private void addVideos(ArrayList urls) {

	}



	//rebuilds a list of urls, removing duplicate entries
	private ArrayList removeDuplicates(ArrayList l) {
		ArrayList newList = new ArrayList();
		boolean isDuplicate;

		for(int x=0; x<l.size(); x++) {
			isDuplicate = false;

			for(int y=x+1; y<l.size(); y++) {
				if (l.get(x).equals(l.get(y))) {
					isDuplicate = true;
				}
			}

			if(!isDuplicate){
				newList.add(x);
			}
		}

		return newList;
	}


	
	// extracts a url from given text (assumes url starts at the beginning of the string)
	private String getUrl(String subMessage) {
		int x = 0;
		
		while(true){
			if (subMessage.substring(x, x+1).equals(" ") || subMessage.substring(x, x+1).equals(""))
				break;
			x++;
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



	//unused chat responder
	/*private void respond(ChatMessage m, String url) {
		try {
			m.getChat().send("Added video: " + url);
		} catch(Exception e) {

		}
	}*/
}