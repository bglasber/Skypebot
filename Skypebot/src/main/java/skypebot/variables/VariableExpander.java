package skypebot.variables;

import com.skype.Chat;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import skypebot.db.IDbManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * User: brad
 * Date: 3/10/13
 * Time: 4:56 PM
 */
public class VariableExpander {

    Set<Class<? extends IVariable>> classesToInitialize;
    Set<IVariable> variablesToExpand = new HashSet<IVariable>();
    Logger logger = Logger.getLogger( this.getClass().getCanonicalName() );

    public VariableExpander( IDbManager manager ) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        classesToInitialize = getIVariableImplementations();
        for( Class<? extends IVariable> c : classesToInitialize ) {
            logger.trace( "Found IVariable Implementation: " + c.toString() );
            variablesToExpand.add( ( IVariable ) c.getConstructor( IDbManager.class ).newInstance( manager ) );
        }
    }

    private Set<Class<? extends IVariable>> getIVariableImplementations() {
        Reflections reflections = new Reflections( "skypebot.variables" );
        return reflections.getSubTypesOf( IVariable.class );
    }

    public String expandVariables(
        String displayNameThatSentMessage,
        Chat chatContext,
        String messageToExpand
    ) {
        for( IVariable var : variablesToExpand ) {
            while( var.isContainedInString( messageToExpand ) ) {
                messageToExpand = var.expandVariableInString(
                    displayNameThatSentMessage,
                    chatContext,
                    messageToExpand
                );
            }
        }
        return messageToExpand;
    }

}
