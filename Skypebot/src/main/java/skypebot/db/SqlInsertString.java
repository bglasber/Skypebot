package skypebot.db;

/**
 * User: brad
 * Date: 3/2/13
 * Time: 1:11 PM
 */
public class SqlInsertString implements ISqlString {

    private String sqlString;

    public SqlInsertString() {

    }

    public SqlInsertString( String sql ) {
        sqlString = sql;
    }

    public String toString() {
        return sqlString;
    }
}
