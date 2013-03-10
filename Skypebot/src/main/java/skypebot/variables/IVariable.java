package skypebot.variables;

public interface IVariable {

    public boolean isContainedInString( String message );

    public String expandVariableInString( String message );
}
