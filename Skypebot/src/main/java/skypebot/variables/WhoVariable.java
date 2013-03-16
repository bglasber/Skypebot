package skypebot.variables;

import com.skype.Chat;
import skypebot.db.DbManager;
import skypebot.db.IDbManager;

/**
 * User: brad
 * Date: 3/16/13
 * Time: 3:23 PM
 */
public class WhoVariable implements IVariable {

    private IDbManager manager;

    public WhoVariable( IDbManager m ) {
        manager = m;
    }

    @Override
    public boolean isContainedInString( String message ) {
        return message.contains( "$who" );
    }

    @Override
    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    ) {
        return message.replaceFirst(
            "\\$who",
            displayNameThatSentMessage
        );
    }
}
