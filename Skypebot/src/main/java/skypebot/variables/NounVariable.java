package skypebot.variables;

import java.sql.SQLException;
import com.skype.SkypeException;

import skypebot.db.DbManager;

public class NounVariable implements IVariable {

	private DbManager dbManager;
	
	public NounVariable(){
		//null constructor
	}
	
	@Override
	public boolean containsVariable(String message) {
		return message.contains("$noun");
	}

	@Override
	public String expandVariable(String message) {
		//Do Db stuff to expand the variable
		
		try {
			String noun = dbManager.getSingleFromDb(dbManager.getSchema().getNounTable(), "noun", message);
			return message.replaceFirst("$noun", noun);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	@Override
	public void setDbManager(DbManager m){
		dbManager = m;
	}

}
