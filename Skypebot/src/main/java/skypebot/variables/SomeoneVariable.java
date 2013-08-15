package skypebot.variables;

import com.skype.Chat;
import com.skype.SkypeException;
import com.skype.User;
import skypebot.db.IDbManager;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 3:09 PM
 */
public class SomeoneVariable implements IVariable {

    private IDbManager manager;

    public SomeoneVariable( IDbManager m ) {
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

            User u = activeUsers[
                ( int ) ( Math.random() * activeUsers.length )
                ];
            String name = u.getFullName().split( " " )[ 0 ];
            if( name.isEmpty() ) {
                name = u.getId();
            }
            return name;
        } catch( SkypeException e ) {
            //just use our username
            return "bucket";
        }
    }
}
