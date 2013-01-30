
package skypebot;

import org.tmatesoft.sqljet.core.SqlJetException;

import skypebot.db.DbManager;
import skypebot.db.IDbProvider;
import skypebot.db.SqliteDb;
import skypebot.db.schema.Schema;
import skypebot.handlers.AddHandler;
import skypebot.handlers.IHandler;
import skypebot.handlers.ResponseHandler;

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


	public static void main(String[] args) throws SkypeException {
		
		
		final IHandler[] handlersInOrder = new IHandler[]{
				new AddHandler(),
				new ResponseHandler()
		};
		Schema s = new Schema();
		final DbManager dbManager = configureDBManager(s);
		
		Skype.setDaemon(false);
		Skype.addChatMessageListener(new ChatMessageAdapter(){
			public void chatMessageReceived(ChatMessage messageReceived) throws SkypeException{
				
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
			dbManager.constructSchema();
		} catch (SqlJetException e) {
			//Could not open db, dump the stack
			e.printStackTrace();
		}
		return dbManager;
	}

}
