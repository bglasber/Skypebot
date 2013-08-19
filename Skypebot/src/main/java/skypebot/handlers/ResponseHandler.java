package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.db.schema.Table;
import skypebot.variables.VariableExpander;

import java.sql.SQLException;

import static skypebot.Skypebot.sanitize;

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
                    new String[]{ "response" },
                    new String[]{ prevResponse[ 1 ] }
                );
                m.getChat().send( "Okay, forgetting '" + prevResponse[ 0 ] + "' -> '" + prevResponse[ 1 ] + "'" );
                return;
            }
            //Gives us a number between 0.0 and 1.0, this should give us 35% chance of not responding
            boolean referencedOverride = m.getContent().contains( "bucket" ) || m.getContent().contains( "Bucket" );
            if( Math.random() > 0.65 &&
                !referencedOverride
                ) {
                logger.debug( "Dropping message, probability constraint not met" );
                //Drop message
                return;
            }
            String response = dbManager.getSingleFromDbThatContains(
                table,
                "query",
                "response",
                sanitize(
                    m.getContent()
                )
            );
            //Don't repeat the same trigger
            if(
                IsNonDuplicatedResponse(
                    m,
                    referencedOverride,
                    response
                )
                ) {
                logger.trace( "PrevResponse - " + prevResponse[ 1 ] );
                logger.trace( "CurResponse - " + response );
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
            else {
                logger.debug( "Dropping response, duplicate response or null" );
            }
        } catch( SkypeException e ) {
        } catch( SQLException e ) {
            e.printStackTrace();
        }

    }

    private boolean IsNonDuplicatedResponse(
        ChatMessage m,
        boolean referencedOverride,
        String response
    ) throws SkypeException {
        return response != null &&
            ( referencedOverride ||
                !( response.equals( prevResponse[ 1 ] ) ||
                    m.getContent().equals( prevResponse[ 0 ] )
                )
            );
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
