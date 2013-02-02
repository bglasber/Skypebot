
package skypebot;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import skypebot.db.DbManager;
import skypebot.db.IDbProvider;
import skypebot.db.SqliteDb;
import skypebot.db.schema.Schema;
import skypebot.handlers.*;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
/**
 * @author Brad Glasbergen
 * @since 2013
 *
 */
public class Skypebot {


	private static Logger logger = Logger.getLogger(Skypebot.class.getCanonicalName());
	
	public static void main(String[] args) throws SkypeException {
		
		DOMConfigurator.configure("log4j.xml");
		
		logger.debug("Adding handlers...");
		final IHandler[] handlersInOrder = new IHandler[]{
				new AddVideoHandler(),
				new GetVideoHandler(),
				new AddHandler(),
				new ResponseHandler()
		};
		Schema s = new Schema();
		final DbManager dbManager = configureDBManager(s);
		
		Skype.setDaemon(false);
		Skype.addChatMessageListener(new ChatMessageAdapter(){
			public void chatMessageReceived(ChatMessage messageReceived) throws SkypeException{
				
				logger.debug("Message Received: " + messageReceived.getContent());
				if(messageReceived.getType().equals(ChatMessage.Type.SAID)){
					for(IHandler h : handlersInOrder){
						if(h.canHandle(messageReceived)){
							h.setManager(dbManager);
							h.handle(messageReceived);
							break;
						}
					}
					
				}
			}
		});
		

	}

	private static DbManager configureDBManager(Schema s) {
		final DbManager dbManager = new DbManager(s);
		IDbProvider dbProvider;
		try {
			dbProvider = new SqliteDb("responses.db");
			dbManager.setProvider(dbProvider);
			logger.debug("Checking Schema...");
			dbManager.constructSchema();
		} catch (SQLException e) {
			//Could not open db, dump the stack
			e.printStackTrace();
			logger.error("Could not construct the schema...");
			logger.error(e);
		}
		logger.debug("Successfully constructed the schema");
		return dbManager;
	}

}
