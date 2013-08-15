package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.db.schema.Table;
import skypebot.variables.VariableExpander;

import java.sql.SQLException;

public class ResponseHandler implements IHandler {

    private IDbManager dbManager;
    private VariableExpander variableExpander;
    private String[] prevResponse = new String[ 2 ];
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );


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
            if( m.getContent().contains( "bucket, forget that" ) ) {
                logger.trace( "Got forget that message" );
                dbManager.deleteRowFromTable(
                    dbManager.getSchema().getResponseTable(),
                    new String[]{ "query", "response" },
                    prevResponse
                );
                return;
            }
            String response = dbManager.getSingleFromDbThatContains(
                table,
                "query",
                "response",
                m.getContent()
            );
            if( response != null ) {
                setPreviousResponse(
                    m,
                    response
                );
                String name = getSenderName( m );
                m.getChat().send(
                    variableExpander.expandVariables(
                        name,
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

    private String getSenderName( ChatMessage m ) throws SkypeException {
        String name = m.getSender().getFullName().split( " " )[ 0 ];
        if( name.isEmpty() ) {
            name = m.getSender().getId();
        }
        return name;
    }

    private void setPreviousResponse(
        ChatMessage m,
        String response
    ) throws SkypeException {
        prevResponse[ 0 ] = m.getContent();
        prevResponse[ 1 ] = response;
    }

    @Override
    public void setManager( IDbManager m ) {
        dbManager = m;
    }

}
