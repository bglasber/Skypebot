package skypebot.db;

/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 3/2/13
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlConditionString implements ISqlString {

    private String sqlString;
    private boolean hasWhere = false;

    public SqlConditionString() {
        sqlString = "";
    }

    public SqlConditionString( String sqlString ) {
        this.sqlString = sqlString;
    }

    private void addWhereIfNeccessary() {
        if( !hasWhere ) {
            sqlString += " WHERE ";
            hasWhere = true;
        }
    }

    public void addContainsCondition(
        String tableColumn,
        String tableValue
    ) {
        addWhereIfNeccessary();
        sqlString += "\"" + tableValue + "\" LIKE \"%\" || " +  tableColumn + " || \"%\"";
    }

    public void addEqualsCondition(
        String tableColumn,
        String tableValue
    ) {
        addWhereIfNeccessary();
        sqlString += tableColumn + " = \"" + tableValue + "\"";
    }

    @Override
    public String getString() {
        return sqlString;
    }
}
