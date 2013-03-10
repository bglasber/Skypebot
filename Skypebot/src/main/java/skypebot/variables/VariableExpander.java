package skypebot.variables;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

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

    public VariableExpander() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        classesToInitialize = getIVariableImplementations();
        for( Class<? extends IVariable> c : classesToInitialize ) {
            logger.trace( "Found IHandler Implementation: " + c.toString() );
            variablesToExpand.add( ( IVariable ) c.newInstance() );
        }
    }

    private Set<Class<? extends IVariable>> getIVariableImplementations() {
        Reflections reflections = new Reflections( "skypebot.variables" );
        return reflections.getSubTypesOf( IVariable.class );
    }

    public String expandVariables( String messageToExpand ) {
        for( IVariable var : variablesToExpand ) {
            if( var.isContainedInString( messageToExpand ) ) {
                messageToExpand = var.expandVariableInString( messageToExpand );
            }
        }
        return messageToExpand;
    }

}
