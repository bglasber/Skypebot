package skypebot.handlers;

import com.skype.ChatMessage;
import com.skype.SkypeException;
import skypebot.db.IDbManager;

import java.sql.SQLException;

public class GetVideoHandler implements IHandler {

    private IDbManager dbManager;


    @Override
    public boolean canHandle( ChatMessage m ) {
        try {
            return m.getContent().toLowerCase().contains( "bucket, vid" );
        } catch( SkypeException e ) {
            //Something weird happened, just drop the message
            return false;
        }
    }


    @Override
    public void setManager( IDbManager m ) {
        dbManager = m;
    }


    @Override
    public void handle( ChatMessage m ) {
        getVideoUrl( m );
    }


    // grabs a random video link from the database
    public void getVideoUrl( ChatMessage m ) {
        try {
            m.getChat().send(
                dbManager.getSingleFromDb(
                    dbManager.getSchema().getVideosTable(),
                    "url"
                )
            );
        } catch( SkypeException e ) {
        } catch( SQLException e ) {
            e.printStackTrace();
        }
    }
}
