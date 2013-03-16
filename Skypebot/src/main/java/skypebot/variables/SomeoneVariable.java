package skypebot.variables;

import com.skype.Chat;
import com.skype.SkypeException;
import com.skype.User;
import skypebot.db.DbManager;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 3:09 PM
 */
public class SomeoneVariable implements IVariable {

    private DbManager manager;

    public SomeoneVariable( DbManager m ){
        manager = m;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$someone" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {

        String name = getRandomDisplayName( chatContext );
        return message.replaceFirst(
            "\\$someone",
            name
        );
    }

    private String getRandomDisplayName( Chat chatContext ) {
        try {
            User[] activeUsers = chatContext.getAllActiveMembers();
            String randomDisplayName = activeUsers[
                ( int ) ( Math.random() * activeUsers.length )
                ].getDisplayName();
            return randomDisplayName;
        } catch( SkypeException e ) {
            //just use our username
            return "bucket";
        }
    }
}
