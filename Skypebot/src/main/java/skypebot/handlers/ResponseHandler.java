package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.IDbManager;
import skypebot.db.schema.Table;
import skypebot.engine.Engine;
import skypebot.variables.VariableExpander;

import java.sql.SQLException;

public class ResponseHandler implements IHandler {

    private IDbManager dbManager;
    private VariableExpander variableExpander;
    private static String[] prevResponse = new String[ 2 ];
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
            String[] previousResponse;
            synchronized( prevResponse ) {
                previousResponse = prevResponse;
            }

            if( m.getContent().contains( "bucket, forget that" ) ) {
                logger.trace( "Got forget that message" );
                dbManager.deleteRowFromTable(
                    dbManager.getSchema().getResponseTable(),
                    new String[]{ "response" },
                    new String[]{ previousResponse[ 1 ] }
                );
                m.getChat().send( "Okay, forgetting '" + previousResponse[ 0 ] + "' -> '" + previousResponse[ 1 ] + "'" );
                return;
            }
            else if( m.getContent().contains( "bucket, what was that" ) ) {
                logger.trace( "Got what was that message" );
                m.getChat().send( "That was '" + previousResponse[ 0 ] + "' -> '" + previousResponse[ 1 ] + "'" );
                return;
            }
            logger.trace( "Putting the message into the message list" );
            Engine.AddToMessageList( m );

            //Gives us a number between 0.0 and 1.0, this should give us 35% chance of not responding
            boolean referencedOverride = m.getContent().contains( "bucket" ) || m.getContent().contains( "Bucket" );
            if( Math.random() > 0.65 &&
                !referencedOverride
                ) {
                logger.debug( "Dropping message, probability constraint not met" );
                //Drop message
                return;
            }

            String message = m.getContent().replaceAll(
                "\"",
                "\\\""
            );

            //Gives us back query matched as 0, response provided as 1
            String[] response = dbManager.getMultipleFieldsFromDbThatContains(
                table,
                "query",
                new String[]{ "query", "response", },
                message
            );
            //Don't repeat the same trigger
            if(
                response != null &&
                    IsNonDuplicatedResponse(
                        m,
                        referencedOverride,
                        response[ 1 ]
                    )
                ) {
                logger.trace( "PrevResponse - " + previousResponse[ 1 ] );
                logger.debug( "CurResponse - " + response[ 1 ] );
                synchronized( prevResponse ) {
                    prevResponse = response;
                }
                String name = getSenderName( m );
                String messageResponse = variableExpander.expandVariables(
                    name,
                    m.getChat(),
                    response[ 1 ]
                );
                Engine.AddToMessageList(
                    "Bucket",
                    messageResponse
                );
                m.getChat().send(
                    messageResponse
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

    @Override
    public void setManager( IDbManager m ) {
        dbManager = m;
    }

}
