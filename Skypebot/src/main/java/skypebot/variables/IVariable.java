package skypebot.variables;

import com.skype.Chat;

public interface IVariable {

    public boolean isContainedInString( String message );

    public String expandVariableInString(
        String displayNameThatSentMessage,
        Chat chatContext,
        String message
    );
}
