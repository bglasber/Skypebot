package skypebot.variables;

import skypebot.db.DbManager;

public interface IVariable {

    public boolean isContainedInString( String message );

    public String expandVariableInString( String message );

    public void setDbManager( DbManager m );
}
