package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import skypebot.db.IDbManager;
import skypebot.db.schema.Table;

/**
 * User: brad
 * Date: 8/15/13
 * Time: 10:56 PM
 */
public class StatsHandler implements IHandler {

    private IDbManager manager;

    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().contains( "bucket, stats" );
        } catch( SkypeException e ) {
            return false;
        }
    }

    @Override
    public void setManager( IDbManager m ) {
        manager = m;
    }

    @Override
    public void handle( ChatMessage m ) {
        try {
            m.getChat().send( "BUCKET STATS:" );
            m.getChat().send( "Responses - " + manager.getDbCount( manager.getSchema().getResponseTable() ) );
            m.getChat().send( "Videos - " + manager.getDbCount( manager.getSchema().getVideosTable() ) );
            m.getChat().send( "Nouns - " + manager.getDbCount( manager.getSchema().getNounTable() ) );
            m.getChat().send( "Plural Nouns - " + manager.getDbCount( manager.getSchema().getNounsTable() ) );
            m.getChat().send( "Verbs - " + manager.getDbCount( manager.getSchema().getVerbTable() ) );
            m.getChat().send( "Past Verbs - " + manager.getDbCount( manager.getSchema().getVerbedTable() ) );
            m.getChat().send( "'ing' verbs - " + manager.getDbCount( manager.getSchema().getVerbingTable() ) );
            m.getChat().send( "Adjectives - " + manager.getDbCount( manager.getSchema().getAdjectiveTable() ) );
        } catch( SkypeException e ) {
            return;
        }
    }
}
