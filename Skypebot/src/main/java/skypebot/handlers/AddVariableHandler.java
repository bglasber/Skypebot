package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import skypebot.db.DbManager;

import java.sql.SQLException;

/**
 * User: brad
 * Date: 3/10/13
 * Time: 6:30 PM
 */
public class AddVariableHandler {

    private static DbManager manager;
    private Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public class AddNounHandler implements IHandler {

        @Override
        public boolean canHandle( ChatMessage m ) {
           try {
               return m.getContent().contains( "bucket, noun+" );
           }
           catch(SkypeException e ){
               //something weird happened, drop the message
               return false;
           }
        }

        @Override
        public void setManager( DbManager m ) {
            AddVariableHandler.manager = m;
        }

        @Override
        public void handle( ChatMessage m ) {
            try {
                String nounToAdd = m.getContent().replace( "bucket, noun+ ", "" );
                boolean wasSuccessful = manager.insertFieldsIntoTable(
                    manager.getSchema().getNounTable(),
                    new String[] { nounToAdd }
                );
                if(!wasSuccessful){
                    logger.error( "Could not insert noun into nouns table!" );
                }
                else {
                    logger.info( "Successfully inserted " + nounToAdd +" into nouns table" );
                }

            } catch( SkypeException e ) {
                //Just drop the message
                return;
            }

        }
    }
}
