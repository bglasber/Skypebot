package skypebot.variables;

import skypebot.db.DbManager;

public interface IVariable {

    public boolean containsVariable( String message );

    public String expandVariable( String message );

    public void setDbManager( DbManager m );
}
