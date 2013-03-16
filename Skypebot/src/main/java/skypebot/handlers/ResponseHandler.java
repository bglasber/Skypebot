package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import skypebot.db.DbManager;
import skypebot.db.schema.Table;
import skypebot.variables.VariableExpander;

import java.sql.SQLException;

public class ResponseHandler implements IHandler {

    private DbManager dbManager;
    private VariableExpander variableExpander;


    public ResponseHandler( VariableExpander expander ) {
        variableExpander = expander;
    }

    @Override
    public boolean canHandle( ChatMessage m ) {
        //The response handler handles everything that comes in if it doesn't match anything else.
        return true;
    }

    @Override
    public void handle( ChatMessage m ) {
        // Get Response From DB
        Table table = dbManager.getSchema().getResponseTable();
        try {
            String response = dbManager.getSingleFromDbThatContains(
                table,
                "query",
                "response",
                m.getContent()
            );
            if( response != null ) {
                m.getChat().send(
                    variableExpander.expandVariables(
                        m.getSender().getDisplayName(),
                        m.getChat(),
                        response
                    )
                );
            }
        } catch( SkypeException e ) {
            return;
        } catch( SQLException e ) {
            e.printStackTrace();
        }

    }

    @Override
    public void setManager( DbManager m ) {
        dbManager = m;
    }

}
