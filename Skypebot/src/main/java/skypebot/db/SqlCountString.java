package skypebot.db;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 12:37 PM
 */
public class SqlCountString implements ISqlString {

    private String sqlString;

    public SqlCountString( String sql ) {
        sqlString = sql;
    }

    public String getString() {
        return sqlString;
    }

}
