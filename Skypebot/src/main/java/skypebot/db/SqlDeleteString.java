package skypebot.db;

/**
 * User: brad
 * Date: 3/17/13
 * Time: 12:52 PM
 */
public class SqlDeleteString implements ISqlString {

    private String sqlString;

    public SqlDeleteString( String sql ) {
        sqlString = sql;
    }

    @Override
    public String getString() {
        return sqlString;
    }
}
