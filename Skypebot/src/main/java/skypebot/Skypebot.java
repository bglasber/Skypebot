package skypebot;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import skypebot.db.DbManager;
import skypebot.db.IDbProvider;
import skypebot.db.SqliteDb;
import skypebot.db.schema.Schema;
import skypebot.engine.Engine;
import skypebot.handlers.*;
import skypebot.handlers.addVariableHandlers.*;
import skypebot.variables.VariableExpander;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * @author Brad Glasbergen
 * @since 2013
 */
public class Skypebot {


    private static Logger logger = Logger.getLogger( Skypebot.class.getCanonicalName() );

    public static void main( String[] args ) throws SkypeException {

        DOMConfigurator.configure( "log4j.xml" );
        Engine e = new Engine();
        e.Launch();


    }


}
