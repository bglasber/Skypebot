package skypebot.variables;

import skypebot.db.DbManager;

import java.sql.SQLException;

public class NounVariable implements IVariable {

    private DbManager dbManager;

    public NounVariable() {
        //null constructor
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$noun" );
    }

    @Override
    public String expandVariableInString( String message ) {
        //Do Db stuff to expand the variable

        try {
            String noun = dbManager.getSingleFromDb(
                dbManager.getSchema().getNounTable(),
                "noun"
            );
            return message.replaceFirst(
                "$noun",
                noun
            );
        } catch( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void setDbManager( DbManager m ) {
        dbManager = m;
    }

}
